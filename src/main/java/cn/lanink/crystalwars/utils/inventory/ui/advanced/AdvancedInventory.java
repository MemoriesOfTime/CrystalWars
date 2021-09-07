package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.gamecore.GameCore;
import cn.lanink.gamecore.api.Info;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryEvent;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
@Info("将会移植到 GameCore 中")
public class AdvancedInventory extends ContainerInventory {

    private Consumer<Player> inventoryCloseConsumer;

    // slotPos , Player
    protected BiConsumer<InventoryClickEvent, Player> inventoryClickedConsumer;

    public AdvancedInventory(@NotNull CrystalWarsEntityMerchant merchant) {
        super(merchant, InventoryType.CHEST);
    }

    public AdvancedInventory(@NotNull CrystalWarsEntityMerchant merchant, @NotNull String overrideTitle) {
        super(merchant, InventoryType.CHEST, new HashMap<>(), InventoryType.CHEST.getDefaultSize(), overrideTitle);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items) {
        super(holder, type, items);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize) {
        super(holder, type, items, overrideSize);
    }

    public AdvancedInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize, String overrideTitle) {
        super(holder, type, items, overrideSize, overrideTitle);
    }

    @Override
    public boolean setItem(int slotPos, Item item) {
        return super.setItem(slotPos, item);
    }

    public boolean setItem(int slotPos, AdvancedClickItem item) {
        return super.setItem(slotPos, item);
    }

    public boolean setItem(int slotPos, @NotNull Item item, @NotNull BiConsumer<InventoryClickEvent, Player> clickConsumer) {
        return setItem(slotPos, new AdvancedClickItem(item.getId(), item.getDamage(), item.getCount()).onClick(clickConsumer));
    }

    public AdvancedInventory onClick(@NotNull BiConsumer<InventoryClickEvent, Player> listener) {
        this.inventoryClickedConsumer = listener;
        return this;
    }

    public AdvancedInventory onClose(@NotNull Consumer<Player> listener) {
        this.inventoryCloseConsumer = listener;
        return this;
    }

    private void callClick(InventoryClickEvent clickEvent, Player player) {
        if(this.inventoryClickedConsumer != null) {
            this.inventoryClickedConsumer.accept(clickEvent, player);
        }
    }

    private void callClose(@NotNull Player player) {
        if(this.inventoryCloseConsumer != null) {
            this.inventoryCloseConsumer.accept(player);
        }
    }

    public static void onEvent(@NotNull InventoryEvent event) {
        Inventory inventory = event.getInventory();
        if(!(inventory instanceof AdvancedInventory)) {
            return;
        }
        if(event instanceof InventoryClickEvent) {
            Item item = ((InventoryClickEvent) event).getSourceItem();
            Player player = ((InventoryClickEvent) event).getPlayer();
            if(item instanceof AdvancedClickItem) {
                ((AdvancedClickItem) item).callClick((InventoryClickEvent) event, player);
            }else {
                ((AdvancedInventory) inventory).callClick((InventoryClickEvent) event, player);
            }
        }else if(event instanceof InventoryCloseEvent) {
            ((AdvancedInventory) inventory).callClose(((InventoryCloseEvent) event).getPlayer());
        }
    }

    public String getJSONData() {
        return GameCore.GSON.toJson(this, ContainerInventory.class);
    }

}
