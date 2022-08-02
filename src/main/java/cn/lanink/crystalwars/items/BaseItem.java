package cn.lanink.crystalwars.items;

import cn.nukkit.item.Item;
import lombok.Getter;

/**
 * @author LT_Name
 */
public class BaseItem {

    @Getter
    private final String internalID;

    public BaseItem(String internalID) {
        this.internalID = internalID;
    }

    public Item toItem() {
        return ItemManager.of(this.internalID);
    }

}
