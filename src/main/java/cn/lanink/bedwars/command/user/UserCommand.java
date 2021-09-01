package cn.lanink.bedwars.command.user;

import cn.lanink.bedwars.command.BaseCommand;
import cn.lanink.bedwars.command.user.sub.JoinRoom;
import cn.nukkit.command.CommandSender;

/**
 * 玩家命令
 *
 * @author lt_name
 */
public class UserCommand extends BaseCommand {

    public UserCommand(String name) {
        super(name, "HuntGame 命令");
        this.setPermission("bedwars.command.user");

        this.addSubCommand(new JoinRoom("join"));
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
