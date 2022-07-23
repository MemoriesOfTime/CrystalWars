package cn.lanink.crystalwars.items;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author LT_Name
 */
public class ItemManager {

    public static final String IS_CRYSTALWARS_TAG = "isCrystalWars";
    public static final String INTERNAL_ID_TAG = "CrystalWarsStringInternalID"; //String
    public static final String INTERNAL_ID_TAG_OLD = "CrystalWarsInternalID"; //int
    public static final String PROPERTY_CANNOTTAKEITOFF_TAG = "CrystalWarsCannotTakeItOff"; //boolean
    public static final String PROPERTY_CANNOTCLICKONINVENTORY_TAG = "CrystalWarsCannotClickOnInventory"; //boolean

    public static Item get(int internalID) {
        return get(null, internalID);
    }

    public static Item get(String internalID) {
        return get(null, internalID);
    }

    public static Item get(Player player, int internalID) {
        return get(player, internalID, 1);
    }

    public static Item get(Player player, String internalID) {
        return get(player, internalID, 1);
    }

    public static Item get(Player player, String internalID, int count) {
        //TODO
        return Item.get(0);
    }

    public static Item get(Player player, int internalID, int count) {
        Item item;
        Language language = CrystalWars.getInstance().getLang(player);
        switch (internalID) {
            case 10100:
                item = Item.get(35, 0, 1);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                return item;
            case 10101:
                item = Item.get(35, 14, 1);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("item_name_select_team", Utils.getShowTeam(player, Team.RED)));
                return item;
            case 10102:
                item = Item.get(35, 4, 1);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("item_name_select_team", Utils.getShowTeam(player, Team.YELLOW)));
                return item;
            case 10103:
                item = Item.get(35, 11, 1);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("item_name_select_team", Utils.getShowTeam(player, Team.BLUE)));
                return item;
            case 10104:
                item = Item.get(35, 13, 1);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("item_name_select_team", Utils.getShowTeam(player, Team.GREEN)));
                return item;

            case 10000:
                item = Item.get(324, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("arenaSet_item_quitRoom"));
                return item;

            case 11001:
                item = Item.get(340, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("arenaSet_item_back"));
                return item;
            case 11002:
                item = Item.get(340, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("arenaSet_item_next"));
                return item;
            case 11003:
                item = Item.get(340, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("arenaSet_item_save"));
                return item;
            case 11004:
                item = Item.get(347, 0, count); //钟表
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                return item;
            case 11005:
                item = Item.get(138, 0, count); //信标
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                return item;
            case 11006:
                item = Item.get(35, 0, count);
                item.setNamedTag(new CompoundTag()
                        .putBoolean(ItemManager.IS_CRYSTALWARS_TAG, true)
                        .putInt(ItemManager.INTERNAL_ID_TAG_OLD, internalID));
                return item;
            case 11007:
                item = Item.get(138, 0, count); //信标
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("arenaSet_item_addResourcesSpawn"));
                return item;
            case 11008:
                item = Item.get(241, 14, count); //红色玻璃
                item.setNamedTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putInt(INTERNAL_ID_TAG_OLD, internalID));
                item.setCustomName(language.translateString("arenaSet_item_removeResourcesSpawn"));
                return item;
            default:
                return Item.get(0);
        }
    }

}
