package cn.lanink.crystalwars.command.user;

import cn.lanink.crystalwars.command.BaseCommand;
import cn.lanink.crystalwars.command.user.sub.JoinRoom;
import cn.lanink.crystalwars.command.user.sub.QuitRoom;
import cn.lanink.crystalwars.utils.FormHelper;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

/**
 * 玩家命令
 *
 * @author lt_name
 */
public class UserCommand extends BaseCommand {

    public UserCommand(String name, String[] aliases) {
        super(name, "CrystalWars command");
        this.setAliases(aliases);
        this.setPermission("crystalwars.command.user");

        this.addSubCommand(new JoinRoom("join"));
        this.addSubCommand(new QuitRoom("quit"));
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(
                "§a/" + this.getName() + " §e打开GUI\n" +
                "§a/" + this.getName() + " join <游戏房间名称> §e加入游戏房间\n" +
                "§a/" + this.getName() + " quit §e退出游戏房间\n"
        );
    }

    @Override
    public void sendGUI(Player player) {
        FormHelper.sendUserMainMenu(player);
    }

}
