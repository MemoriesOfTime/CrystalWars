package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;

/**
 * @author LT_Name
 */
public class CreateArena extends BaseSubCommand {

    public CreateArena(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isOp() && sender.isPlayer();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c请输入世界名称！");
            return false;
        }
        Player player = (Player) sender;
        if (!this.crystalWars.getArenaConfigs().containsKey(args[1])) {
            if (Server.getInstance().loadLevel(args[1])) {
                Level level = Server.getInstance().getLevelByName(args[1]);
                this.crystalWars.getOrCreateArenaConfig(level);
                sender.sendMessage("§a游戏房间: §f" + args[1] + " §a创建成功！");
                if (player.getLevel() != level) {
                    player.teleport(level.getSafeSpawn());
                }
                Server.getInstance().dispatchCommand(sender, this.crystalWars.getCmdAdmin() + " SetArena");
            } else {
                sender.sendMessage("§c世界: §f" + args[1] + " §c不存在！请输入一个正确的世界名称！");
            }
        } else {
            sender.sendMessage("§c已存在 §f" + args[1] + " §c游戏房间配置文件！");
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("worldName", CommandParamType.TEXT) };
    }
}
