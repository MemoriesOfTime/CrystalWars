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
public class ItemManager implements ItemInternalId {

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
        Item item;
        Language language = CrystalWars.getInstance().getLang(player);
        switch (internalID) {
            case ITEM_INTERNALID_PLATFORM:
                item = Item.get(Item.BLAZE_ROD, 0, count);
                item.setCompoundTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putString(INTERNAL_ID_TAG, internalID));
                item.setCustomName(language.translateString("item_platform_name"));
                return item;
            case ITEM_INTERNALID_BRIDGEEGG:
                item = Item.get(Item.EGG, 0, count);
                item.setCompoundTag(new CompoundTag()
                        .putBoolean(IS_CRYSTALWARS_TAG, true)
                        .putString(INTERNAL_ID_TAG, internalID));
                item.setCustomName(language.translateString("item_bridgeegg_name"));
                return item;
            //TODO

            default:
                return Item.get(0);
        }
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

    public static Item of(String string) {
        try {
            if (CrystalWars.debug) {
                CrystalWars.getInstance().getLogger().info("[debug] ItemManager#of( " + string + " )");
            }

            // id:damage x count
            String[] s1 = string.split("x");
            Item item;
            if (s1[0].startsWith(INTERNAL_ID_PREFIX)) {
                item = get(s1[0]);
            }else {
                item = Item.fromString(s1[0]);
            }

            if (item != null) {
                if (s1.length > 1) {
                    item.setCount(Utils.toInt(s1[1]));
                }
                if (CrystalWars.debug) {
                    CrystalWars.getInstance().getLogger().info("[debug] ItemManager#of( " + string + " )  out: " + item);
                }
                return item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CrystalWars.debug) {
            CrystalWars.getInstance().getLogger().info("[debug] ItemManager#of( " + string + " )  error out: air");
        }
        return Item.get(Item.AIR);
    }

}
