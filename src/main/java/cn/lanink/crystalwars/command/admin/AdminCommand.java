package cn.lanink.crystalwars.command.admin;

import cn.lanink.crystalwars.command.BaseCommand;
import cn.lanink.crystalwars.command.admin.sub.Reload;
import cn.lanink.crystalwars.command.admin.sub.UnloadArena;
import cn.lanink.crystalwars.form.FormHelper;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

/**
 * @author LT_Name
 */
public class AdminCommand extends BaseCommand {

    public AdminCommand(String name, String[] aliases) {
        super(name, "CrystalWars 管理命令");
        this.setAliases(aliases);
        this.setPermission("crystalwars.command.admin");

        //TODO
        this.addSubCommand(new UnloadArena("UnloadArena"));
        this.addSubCommand(new Reload("reload"));

    }

    @Override
    public void sendHelp(CommandSender sender) {
        //TODO
    }

    @Override
    public void sendGUI(Player player) {
        FormHelper.sendAdminMainMenu(player);
    }
}
