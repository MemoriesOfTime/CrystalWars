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
            case 11001:
                item = Item.get(340, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                item.setCustomName("上一步");
                return item;
            case 11002:
                item = Item.get(340, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                item.setCustomName("下一步");
                return item;
            case 11003:
                item = Item.get(340, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                item.setCustomName("保存");
                return item;
            case 11004:
                item = Item.get(347, 0, count); //钟表
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                return item;
            case 11005:
                item = Item.get(138, 0, count); //信标
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                return item;
            case 11006:
                item = Item.get(35, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(ItemManager.IS_CRYSTALWARS_TAG, true)
                        .putInt(ItemManager.INTERNAL_ID_TAG, internalID));
                return item;
            case 11007:
                item = Item.get(138, 0, count); //信标
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                item.setCustomName("添加资源生成点");
                return item;
            case 11008:
                item = Item.get(241, 14, count); //红色玻璃
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG, internalID));
                item.setCustomName("删除资源生成点");
                return item;
            default:
                return Item.get(0);
        }
    }

}
