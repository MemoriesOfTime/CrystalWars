package cn.lanink.crystalwars.command.user.sub;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class QuitRoom extends BaseSubCommand {

    public QuitRoom(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isPlayer();
    }

    @Override
    public String[] getAliases() {
        return new String[] { "退出" };
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        for (BaseArena arena : this.crystalWars.getArenas().values()) {
            if (arena.isPlaying(player)) {
                arena.quitRoom(player);
                return true;
            }
        }
        sender.sendMessage("§a你还没有加入任何游戏房间！");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
