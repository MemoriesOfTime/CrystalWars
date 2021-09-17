package cn.lanink.crystalwars.items;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author LT_Name
 */
public class ItemManager {

    public static final String IS_CRYSTALWARS_TAG = "isCrystalWars";
    public static final String INTERNAL_ID_TAG = "CrystalWarsInternalID";

    public static Item get(int internalID) {
        return get(null, internalID);
    }

    public static Item get(Player player, int internalID) {
        return get(player, internalID, 1);
    }

    public static Item get(Player player, int internalID, int count) {
        Item item;
        switch (internalID) {
            case 10000:
                item = Item.get(324, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                item.setCustomName("退出游戏房间");
                return item;
            default:
                return Item.get(0);
        }
    }

}
