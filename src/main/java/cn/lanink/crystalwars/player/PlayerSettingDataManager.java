package cn.lanink.crystalwars.player;

import cn.lanink.crystalwars.CrystalWars;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
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
        File[] files = new File(CrystalWars.getInstance().getPlayerSettingsPath()).listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        files = Arrays.stream(files).filter(File::isDirectory).toArray(File[]::new);
        for (File file : files) {
            File[] files1 = file.listFiles();
            if (files1 == null || files1.length == 0) {
                continue;
            }
            Arrays.stream(files1)
                    .filter(File::isFile)
                    .filter(f -> f.getName().endsWith(".yml"))
                    .forEach(f -> {
                        String name = f.getName().split("\\.")[0];
                        PLAYER_SETTING_DATA_MAP.put(
                                name,
                                new PlayerSettingData(new Config(f, Config.YAML), name)
                        );
                    });
        }
    }

    public static void save() {
        for (PlayerSettingData data : PLAYER_SETTING_DATA_MAP.values()) {
            data.save();
        }
    }

    public static PlayerSettingData getData(@NotNull Player player) {
        return getData(player.getName());
    }

    public static PlayerSettingData getData(@NotNull String player) {
        if (!PLAYER_SETTING_DATA_MAP.containsKey(player)) {
            String fileString = CrystalWars.getInstance().getPlayerSettingsPath() +
                    player.substring(0,1).toLowerCase() + "/" + player + ".yml";
            Config config = new Config(fileString, Config.YAML);
            PlayerSettingData value = new PlayerSettingData(config, player);
            PLAYER_SETTING_DATA_MAP.put(player, value);
        }
        return PLAYER_SETTING_DATA_MAP.get(player);
    }

}
