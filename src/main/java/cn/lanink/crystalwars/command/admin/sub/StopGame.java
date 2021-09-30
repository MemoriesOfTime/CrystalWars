package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

/**
 * @author LT_Name
 */
public class StopGame extends BaseSubCommand {

    public StopGame(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String name;
        if (args.length >= 2) {
            name = args[1];
        }else {
            if (sender instanceof Player) {
                name = ((Player) sender).getLevel().getFolderName();
            }else {
                sender.sendMessage("请输入游戏房间名称，或在游戏中使用此命令！");
                return true;
            }
        }
        BaseArena arena = this.crystalWars.getArenas().get(name);

        if (arena == null) {
            sender.sendMessage("游戏房间: " + name + " 不存在或未加载！");
            return true;
        }
        if (arena.getArenaStatus() != BaseArena.ArenaStatus.GAME &&
                arena.getArenaStatus() != BaseArena.ArenaStatus.VICTORY) {
            sender.sendMessage("游戏房间: " + name + " 没有开始游戏，无需停止！");
            return true;
        }

        arena.gameEnd();
        sender.sendMessage("游戏房间: " + name + " 已停止！");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("roomName", true, CommandParamType.TEXT) };
    }
}
