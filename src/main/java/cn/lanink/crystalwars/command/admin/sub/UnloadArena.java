package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.lanink.gamecore.utils.Language;
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
        Language language = CrystalWars.getInstance().getLang();
        if (args.length >= 2) {
            String name = args[1];
            if (!this.crystalWars.getArenas().containsKey(name)) {
                sender.sendMessage(language.translateString("tips_roomUnprepared", name));
                return true;
            }
            this.crystalWars.unloadArena(name);
            sender.sendMessage(language.translateString("tips_unloadRoom", name));
        }else {
            this.crystalWars.unloadAllArena();
            sender.sendMessage(language.translateString("tips_unloadAllRooms"));
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("roomName", true, CommandParamType.TEXT) };
    }

}
