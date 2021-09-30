package cn.lanink.crystalwars.player;

import cn.nukkit.utils.Config;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * 玩家个性化设置数据
 *
 * @author LT_Name
 */
@Data
public class PlayerSettingData {

    private final Config config;

    private final String name;
    private ShopType shopType;

    public PlayerSettingData(@NotNull Config config, @NotNull String playerName) {
        this.config = config;

        this.name = playerName;
        try {
            this.shopType = ShopType.valueOf(this.config.getString("shopType"));
        } catch (Exception e) {
            this.shopType = ShopType.AUTO;
        }
    }

    public void save() {
        this.save(false);
    }

    public void save(boolean async) {
        this.config.set("shopType", this.shopType.name());

        this.config.save(async);
    }

    public enum ShopType {
        AUTO,
        CHEST,
        GUI
    }

}
