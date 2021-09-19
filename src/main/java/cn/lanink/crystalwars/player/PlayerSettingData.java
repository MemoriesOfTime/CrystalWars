package cn.lanink.crystalwars.player;

import lombok.Data;

/**
 * 玩家个性化设置数据
 *
 * @author LT_Name
 */
@Data
public class PlayerSettingData {

    private final String name;
    private ShopType shopType = ShopType.AUTO;

    public enum ShopType {
        AUTO,
        CHEST,
        GUI
    }

}
