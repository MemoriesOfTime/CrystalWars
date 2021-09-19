package cn.lanink.crystalwars.supplier.config;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.supplier.config.items.SupplyItemConfig;
import cn.lanink.crystalwars.supplier.config.pages.SupplyPageConfig;
import cn.nukkit.utils.Config;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author iGxnon
 * @date 2021/9/3
 */
@Getter
@ToString
public class SupplyConfig {

    private final String dirName;

    private final ImmutableMap<String, SupplyPageConfig> pageConfigMap;

    private final ImmutableMap<String, SupplyItemConfig> itemConfigMap;

    private SupplyPageConfig defaultPageConfig;

    private static final CrystalWars CRYSTAL_WARS = CrystalWars.getInstance();

    public SupplyConfig(@NotNull String dirName, File path) {
        this.dirName = dirName;
        File[] childDir = path.listFiles();
        if(childDir == null) {
            throw new RuntimeException("加载" + dirName + "失败!");
        }

        childDir = Arrays.stream(childDir).filter(File::isDirectory).toArray(File[]::new);

        if(childDir.length != 2 ||
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

        ImmutableMap.Builder<String, SupplyItemConfig> itemConfigMapBuilder = ImmutableMap.builder();
        Arrays.stream(itemsFiles)
                .filter(this::checkItemFileCorrect)
                .forEach(itemFile -> {
                    String fileName = itemFile.getName().split("\\.")[0];
                    itemConfigMapBuilder.put(fileName, new SupplyItemConfig(fileName, itemFile));
                });
        itemConfigMap = itemConfigMapBuilder.build();

        ImmutableMap.Builder<String, SupplyPageConfig> supplyPageConfigBuilder = ImmutableMap.builder();
        Arrays.stream(pagesFiles)
                .filter(this::checkPageFileCorrect)
                .forEach(pageFile -> {
                    String fileName = pageFile.getName().split("\\.")[0];
                    SupplyPageConfig pageConfig = new SupplyPageConfig(fileName, pageFile, this);
                    supplyPageConfigBuilder.put(fileName, pageConfig);
                    Config config = new Config(pageFile, Config.YAML);
                    if(config.getBoolean("default", false)) {
                        this.defaultPageConfig = pageConfig;
                    }
                });
        pageConfigMap = supplyPageConfigBuilder.build();
        if(this.defaultPageConfig == null) {
            throw new RuntimeException("商店供给:" + dirName + " 无默认界面!");
        }
    }

    private boolean checkItemFileCorrect(File file) {
        if(!file.getName().endsWith(".yml")) {
            return false;
        }

        Config config = new Config(file, Config.YAML);
        List<String> authorizedKey = Arrays.asList("title", "subTitle", "item", "pos", "cost", "count", "lore");

        for (String necessary : authorizedKey) {
            if (!config.exists(necessary)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPageFileCorrect(File file) {
        if(!file.getName().endsWith(".yml")) {
            return false;
        }

        Config config = new Config(file, Config.YAML);
        List<String> authorizedKey = Arrays.asList("title", "linkItems", "items", "default");

        // 可以不包含 LinkItem
        if(config.getAll().keySet().size() != authorizedKey.size()) {
            if(config.getAll().containsKey("linkItem") || config.getAll().containsKey("default")) {
                return false;
            }
        }

        for (Map.Entry<String, Object> entry : config.getAll().entrySet()){
            if(!authorizedKey.contains(entry.getKey())) {
                return false;
            }
        }
        return true;
    }
}
