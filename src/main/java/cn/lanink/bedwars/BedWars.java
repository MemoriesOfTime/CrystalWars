package cn.lanink.bedwars;

import cn.lanink.bedwars.scoreboard.base.IScoreboard;
import cn.lanink.bedwars.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class BedWars extends PluginBase {

    public static final String VERSION = "?";
    public static boolean debug = false;
    public static final Random RANDOM = new Random();

    private static BedWars bedWars;

    private Config config;

    //Language
    private String defaultLanguage = "zh_CN";
    private final Map<String, String> languageMappingTable = new HashMap<>();
    private final Map<String, Language> languageMap = new HashMap<>();
    private final Map<Player, String> playerLanguage = new ConcurrentHashMap<>();

    private IScoreboard scoreboard;

    public static BedWars getInstance() {
        return bedWars;
    }

    @Override
    public void onLoad() {
        bedWars = this;

        this.saveDefaultConfig();
        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);

        if (this.config.getBoolean("debug", false)) {
            debug = true;
            this.getLogger().warning("§c 警告：您开启了debug模式！");
            this.getLogger().warning("§c Warning: You have turned on debug mode!");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {

            }
        }

        this.saveResource("Language/zh_CN.yml", "Language/cache/new_zh_CN.yml", true);
        List<String> languages = Arrays.asList("zh_CN", "en_US");
        for (String language : languages) {
            this.saveResource("Language/" + language + ".yml");
        }
        this.defaultLanguage = this.config.getString("defaultLanguage", "zh_CN");
        this.languageMappingTable.putAll(this.config.get("languageMappingTable", new HashMap<>()));
        File[] files = new File(getDataFolder() + "/Language").listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }
                String name = file.getName().split("\\.")[0];
                Language language = new Language(new Config(file, Config.YAML));
                if (this.config.getBoolean("autoUpdateLanguage")) {
                    //更新插件自带的语言文件
                    if (languages.contains(name)) {
                        this.saveResource("Language/" + name + ".yml",
                                "Language/cache/new.yml", true);
                        language.update(new Config(this.getDataFolder() + "/Language/cache/new.yml", Config.YAML));
                    }
                    //以zh_CN为基础 更新所有语言文件
                    language.update(new Config(this.getDataFolder() + "/Language/cache/new_zh_CN.yml", Config.YAML));
                }
                this.languageMap.put(name, language);
                this.getLogger().info("§aLanguage: " + name + " loaded !");
            }
        }
        if (this.languageMap.isEmpty()) {
            this.getLogger().error("§cFailed to load language file! The plugin does not work");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!this.languageMap.containsKey(this.defaultLanguage)) {
            this.getLogger().error("§cNo default language found: " + this.defaultLanguage + " Has been set to 'zh_CN'");
            this.defaultLanguage = "zh_CN";
        }

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    public Map<Player, String> getPlayerLanguage() {
        return this.playerLanguage;
    }

    public Language getLanguage() {
        return this.getLanguage(null);
    }

    public Language getLanguage(Object obj) {
        if (obj instanceof Player) {
            Player player = (Player) obj;
            String lang = this.playerLanguage.getOrDefault(player, this.defaultLanguage);
            if (!this.languageMap.containsKey(lang) && this.languageMappingTable.containsKey(lang)) {
                lang = this.languageMappingTable.get(lang);
            }
            if (!this.languageMap.containsKey(lang)) {
                lang = this.defaultLanguage;
            }
            return this.languageMap.get(lang);
        }
        return this.languageMap.get(this.defaultLanguage);
    }


}
