package cn.lanink.crystalwars.listener.defaults;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.ArenaSet;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.utils.FormHelper;
import cn.lanink.gamecore.utils.PlayerDataUtils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author LT_Name
 */
@SuppressWarnings("unused")
public class PlayerJoinAndQuit implements Listener {

    private final CrystalWars crystalWars;

    public PlayerJoinAndQuit(CrystalWars crystalWars) {
        this.crystalWars = crystalWars;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        player.addServerSettings(FormHelper.getPlayerSetting(player));
        if (this.crystalWars.getArenas().containsKey(player.getLevel().getFolderName())) {
            Server.getInstance().getScheduler().scheduleDelayedTask(this.crystalWars, () -> {
                if (player.isOnline()) {
                    File file = new File(crystalWars.getDataFolder() + "/PlayerInventory/" + player.getName() + ".json");
                    if (file.exists()) {
                        PlayerDataUtils.PlayerData playerData = PlayerDataUtils.create(player, file);
                        if (file.delete()) {
                            playerData.restoreAll();
                        }
                    }
                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                }
            }, 1);
        }
        File file = new File(CrystalWars.getInstance().getDataFolder() + "/cache/ArenaSetPlayerData/" + player.getName() + ".json");
        if (file.exists()) {
            PlayerDataUtils.create(player, file).restoreAll();
            file.delete();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        for (BaseArena arena : this.crystalWars.getArenas().values()) {
            if (arena.isPlaying(player)) {
                arena.quitRoom(player);
            }
        }
        ArenaSet arenaSet = this.crystalWars.getArenaSetMap().get(player);
        if (arenaSet != null) {
            arenaSet.exit();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTp(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String fromLevel = event.getFrom().getLevel().getFolderName();
        String toLevel = event.getTo().getLevel().getFolderName();
        if (player == null || fromLevel == null || toLevel == null) {
            return;
        }
        if (!fromLevel.equals(toLevel)) {
            LinkedHashMap<String, BaseArena> arenas = this.crystalWars.getArenas();
            if (arenas.containsKey(fromLevel) && arenas.get(fromLevel).isPlaying(player)) {
                arenas.get(fromLevel).quitRoom(player);
            }else if (!player.isOp() && arenas.containsKey(toLevel) && !arenas.get(toLevel).isPlaying(player)) {
                event.setCancelled(true);
                player.sendMessage(this.crystalWars.getLang(player).translateString("tips_useCommandToJoinRoom"));
            }
        }
    }

}
