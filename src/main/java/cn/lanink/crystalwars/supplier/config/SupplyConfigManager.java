package cn.lanink.crystalwars.supplier.config;

import cn.lanink.crystalwars.CrystalWars;
import cn.nukkit.utils.Config;
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
            CRYSTAL_WARS.saveResource("Supply/BaseSupply.yml");
            CRYSTAL_WARS.saveResource("Supply/Items/goldapple.yml");
            CRYSTAL_WARS.saveResource("Supply/Items/wool.yml");
            // TODO save more
        }
        if(dir.listFiles() == null || Objects.requireNonNull(dir.listFiles()).length <= 1) {
            return;
        }
        AtomicInteger count = new AtomicInteger();
        Stream.of(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isFile)
                .forEach(file -> {
                    Config config = new Config(file, Config.YAML);
                    String name = file.getName().split("\\.")[0];
                    SupplyConfig supplyConfig = new SupplyConfig(name, config);
                    SUPPLY_CONFIG_MAP.put(name, supplyConfig);
                    count.incrementAndGet();
                });
        CRYSTAL_WARS.getLogger().info("已成功加载" + count + "个商店配置");
        if(CrystalWars.debug) {
            CRYSTAL_WARS.getLogger().warning(SUPPLY_CONFIG_MAP.toString());
        }
    }


}
