package cn.lanink.crystalwars.command.admin.sub;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.lanink.crystalwars.items.generation.ItemGenerationConfigManager;
import cn.lanink.crystalwars.supplier.SupplyConfigManager;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

/**
 * @author LT_Name
 */
public class Reload extends BaseSubCommand {

    public Reload(String name) {
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
        this.crystalWars.unloadAllArena();
        this.crystalWars.unloadAllListener();

        SupplyConfigManager.loadAllSupplyConfig();
        ItemGenerationConfigManager.loadAllItemGeneration();

        this.crystalWars.loadAllListener();
        this.crystalWars.loadAllArena();
        sender.sendMessage(CrystalWars.getInstance().getLang().translateString("tips_reloadRoom_success"));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
