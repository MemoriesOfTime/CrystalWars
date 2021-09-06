package cn.lanink.crystalwars.utils.inventory.ui.listener;

import cn.lanink.crystalwars.utils.inventory.ui.advanced.AdvancedInventory;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;


/**
 * @author iGxnon
 * @date 2021/9/6
 */
public class InventoryListener implements Listener {

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onInventoryClick(InventoryClickEvent event) {
        AdvancedInventory.onEvent(event);
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onInventoryClose(InventoryCloseEvent event) {
        AdvancedInventory.onEvent(event);
    }

}
