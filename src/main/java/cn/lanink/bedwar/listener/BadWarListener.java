package cn.lanink.bedwar.listener;

import cn.lanink.bedwar.BedWars;
import cn.lanink.bedwar.event.BadWarRoomStartEvent;
import cn.lanink.bedwar.event.BedWarPlayerDamageEvent;
import cn.lanink.bedwar.event.BedWarPlayerDeathEvent;
import cn.lanink.bedwar.event.BedWarRoomChooseIdentityEvent;
import cn.lanink.bedwar.room.Room;
import cn.lanink.bedwar.tasks.game.MineralTask;
import cn.lanink.bedwar.tasks.game.TimeTask;
import cn.lanink.bedwar.utils.Language;
import cn.lanink.bedwar.utils.Tools;
import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.level.Sound;
import me.onebone.economyapi.EconomyAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * 游戏监听器（插件事件）
 * @author lt_name
 */
public class BadWarListener implements Listener {

    private final BedWars murderMystery;
    private final Language language;

    public BadWarListener(BedWars murderMystery) {
        this.murderMystery = murderMystery;
        this.language = murderMystery.getLanguage();
    }

    /**
     * 房间开始事件
     * @param event 事件
     */
    @EventHandler
    public void onRoomStart(BadWarRoomStartEvent event) {
        Room room = event.getRoom();
        Server.getInstance().getPluginManager().callEvent(new BedWarRoomChooseIdentityEvent(room));
        int i = 0;
        for (Map.Entry<Player, Integer> entry : room.getPlayers().entrySet()) {

        }



        room.setMode(2);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                BedWars.getInstance(), new TimeTask(this.murderMystery, room), 20,true);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                BedWars.getInstance(), new MineralTask(this.murderMystery, room), 20);
    }

    /**
     * 玩家分配身份事件
     * @param event 事件
     */
    @EventHandler
    public void onChooseIdentity(BedWarRoomChooseIdentityEvent event) {
        if (!event.isCancelled()) {
            Room room = event.getRoom();
            LinkedHashMap<Player, Integer> players = room.getPlayers();
            int random1 = new Random().nextInt(players.size()) + 1;
            int random2;
            do {
                random2 = new Random().nextInt(players.size()) + 1;
            }while (random1 == random2);
            int j = 0;
            for (Player player : players.keySet()) {
                j++;
                player.getInventory().clearAll();
                //侦探
                if (j == random1) {
                    room.addPlaying(player, 2);
                    player.sendTitle(this.language.titleDetectiveTitle,
                            this.language.titleDetectiveSubtitle, 10, 40, 10);
                    continue;
                }
                //杀手
                if (j == random2) {
                    room.addPlaying(player, 3);
                    player.sendTitle(this.language.titleKillerTitle,
                            this.language.titleKillerSubtitle, 10, 40, 10);
                    continue;
                }
                room.addPlaying(player, 1);
                player.sendTitle(this.language.titleCommonPeopleTitle,
                        this.language.titleCommonPeopleSubtitle, 10, 40, 10);
            }
        }
    }

    /**
     * 玩家被攻击事件(符合游戏条件的攻击)
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDamage(BedWarPlayerDamageEvent event) {
        if (!event.isCancelled()) {
            Player player1 = event.getDamage();
            Player player2 = event.getPlayer();
            Room room = event.getRoom();
            if (player1 == null || player2 == null || room == null) {
                return;
            }
            //攻击者是杀手
            if (room.getPlayerMode(player1) == 3) {
                player1.sendMessage(this.language.killPlayer);
                player2.sendTitle(this.language.deathTitle,
                        this.language.deathByKillerSubtitle, 20, 60, 20);
            }else { //攻击者是平民或侦探
                if (room.getPlayerMode(player2) == 3) {
                    player1.sendMessage(this.language.killKiller);
                    int money = this.murderMystery.getConfig().getInt("击杀杀手额外奖励", 0);
                    if (money > 0) {
                        EconomyAPI.getInstance().addMoney(player1, money);
                        player1.sendMessage(this.language.victoryKillKillerMoney.replace("%money%", money + ""));
                    }
                    player2.sendTitle(this.language.deathTitle,
                            this.language.killerDeathSubtitle, 10, 20, 20);
                } else {
                    player1.sendTitle(this.language.deathTitle,
                            this.language.deathByDamageTeammateSubtitle, 20, 60, 20);
                    player2.sendTitle(this.language.deathTitle,
                            this.language.deathByTeammateSubtitle, 20, 60, 20);
                    Server.getInstance().getPluginManager().callEvent(new BedWarPlayerDeathEvent(room, player1));
                }
            }
            Server.getInstance().getPluginManager().callEvent(new BedWarPlayerDeathEvent(room, player2));
        }
    }

    /**
     * 玩家死亡事件（游戏中死亡）
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDeath(BedWarPlayerDeathEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            Room room = event.getRoom();
            if (player == null || room == null) {
                return;
            }
            player.getInventory().clearAll();
            player.setAllowModifyWorld(true);
            player.setAdventureSettings((new AdventureSettings(player)).set(AdventureSettings.Type.ALLOW_FLIGHT, true));
            player.setGamemode(3);
            if (room.getPlayerMode(player) == 2) {
                room.getLevel().dropItem(player, Tools.getMurderItem(1));
            }
            room.addPlaying(player, 0);
            Tools.setPlayerInvisible(player, true);
            Tools.addSound(room, Sound.GAME_PLAYER_HURT);
        }
    }

}
