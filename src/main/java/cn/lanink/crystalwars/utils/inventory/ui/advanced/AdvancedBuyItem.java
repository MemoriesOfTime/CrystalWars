package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.supplier.config.items.SupplyItemConfig;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
public class AdvancedBuyItem extends AdvancedClickItem{

    private final SupplyItemConfig itemConfig;

    public AdvancedBuyItem(@NotNull Item item, @NotNull SupplyItemConfig itemConfig) {
        super(item.getId(), item.getDamage(), item.getCount(), item.getName());
        this.setCustomName(item.getCustomName());
        this.itemConfig = itemConfig;
    }

    @Override
    public void callClick(InventoryClickEvent clickEvent, Player player) {
        clickEvent.setCancelled(true);
        BaseArena arena = CrystalWars.getInstance().getArenas().get(player.getLevel().getFolderName());
        Language language = CrystalWars.getInstance().getLang();
        if(arena == null) {
            player.sendMessage(language.translateString("tips_buyItem_notInRoom"));
            return;
        }
        if(!player.getInventory().canAddItem(this.itemConfig.getItem())) {
            player.sendTip(language.translateString("buyItem_inventoryFull"));
            return;
        }
        for (Item cost : this.itemConfig.getCost()) {
            if(!player.getInventory().contains(cost)) {
                player.sendTip(language.translateString("buyItem_lackOfNeededItems"));
                return;
            }
        }
        if (!this.itemConfig.isOvertimeCanBuy() && arena.isOvertime()) {
            player.sendTip(language.translateString("tips_buyItem_canNotBuyForOvertime"));
            return;
        }
        for (Item cost : this.itemConfig.getCost()) {
            player.getInventory().removeItem(cost);
        }
        Item item = this.itemConfig.getItem();
        if(this.itemConfig.isTeamChangeItem()) {
            if(arena == null) {
                player.sendMessage(language.translateString("buyItem_notInRoom"));
                return;
            }
            item = Utils.getTeamColorItem(item, arena.getPlayerData(player).getTeam());
        }
        player.getInventory().addItem(item);
        player.sendTip(language.translateString("buyItem_success"));
    }

}
