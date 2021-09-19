package cn.lanink.crystalwars.player;

import cn.nukkit.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author LT_Name
 */
public class PlayerSettingDataManager {

    private static final HashMap<String, PlayerSettingData> PLAYER_SETTING_DATA_MAP = new HashMap<>();

    private PlayerSettingDataManager() {
        throw new RuntimeException("哎呀！你不能实例化这个类！");
    }

    public static void load() {
        //TODO
    }

    public static void save() {
        //TODO
    }

    public static PlayerSettingData getData(@NotNull Player player) {
        return getData(player.getName());
    }

    public static PlayerSettingData getData(@NotNull String player) {
        if (!PLAYER_SETTING_DATA_MAP.containsKey(player)) {
            PLAYER_SETTING_DATA_MAP.put(player, new PlayerSettingData(player));
        }
        return PLAYER_SETTING_DATA_MAP.get(player);
    }

}
