package cn.lanink.crystalwars.items;

/**
 * @author LT_Name
 */
public interface ItemInternalId {

    //tag key
    String IS_CRYSTALWARS_TAG = "isCrystalWars"; //boolean
    String INTERNAL_ID_TAG = "CrystalWarsStringInternalID"; //String
    String INTERNAL_ID_TAG_OLD = "CrystalWarsInternalID"; //int
    String PROPERTY_CANNOTTAKEITOFF_TAG = "CrystalWarsCannotTakeItOff"; //boolean
    String PROPERTY_CANNOTCLICKONINVENTORY_TAG = "CrystalWarsCannotClickOnInventory"; //boolean

    //内部id
    String INTERNAL_ID_PREFIX = "CrystalWars:";
    String ITEM_INTERNALID_PLATFORM = INTERNAL_ID_PREFIX + "Platform";
    String ITEM_INTERNALID_BRIDGEEGG = INTERNAL_ID_PREFIX + "BridgeEgg";
    
}
