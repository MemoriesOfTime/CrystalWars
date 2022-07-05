package cn.lanink.crystalwars.theme;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.gamecore.utils.Language;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author LT_Name
 */
public class ThemeManager {

    private static final CrystalWars CRYSTAL_WARS = CrystalWars.getInstance();

    @Getter
    private static final HashMap<String, Theme> THEME_MAP = new HashMap<>();

    public static void load() {
        CRYSTAL_WARS.saveResource("Theme/变量介绍.txt", true);
        CRYSTAL_WARS.saveResource("Theme/DefaultTheme.yml");
        CRYSTAL_WARS.saveResource("Theme/DefaultThemeEnglish.yml");
        Language language = CrystalWars.getInstance().getLang();
        File[] files = new File(CRYSTAL_WARS.getThemePath()).listFiles();
        if(files == null || files.length == 0) {
            return;
        }
        AtomicInteger count = new AtomicInteger();
        Stream.of(Objects.requireNonNull(files))
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".yml"))
                .forEach(themeFile -> {
                    String name = themeFile.getName().split("\\.")[0];
                    THEME_MAP.put(name, new Theme(name, themeFile));
                    count.incrementAndGet();
                });
        CRYSTAL_WARS.getLogger().info(language.translateString("themeManager_loadTemplate", count));
        if(CrystalWars.debug) {
            CRYSTAL_WARS.getLogger().info(language.translateString("themeManager_loadTemplate_debug", THEME_MAP));
        }
    }

    public static Theme getTheme(@NotNull String name) {
        return THEME_MAP.get(name);
    }

}
