package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.supplier.config.pages.SupplyPageConfig;
import cn.nukkit.Player;
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
    public void callClick(int slotPos, Player player) {
        player.removeAllWindows();
        AdvancedInventory newWindow = this.pageConfig.generateWindow();
        if(pageConfig.getLinkItems() != null) {
            Item afterClick = pageConfig.getLinkItems().get(slotPos).getAfterClick();
            if(afterClick != null) {
                newWindow.setItem(slotPos, afterClick);
            }
        }
        player.addWindow(newWindow);
    }
}
