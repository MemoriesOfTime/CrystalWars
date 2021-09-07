package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.supplier.config.pages.SupplyPageConfig;
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
        super(item.getId(), item.getDamage(), item.getCount());
        this.pageConfig = nextPageConfig;
    }

    @Override
    public void callClick(InventoryClickEvent clickEvent, Player player) {
        player.removeAllWindows();
        if(!(clickEvent.getInventory().getHolder() instanceof CrystalWarsEntityMerchant)) {
            return;
        }
        AdvancedInventory newWindow = this.pageConfig.generateWindow((CrystalWarsEntityMerchant) clickEvent.getInventory().getHolder());
        if(pageConfig.getLinkItems() != null) {
            Item afterClick = pageConfig.getLinkItems().get(clickEvent.getSlot()).getAfterClick();
            if(afterClick != null) {
                newWindow.setItem(clickEvent.getSlot(), afterClick);
            }
        }
        player.addWindow(newWindow);
    }
}
