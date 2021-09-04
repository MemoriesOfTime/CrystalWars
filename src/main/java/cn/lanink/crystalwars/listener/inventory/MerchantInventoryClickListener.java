package cn.lanink.crystalwars.listener.inventory;

import cn.lanink.crystalwars.entity.CrystalWarsEntityBaseMerchant;
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
public class MerchantInventoryClickListener implements InventoryClickListener<CrystalWarsEntityBaseMerchant.MerchantInventory>{

    private final ConcurrentHashMap<Long, CrystalWarsEntityBaseMerchant.MerchantInventory> listenerInventories = new ConcurrentHashMap<>();

    @Getter
    private final CrystalWarsEntityBaseMerchant merchant;

    @Override
    public void addToListen(long runtimeId, CrystalWarsEntityBaseMerchant.MerchantInventory inventory) {
        listenerInventories.put(runtimeId, inventory);
    }

    @Override
    public Map<Long, CrystalWarsEntityBaseMerchant.MerchantInventory> getListenerInventories() {
        return listenerInventories;
    }

    @Override
    public void clearListener() {
        listenerInventories.clear();
    }

    public MerchantInventoryClickListener(CrystalWarsEntityBaseMerchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public void removeFromListen(long runtimeId) {
        listenerInventories.remove(runtimeId);
    }

    @Override
    public void removeFromListen(CrystalWarsEntityBaseMerchant.MerchantInventory inventory) {
        removeFromListen(inventory.getRid());
    }

    @Override
    public void addToListen(CrystalWarsEntityBaseMerchant.MerchantInventory inventory) {
        addToListen(inventory.getRid(), inventory);
    }

    @Override
    public CrystalWarsEntityBaseMerchant.MerchantInventory getListenerInventory(long rid) {
        return listenerInventories.get(rid);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if(event.getInventory() instanceof CrystalWarsEntityBaseMerchant.MerchantInventory) {
            CrystalWarsEntityBaseMerchant.MerchantInventory inventory = (CrystalWarsEntityBaseMerchant.MerchantInventory) event.getInventory();
            Player player = event.getPlayer();
            CrystalWarsEntityBaseMerchant merchant = getMerchant();
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
