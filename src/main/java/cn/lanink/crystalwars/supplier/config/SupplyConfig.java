package cn.lanink.crystalwars.supplier.config;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.supplier.config.items.SupplyItemConfig;
import cn.lanink.crystalwars.supplier.config.pages.SupplyPageConfig;
import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * @author iGxnon
 * @date 2021/9/3
 */
@Getter
@ToString
public class SupplyConfig {

    private final String dirName;

    private final List<SupplyPageConfig> pageConfigs = new ArrayList<>();

    private final Map<String, SupplyItemConfig> itemConfigMap = new HashMap<>();

    private static final CrystalWars CRYSTAL_WARS = CrystalWars.getInstance();

    public SupplyConfig(@NotNull String dirName, File path) {
        this.dirName = dirName;
        File[] childDir = path.listFiles();
        if(childDir == null ||
                childDir.length != 2 ||
                !Arrays.asList("items", "pages").contains(childDir[0].getName()) ||
                !Arrays.asList("items", "pages").contains(childDir[1].getName())) {
            throw new RuntimeException("加载" + dirName + "失败!");
        }
        File itemsPath = new File(path, "items");
        File[] itemsFiles = itemsPath.listFiles();
        File pagesPath = new File(path, "pages");
        File[] pagesFiles = pagesPath.listFiles();
        if(itemsFiles == null || pagesFiles == null) {
            throw new RuntimeException("加载" + dirName + "失败!");
        }

        Arrays.stream(itemsFiles)
                .filter(this::checkItemFileCorrect)
                .forEach(itemFile -> {
                    String fileName = itemFile.getName().split("\\.")[0];
                    itemConfigMap.put(fileName, new SupplyItemConfig(fileName, new Config(itemFile, Config.YAML)));
                });


    }

    private boolean checkItemFileCorrect(File file) {
        
        if(!file.getName().endsWith(".yml")) {
            return false;
        }

        Config config = new Config(file, Config.YAML);
        List<String> authorizedKey = Arrays.asList("title", "subTitle", "pos", "cost", "count", "lore");

        if(config.getAll().keySet().size() != authorizedKey.size()) {
            return false;
        }

        for(Map.Entry<String, Object> entry : config.getAll().entrySet()) {
            if(!authorizedKey.contains(entry.getKey())) {
                return false;
            }
        }
        return true;
    }
}
