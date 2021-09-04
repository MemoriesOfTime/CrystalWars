package cn.lanink.crystalwars.command.admin;

import cn.lanink.crystalwars.command.BaseCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

/**
 * @author LT_Name
 */
public class AdminCommand extends BaseCommand {

    public AdminCommand(String name, String[] aliases) {
        super(name, "CrystalWars 管理命令");
        this.setAliases(aliases);

        //TODO

    }

    @Override
    public void sendHelp(CommandSender sender) {

    }

    @Override
    public void sendGUI(Player player) {

    }
}
