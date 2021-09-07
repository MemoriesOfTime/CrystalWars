package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.gamecore.api.Info;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
@Info("将会移植到 GameCore 中")
public class AdvancedClickItem extends Item {

    private BiConsumer<InventoryClickEvent, Player> clickConsumer;

    public AdvancedClickItem(int id) {
        super(id);
    }

    public AdvancedClickItem(int id, Integer meta) {
        super(id, meta);
    }

    public AdvancedClickItem(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    public AdvancedClickItem(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public AdvancedClickItem onClick(@NotNull BiConsumer<InventoryClickEvent, @NotNull Player> consumer) {
        this.clickConsumer = consumer;
        return this;
    }

    public void callClick(InventoryClickEvent clickEvent, Player player) {
        if(this.clickConsumer != null) {
            this.clickConsumer.accept(clickEvent, player);
        }
    }

}
