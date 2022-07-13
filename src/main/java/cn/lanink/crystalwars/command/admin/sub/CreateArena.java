package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.lanink.crystalwars.utils.FormHelper;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;

/**
 * @author LT_Name
 */
public class CreateArena extends BaseSubCommand {

    public CreateArena(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isOp() && sender.isPlayer();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Language language = this.crystalWars.getLang(player);
        if (args.length < 2) {
            FormHelper.sendAdminCreateArena(player);
            return true;
        }
        if (!this.crystalWars.getArenaConfigs().containsKey(args[1])) {
            if (Server.getInstance().loadLevel(args[1])) {
                Level level = Server.getInstance().getLevelByName(args[1]);
                this.crystalWars.getOrCreateArenaConfig(level);
                sender.sendMessage(language.translateString("tips_createRoom_success", args[1]));
                if (player.getLevel() != level) {
                    player.teleport(level.getSpawnLocation());
                }
                Server.getInstance().dispatchCommand(sender, this.crystalWars.getCmdAdmin() + " SetArena " + args[1]);
            } else {
                sender.sendMessage(language.translateString("tips_createRoom_levelNotFound", args[1]));
            }
        } else {
            sender.sendMessage(language.translateString("tips_createRoom_roomExist", args[1]));
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{ CommandParameter.newType("worldName", CommandParamType.TEXT) };
    }
}
