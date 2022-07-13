package cn.lanink.crystalwars;

import cn.lanink.crystalwars.arena.ArenaSet;
import cn.lanink.crystalwars.arena.ArenaTickTask;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.classic.ClassicArena;
import cn.lanink.crystalwars.command.admin.AdminCommand;
import cn.lanink.crystalwars.command.user.UserCommand;
import cn.lanink.crystalwars.items.generation.ItemGenerationConfigManager;
import cn.lanink.crystalwars.listener.defaults.ArenaSetListener;
import cn.lanink.crystalwars.listener.defaults.DefaultGameListener;
import cn.lanink.crystalwars.listener.defaults.PlayerJoinAndQuit;
import cn.lanink.crystalwars.player.PlayerSettingDataManager;
import cn.lanink.crystalwars.supplier.SupplyConfigManager;
import cn.lanink.crystalwars.theme.ThemeManager;
import cn.lanink.crystalwars.utils.MetricsLite;
import cn.lanink.crystalwars.utils.RsNpcVariable;
import cn.lanink.crystalwars.utils.Watchdog;
import cn.lanink.crystalwars.utils.inventory.ui.listener.InventoryListener;
import cn.lanink.gamecore.listener.BaseGameListener;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.google.common.base.Preconditions;
import com.smallaswater.npc.variable.VariableManage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LT_Name
 */
public class CrystalWars extends PluginBase {

    public static final String PLUGIN_NAME = "§1C§2r§3y§cs§5t§6a§al§cW§ba§1r§5s§r";
    public static final String VERSION = "?";
    public static boolean debug = false;
    public static final Random RANDOM = new Random();
    public static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            5,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            task -> new Thread(task, "CrystalWars Restore World Thread")
    );

    @Getter
    private boolean hasTips = false;

    private static CrystalWars crystalWars;

    private Config config;

    @Getter
    private final LinkedHashMap<Integer, Skin> skins = new LinkedHashMap<>();
    @Getter
    private static final LinkedHashMap<String, Class<? extends BaseArena>> ARENA_CLASS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Class<? extends BaseGameListener<BaseArena>>> LISTENER_CLASS = new LinkedHashMap<>();

    @Getter
    private final LinkedHashMap<String, BaseGameListener<BaseArena>> gameListeners = new LinkedHashMap<>();

    @Getter
    private final HashMap<String, Config> arenaConfigs = new HashMap<>();
    @Getter
    private final LinkedHashMap<String, BaseArena> arenas = new LinkedHashMap<>();

    @Getter
    private final HashMap<Player, ArenaSet> arenaSetMap = new HashMap<>();

    @Getter
    private String serverWorldPath;
    @Getter
    private String worldBackupPath;
    @Getter
    private String arenaConfigPath;
    @Getter
    private String playerSettingsPath;
    @Getter
    private String themePath;

    @Getter
    private String cmdUser;
    @Getter
    private String cmdAdmin;
    @Getter
    private List<String> cmdUserAliases;
    @Getter
    private List<String> cmdAdminAliases;

    @Getter
    private List<String> victoryCmd;
    @Getter
    private List<String> defeatCmd;

    private String defaultLanguage;
    private final HashMap<String, Language> languageMap = new HashMap<>();

    public static CrystalWars getInstance() {
        return crystalWars;
    }

    @Override
    public void onLoad() {
        Preconditions.checkState(crystalWars == null, "Already initialized!");
        crystalWars = this;

        this.serverWorldPath = this.getServer().getFilePath() + "/worlds/";
        this.worldBackupPath = this.getDataFolder() + "/LevelBackup/";
        this.arenaConfigPath = this.getDataFolder() + "/Arena/";
        this.playerSettingsPath = this.getDataFolder() + "/PlayerSettings/";
        this.themePath = this.getDataFolder() + "/Theme/";

        List<String> list = Arrays.asList("Arena", "LevelBackup", "PlayerSettings");
        for (String fileName : list) {
            File file = new File(this.getDataFolder(), fileName);
            if (!file.exists() && !file.mkdirs()) {
                getLogger().error(fileName + " 文件夹初始化失败");
            }
        }

        this.saveDefaultConfig();
        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);

        if (this.config.getBoolean("debug", false)) {
            debug = true;
            this.getLogger().warning("§c=========================================");
            this.getLogger().warning("§c 警告：您开启了debug模式！");
            this.getLogger().warning("§c Warning: You have turned on debug mode!");
            this.getLogger().warning("§c=========================================");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {

            }
        }

        this.loadLanguage();

        registerListener("DefaultGameListener", DefaultGameListener.class);

        registerArenaClass("classic", ClassicArena.class);
    }

    @Override
    public void onEnable() {
        //检查Tips
        try {
            Class.forName("tip.Main");
            this.hasTips = true;
        } catch (Exception ignored) {

        }
        this.loadSkins();
        ThemeManager.load();
        PlayerSettingDataManager.load();
        SupplyConfigManager.loadAllSupplyConfig();
        ItemGenerationConfigManager.loadAllItemGeneration();

        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(this), this);
        this.getServer().getPluginManager().registerEvents(new ArenaSetListener(this), this);
        this.loadAllListener();

        this.getServer().getScheduler().scheduleRepeatingTask(this, new ArenaTickTask(this), 1);
        this.getServer().getScheduler().scheduleRepeatingTask(this, new Watchdog(this, 10), 20, true);

        this.loadAllArena();
        this.cmdUser = this.config.getString("cmdUser", "CrystalWars");
        this.cmdUserAliases = this.config.getStringList("cmdUserAliases");
        this.cmdAdmin = this.config.getString("cmdAdmin", "CrystalWarsAdmin");
        this.cmdAdminAliases = this.config.getStringList("cmdAdminAliases");
        this.victoryCmd = this.config.getStringList("VictoryExecuteCommand");
        this.defeatCmd = this.config.getStringList("DefeatExecuteCommand");

        this.getServer().getCommandMap().register("CrystalWars".toLowerCase(),
                new UserCommand(this.cmdUser, this.cmdUserAliases.toArray(new String[0])));
        this.getServer().getCommandMap().register("CrystalWars".toLowerCase(),
                new AdminCommand(this.cmdAdmin, this.cmdAdminAliases.toArray(new String[0])));

        //注册RsNpcX变量
        try {
            Class.forName("com.smallaswater.npc.variable.BaseVariableV2");
            VariableManage.addVariableV2("CrystalWars", RsNpcVariable.class);
        }catch (Exception ignored) {

        }

        try {
            new MetricsLite(this, 12737);
        }catch (Exception ignored) {

        }

        this.getLogger().info(this.getLang().translateString("plugin_enable", VERSION));
    }

    @Override
    public void onDisable() {
        this.unloadAllArena();
        this.arenas.clear();
        this.arenaConfigs.clear();

        this.unloadAllListener();

        ArenaTickTask.clearAll();
        Watchdog.clearAll();

        EXECUTOR.shutdown();

        PlayerSettingDataManager.save();

        this.getLogger().info(this.getLang().translateString("plugin_disable"));
    }


    private void loadLanguage() {
        List<String> languages = Arrays.asList("zh_CN", "en_US");
        this.defaultLanguage = this.config.getString("pluginLanguage", "zh_CN");
        if (!languages.contains(this.defaultLanguage)) {
            this.getLogger().error("Language" + this.defaultLanguage + "Not supported, will load Chinese!");
            this.defaultLanguage = "zh_CN";
        }
        for (String language : languages) {
            Config languageConfig = new Config(Config.PROPERTIES);
            languageConfig.load(this.getResource("Resources/Language/" + language + ".properties"));
            this.languageMap.put(language, new Language(languageConfig));
        }

        this.getLogger().info(this.getLang().translateString("plugin_LanguageLoaded"));
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    public static void registerArenaClass(@NotNull String name, @NotNull Class<? extends BaseArena> arenaClass) {
        ARENA_CLASS.put(name, arenaClass);
    }

    public static void registerListener(@NotNull String name, @NotNull Class<? extends BaseGameListener<BaseArena>> listerClass) {
        LISTENER_CLASS.put(name, listerClass);
    }


    public void loadSkins() {
        File[] files = (new File(this.getDataFolder() + "/Skins")).listFiles();
        if (files != null && files.length > 0) {
            int x = 0;
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                String skinName = file.getName();
                File skinFile = new File(this.getDataFolder() + "/Skins/" + skinName + "/skin.png");
                if (skinFile.exists()) {
                    Skin skin = new Skin();
                    skin.setTrusted(true);
                    BufferedImage skinData = null;
                    try {
                        skinData = ImageIO.read(skinFile);
                    } catch (Exception ignored) {
                        this.getLogger().warning(this.getLang().translateString("loadSkin_wrongFormat",skinName));
                    }
                    if (skinData != null) {
                        skin.setSkinData(skinData);
                        skin.setSkinId(skinName);

                        this.skins.put(x, skin);
                        x++;
                    } else {
                        this.getLogger().warning(this.getLang().translateString("loadSkin_wrongFormat",skinName));
                    }
                } else {
                    this.getLogger().warning(this.getLang().translateString("loadSkin_skinNotFound",skinName));
                }
            }
        }
    }

    public void loadAllListener() {
        for (Map.Entry<String, Class<? extends BaseGameListener<BaseArena>>> entry : LISTENER_CLASS.entrySet()) {
            try {
                BaseGameListener<BaseArena> baseGameListener = entry.getValue().getConstructor().newInstance();
                baseGameListener.init(entry.getKey());
                this.getServer().getPluginManager().registerEvents(baseGameListener, this);
                this.gameListeners.put(entry.getKey(), baseGameListener);
                if (CrystalWars.debug) {
                    this.getLogger().info("[debug] registerListener: [ " + baseGameListener.getListenerName() + " ]");
                }
            } catch (Exception e) {
                this.getLogger().error(this.getLang().translateString("plugin_registerListener_error"), e);
            }
        }
    }

    public void unloadAllListener() {
        for (BaseGameListener<BaseArena> listener : this.gameListeners.values()) {
            HandlerList.unregisterAll(listener);
            if (CrystalWars.debug) {
                this.getLogger().info("[debug] UnregisterListener [ " + listener.getListenerName() + " ]");
            }
        }
        this.gameListeners.clear();
    }

    public void loadAllArena() {
        File[] files = new File(this.getDataFolder() + "/Arena").listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        files = Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".yml"))
                .toArray(File[]::new);
        for (File file : files) {
            this.loadArena(file.getName().split("\\.")[0]);
        }
    }

    public void loadArena(String world) {
        Config config = this.getOrCreateArenaConfig(world);
        if (!Server.getInstance().loadLevel(world)) {
            this.getLogger().error(this.getLang().translateString("plugin_loadArena_WorldNotExist", world));
            return;
        }
        String gameMode = config.getString("gameMode", "classic");
        if (!ARENA_CLASS.containsKey(gameMode)) {
            this.getLogger().error(this.getLang().translateString("plugin_loadArena_GameModeNotExist", world, gameMode));
            return;
        }
        try {
            Constructor<? extends BaseArena> constructor = ARENA_CLASS.get(gameMode).getConstructor(String.class, Config.class);
            BaseArena baseArena = constructor.newInstance(world, config);
            baseArena.setGameMode(gameMode);
            this.arenas.put(world, baseArena);
            this.getLogger().info(this.getLang().translateString("plugin_loadArena_Loaded", world));
        } catch (Exception e) {
            this.getLogger().error(this.getLang().translateString("plugin_loadArena_Error"), e);
        }
    }

    public void unloadAllArena() {
        for (String world : new HashSet<>(this.arenas.keySet())) {
            this.unloadArena(world);
        }
    }

    public void unloadArena(String world) {
        if (this.arenas.containsKey(world)) {
            BaseArena arena = this.arenas.remove(world);
            try {
                arena.gameEnd();
            } catch (Exception ignored) {

            }
            for (BaseGameListener<BaseArena> listener : this.gameListeners.values()) {
                listener.removeListenerRoom(world);
            }
            ArenaTickTask.removeArena(arena);
            Watchdog.removeArena(arena);
            this.arenaConfigs.remove(world);
            this.getLogger().info(this.getLang().translateString("plugin_unloadArena_Unloaded", world));
        }
    }

    public Config getOrCreateArenaConfig(Level level) {
        return this.getOrCreateArenaConfig(level.getFolderName());
    }

    public Config getOrCreateArenaConfig(String level) {
        if (!this.arenaConfigs.containsKey(level)) {
            String targetFile = "/Arena/" + level + ".yml";
            this.saveResource("arena.yml", targetFile, false);
            Config config = new Config(this.getDataFolder() + targetFile, Config.YAML);
            this.arenaConfigs.put(level, config);
        }
        return this.arenaConfigs.get(level);
    }

    public Language getLang() {
        return this.getLang(null);
    }

    public Language getLang(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerLanguage = player.getLoginChainData().getLanguageCode();
            //TODO 转换（例如将zh转换为zh_CN zh_HK转换为zh_CN）

            if (!this.languageMap.containsKey(playerLanguage)) {
                playerLanguage = this.defaultLanguage;
            }
            return this.languageMap.get(playerLanguage);
        }
        return this.languageMap.get(this.defaultLanguage);
    }

}
