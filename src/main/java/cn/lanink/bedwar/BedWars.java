package cn.lanink.bedwar;

import cn.lanink.bedwar.command.AdminCommand;
import cn.lanink.bedwar.command.UserCommand;
import cn.lanink.bedwar.listener.BadWarListener;
import cn.lanink.bedwar.listener.PlayerGameListener;
import cn.lanink.bedwar.listener.PlayerJoinAndQuit;
import cn.lanink.bedwar.listener.RoomLevelProtection;
import cn.lanink.bedwar.room.Room;
import cn.lanink.bedwar.ui.GuiListener;
import cn.lanink.bedwar.utils.Language;
import cn.lanink.lib.scoreboard.IScoreboard;
import cn.lanink.lib.scoreboard.ScoreboardDe;
import cn.lanink.lib.scoreboard.ScoreboardGt;
import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * BedWar
 * @author lt_name
 */
public class BedWars extends PluginBase {

    public static String VERSION = "?";
    private static BedWars bedWars;
    private Language language;
    private Config config;
    private LinkedHashMap<String, Config> roomConfigs = new LinkedHashMap<>();
    private LinkedHashMap<String, Room> rooms = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Skin> skins = new LinkedHashMap<>();
    private IScoreboard scoreboard;

    public static BedWars getInstance() { return bedWars; }

    @Override
    public void onEnable() {
        getLogger().info("§e插件开始加载！本插件是免费哒~如果你花钱了，那一定是被骗了~");
        if (bedWars == null) {
            bedWars = this;
        }
        saveDefaultConfig();
        this.config = new Config(getDataFolder() + "/config.yml", 2);

        getLogger().info("§e插件开始加载！本插件是免费哒~如果你花钱了，那一定是被骗了~");
        getLogger().info("§l§eVersion: " + VERSION);
        //加载计分板
        try {
            Class.forName("de.theamychan.scoreboard.ScoreboardPlugin");
            this.scoreboard = new ScoreboardDe();
        } catch (ClassNotFoundException e1) {
            try {
                Class.forName("gt.creeperface.nukkit.scoreboardapi.ScoreboardAPI");
                this.scoreboard = new ScoreboardGt();
            } catch (ClassNotFoundException e) {
                getLogger().error("请安装计分板前置！！！");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }
        this.loadResources();
        File file1 = new File(this.getDataFolder() + "/Rooms");
        File file2 = new File(this.getDataFolder() + "/PlayerInventory");
        File file3 = new File(this.getDataFolder() + "/Skins");
        if (!file1.exists() && !file1.mkdirs()) {
            getLogger().error("Rooms 文件夹初始化失败");
        }
        if (!file2.exists() && !file2.mkdirs()) {
            getLogger().error("PlayerInventory 文件夹初始化失败");
        }
        if (!file3.exists() && !file3.mkdirs()) {
            getLogger().warning("Skins 文件夹初始化失败");
        }
        getLogger().info("§e开始加载房间");
        this.loadRooms();
        getLogger().info("§e开始加载皮肤");
        this.loadSkins();
        getServer().getCommandMap().register("", new UserCommand(this.config.getString("插件命令", "killer")));
        getServer().getCommandMap().register("", new AdminCommand(this.config.getString("管理命令", "kadmin")));
        getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(), this);
        getServer().getPluginManager().registerEvents(new RoomLevelProtection(), this);
        getServer().getPluginManager().registerEvents(new PlayerGameListener(this), this);
        getServer().getPluginManager().registerEvents(new BadWarListener(this), this);
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getLogger().info("§e插件加载完成！欢迎使用！");
    }

    @Override
    public void onDisable() {
        this.config.save();
        if (this.rooms.values().size() > 0) {
            Iterator<Map.Entry<String, Room>> it = this.rooms.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Room> entry = it.next();
                if (entry.getValue().getPlayers().size() > 0) {
                    entry.getValue().endGame(false);
                    getLogger().info("§c房间：" + entry.getKey() + " 非正常结束！");
                }else {
                    getLogger().info("§c房间：" + entry.getKey() + " 已卸载！");
                }
                it.remove();
            }
        }
        this.rooms.clear();
        this.roomConfigs.clear();
        this.skins.clear();
        getServer().getScheduler().cancelTask(this);
        getLogger().info("§c插件卸载完成！");
    }

    public IScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public Language getLanguage() {
        return this.language;
    }

    public Config getConfig() {
        return this.config;
    }

    public LinkedHashMap<String, Room> getRooms() {
        return this.rooms;
    }

    public Config getRoomConfig(Level level) {
        return getRoomConfig(level.getName());
    }

    private Config getRoomConfig(String level) {
        if (this.roomConfigs.containsKey(level)) {
            return this.roomConfigs.get(level);
        }
        Config config = new Config(getDataFolder() + "/Rooms/" + level + ".yml", 2);
        this.roomConfigs.put(level, config);
        return config;
    }

    public LinkedHashMap<Integer, Skin> getSkins() {
        return this.skins;
    }

    /**
     * 加载所有房间
     */
    private void loadRooms() {
        File[] s = new File(getDataFolder() + "/Rooms").listFiles();
        if (s != null) {
            for (File file1 : s) {
                String[] fileName = file1.getName().split("\\.");
                if (fileName.length > 0) {
                    Config config = getRoomConfig(fileName[0]);
                    Room room = new Room(config);
                    this.rooms.put(fileName[0], room);
                    getLogger().info("§a房间：" + fileName[0] + " 已加载！");
                }
            }
        }
        getLogger().info("§e房间加载完成！当前已加载 " + this.rooms.size() + " 个房间！");
    }

    /**
     * 卸载所有房间
     */
    public void unloadRooms() {
        if (this.rooms.values().size() > 0) {
            Iterator<Map.Entry<String, Room>> it = this.rooms.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, Room> entry = it.next();
                entry.getValue().endGame();
                getLogger().info("§c房间：" + entry.getKey() + " 已卸载！");
                it.remove();
            }
            this.rooms.clear();
        }
        if (this.roomConfigs.values().size() > 0) {
            this.roomConfigs.clear();
        }
        getServer().getScheduler().cancelTask(this);
    }

    /**
     * 重载所有房间
     */
    public void reLoadRooms() {
        this.unloadRooms();
        this.loadRooms();
    }

    /**
     * 加载所有皮肤
     */
    private void loadSkins() {
        File[] files = (new File(getDataFolder() + "/Skins")).listFiles();
        if (files != null && files.length > 0) {
            int x = 0;
            for (File file : files) {
                String skinName = file.getName();
                File skinFile = new File(getDataFolder() + "/Skins/" + skinName + "/skin.png");
                if (skinFile.exists()) {
                    Skin skin = new Skin();
                    BufferedImage skinData = null;
                    try {
                        skinData = ImageIO.read(skinFile);
                    } catch (IOException ignored) {
                        getLogger().warning(skinName + "加载失败，这可能不是一个正确的图片");
                    }
                    if (skinData != null) {
                        skin.setSkinData(skinData);
                        skin.setSkinId(skinName);
                        getLogger().info("§a编号: " + x + " 皮肤: " + skinName + " 已加载");
                        this.skins.put(x, skin);
                        x++;
                    }else {
                        getLogger().warning(skinName + "加载失败，这可能不是一个正确的图片");
                    }
                } else {
                    getLogger().warning(skinName + "加载失败，请将皮肤文件命名为 skin.png");
                }
            }
        }
        if (this.skins.size() > 15) {
            getLogger().info("§e皮肤加载完成！当前已加载 " + this.skins.size() + " 个皮肤！");
        }else {
            getLogger().warning("§c当前皮肤数量小于16，部分玩家仍可使用自己的皮肤");
        }
    }

    private void loadResources() {
        //版本信息
        getLogger().info("§l§e版本: " + VERSION);
        getLogger().info("§e开始加载资源文件");
        //语言文件
        saveResource("Resources/Language/zh_CN.yml", "/Resources/Language/zh_CN.yml", false);
        String s = this.config.getString("language", "zh_CN");
        File languageFile = new File(getDataFolder() + "/Resources/Language/" + s + ".yml");
        if (languageFile.exists()) {
            getLogger().info("§aLanguage: " + s + " loaded !");
            this.language = new Language(new Config(languageFile, 2));
        }else {
            getLogger().warning("§cLanguage: " + s + " Not found, Load the default language !");
            this.language = new Language(new Config());
        }
        getLogger().info("§e资源文件加载完成");
    }

    public void roomSetSpawn(Player player, Config config) {
        String spawn = player.getFloorX() + ":" + player.getFloorY() + ":" + player.getFloorZ();
        String world = player.getLevel().getName();
        config.set("World", world);
        config.set("出生点", spawn);
        config.save();
    }

    public void roomAddRandomSpawn(Player player, Config config) {
        this.roomAddRandomSpawn(player.getFloorX(), player.getFloorY(), player.getFloorZ(), config);
    }

    private void roomAddRandomSpawn(int x, int y, int z, Config config) {
        String s = x + ":" + y + ":" + z;
        List<String> list = config.getStringList("randomSpawn");
        list.add(s);
        config.set("randomSpawn", list);
        config.save();
    }

    public void roomAddGoldSpawn(Player player, Config config) {
        this.roomAddGoldSpawn(player.getFloorX(), player.getFloorY() + 1, player.getFloorZ(), config);
    }

    private void roomAddGoldSpawn(int x, int y, int z, Config config) {
        String s = x + ":" + y + ":" + z;
        List<String> list = config.getStringList("goldSpawn");
        list.add(s);
        config.set("goldSpawn", list);
        config.save();
    }

    public void roomSetWaitTime(Integer waitTime, Config config) {
        config.set("等待时间", waitTime);
        config.save();
    }

    public void roomSetGameTime(Integer gameTime, Config config) {
        config.set("游戏时间", gameTime);
        config.save();
    }

    public void roomSetGoldSpawnTime(Integer goldSpawnTime, Config config) {
        config.set("goldSpawnTime", goldSpawnTime);
        config.save();
    }

}
