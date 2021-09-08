package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

/**
 * @author LT_Name
 */
public class UnloadArena extends BaseSubCommand {

    public UnloadArena(String name) {
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
        if (args.length >= 2) {
            String name = args[1];
            if (!this.crystalWars.getArenas().containsKey(name)) {
                sender.sendMessage("游戏房间: " + name + " 还未加载！无法卸载！");
                return true;
            }
            this.crystalWars.unloadArena(name);
            sender.sendMessage("游戏房间: " + name + " 卸载完成！");
        }else {
            this.crystalWars.unloadAllArena();
            sender.sendMessage("已卸载所有游戏房间！");
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }

}
