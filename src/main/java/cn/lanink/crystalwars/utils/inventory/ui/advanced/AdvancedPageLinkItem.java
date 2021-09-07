package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.supplier.config.pages.SupplyPageConfig;
import cn.lanink.crystalwars.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

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

        Optional<Inventory> topWindow = player.getTopWindow();
        if (topWindow.isPresent() && topWindow.get() instanceof AdvancedInventory) {
            try {
                Method removeWindow = player.getClass().getDeclaredMethod("removeWindow", Inventory.class, boolean.class);
                removeWindow.setAccessible(true);
                removeWindow.invoke(player, topWindow.get(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AdvancedInventory newWindow = this.pageConfig.generateWindow((CrystalWarsEntityMerchant) clickEvent.getInventory().getHolder());
        if(pageConfig.getLinkItems() != null) {
            if(pageConfig.getLinkItems().get(clickEvent.getSlot()).getAfterClick() != null) {
                // TODO Fix bug
                Item afterClick = pageConfig.getLinkItems().get(clickEvent.getSlot()).getAfterClick().setCustomName(clickEvent.getInventory().getName());
                if(afterClick != null) {
                    newWindow.setItem(clickEvent.getSlot(), afterClick);
                }
            }
        }
        // 延迟一下
        Server.getInstance().getScheduler().scheduleDelayedTask(CrystalWars.getInstance(), new Task() {
            @Override
            public void onRun(int i) {
                int id = player.getWindowId(newWindow);
                if (id == -1) {
                    player.addWindow(newWindow);
                }else {
                    Inventory inventory = player.getWindowById(id);
                    inventory.open(player);
                }
            }
        }, 5);

    }
}
