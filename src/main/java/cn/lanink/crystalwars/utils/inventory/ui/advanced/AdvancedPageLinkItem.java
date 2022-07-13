package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.supplier.pages.SupplyPageConfig;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
public class AdvancedPageLinkItem extends AdvancedClickItem {

    private final SupplyPageConfig pageConfig;

    public AdvancedPageLinkItem(@NotNull Item item, @NotNull SupplyPageConfig nextPageConfig) {
        super(item.getId(), item.getDamage(), item.getCount(), item.getName());
        this.setCustomName(item.getCustomName());
        this.pageConfig = nextPageConfig;
    }

    @Override
    public void callClick(InventoryClickEvent clickEvent, Player player) {
        if(!(clickEvent.getInventory().getHolder() instanceof CrystalWarsEntityMerchant)) {
            return;
        }
        if (!(clickEvent.getInventory() instanceof AdvancedInventory)) {
            return;
        }

        AdvancedInventory newWindow = this.pageConfig.generateWindow((AdvancedInventory) clickEvent.getInventory());
        // 使用 afterClick 后所有的页面 linkItems 需要保持一致
        if(pageConfig.getLinkItems() != null) {
            if(pageConfig.getLinkItems().get(clickEvent.getSlot()).getAfterClick() != null) {
                Item afterClick = pageConfig.getLinkItems().get(clickEvent.getSlot()).getAfterClick().setCustomName(this.getCustomName());
                newWindow.setItem(clickEvent.getSlot(), afterClick);
            }
        }
    }
}
