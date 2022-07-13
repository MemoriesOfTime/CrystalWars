package cn.lanink.crystalwars.command.user;

import cn.lanink.crystalwars.command.BaseCommand;
import cn.lanink.crystalwars.command.user.sub.JoinRoom;
import cn.lanink.crystalwars.command.user.sub.QuitRoom;
import cn.lanink.crystalwars.utils.FormHelper;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

/**
 * 玩家命令
 *
 * @author lt_name
 */
public class UserCommand extends BaseCommand {

    public UserCommand(String name, String[] aliases) {
        super(name, "CrystalWars 命令");
        this.setAliases(aliases);
        this.setPermission("crystalwars.command.user");

        this.addSubCommand(new JoinRoom("join"));
        this.addSubCommand(new QuitRoom("quit"));
    }

    @Override
    public void sendHelp(CommandSender sender) {
        Language language = this.crystalWars.getLang(sender);
        sender.sendMessage(
                "§a/" + this.getName() + " "+language.translateString("tips_help_openGui")+"\n" +
                "§a/" + this.getName() + " "+language.translateString("tips_helps_joinRoom")+"\n" +
                "§a/" + this.getName() + " "+language.translateString("tips_helps_quitRoom")+"\n"
        );
    }

    @Override
    public void sendGUI(Player player) {
        FormHelper.sendUserMainMenu(player);
    }

}
