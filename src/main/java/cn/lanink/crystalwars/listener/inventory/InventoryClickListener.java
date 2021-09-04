package cn.lanink.crystalwars.listener.inventory;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Listener;
import cn.nukkit.inventory.Inventory;

import java.util.Map;

/**
 * @author iGxnon
 * @date 2021/9/4
 */
public interface InventoryClickListener<T extends Inventory> extends Listener {

    void addToListen(long runtimeId, T inventory);

    Map<Long, T> getListenerInventories();

    void clearListener();

    void removeFromListen(long runtimeId);


    default void removeFromListen(T inventory) {
        if(inventory.getHolder() instanceof Entity) {
            removeFromListen(((Entity) inventory.getHolder()).getId());
        }
    }

    default void addToListen(T inventory) {
        if(inventory.getHolder() instanceof Entity) {
            addToListen(((Entity) inventory.getHolder()).getId(), inventory);
        }
    }

    T getListenerInventory(long runtimeId);

}
