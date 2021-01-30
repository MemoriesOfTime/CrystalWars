package cn.lanink.bedwar.room;

import cn.lanink.bedwar.BedWars;
import cn.lanink.bedwar.tasks.TipsTask;
import cn.lanink.bedwar.tasks.WaitTask;
import cn.lanink.bedwar.utils.SavePlayerInventory;
import cn.lanink.bedwar.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import tip.messages.NameTagMessage;
import tip.utils.Api;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间类
 */
public class Room {

    private int mode; //0等待重置 1玩家等待中 2玩家游戏中 3胜利结算中
    public int waitTime, gameTime; //秒
    public int victory; //胜利者
    private final int setWaitTime, setGameTime;
    public final int setCopperSpawnTime, setIronSpawnTime, setGoldSpawnTime, setDiamondSpawnTime;
    private LinkedHashMap<Player, Integer> players = new LinkedHashMap<>(); //0未分配 1红队 2蓝队 3黄队 4绿队
    private LinkedHashMap<Player, Integer> skinNumber = new LinkedHashMap<>(); //玩家使用皮肤编号，用于防止重复使用
    private LinkedHashMap<Player, Skin> skinCache = new LinkedHashMap<>(); //缓存玩家皮肤，用于退出房间时还原
    private final List<String> copperSpawn, ironSpawn, goldSpawn, diamondSpawn;
    private final String spawn, world, radSpawn, blueSpawn, yellowSpawn, greenSpawn;

    /**
     * 初始化
     * @param config 配置文件
     */
    public Room(Config config) {
        this.setWaitTime = config.getInt("等待时间", 120);
        this.setGameTime = config.getInt("游戏时间", 600);

        this.setCopperSpawnTime = config.getInt("copperSpawnTime");
        this.setIronSpawnTime = config.getInt("ironSpawnTime");
        this.setGoldSpawnTime = config.getInt("goldSpawnTime");
        this.setDiamondSpawnTime = config.getInt("diamondSpawnTime");

        this.spawn = config.getString("出生点", null);

        this.radSpawn = config.getString("radSpawn", null);
        this.blueSpawn = config.getString("blueSpawn", null);
        this.yellowSpawn = config.getString("yellowSpawn", null);
        this.greenSpawn = config.getString("greenSpawn", null);

        this.copperSpawn = config.getStringList("copperSpawn");
        this.ironSpawn = config.getStringList("ironSpawn");
        this.goldSpawn = config.getStringList("goldSpawn");
        this.diamondSpawn = config.getStringList("diamondSpawn");

        this.world = config.getString("World", null);
        this.initTime();
        this.mode = 0;
    }

    /**
     * 初始化Task
     */
    private void initTask() {
        this.setMode(1);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                BedWars.getInstance(), new WaitTask(BedWars.getInstance(), this), 20, true);
        Server.getInstance().getScheduler().scheduleRepeatingTask(
                BedWars.getInstance(), new TipsTask(BedWars.getInstance(), this), 20);
    }

    /**
     * 初始化时间参数
     */
    public void initTime() {
        this.waitTime = this.setWaitTime;
        this.gameTime = this.setGameTime;
    }

    /**
     * @param mode 房间状态
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return 房间状态
     */
    public int getMode() {
        return this.mode;
    }

    /**
     * 结束本局游戏
     */
    public void endGame() {
        this.endGame(true);
    }

    /**
     * 结束本局游戏
     * @param normal 正常关闭
     */
    public void endGame(boolean normal) {
        this.mode = 0;
        if (normal) {
            if (this.players.values().size() > 0 ) {
                Iterator<Map.Entry<Player, Integer>> it = this.players.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry<Player, Integer> entry = it.next();
                    it.remove();
                    this.quitRoomOnline(entry.getKey());
                }
            }
        }else {
            this.getLevel().getPlayers().values().forEach(
                    player -> player.kick(BedWars.getInstance().getLanguage().roomSafeKick));
        }
        this.initTime();
        this.skinNumber.clear();
        this.skinCache.clear();
        Tools.cleanEntity(this.getLevel(), true);
    }

    /**
     * 加入房间
     * @param player 玩家
     */
    public void joinRoom(Player player) {
        if (this.players.values().size() < 16) {
            if (this.mode == 0) {
                this.initTask();
            }
            this.addPlaying(player);
            Tools.rePlayerState(player, true);
            SavePlayerInventory.save(player);
            player.teleport(this.getSpawn());
            this.setRandomSkin(player, false);
            Tools.giveItem(player, 10);
            NameTagMessage nameTagMessage = new NameTagMessage(this.world, true, "");
            Api.setPlayerShowMessage(player.getName(), nameTagMessage);
            player.sendMessage(BedWars.getInstance().getLanguage().joinRoom.replace("%name%", this.world));
        }
    }

    /**
     * 退出房间
     * @param player 玩家
     */
    public void quitRoom(Player player) {
        this.quitRoom(player, true);
    }

    /**
     * 退出房间
     * @param player 玩家
     * @param online 是否在线
     */
    public void quitRoom(Player player, boolean online) {
        if (this.isPlaying(player)) {
            this.delPlaying(player);
        }
        if (online) {
            this.quitRoomOnline(player);
        }else {
            this.skinNumber.remove(player);
            this.skinCache.remove(player);
        }
    }

    /**
     * 退出房间
     * @param player 玩家
     */
    private void quitRoomOnline(Player player) {
        Tools.removePlayerShowMessage(this.world, player);
        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
        Tools.rePlayerState(player, false);
        SavePlayerInventory.restore(player);
        this.setRandomSkin(player, true);
    }

    /**
     * 设置玩家随机皮肤
     * @param player 玩家
     * @param restore 是否为还原
     */
    public void setRandomSkin(Player player, boolean restore) {
        if (restore) {
            if (this.skinCache.containsKey(player)) {
                Tools.setPlayerSkin(player, this.skinCache.get(player));
                this.skinCache.remove(player);
            }
            this.skinNumber.remove(player);
        }else {
            for (Map.Entry<Integer, Skin> entry : BedWars.getInstance().getSkins().entrySet()) {
                if (!this.skinNumber.containsValue(entry.getKey())) {
                    this.skinCache.put(player, player.getSkin());
                    this.skinNumber.put(player, entry.getKey());
                    Tools.setPlayerSkin(player, entry.getValue());
                    return;
                }
            }
        }
    }

    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     */
    public void addPlaying(Player player) {
        if (!this.players.containsKey(player)) {
            this.addPlaying(player, 0);
        }
    }

    /**
     * 记录在游戏内的玩家
     * @param player 玩家
     * @param mode 身份
     */
    public void addPlaying(Player player, Integer mode) {
        this.players.put(player, mode);
    }

    /**
     * 删除记录
     * @param player 玩家
     */
    private void delPlaying(Player player) {
        this.players.remove(player);
    }

    /**
     * @return boolean 玩家是否在游戏里
     * @param player 玩家
     */
    public boolean isPlaying(Player player) {
        return this.players.containsKey(player);
    }

    /**
     * @return 玩家列表
     */
    public LinkedHashMap<Player, Integer> getPlayers() {
        return this.players;
    }

    /**
     * @return 玩家身份
     */
    public Integer getPlayerMode(Player player) {
        if (isPlaying(player)) {
            return this.players.get(player);
        }else {
            return null;
        }
    }

    /**
     * @return 等待出生点
     */
    public Position getSpawn() {
        String[] s = this.spawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                this.getLevel());
    }

    /**
     * @return 红队出生点
     */
    public Position getRadSpawn() {
        String[] s = this.radSpawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                this.getLevel());
    }

    /**
     * @return 蓝队出生点
     */
    public Position getBlueSpawn() {
        String[] s = this.blueSpawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                this.getLevel());
    }

    /**
     * @return 黄队出生点
     */
    public Position getYellowSpawn() {
        String[] s = this.yellowSpawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                this.getLevel());
    }

    /**
     * @return 绿队出生点
     */
    public Position getGreenSpawn() {
        String[] s = this.greenSpawn.split(":");
        return new Position(Integer.parseInt(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                this.getLevel());
    }

    /**
     * @return 等待时间
     */
    public int getWaitTime() {
        return this.setWaitTime;
    }

    /**
     * @return 游戏时间
     */
    public int getGameTime() {
        return this.setGameTime;
    }

    /**
     * @return 铜锭刷新点
     */
    public List<String> getCopperSpawn() {
        return this.copperSpawn;
    }

    /**
     * @return 铁锭刷新点
     */
    public List<String> getIronSpawn() {
        return this.ironSpawn;
    }

    /**
     * @return 金锭产出地点
     */
    public List<String> getGoldSpawn() {
        return this.goldSpawn;
    }

    public List<String> getDiamondSpawn() {
        return this.diamondSpawn;
    }

    /**
     * @return 游戏世界
     */
    public Level getLevel() {
        return Server.getInstance().getLevelByName(this.world);
    }

    /**
     * 获取玩家在游戏中使用的皮肤
     * @param player 玩家
     * @return 皮肤
     */
    public Skin getPlayerSkin(Player player) {
        if (this.skinNumber.containsKey(player)) {
            return BedWars.getInstance().getSkins().get(this.skinNumber.get(player));
        }
        return player.getSkin();
    }

}
