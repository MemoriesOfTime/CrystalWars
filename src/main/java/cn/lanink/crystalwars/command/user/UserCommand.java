package cn.lanink.crystalwars.command.user;

import cn.lanink.crystalwars.command.BaseCommand;
import cn.lanink.crystalwars.command.user.sub.JoinRoom;
import cn.lanink.crystalwars.command.user.sub.QuitRoom;
import cn.nukkit.command.CommandSender;

/**
 * 玩家命令
 *
 * @author lt_name
 */
public class UserCommand extends BaseCommand {

    public UserCommand(String name) {
        super(name, "HuntGame 命令");
        this.setPermission("crystalwars.command.user");

        this.addSubCommand(new JoinRoom("join"));
        this.addSubCommand(new QuitRoom("quit"));
    }

    @Override
    public void sendHelp(CommandSender sender) {
        //TODO
        sender.sendMessage("");
    }

    @Override
    public void sendGUI(CommandSender sender) {

    }
}
