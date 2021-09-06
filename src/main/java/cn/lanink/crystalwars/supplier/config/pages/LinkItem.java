package cn.lanink.crystalwars.supplier.config.pages;

import cn.lanink.gamecore.api.Info;
import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
@Getter
@ToString
public class LinkItem {

    private final int slotPos;

    private final Item item;

    @Info("从 SupplyConfig 中的 pageConfigs获取 page数据")
    private final String pageFileName;

    private final Item afterClick;

    public LinkItem(@NotNull Item item, int slotPos, @NotNull String pageFileName) {
        this(item, slotPos, pageFileName, null);
    }

    public LinkItem(@NotNull Item item, int slotPos, @NotNull String pageFileName, @Nullable Item afterClick) {
        this.item = item;
        this.slotPos = slotPos;
        this.pageFileName = pageFileName;
        this.afterClick = afterClick;
    }

    public Item getItem() {
        return item.clone();
    }

    public Item getAfterClick() {
        if(afterClick != null) {
            return afterClick.clone();
        }
        return null;
    }
}
