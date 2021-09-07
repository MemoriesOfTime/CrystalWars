package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.supplier.config.pages.SupplyPageConfig;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
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
        AdvancedInventory newWindow = this.pageConfig.generateWindow((CrystalWarsEntityMerchant) clickEvent.getInventory().getHolder());
        if(pageConfig.getLinkItems() != null) {
            if(pageConfig.getLinkItems().get(clickEvent.getSlot()).getAfterClick() != null) {
                Item afterClick = pageConfig.getLinkItems().get(clickEvent.getSlot()).getAfterClick().setCustomName(clickEvent.getInventory().getName());
                if(afterClick != null) {
                    newWindow.setItem(clickEvent.getSlot(), afterClick);
                }
            }
        }
        // 延迟一下
        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                player.addWindow(newWindow);
            }
        }, 5);

    }
}
