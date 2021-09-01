package cn.lanink.bedwars.arena;

import cn.lanink.bedwars.BedWars;
import cn.lanink.bedwars.utils.Watchdog;
import cn.lanink.bedwars.utils.exception.ArenaLoadException;
import cn.lanink.gamecore.room.IRoom;
import cn.lanink.gamecore.utils.FileUtil;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LT_Name
 */
public abstract class BaseArena extends ArenaConfig implements IRoom {

    protected BedWars bedWars = BedWars.getInstance();

    @Getter
    private String gameMode;

    @Setter
    @Getter
    private ArenaStatus arenaStatus;

    @Getter
    private Level gameWorld;

    @Getter
    private final Map<Player, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public BaseArena(@NotNull Config config) throws ArenaLoadException {
        super(config);

        if (!Server.getInstance().loadLevel(this.getGameWorldName())) {
            throw new ArenaLoadException("世界: " + this.getGameWorldName() + " 加载失败！");
        }
        this.gameWorld = Server.getInstance().getLevelByName(this.getGameWorldName());

        //备份游戏世界
        File backup = new File(this.bedWars.getWorldBackupPath() + this.getGameWorldName());
        if (!backup.exists()) {
            this.bedWars.getLogger().info("地图: " + this.getGameWorldName() + " 备份不存在，正在备份...");
            Server.getInstance().unloadLevel(this.gameWorld);
            if (FileUtil.copyDir(this.bedWars.getServerWorldPath() + this.getGameWorldName(), backup)) {
                Server.getInstance().loadLevel(this.getGameWorldName());
                this.gameWorld = Server.getInstance().getLevelByName(this.getGameWorldName());
            }else {
                throw new ArenaLoadException("地图: " + this.getGameWorldName() + " 备份失败！");
            }
            this.bedWars.getLogger().info("地图: " + this.getGameWorldName() + " 备份完成！");
        }

        this.initData();

        this.setArenaStatus(ArenaStatus.TASK_NEED_INITIALIZED);
        Watchdog.add(this);
    }

    public void setGameMode(String gameMode) {
        if (this.gameMode == null) {
            this.gameMode = gameMode;
        }
    }

    public void initData() {
        this.playerDataMap.clear();
    }

    public boolean canJoin() {
        return (this.getArenaStatus() == ArenaStatus.TASK_NEED_INITIALIZED || this.getArenaStatus() == ArenaStatus.WAIT);
    }

    public boolean joinRoom(@NotNull Player player) {
        if (this.getArenaStatus() == ArenaStatus.TASK_NEED_INITIALIZED) {
            this.setArenaStatus(ArenaStatus.WAIT);
            ArenaTickTask.addArena(this);
        }

        PlayerData playerData = this.getOrCreatePlayerData(player);
        playerData.saveBeforePlayerData();
        this.getPlayerDataMap().put(player, playerData);

        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        player.setGamemode(Player.ADVENTURE);

        player.teleport(Position.fromObject(this.getWaitSpawn(), this.getGameWorld()));
        return true;
    }

    public boolean quitRoom(@NotNull Player player) {
        PlayerData playerData = this.getPlayerData(player);
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        playerData.restoreBeforePlayerData();
        this.getPlayerDataMap().remove(player);
        return true;
    }

    /**
     * 房间Tick
     *
     * @param tick tick
     */
    public void onUpdate(int tick) {
        switch (this.arenaStatus) {
            case WAIT:
                this.onUpdateWait(tick);
                break;
            case GAME:
                this.onUpdateGame(tick);
                break;
            case VICTORY:
                this.onUpdateVictory(tick);
                break;
            default:
                ArenaTickTask.removeArena(this);
                break;
        }
    }

    public void onUpdateWait(int tick) {
        if (tick%20 != 0) {
            return;
        }

        if (this.getPlayerDataMap().isEmpty()) {
            this.setArenaStatus(ArenaStatus.TASK_NEED_INITIALIZED);
            ArenaTickTask.removeArena(this);
            return;
        }

        for (Map.Entry<Player, PlayerData> entry : this.playerDataMap.entrySet()) {

        }
        Watchdog.resetTime(this);
    }

    public void onUpdateGame(int tick) {
        if (tick%20 != 0) {
            return;
        }
        //TODO

        //资源生成
        for (ResourceGeneration resourceGeneration : this.getResourceGenerations()) {
            if ((this.getGameWorld().isDaytime() && resourceGeneration.getConfig().isCanSpawnOnDay()) ||
                    (!this.getGameWorld().isDaytime() && resourceGeneration.getConfig().isCanSpawnOnNight())) {
                resourceGeneration.setCoolDownTime(resourceGeneration.getCoolDownTime() - 1);
                if (resourceGeneration.getCoolDownTime() <= 0) {
                    Item item = resourceGeneration.getConfig().getItem();
                    item.setCount(resourceGeneration.getConfig().getSpawnCount());
                    this.getGameWorld().dropItem(resourceGeneration.getVector3(), item);
                }
            }
        }

        Watchdog.resetTime(this);
    }

    public void onUpdateVictory(int tick) {
        //TODO

        Watchdog.resetTime(this);
    }

    public void gameStart() {
        this.assignTeam();

        //生成床
        for (Team team : Team.values()) {
            Vector3 bedVector3 = this.getTeamBed(team);
            //TODO 床是两个方块
            this.gameWorld.setBlock(bedVector3, Block.get(BlockID.BED_BLOCK));
        }

        //游戏开始，重生所有玩家
        for (Player player : this.playerDataMap.keySet()) {
            this.playerRespawn(player);
        }

    }

    public void gameEnd() {
        this.setArenaStatus(ArenaStatus.LEVEL_NOT_LOADED);
        //TODO
    }

    public void assignTeam() {
        //TODO
    }

    /**
     * 玩家死亡
     *
     * @param player 玩家
     */
    public void playerDeath(@NotNull Player player) {
        PlayerData playerData = this.getPlayerData(player);
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        player.setGamemode(Player.VIEW);
        playerData.setPlayerStatus(PlayerData.PlayerStatus.WAIT_SPAWN);
        //TODO 可能需要调整
        playerData.setWaitSpawnTime(5);
    }

    /**
     * 玩家重生
     *
     * @param player 玩家
     */
    public void playerRespawn(@NotNull Player player) {
        PlayerData playerData = this.getPlayerData(player);
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        player.teleport(this.getTeamSpawn(playerData.getTeam()));
        player.setGamemode(Player.SURVIVAL);
        playerData.setPlayerStatus(PlayerData.PlayerStatus.SURVIVE);
    }

    public boolean isPlaying(@NotNull Player player) {
        return this.playerDataMap.containsKey(player);
    }

    public PlayerData getPlayerData(@NotNull Player player) {
        return this.playerDataMap.get(player);
    }

    public PlayerData getOrCreatePlayerData(@NotNull Player player) {
        if (this.playerDataMap.containsKey(player)) {
            return this.playerDataMap.get(player);
        }
        return new PlayerData(player);
    }

    /**
     * 获取队伍玩家列表
     *
     * @param team 队伍
     * @return 队伍中的玩家
     */
    public List<Player> getPlayers(Team team) {
        ArrayList<Player> players = new ArrayList<>();
        for (Map.Entry<Player, PlayerData> entry : this.playerDataMap.entrySet()) {
            if (entry.getValue().getTeam() == team) {
                players.add(entry.getKey());
            }
        }
        return players;
    }

    @Override
    public Level getLevel() {
        return this.getGameWorld();
    }

    @Override
    public String getLevelName() {
        return this.getGameWorldName();
    }

    public enum ArenaStatus {
        /**
         * 世界需要加载
         */
        LEVEL_NOT_LOADED,

        /**
         * task需要初始化
         */
        TASK_NEED_INITIALIZED,

        /**
         * 等待更多玩家加入
         */
        WAIT,

        /**
         * 游戏中
         */
        GAME,

        /**
         * 胜利结算中
         */
        VICTORY
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BaseArena arena = (BaseArena) o;
        return Objects.equals(this.getGameWorldName(), arena.getGameWorldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getGameWorldName());
    }
}
