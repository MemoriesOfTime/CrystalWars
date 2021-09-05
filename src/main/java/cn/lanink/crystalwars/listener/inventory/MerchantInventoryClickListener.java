package cn.lanink.crystalwars.listener.inventory;

import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iGxnon
 * @date 2021/9/4
 */
public class MerchantInventoryClickListener implements InventoryClickListener<CrystalWarsEntityMerchant.MerchantInventory>{

    private final ConcurrentHashMap<Long, CrystalWarsEntityMerchant.MerchantInventory> listenerInventories = new ConcurrentHashMap<>();

    @Getter
    private final CrystalWarsEntityMerchant merchant;

    @Override
    public void addToListen(long runtimeId, CrystalWarsEntityMerchant.MerchantInventory inventory) {
        listenerInventories.put(runtimeId, inventory);
    }

    @Override
    public Map<Long, CrystalWarsEntityMerchant.MerchantInventory> getListenerInventories() {
        return listenerInventories;
    }

    @Override
    public void clearListener() {
        listenerInventories.clear();
    }

    public MerchantInventoryClickListener(CrystalWarsEntityMerchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public void removeFromListen(long runtimeId) {
        listenerInventories.remove(runtimeId);
    }

    @Override
    public void removeFromListen(CrystalWarsEntityMerchant.MerchantInventory inventory) {
        removeFromListen(inventory.getRid());
    }

    @Override
    public void addToListen(CrystalWarsEntityMerchant.MerchantInventory inventory) {
        addToListen(inventory.getRid(), inventory);
    }

    @Override
    public CrystalWarsEntityMerchant.MerchantInventory getListenerInventory(long rid) {
        return listenerInventories.get(rid);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if(event.getInventory() instanceof CrystalWarsEntityMerchant.MerchantInventory) {
            CrystalWarsEntityMerchant.MerchantInventory inventory = (CrystalWarsEntityMerchant.MerchantInventory) event.getInventory();
            Player player = event.getPlayer();
            CrystalWarsEntityMerchant merchant = getMerchant();
            if(getListenerInventories().containsKey(inventory.getRid())) {
                event.setCancelled(true);
                Item selectSupply = event.getSourceItem();
                if(!inventory.contains(selectSupply)) {
                    return;
                }

            }
        }
    }

}
