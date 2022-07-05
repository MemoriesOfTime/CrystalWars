package cn.lanink.crystalwars.command.admin;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.command.BaseCommand;
import cn.lanink.crystalwars.command.admin.sub.*;
import cn.lanink.crystalwars.utils.FormHelper;
import cn.lanink.gamecore.utils.Language;
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

        this.addSubCommand(new CreateArena("CreateArena"));
        this.addSubCommand(new SetArena("SetArena"));

        this.addSubCommand(new StartGame("StartGame"));
        this.addSubCommand(new StopGame("StopGame"));

        this.addSubCommand(new UnloadArena("UnloadArena"));
        this.addSubCommand(new Reload("reload"));

    }

    @Override
    public void sendHelp(CommandSender sender) {
        Language language = CrystalWars.getInstance().getLang();
        sender.sendMessage(
                "§a/" + this.getName() + " "+language.translateString("tips_help_openGui")+"\n" +
                        "§a/" + this.getName() + " "+language.translateString("tips_helps_createRoom")+"\n" +
                        "§a/" + this.getName() + " "+language.translateString("tips_helps_setArena")+"\n" +
                        "§a/" + this.getName() + " "+language.translateString("tips_helps_startGame")+"\n" +
                        "§a/" + this.getName() + " "+language.translateString("tips_helps_unloadArena")+"\n" +
                        "§a/" + this.getName() + " "+language.translateString("tips_helps_unloadAllArena")+"\n" +
                        "§a/" + this.getName() + " "+language.translateString("tips_helps_reload")+"\n"

        );
    }

    @Override
    public void sendGUI(Player player) {
        FormHelper.sendAdminMainMenu(player);
    }
}
