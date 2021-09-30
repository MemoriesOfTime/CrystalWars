package cn.lanink.crystalwars.command.admin;

import cn.lanink.crystalwars.command.BaseCommand;
import cn.lanink.crystalwars.command.admin.sub.*;
import cn.lanink.crystalwars.utils.FormHelper;
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
        sender.sendMessage(
                "§a/" + this.getName() + " §e打开GUI\n" +
                        "§a/" + this.getName() + " CreateArena <地图名称> §e创建新的游戏房间\n" +
                        "§a/" + this.getName() + " SetArena <地图名称> §e设置游戏房间\n" +
                        "§a/" + this.getName() + " StartGame [地图名称] §e跳过等待倒计时，开始游戏\n" +
                        "§a/" + this.getName() + " UnloadArena <游戏房间名称> §e卸载指定游戏房间\n" +
                        "§a/" + this.getName() + " UnloadArena §e卸载所有游戏房间\n" +
                        "§a/" + this.getName() + " reload §e重载插件配置\n"

        );
    }

    @Override
    public void sendGUI(Player player) {
        FormHelper.sendAdminMainMenu(player);
    }
}
