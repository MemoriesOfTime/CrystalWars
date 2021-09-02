package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.crystalwars.utils.Watchdog;
import cn.lanink.crystalwars.utils.exception.ArenaLoadException;
import cn.lanink.gamecore.room.IRoom;
import cn.lanink.gamecore.scoreboard.ScoreboardUtil;
import cn.lanink.gamecore.utils.FileUtil;
import cn.lanink.gamecore.utils.Tips;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LT_Name
 */
public abstract class BaseArena extends ArenaConfig implements IRoom {

    protected CrystalWars crystalWars = CrystalWars.getInstance();

    @Getter
    private String gameMode;

    @Setter
    @Getter
    private ArenaStatus arenaStatus;

    @Getter
    private String gameWorldName;
    @Getter
    private Level gameWorld;

    private int waitTime;
    private int gameTime;

    @Getter
    private final Map<Player, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    private final HashMap<Team, CrystalWarsEntityEndCrystal> teamEntityEndCrystalMap = new HashMap<>();

    public BaseArena(@NotNull String gameWorldName, @NotNull Config config) throws ArenaLoadException {
        super(config);
        this.gameWorldName = gameWorldName;

        if (!Server.getInstance().loadLevel(this.getGameWorldName())) {
            throw new ArenaLoadException("世界: " + this.getGameWorldName() + " 加载失败！");
        }
        this.gameWorld = Server.getInstance().getLevelByName(this.getGameWorldName());

        //备份游戏世界
        File backup = new File(this.crystalWars.getWorldBackupPath() + this.getGameWorldName());
        if (!backup.exists()) {
            this.crystalWars.getLogger().info("地图: " + this.getGameWorldName() + " 备份不存在，正在备份...");
            Server.getInstance().unloadLevel(this.gameWorld);
            if (FileUtil.copyDir(this.crystalWars.getServerWorldPath() + this.getGameWorldName(), backup)) {
                Server.getInstance().loadLevel(this.getGameWorldName());
                this.gameWorld = Server.getInstance().getLevelByName(this.getGameWorldName());
            }else {
                throw new ArenaLoadException("地图: " + this.getGameWorldName() + " 备份失败！");
            }
            this.crystalWars.getLogger().info("地图: " + this.getGameWorldName() + " 备份完成！");
        }

        this.initData();

        for (String name : this.getListeners()) {
            try {
                this.crystalWars.getGameListeners().get(name).addListenerRoom(this);
            } catch (Exception e) {
                this.crystalWars.getLogger().error("", e);
            }
        }

        this.setArenaStatus(ArenaStatus.TASK_NEED_INITIALIZED);
        Watchdog.add(this);
    }

    public void setGameMode(String gameMode) {
        if (this.gameMode == null) {
            this.gameMode = gameMode;
        }
    }

    public List<String> getListeners() {
        ArrayList<String> list = new ArrayList<>();

        list.add("DefaultGameListener");

        return list;
    }

    public void initData() {
        this.waitTime = this.getSetWaitTime();
        this.gameTime = this.getSetGameTime();
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

        if (this.crystalWars.isHasTips()) {
            Tips.closeTipsShow(this.getGameWorldName(), player);
        }

        PlayerData playerData = this.getOrCreatePlayerData(player);
        playerData.saveBeforePlayerData();
        this.getPlayerDataMap().put(player, playerData);

        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        //等待游戏开始时使用冒险模式
        player.setGamemode(Player.ADVENTURE);

        player.teleport(Position.fromObject(this.getWaitSpawn(), this.getGameWorld()));
        return true;
    }

    public boolean quitRoom(@NotNull Player player) {
        if (this.crystalWars.isHasTips()) {
            Tips.removeTipsConfig(this.getGameWorldName(), player);
        }

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
            this.waitTime = this.getSetWaitTime();
            this.setArenaStatus(ArenaStatus.TASK_NEED_INITIALIZED);
            ArenaTickTask.removeArena(this);
            return;
        }else {
            if (this.getPlayerDataMap().size() >= this.getMinPlayers()) {
                this.waitTime--;
                if (this.waitTime <= 0) {
                    this.gameStart();
                    return;
                }
            }else {
                this.waitTime = this.getSetWaitTime();
            }
        }

        for (Map.Entry<Player, PlayerData> entry : this.playerDataMap.entrySet()) {
            //计分板
            LinkedList<String> list = new LinkedList<>();
            list.add(Utils.getSpace(list));
            list.add("开始倒计时: " + Utils.formatCountdown(this.waitTime));
            list.add(Utils.getSpace(list));
            list.add("玩家数量: " + getPlayerDataMap().size() + "/" + this.getMaxPlayers());
            list.add(Utils.getSpace(list));
            ScoreboardUtil.getScoreboard().showScoreboard(entry.getKey(), "CrystalWars", list);
        }
        Watchdog.resetTime(this);
    }

    public void onUpdateGame(int tick) {
        if (tick%20 != 0) {
            return;
        }

        for (Map.Entry<Player, PlayerData> entry : this.getPlayerDataMap().entrySet()) {
            //玩家复活
            if (entry.getValue().getPlayerStatus() == PlayerData.PlayerStatus.WAIT_SPAWN) {
                entry.getValue().setWaitSpawnTime(entry.getValue().getWaitSpawnTime() - 1);
                if (entry.getValue().getWaitSpawnTime() <= 0) {
                    this.playerRespawn(entry.getKey());
                }
            }

            //计分板
            LinkedList<String> list = new LinkedList<>();
            list.add(Utils.getSpace(list));
            list.add("游戏结束倒计时:" + Utils.formatCountdown(this.gameTime));
            list.add(Utils.getSpace(list));
            list.add("红队: 水晶: " + Utils.getShowHealth(this.teamEntityEndCrystalMap.get(Team.RED)) +
                    " 存活: " + this.getSurvivingPlayers(Team.RED).size());
            list.add("黄队: 水晶: " + Utils.getShowHealth(this.teamEntityEndCrystalMap.get(Team.YELLOW)) +
                    " 存活: " + this.getSurvivingPlayers(Team.YELLOW).size());
            list.add("蓝队: 水晶: " + Utils.getShowHealth(this.teamEntityEndCrystalMap.get(Team.BLUE)) +
                    " 存活: " + this.getSurvivingPlayers(Team.BLUE).size());
            list.add("绿队: 水晶: " + Utils.getShowHealth(this.teamEntityEndCrystalMap.get(Team.GREEN)) +
                    " 存活: " + this.getSurvivingPlayers(Team.GREEN).size());
            list.add(Utils.getSpace(list));
            ScoreboardUtil.getScoreboard().showScoreboard(entry.getKey(), "CrystalWars", list);
        }

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

        //生成水晶
        for (Team team : Team.values()) {
            if (team == Team.NULL || this.getPlayers(team).isEmpty()) {
                continue;
            }

            Position crystalPos = Position.fromObject(this.getTeamCrystal(team), this.getGameWorld());
            CrystalWarsEntityEndCrystal entityEndCrystal = new CrystalWarsEntityEndCrystal(
                    crystalPos.getChunk(),
                    Entity.getDefaultNBT(crystalPos),
                    team
            );
            entityEndCrystal.spawnToAll();
            CrystalWarsEntityEndCrystal oldCrystal = this.teamEntityEndCrystalMap.put(team, entityEndCrystal);
            if (oldCrystal != null) {
                oldCrystal.close();
            }
        }

        //游戏开始，重生所有玩家
        for (Player player : this.playerDataMap.keySet()) {
            this.playerRespawn(player);
        }

        this.setArenaStatus(ArenaStatus.GAME);
    }

    public void gameEnd() {
        this.setArenaStatus(ArenaStatus.LEVEL_NOT_LOADED);
        //TODO
        for (Player player : new HashSet<>(this.getPlayerDataMap().keySet())) {
            this.quitRoom(player);
        }
    }

    /**
     * 分配玩家队伍
     */
    public void assignTeam() {
        int baseCount = this.getPlayerDataMap().size() / (Team.values().length - 1);
        if (baseCount < 1) {
            baseCount = 1;
        }

        //TODO 改成兼容多个队伍数量
        LinkedList<Player> noTeamPlayers = new LinkedList<>();
        LinkedList<Player> redTeamPlayers = new LinkedList<>();
        LinkedList<Player> yellowTeamPlayers = new LinkedList<>();
        LinkedList<Player> blueTeamPlayers = new LinkedList<>();
        LinkedList<Player> greenTeamPlayers = new LinkedList<>();
        for (Map.Entry<Player, PlayerData> entry : this.getPlayerDataMap().entrySet()) {
            if (entry.getValue().getTeam() == Team.NULL) {
                noTeamPlayers.add(entry.getKey());
            }else if (entry.getValue().getTeam() == Team.RED) {
                redTeamPlayers.add(entry.getKey());
            }else if (entry.getValue().getTeam() == Team.YELLOW) {
                yellowTeamPlayers.add(entry.getKey());
            }else if (entry.getValue().getTeam() == Team.BLUE) {
                blueTeamPlayers.add(entry.getKey());
            }else if (entry.getValue().getTeam() == Team.GREEN) {
                greenTeamPlayers.add(entry.getKey());
            }
        }

        Collections.shuffle(noTeamPlayers, CrystalWars.RANDOM);

        while (true) {
            Player cache = null;

            if (!noTeamPlayers.isEmpty()) {
                cache = noTeamPlayers.poll();
            }else if (redTeamPlayers.size() > baseCount + 2) {
                cache = redTeamPlayers.get(CrystalWars.RANDOM.nextInt(redTeamPlayers.size()));
                redTeamPlayers.remove(cache);
            }else if (yellowTeamPlayers.size() > baseCount + 2) {
                cache = yellowTeamPlayers.get(CrystalWars.RANDOM.nextInt(yellowTeamPlayers.size()));
                yellowTeamPlayers.remove(cache);
            }else if (blueTeamPlayers.size() > baseCount + 2) {
                cache = blueTeamPlayers.get(CrystalWars.RANDOM.nextInt(blueTeamPlayers.size()));
                blueTeamPlayers.remove(cache);
            }else if (greenTeamPlayers.size() > baseCount + 2) {
                cache = greenTeamPlayers.get(CrystalWars.RANDOM.nextInt(greenTeamPlayers.size()));
                greenTeamPlayers.remove(cache);
            }

            if (cache == null) {
                break;
            }

            if (redTeamPlayers.size() < baseCount) {
                redTeamPlayers.add(cache);
                continue;
            }else if (yellowTeamPlayers.size() < baseCount) {
                yellowTeamPlayers.add(cache);
                continue;
            }else if (blueTeamPlayers.size() < baseCount) {
                blueTeamPlayers.add(cache);
                continue;
            }else if (greenTeamPlayers.size() < baseCount) {
                greenTeamPlayers.add(cache);
                continue;
            }

            switch (CrystalWars.RANDOM.nextInt(4)) {
                case 0:
                    redTeamPlayers.add(cache);
                    break;
                case 1:
                    yellowTeamPlayers.add(cache);
                    break;
                case 2:
                    blueTeamPlayers.add(cache);
                    break;
                default:
                    greenTeamPlayers.add(cache);
                    break;
            }
        }

        for (Player player : redTeamPlayers) {
            this.getPlayerData(player).setTeam(Team.RED);
        }
        for (Player player : yellowTeamPlayers) {
            this.getPlayerData(player).setTeam(Team.YELLOW);
        }
        for (Player player : blueTeamPlayers) {
            this.getPlayerData(player).setTeam(Team.BLUE);
        }
        for (Player player : greenTeamPlayers) {
            this.getPlayerData(player).setTeam(Team.GREEN);
        }

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
        //TODO 检查床是否存在
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
        if (playerData.getTeam() == Team.NULL) {
            return;
        }
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
     * 根据队伍获取玩家
     *
     * @param team 队伍
     * @return 队伍中的玩家
     */
    public List<Player> getPlayers(@NotNull Team team) {
        ArrayList<Player> players = new ArrayList<>();
        for (Map.Entry<Player, PlayerData> entry : this.playerDataMap.entrySet()) {
            if (entry.getValue().getTeam() == team) {
                players.add(entry.getKey());
            }
        }
        return players;
    }

    /**
     * 根据队伍获取存活的玩家
     *
     * @param team 队伍
     * @return 存活的玩家
     */
    public List<Player> getSurvivingPlayers(@NotNull Team team) {
        ArrayList<Player> players = new ArrayList<>();
        for (Player player : this.getPlayers(team)) {
            if (this.getPlayerData(player).getPlayerStatus() == PlayerData.PlayerStatus.SURVIVE) {
                players.add(player);
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
