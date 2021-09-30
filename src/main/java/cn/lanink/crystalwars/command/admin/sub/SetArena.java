package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.arena.ArenaSet;
import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.lanink.crystalwars.utils.FormHelper;
import cn.lanink.crystalwars.utils.exception.ArenaLoadException;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;

/**
 * @author LT_Name
 */
public class SetArena extends BaseSubCommand {

    public SetArena(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isPlayer() && sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (this.crystalWars.getArenaSetMap().containsKey(player)) {
            this.crystalWars.getArenaSetMap().get(player).exit();
            sender.sendMessage("你已退出设置！");
            return true;
        }
        if (args.length < 2) {
            FormHelper.sendAdminSetArena(player);
            return true;
        }
        String worldName = args[1];
        if (this.crystalWars.getArenaConfigs().containsKey(worldName) && Server.getInstance().loadLevel(worldName)) {
            try {
                this.crystalWars.getArenaSetMap().put(player, new ArenaSet(worldName, this.crystalWars.getOrCreateArenaConfig(worldName), player));
            } catch (ArenaLoadException e) {
                this.crystalWars.getLogger().error("执行命令: " + this.crystalWars.getCmdAdmin() + " SetArena " + worldName + " 时出错！", e);
                sender.sendMessage("§c错误！请参考控制台输出！");
                return true;
            }
            Level level = Server.getInstance().getLevelByName(worldName);
            if (player.getLevel() != level) {
                player.teleport(level.getSafeSpawn());
            }
        }else {
            sender.sendMessage("§c游戏房间: §f" + args[1] + " §c不存在，请先创建！");
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("worldName", CommandParamType.TEXT) };
    }
}
