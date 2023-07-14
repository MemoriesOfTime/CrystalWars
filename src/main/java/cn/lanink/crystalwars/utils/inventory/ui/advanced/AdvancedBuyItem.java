package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.supplier.items.SupplyItemConfig;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
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
        Language language = CrystalWars.getInstance().getLang(player);
        if(arena == null) {
            player.sendMessage(language.translateString("tips_buyItem_notInRoom"));
            return;
        }
        if(!player.getInventory().canAddItem(this.itemConfig.getItem())) {
            Utils.playSound(player,Sound.MOB_ENDERMEN_PORTAL);
            player.sendTip(language.translateString("buyItem_inventoryFull"));
            return;
        }
        for (Item cost : this.itemConfig.getCost()) {
            if(!player.getInventory().contains(cost)) {
                Utils.playSound(player,Sound.MOB_ENDERMEN_PORTAL);
                player.sendTip(language.translateString("buyItem_lackOfNeededItems"));
                return;
            }
        }
        if (!this.itemConfig.isOvertimeCanBuy() && arena.isOvertime()) {
            Utils.playSound(player,Sound.MOB_ENDERMEN_PORTAL);
            player.sendTip(language.translateString("tips_buyItem_canNotBuyForOvertime"));
            return;
        }
        for (Item cost : this.itemConfig.getCost()) {
            player.getInventory().removeItem(cost);
        }
        Item item = this.itemConfig.getItem();
        if(this.itemConfig.isTeamChangeItem()) {
            item = Utils.getTeamColorItem(item, arena.getPlayerData(player).getTeam());
        }
        player.getInventory().addItem(item);
        Utils.playSound(player, Sound.RANDOM_ORB);
        player.sendTip(language.translateString("buyItem_success"));
    }

}
