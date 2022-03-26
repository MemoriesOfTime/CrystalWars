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
                sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_StartGame_needArenaName"));
                return true;
            }
        }
        BaseArena arena = this.crystalWars.getArenas().get(name);

        if (arena == null) {
            sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_StartGame_ArenaNotLoaded", name));
            return true;
        }
        if (arena.getArenaStatus() != BaseArena.ArenaStatus.WAIT) {
            sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_StartGame_statusNotMeetTheCondition", name));
            return true;
        }
        if (arena.getPlayerCount() < 2) {
            sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_StartGame_notEnoughPlayers", name));
            return true;
        }

        arena.gameStart();
        sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_StartGame_start", name));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("roomName", true, CommandParamType.TEXT) };
    }
}
