package cn.lanink.bedwars;

import cn.lanink.bedwars.arena.ArenaConfig;
import cn.lanink.bedwars.arena.ArenaTickTask;
import cn.lanink.bedwars.arena.BaseArena;
import cn.lanink.bedwars.arena.classic.ClassicArena;
import cn.lanink.bedwars.listener.defaults.PlayerJoinAndQuit;
import cn.lanink.bedwars.utils.Watchdog;
import cn.lanink.gamecore.listener.BaseGameListener;
import cn.lanink.gamecore.scoreboard.ScoreboardUtil;
import cn.lanink.gamecore.scoreboard.base.IScoreboard;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author lt_name
 */
public class BedWars extends PluginBase {

    public static final String VERSION = "?";
    public static boolean debug = false;
    public static final Random RANDOM = new Random();

    private static BedWars bedWars;

    private Config config;

    private IScoreboard scoreboard;

    private static final LinkedHashMap<String, Class<? extends BaseArena>> ARENA_CLASS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, Class<? extends BaseGameListener<BaseArena>>> LISTENER_CLASS = new LinkedHashMap<>();

    private final LinkedHashMap<String, BaseGameListener<BaseArena>> gameListeners = new LinkedHashMap<>();

    private final HashMap<String, ArenaConfig> arenaConfigs = new HashMap<>();
    @Getter
    private final LinkedHashMap<String, BaseArena> arenas = new LinkedHashMap<>();

    @Getter
    private String serverWorldPath;
    @Getter
    private String worldBackupPath;
    @Getter
    private String roomConfigPath;

    public static BedWars getInstance() {
        return bedWars;
    }

    @Override
    public void onLoad() {
        bedWars = this;

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

        registerArenaClass("classic", ClassicArena.class);
    }

    @Override
    public void onEnable() {
        this.scoreboard = ScoreboardUtil.getScoreboard();

        this.getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(this), this);
        this.loadAllListener();

        this.getServer().getScheduler().scheduleRepeatingTask(this, new ArenaTickTask(this), 1);
        this.getServer().getScheduler().scheduleRepeatingTask(this, new Watchdog(this), 20, true);

        this.getLogger().info("插件加载完成！ 版本: " + VERSION);
    }

    @Override
    public void onDisable() {

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
                if (BedWars.debug) {
                    this.getLogger().info("[debug] registerListener: " + baseGameListener.getListenerName());
                }
            } catch (Exception e) {
                this.getLogger().error("加载监听器时出错：", e);
            }
        }
    }

}
