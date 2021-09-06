package cn.lanink.crystalwars.supplier.config;

import cn.lanink.crystalwars.CrystalWars;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author iGxnon
 * @date 2021/9/4
 */
public class SupplyConfigManager {

    private static final CrystalWars CRYSTAL_WARS = CrystalWars.getInstance();

    @Getter
    private static final Map<String, SupplyConfig> SUPPLY_CONFIG_MAP = new HashMap<>();

    private SupplyConfigManager() {
        throw new RuntimeException("哎呀！你不能实例化这个类！");
    }

    public static void loadAllSupplyConfig() {
        File dir = new File(CRYSTAL_WARS.getDataFolder(), "/Supply/");
        if(!dir.exists()) {
            dir.mkdirs();
            SupplyConfigManager.saveDefaultSupply();
        }
        final File[] files = dir.listFiles();
        if(files == null || Objects.requireNonNull(files).length <= 1) {
            return;
        }
        AtomicInteger count = new AtomicInteger();

        Stream.of(Objects.requireNonNull(files))
                .filter(File::isDirectory)
                .forEach(supplyDir -> {
                    SupplyConfig supplyConfig = new SupplyConfig(supplyDir.getName(), supplyDir);
                    SUPPLY_CONFIG_MAP.put(supplyDir.getName(), supplyConfig);
                    count.incrementAndGet();
                });
        CRYSTAL_WARS.getLogger().info("已成功加载" + count + "个商店配置");
        if(CrystalWars.debug) {
            CRYSTAL_WARS.getLogger().warning(SUPPLY_CONFIG_MAP.toString());
        }
    }

    private static void saveDefaultSupply() {
        CRYSTAL_WARS.saveResource("Supply/DefaultSupply/items/goldenApple.yml");
        CRYSTAL_WARS.saveResource("Supply/DefaultSupply/pages/pageBlock.yml");
        CRYSTAL_WARS.saveResource("Supply/DefaultSupply/pages/pageDefault.yml");
        CRYSTAL_WARS.saveResource("Supply/DefaultSupply/pages/pageProp.yml");
    }


}