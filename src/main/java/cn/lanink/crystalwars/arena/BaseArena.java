package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.entity.EntityText;
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
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private final String gameWorldName;
    @Getter
    private Level gameWorld;

    private int waitTime;
    private int gameTime;
    private int victoryTime;

    @Getter
    private final Map<Player, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    private final HashMap<Team, CrystalWarsEntityEndCrystal> teamEntityEndCrystalMap = new HashMap<>();
    private final HashMap<Team, CrystalWarsEntityMerchant> teamEntityMerchantMap = new HashMap<>();
    private final HashMap<ResourceGeneration, EntityText> resourceGenerationText = new HashMap<>();

    private Team victoryTeam = Team.NULL;

    @Getter
    private boolean isOvertime = false;

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
        Watchdog.addArena(this);
    }

    public final void setGameMode(String gameMode) {
        if (this.gameMode == null) {
            this.gameMode = gameMode;
        }else {
            throw new RuntimeException("重复设置房间游戏模式！");
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
        this.victoryTime = this.getSetVictoryTime();

        this.playerDataMap.clear();

        this.teamEntityEndCrystalMap.clear();
        this.teamEntityMerchantMap.clear();
        this.resourceGenerationText.clear();

        this.victoryTeam = Team.NULL;
        this.isOvertime = false;
    }

    public boolean canJoin() {
        return (this.getArenaStatus() == ArenaStatus.TASK_NEED_INITIALIZED || this.getArenaStatus() == ArenaStatus.WAIT) &&
                this.getPlayerCount() < this.getMaxPlayers();
    }

    public boolean joinRoom(@NotNull Player player) {
        if (!this.canJoin() || this.getPlayerDataMap().containsKey(player)) {
            return false;
        }

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

        player.setHealth(player.getMaxHealth());
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

        PlayerData playerData = this.getPlayerDataMap().remove(player);
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        playerData.restoreBeforePlayerData();
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
            if (this.getPlayerCount() >= this.getMinPlayers()) {
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
            if (this.getPlayerCount() >= this.getMinPlayers()) {
                list.add("§f◎ §f开始倒计时:  §a" + Utils.formatCountdown(this.waitTime));
            }else {
                list.add("§f◎ §c等待玩家加入中...");
            }
            list.add(Utils.getSpace(list));
            list.add("§f◎  §a" + this.getPlayerCount() + "§e/§a" + this.getMinPlayers() + " §8(§6Max:§a" + this.getMaxPlayers() + "§8)");
            list.add(Utils.getSpace(list));
            ScoreboardUtil.getScoreboard().showScoreboard(entry.getKey(), CrystalWars.PLUGIN_NAME, list);
        }
        Watchdog.resetTime(this);
    }

    public void onUpdateGame(int tick) {
        if (tick%20 != 0) {
            return;
        }

        this.gameTime--;

        for (Map.Entry<Player, PlayerData> entry : this.getPlayerDataMap().entrySet()) {
            //玩家复活
            if (entry.getValue().getPlayerStatus() == PlayerData.PlayerStatus.WAIT_SPAWN) {
                if (this.isTeamCrystalSurviving(entry.getValue().getTeam())) {
                    int waitSpawnTime = entry.getValue().getWaitSpawnTime() - 1;
                    entry.getValue().setWaitSpawnTime(waitSpawnTime);
                    if (waitSpawnTime <= 0) {
                        entry.getKey().sendTitle("", ""); //清掉之前的内容
                        this.playerRespawn(entry.getKey());
                    }else {
                        entry.getKey().sendTitle("", "§e" + waitSpawnTime + "§a秒后复活！");
                    }
                }else {
                    entry.getValue().setPlayerStatus(PlayerData.PlayerStatus.DEATH);
                }
            }

            //计分板
            LinkedList<String> list = new LinkedList<>();
            list.add(Utils.getSpace(list));
            list.add("§f◎ §f倒计时:  §a" + Utils.formatCountdown(this.gameTime) + (this.isOvertime() ? " §f(加时赛)" : ""));
            list.add(Utils.getSpace(list));
            for (Map.Entry<Team, CrystalWarsEntityEndCrystal> e1 : this.teamEntityEndCrystalMap.entrySet()) {
                List<Player> survivingPlayers = this.getSurvivingPlayers(e1.getKey());
                if (this.isTeamCrystalSurviving(e1.getKey()) || !survivingPlayers.isEmpty()) {
                    list.add("§f◎ -" + Utils.getShowTeam(e1.getKey()) + "§f- §e水晶:§a" + Utils.getShowHealth(e1.getValue()) + " §8(§a" + survivingPlayers.size() + "§8)");
                }else {
                    list.add("§f◎ -" + Utils.getShowTeam(e1.getKey()) + "§f- §c(oT-T)尸");
                }
            }
            list.add(Utils.getSpace(list));
            ScoreboardUtil.getScoreboard().showScoreboard(entry.getKey(), CrystalWars.PLUGIN_NAME, list);
        }

        //资源生成
        for (ResourceGeneration resourceGeneration : this.getResourceGenerations()) {
            if (resourceGeneration.canSpawn(this)) {
                resourceGeneration.setCoolDownTime(resourceGeneration.getCoolDownTime() - 1);
                if (resourceGeneration.getCoolDownTime() <= 0) {
                    resourceGeneration.setCoolDownTime(resourceGeneration.getConfig().getSpawnTime());
                    Item item = resourceGeneration.getConfig().getItem();
                    item.setCount(resourceGeneration.getConfig().getSpawnCount());
                    this.getGameWorld().dropItem(resourceGeneration.getVector3(), item);
                }
                EntityText entityText = this.resourceGenerationText.get(resourceGeneration);
                if (entityText == null) {
                    entityText = new EntityText(
                            Position.fromObject(resourceGeneration.getVector3(), this.getGameWorld()), "");
                    entityText.spawnToAll();
                    this.resourceGenerationText.put(resourceGeneration, entityText);
                }
                entityText.setNameTag(resourceGeneration.getConfig().getShowName()
                        .replace("%progressBar%",
                                Utils.getProgressBar(
                                        resourceGeneration.getConfig().getSpawnTime() - resourceGeneration.getCoolDownTime(),
                                        resourceGeneration.getConfig().getSpawnTime()
                                )
                        )
                );
            }
        }

        //胜利判断
        int count = 0;
        Team survivingTeam = Team.NULL;
        for (Team team : this.teamEntityEndCrystalMap.keySet()) {
            if ((this.isTeamCrystalSurviving(team) && !this.getPlayers(team).isEmpty()) || !this.getSurvivingPlayers(team).isEmpty()) {
                count++;
                survivingTeam = team;
            }
        }
        if (count <= 1) {
            this.setArenaStatus(ArenaStatus.VICTORY);
            this.victoryTeam = survivingTeam;
        }else if (this.gameTime <= 0) {
            //加时赛
            if (this.isOvertime()) {
                this.setArenaStatus(ArenaStatus.VICTORY);
                this.victoryTeam = survivingTeam;
            }else {
                this.isOvertime = true;
                this.gameTime = this.getSetOvertime();
                for (CrystalWarsEntityEndCrystal crystal : this.teamEntityEndCrystalMap.values()) {
                    if (!crystal.isClosed()) {
                        crystal.explode();
                    }
                }
            }
        }

        Watchdog.resetTime(this);
    }

    public void onUpdateVictory(int tick) {
        if (tick%20 != 0) {
            return;
        }

        if (this.getPlayerDataMap().isEmpty()) {
            this.gameEnd();
            return;
        }

        for (Map.Entry<Player, PlayerData> entry : this.playerDataMap.entrySet()) {
            if (this.victoryTeam != Team.NULL) {
                if (entry.getValue().getTeam() == this.victoryTeam &&
                        entry.getValue().getPlayerStatus() == PlayerData.PlayerStatus.SURVIVE) {
                    Utils.spawnFirework(entry.getKey());
                }
            }

            if (this.victoryTeam == Team.NULL) {
                entry.getKey().sendTip("§e胜利队伍: §f平局§e！");
            }else {
                entry.getKey().sendTip("§e胜利队伍: " + Utils.getShowTeam(this.victoryTeam) + "§e！");
            }

            LinkedList<String> list = new LinkedList<>();
            list.add(Utils.getSpace(list));
            if (this.victoryTeam == Team.NULL) {
                list.add("§f◎ §e胜利队伍: §f平局§e！");
            }else {
                list.add("§f◎ §e胜利队伍: " + Utils.getShowTeam(this.victoryTeam) + "§e！");
            }
            list.add(Utils.getSpace(list));
            ScoreboardUtil.getScoreboard().showScoreboard(entry.getKey(), CrystalWars.PLUGIN_NAME, list);
        }

        this.victoryTime--;
        if (this.victoryTime <= 0) {
            this.gameEnd();
        }

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
                    this,
                    team
            );
            entityEndCrystal.spawnToAll();
            CrystalWarsEntityEndCrystal oldCrystal = this.teamEntityEndCrystalMap.put(team, entityEndCrystal);
            if (oldCrystal != null) {
                oldCrystal.close();
            }
        }

        //生成商店
        for (Team team : Team.values()) {
            if (team == Team.NULL || this.getPlayers(team).isEmpty()) {
                continue;
            }
            Position shopPos = Position.fromObject(this.getTeamShop(team), this.getGameWorld());
            CrystalWarsEntityMerchant shop = new CrystalWarsEntityMerchant(
                    shopPos.getChunk(),
                    Entity.getDefaultNBT(shopPos),
                    team,
                    this.getSupply()
            );
            shop.spawnToAll();
            CrystalWarsEntityMerchant oldEntity = this.teamEntityMerchantMap.put(team, shop);
            if (oldEntity != null) {
                oldEntity.close();
            }
        }

        //游戏开始，重生所有玩家
        for (Player player : this.playerDataMap.keySet()) {
            this.playerRespawn(player);
        }

        this.setArenaStatus(ArenaStatus.GAME);
    }

    public void gameEnd() {
        ArenaStatus oldStatus = this.getArenaStatus();
        this.setArenaStatus(ArenaStatus.TASK_NEED_INITIALIZED);
        for (Player player : new HashSet<>(this.getPlayerDataMap().keySet())) {
            this.quitRoom(player);
        }

        for (CrystalWarsEntityEndCrystal crystal : this.teamEntityEndCrystalMap.values()) {
            crystal.close();
        }

        for (CrystalWarsEntityMerchant merchant : this.teamEntityMerchantMap.values()) {
            merchant.close();
        }

        for (EntityText text : this.resourceGenerationText.values()) {
            text.close();
        }

        if (oldStatus == ArenaStatus.GAME || oldStatus == ArenaStatus.VICTORY) {
            this.setArenaStatus(ArenaStatus.LEVEL_NOT_LOADED);
            if (CrystalWars.debug) {
                this.crystalWars.getLogger().info("§a游戏房间: " + this.getGameWorldName() + " 正在还原地图...");
            }
            Server.getInstance().unloadLevel(this.getGameWorld());
            File levelFile = new File(Server.getInstance().getFilePath() + "/worlds/" + this.getGameWorldName());
            File backup = new File(this.crystalWars.getWorldBackupPath() + this.getGameWorldName());
            if (!backup.exists()) {
                this.crystalWars.getLogger().error("§c游戏房间: " + this.getGameWorldName() + " 地图备份不存在！还原失败！");
                this.crystalWars.unloadArena(this.getGameWorldName());
            }
            CompletableFuture.runAsync(() -> {
                if (FileUtil.deleteFile(levelFile) && FileUtil.copyDir(backup, levelFile)) {
                    Server.getInstance().loadLevel(getGameWorldName());
                    this.gameWorld = Server.getInstance().getLevelByName(getGameWorldName());
                    setArenaStatus(ArenaStatus.TASK_NEED_INITIALIZED);
                    if (CrystalWars.debug) {
                        this.crystalWars.getLogger().info("§a游戏房间: " + getGameWorldName() + " 地图还原完成！");
                    }
                }else {
                    this.crystalWars.getLogger().error("§c游戏房间: " + getGameWorldName() + " 地图还原失败！请检查文件权限！");
                    Server.getInstance().getScheduler().scheduleTask(
                            this.crystalWars,
                            () -> this.crystalWars.unloadArena(getGameWorldName())
                    );
                }
            }, CrystalWars.EXECUTOR);
        }
    }

    /**
     * 分配玩家队伍
     */
    public void assignTeam() {
        ArrayList<Team> teams = new ArrayList<>(Arrays.asList(Team.values()));
        teams.remove(Team.NULL);

        int baseCount = Math.max(1, this.getPlayerCount() / teams.size());

        HashMap<Team, LinkedList<Player>> teamPlayers = new HashMap<>();
        for (Team team : Team.values()) {
            teamPlayers.put(team, new LinkedList<>());
        }

        for (Map.Entry<Player, PlayerData> entry : this.getPlayerDataMap().entrySet()) {
            teamPlayers.get(entry.getValue().getTeam()).add(entry.getKey());
        }

        //打乱顺序
        for (LinkedList<Player> list : teamPlayers.values()) {
            Collections.shuffle(list, CrystalWars.RANDOM);
        }

        while (true) {
            Player cache = null;

            if (!teamPlayers.get(Team.NULL).isEmpty()) {
                cache = teamPlayers.get(Team.NULL).poll();
            }else {
                for (Map.Entry<Team, LinkedList<Player>> entry : teamPlayers.entrySet()) {
                    if (entry.getKey() == Team.NULL) {
                        continue;
                    }
                    if (entry.getValue().size() > baseCount + 1) {
                        cache = entry.getValue().poll();
                        break;
                    }
                }
            }

            if (cache == null) {
                break;
            }

            for (Map.Entry<Team, LinkedList<Player>> entry : teamPlayers.entrySet()) {
                if (entry.getKey() == Team.NULL) {
                    continue;
                }
                if (entry.getValue().size() < baseCount) {
                    entry.getValue().add(cache);
                    cache = null;
                    break;
                }
            }

            if (cache != null) {
                teamPlayers.get(teams.get(CrystalWars.RANDOM.nextInt(teams.size()))).add(cache);
            }
        }

        for (Map.Entry<Team, LinkedList<Player>> entry : teamPlayers.entrySet()) {
            if (entry.getKey() == Team.NULL) {
                continue;
            }
            for (Player player : entry.getValue()) {
                this.getPlayerData(player).setTeam(entry.getKey());
            }
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
        if (this.isTeamCrystalSurviving(playerData.getTeam())) {
            playerData.setPlayerStatus(PlayerData.PlayerStatus.WAIT_SPAWN);
        }else {
            playerData.setPlayerStatus(PlayerData.PlayerStatus.DEATH);
        }
        playerData.setDeathCount(playerData.getDeathCount() + 1);
        //TODO 可能需要调整
        playerData.setWaitSpawnTime(5);
        player.getLevel().addSound(player, Sound.GAME_PLAYER_HURT);
    }

    /**
     * 玩家重生
     *
     * @param player 玩家
     */
    public void playerRespawn(@NotNull Player player) {
        PlayerData playerData = this.getPlayerData(player);
        if (playerData.getTeam() == Team.NULL) {
            return;
        }
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();

        player.getInventory().setHelmet(Utils.getTeamColorItem(Item.get(Item.LEATHER_CAP), playerData.getTeam()));
        player.getInventory().setChestplate(Utils.getTeamColorItem(Item.get(Item.LEATHER_TUNIC), playerData.getTeam()));
        player.getInventory().addItem(Item.get(Item.WOODEN_SWORD));

        player.teleport(this.getTeamSpawn(playerData.getTeam()));
        player.setGamemode(Player.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
        playerData.setPlayerStatus(PlayerData.PlayerStatus.SURVIVE);
    }

    public boolean isPlaying(@NotNull Player player) {
        return this.playerDataMap.containsKey(player);
    }

    public int getPlayerCount() {
        return this.getPlayerDataMap().size();
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

    public boolean isTeamCrystalSurviving(@NotNull Team team) {
        if (this.teamEntityEndCrystalMap.containsKey(team)) {
            return !this.teamEntityEndCrystalMap.get(team).isClosed();
        }
        return false;
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
        return Objects.hash(this.getGameWorldName());
    }
}
