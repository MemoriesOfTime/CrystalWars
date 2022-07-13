package cn.lanink.crystalwars.items.generation;

import cn.lanink.crystalwars.CrystalWars;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

/**
 * 物品生成配置(游戏内资源点) 管理器
 *
 * @author LT_Name
 */
public class ItemGenerationConfigManager {

    private static final CrystalWars CRYSTAL_WARS = CrystalWars.getInstance();
    @Getter
    private static final HashMap<String, ItemGenerationConfig> ITEM_GENERATION_CONFIG_MAP = new HashMap<>();

    private ItemGenerationConfigManager() {
        throw new RuntimeException("哎呀！你不能实例化这个类！");
    }

    /**
     * 加载所有物品生成配置
     */
    public static void loadAllItemGeneration() {
        File dataFolder = new File(CRYSTAL_WARS.getDataFolder(), "/ItemGeneration/");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
            CRYSTAL_WARS.saveResource("ItemGeneration/Diamond.yml");
            CRYSTAL_WARS.saveResource("ItemGeneration/GoldIngot.yml");
            CRYSTAL_WARS.saveResource("ItemGeneration/IronIngot.yml");
        }
        File[] files = dataFolder.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        int count = 0;
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            try {
                String name = file.getName().split("\\.")[0];
                ITEM_GENERATION_CONFIG_MAP.put(name, new ItemGenerationConfig(name, new Config(file, Config.YAML)));
                count++;
            } catch (Exception e) {
                CRYSTAL_WARS.getLogger().error("加载物品生成配置失败，请检查配置文件！", e);
            }
        }
        CRYSTAL_WARS.getLogger().info(CRYSTAL_WARS.getLang().translateString("loadItem_generationConfigOnload", count));
    }

    public static ItemGenerationConfig getItemGenerationConfig(@NotNull String name) {
        return ITEM_GENERATION_CONFIG_MAP.get(name);
    }

}
