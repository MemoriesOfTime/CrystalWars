package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
@Getter
public class AdvancedClickItem extends Item {

    private BiConsumer<Integer, Player> clickConsumer;

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

    public AdvancedClickItem onClick(@NotNull BiConsumer<Integer, @NotNull Player> consumer) {
        clickConsumer = consumer;
        return this;
    }

    public void callClick(int slotPos, Player player) {
        if(this.clickConsumer != null) {
            this.clickConsumer.accept(slotPos, player);
        }
    }

}
