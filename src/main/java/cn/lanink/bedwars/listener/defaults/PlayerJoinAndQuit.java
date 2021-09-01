package cn.lanink.bedwars.listener.defaults;

import cn.lanink.bedwars.BedWars;
import cn.lanink.bedwars.arena.BaseArena;
import cn.lanink.gamecore.utils.SavePlayerInventory;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;

import java.util.LinkedHashMap;

/**
 * @author LT_Name
 */
@SuppressWarnings("unused")
public class PlayerJoinAndQuit implements Listener {

    private final BedWars bedWars;

    public PlayerJoinAndQuit(BedWars bedWars) {
        this.bedWars = bedWars;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (this.bedWars.getArenas().containsKey(player.getLevel().getFolderName())) {
            Server.getInstance().getScheduler().scheduleDelayedTask(this.bedWars, () -> {
                if (player.isOnline()) {
                    SavePlayerInventory.restore(this.bedWars, player);
                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                }
            }, 1);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        for (BaseArena arena : this.bedWars.getArenas().values()) {
            if (arena.isPlaying(player)) {
                arena.quitRoom(player);
            }
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
            LinkedHashMap<String, BaseArena> arenas = this.bedWars.getArenas();
            if (arenas.containsKey(fromLevel) && arenas.get(fromLevel).isPlaying(player)) {
                event.setCancelled(true);
                player.sendMessage("§c请使用命令退出游戏房间！");
            }else if (!player.isOp() && arenas.containsKey(toLevel) && !arenas.get(toLevel).isPlaying(player)) {
                event.setCancelled(true);
                player.sendMessage("§c请使用命令加入游戏房间！");
            }
        }
    }

}
