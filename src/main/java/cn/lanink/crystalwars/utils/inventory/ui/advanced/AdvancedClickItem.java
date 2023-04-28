package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
// TODO 分离该类和 Item 类的继承关系
public class AdvancedClickItem extends Item {

    @Getter
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

    public AdvancedClickItem onClick(@NotNull BiConsumer<InventoryClickEvent, Player> consumer) {
        this.clickConsumer = consumer;
        return this;
    }

    public void callClick(InventoryClickEvent clickEvent, Player player) {
        if(this.clickConsumer != null) {
            this.clickConsumer.accept(clickEvent, player);
        }
    }

    @Override
    public AdvancedClickItem setCustomName(String name) {
        return (AdvancedClickItem) super.setCustomName(name);
    }

    @Override
    public AdvancedClickItem clone() {
        return (AdvancedClickItem) super.clone();
    }
}
