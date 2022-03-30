package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
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
                sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_UnloadArena_ArenaNotLoaded"));
                return true;
            }
            this.crystalWars.unloadArena(name);
            sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_UnloadArena_unload", name));
        }else {
            this.crystalWars.unloadAllArena();
            sender.sendMessage(this.crystalWars.getLanguage().translateString("plugin_command_admin_UnloadArena_unloadAll"));
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("roomName", true, CommandParamType.TEXT) };
    }

}
