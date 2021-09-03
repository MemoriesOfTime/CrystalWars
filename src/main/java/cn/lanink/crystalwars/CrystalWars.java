package cn.lanink.crystalwars;

import cn.lanink.crystalwars.arena.ArenaTickTask;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.classic.ClassicArena;
import cn.lanink.crystalwars.command.user.UserCommand;
import cn.lanink.crystalwars.listener.defaults.DefaultGameListener;
import cn.lanink.crystalwars.listener.defaults.PlayerJoinAndQuit;
import cn.lanink.crystalwars.utils.Watchdog;
import cn.lanink.gamecore.listener.BaseGameListener;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author lt_name
 */
public class CrystalWars extends PluginBase {

    public static final String VERSION = "?";
    public static boolean debug = false;
    public static final Random RANDOM = new Random();

    @Getter
    private boolean hasTips = false;

    private static CrystalWars crystalWars;

    private Config config;

    private static final LinkedHashMap<String, Class<? extends BaseArena>> ARENA_CLASS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Class<? extends BaseGameListener<BaseArena>>> LISTENER_CLASS = new LinkedHashMap<>();

    @Getter
    private final LinkedHashMap<String, BaseGameListener<BaseArena>> gameListeners = new LinkedHashMap<>();

    private final HashMap<String, Config> arenaConfigs = new HashMap<>();
    @Getter
    private final LinkedHashMap<String, BaseArena> arenas = new LinkedHashMap<>();

    @Getter
    private String serverWorldPath;
    @Getter
    private String worldBackupPath;
    @Getter
    private String roomConfigPath;

    public static CrystalWars getInstance() {
        return crystalWars;
    }

    @Override
    public void onLoad() {
        crystalWars = this;

        this.serverWorldPath = this.getServer().getFilePath() + "/worlds/";
        this.worldBackupPath = this.getDataFolder() + "/RoomLevelBackup/";
        this.roomConfigPath = this.getDataFolder() + "/Rooms/";

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

        registerListener("DefaultGameListener", DefaultGameListener.class);

        registerArenaClass("classic", ClassicArena.class);
    }

    @Override
    public void onEnable() {
        //检查Tips
        try {
            Class.forName("tip.Main");
            if (getServer().getPluginManager().getPlugin("Tips").isDisabled()) {
                throw new RuntimeException("Not Loaded");
            }
            this.hasTips = true;
        } catch (Exception ignored) {

        }

        this.getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(this), this);
        this.loadAllListener();

        this.getServer().getScheduler().scheduleRepeatingTask(this, new ArenaTickTask(this), 1);
        this.getServer().getScheduler().scheduleRepeatingTask(this, new Watchdog(this), 20, true);

        this.loadAllArena();

        this.getServer().getCommandMap().register("CrystalWars".toLowerCase(), new UserCommand("CrystalWars"));

        this.getLogger().info("插件加载完成！ 版本: " + VERSION);
    }

    @Override
    public void onDisable() {
        for (BaseArena arena : this.arenas.values()) {
            arena.gameEnd();
        }
        this.arenas.clear();
        this.arenaConfigs.clear();

        ArenaTickTask.clearAll();
        Watchdog.clearAll();

        this.getLogger().info("插件卸载完毕！");
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

    public void loadAllListener() {
        for (Map.Entry<String, Class<? extends BaseGameListener<BaseArena>>> entry : LISTENER_CLASS.entrySet()) {
            try {
                BaseGameListener<BaseArena> baseGameListener = entry.getValue().getConstructor().newInstance();
                baseGameListener.init(entry.getKey());
                this.gameListeners.put(entry.getKey(), baseGameListener);
                if (CrystalWars.debug) {
                    this.getLogger().info("[debug] registerListener: " + baseGameListener.getListenerName());
                }
            } catch (Exception e) {
                this.getLogger().error("加载监听器时出错：", e);
            }
        }
    }

    public void loadAllArena() {
        File[] files = new File(this.getDataFolder() + "/Arena").listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                this.loadArena(file.getName().split("\\.")[0]);
            }
        }
    }

    public void loadArena(String world) {
        Config config = this.getArenaConfig(world);
        if (!Server.getInstance().loadLevel(world)) {
            this.getLogger().error("游戏房间: " + world + " 地图不存在！无法加载！");
            return;
        }
        String gameMode = config.getString("gameMode", "classic");
        if (!ARENA_CLASS.containsKey(gameMode)) {
            this.getLogger().error("游戏房间: " + world + " 游戏模式:" + gameMode + " 不存在！无法加载！");
            return;
        }
        try {
            Constructor<? extends BaseArena> constructor = ARENA_CLASS.get(gameMode).getConstructor(String.class, Config.class);
            BaseArena baseArena = constructor.newInstance(world, config);
            baseArena.setGameMode(gameMode);
            this.arenas.put(world, baseArena);
            this.getLogger().info("游戏房间:" + world + " 加载完成！");
        } catch (Exception e) {
            this.getLogger().error("加载游戏房间时出现错误！", e);
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
            this.getLogger().info("游戏房间: " + world + " 卸载完成！");
            this.arenaConfigs.remove(world);
        }
    }

    public Config getArenaConfig(Level level) {
        return this.getArenaConfig(level.getFolderName());
    }

    public Config getArenaConfig(String level) {
        if (!this.arenaConfigs.containsKey(level)) {
            Config config = new Config(this.getDataFolder() + "/Arena/" + level + ".yml", Config.YAML);
            this.arenaConfigs.put(level, config);
        }
        return this.arenaConfigs.get(level);
    }

}
