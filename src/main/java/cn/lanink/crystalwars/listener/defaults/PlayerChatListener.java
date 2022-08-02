package cn.lanink.crystalwars.listener.defaults;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.gamecore.listener.BaseGameListener;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @author LT_Name
 */
public class PlayerChatListener extends BaseGameListener<BaseArena> {

    /**
     * 玩家执行命令事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (player == null || message == null) {
            return;
        }
        BaseArena arena = this.getListenerRoom(player.getLevel());
        if (arena == null || !arena.isPlaying(player)) {
            return;
        }

        message = message.replace("/", "").split(" ")[0];
        if (CrystalWars.getInstance().getCmdUser().equalsIgnoreCase(message) ||
                CrystalWars.getInstance().getCmdAdmin().equalsIgnoreCase(message)) {
            return;
        }
        for (String string : CrystalWars.getInstance().getCmdUserAliases()) {
            if (string.equalsIgnoreCase(message)) {
                return;
            }
        }
        for (String string : CrystalWars.getInstance().getCmdAdminAliases()) {
            if (string.equalsIgnoreCase(message)) {
                return;
            }
        }
        for (String string : CrystalWars.getInstance().getCmdWhitelist()) {
            if (string.equalsIgnoreCase(message)) {
                return;
            }
        }
        event.setMessage("");
        event.setCancelled(true);
        player.sendMessage(CrystalWars.getInstance().getLang(player).translateString("tips_commands_notAvailableInArena"));
    }

    /**
     * 玩家聊天事件
     * @param event 事件
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onChat(PlayerChatEvent event) {
        //TODO 队伍聊天 全局聊天
    }

}
