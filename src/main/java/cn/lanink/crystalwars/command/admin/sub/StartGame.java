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
public class StartGame extends BaseSubCommand {

    public StartGame(String name) {
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
        if (arena.getArenaStatus() != BaseArena.ArenaStatus.WAIT) {
            sender.sendMessage("游戏房间: " + name + " 不满足要求，无法开始游戏！");
            return true;
        }
        if (arena.getPlayerCount() < 2) {
            sender.sendMessage("游戏房间: " + name + " 玩家少于两人，无法开始游戏！");
            return true;
        }

        arena.gameStart();
        sender.sendMessage("游戏房间: " + name + " 已开始游戏！");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("roomName", true, CommandParamType.TEXT) };
    }
}
