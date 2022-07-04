package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.entity.EntityText;
import cn.lanink.crystalwars.event.CrystalWarsArenaPlayerJoinEvent;
import cn.lanink.crystalwars.event.CrystalWarsArenaPlayerQuitEvent;
import cn.lanink.crystalwars.items.ItemManager;
import cn.lanink.crystalwars.player.PlayerSettingDataManager;
import cn.lanink.crystalwars.theme.Theme;
import cn.lanink.crystalwars.theme.ThemeManager;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.crystalwars.utils.Watchdog;
import cn.lanink.crystalwars.utils.exception.ArenaLoadException;
import cn.lanink.gamecore.room.IRoom;
import cn.lanink.gamecore.scoreboard.ScoreboardUtil;
import cn.lanink.gamecore.utils.FileUtil;
import cn.lanink.gamecore.utils.Language;
import cn.lanink.gamecore.utils.Tips;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
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

    @Getter
    private int waitTime;
    @Getter
    private int gameTime;
    @Getter
    private int victoryTime;

    @Getter
    private final Map<Player, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    private final HashMap<Team, CrystalWarsEntityEndCrystal> teamEntityEndCrystalMap = new HashMap<>();
    private final HashMap<Team, CrystalWarsEntityMerchant> teamEntityMerchantMap = new HashMap<>();
    private final HashMap<ResourceGeneration, EntityText> resourceGenerationText = new HashMap<>();

    @Getter
    private Team victoryTeam = Team.NULL;

    @Getter
    private boolean isOvertime = false;

    @Getter
    private final HashSet<Vector3> playerPlaceBlocks = new HashSet<>();

    private final HashMap<Player, Integer> skinNumber = new HashMap<>(); //玩家使用皮肤编号，用于防止重复使用

    private final HashMap<Player, Skin> skinCache = new HashMap<>(); //缓存玩家皮肤，用于退出房间时还原


    public BaseArena(@NotNull String gameWorldName, @NotNull Config config) throws ArenaLoadException {
        super(config);
        this.gameWorldName = gameWorldName;
        Language language = CrystalWars.getInstance().getLang();
        if (!Server.getInstance().loadLevel(this.getGameWorldName())) {
            throw new ArenaLoadException(language.translateString("arena_loadFailed",this.getGameWorldName()));
        }
        this.gameWorld = Server.getInstance().getLevelByName(this.getGameWorldName());

        //备份游戏世界
        File backup = new File(this.crystalWars.getWorldBackupPath() + this.getGameWorldName());
        if (!backup.exists()) {
            this.crystalWars.getLogger().info(language.translateString("arena_backUpNotFound",this.getGameWorldName()));
            Server.getInstance().unloadLevel(this.gameWorld);
            if (FileUtil.copyDir(this.crystalWars.getServerWorldPath() + this.getGameWorldName(), backup)) {
                Server.getInstance().loadLevel(this.getGameWorldName());
                this.gameWorld = Server.getInstance().getLevelByName(this.getGameWorldName());
            }else {
                throw new ArenaLoadException(language.translateString("arena_backupFailed",this.getGameWorldName()));
            }
            this.crystalWars.getLogger().info(language.translateString("arena_backupSucceeded",this.getGameWorldName()));
        }
        this.initLevel();

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
            throw new RuntimeException(CrystalWars.getInstance().getLang().translateString("arena_setGamemodeRepeatedly"));
        }
    }

    public List<String> getListeners() {
        ArrayList<String> list = new ArrayList<>();

        list.add("DefaultGameListener");

        return list;
    }

    /**
     * 初始化房间数据
     */
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

        this.playerPlaceBlocks.clear();
    }

    /**
     * 初始化世界规则
     */
    private void initLevel() {
        this.getGameWorld().setThundering(false);
        this.getGameWorld().setRaining(false);
        this.getGameWorld().getGameRules().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
    }

    public boolean canJoin() {
        return (this.getArenaStatus() == ArenaStatus.TASK_NEED_INITIALIZED || this.getArenaStatus() == ArenaStatus.WAIT) &&
                this.getPlayerCount() < this.getMaxPlayers();
    }

    public boolean joinRoom(@NotNull Player player) {
        if (!this.canJoin() || this.getPlayerDataMap().containsKey(player)) {
            return false;
        }

        Server.getInstance().getPluginManager().callEvent(new CrystalWarsArenaPlayerJoinEvent(this, player));

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
        this.setRandomSkin(player);

        player.setHealth(player.getMaxHealth());
        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();

        player.getInventory().setItem(8, ItemManager.get(player, 10000));
        //等待游戏开始时使用冒险模式
        player.setGamemode(Player.ADVENTURE);

        player.teleport(Position.fromObject(this.getWaitSpawn(), this.getGameWorld()));
        return true;
    }

    public boolean quitRoom(@NotNull Player player) {
        if (!this.getPlayerDataMap().containsKey(player)) {
            return false;
        }

        Server.getInstance().getPluginManager().callEvent(new CrystalWarsArenaPlayerQuitEvent(this, player));

        if (this.crystalWars.isHasTips()) {
            Tips.removeTipsConfig(this.getGameWorldName(), player);
        }

        PlayerData playerData = this.getPlayerDataMap().remove(player);
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();
        playerData.restoreBeforePlayerData();
        this.restorePlayerSkin(player);
        ScoreboardUtil.getScoreboard().closeScoreboard(player);
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
            Theme theme = ThemeManager.getTheme(PlayerSettingDataManager.getData(entry.getKey()).getTheme());
            ArrayList<String> list = new ArrayList<>();
            for (String string : theme.getScoreboardLineWait(this, entry.getKey())) {
                list.add(string.replace("{time}", Utils.formatCountdown(this.waitTime)));
            }
            ScoreboardUtil.getScoreboard().showScoreboard(
                    entry.getKey(),
                    theme.getScoreboardTitleWait(this, entry.getKey()),
                    list
            );
        }
        Watchdog.resetTime(this);
    }

    public void onUpdateGame(int tick) {
        if (tick%20 != 0) {
            return;
        }

        this.gameTime--;

        for (Map.Entry<Player, PlayerData> entry : this.getPlayerDataMap().entrySet()) {
            //无敌时间计算
            if (entry.getValue().getPlayerInvincibleTime() > 0) {
                entry.getValue().setPlayerInvincibleTime(entry.getValue().getPlayerInvincibleTime() - 1);
            }
            //玩家复活
            if (entry.getValue().getPlayerStatus() == PlayerData.PlayerStatus.WAIT_SPAWN) {
                if (this.isTeamCrystalSurviving(entry.getValue().getTeam())) {
                    int waitSpawnTime = entry.getValue().getWaitSpawnTime() - 1;
                    entry.getValue().setWaitSpawnTime(waitSpawnTime);
                    Language language = CrystalWars.getInstance().getLang();
                    if (waitSpawnTime <= 0) {
                        //清掉之前的内容
                        entry.getKey().sendTitle(language.translateString("title_respawn"), "", 5, 15, 10);
                        this.playerRespawn(entry.getKey());
                    }else {
                        entry.getKey().sendTitle(language.translateString("title_wasted"), language.translateString("title_wasted_sub", waitSpawnTime), 0, 30, 10);
                    }
                }else {
                    entry.getValue().setPlayerStatus(PlayerData.PlayerStatus.DEATH);
                }
            }

            //计分板
            Theme theme = ThemeManager.getTheme(PlayerSettingDataManager.getData(entry.getKey()).getTheme());
            ArrayList<String> list = new ArrayList<>();
            for (String string : theme.getScoreboardLineGame(this, entry.getKey())) {
                list.add(string.replace("{time}", Utils.formatCountdown(this.gameTime)));
            }
            ScoreboardUtil.getScoreboard().showScoreboard(
                    entry.getKey(),
                    theme.getScoreboardTitleGame(this, entry.getKey()),
                    list
            );
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
                if (!"".equals(resourceGeneration.getConfig().getShowName().trim())) {
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
        }

        //胜利判断
        int count = 0;
        Team survivingTeam = Team.NULL;
        for (Team team : this.teamEntityEndCrystalMap.keySet()) {
            //队伍中的玩家都离开游戏后炸掉水晶
            if (this.getPlayers(team).isEmpty() && this.isTeamCrystalSurviving(team)) {
                this.teamEntityEndCrystalMap.get(team).explode();
                continue;
            }
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
                this.victoryTeam = Team.NULL;
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

            Theme theme = ThemeManager.getTheme(PlayerSettingDataManager.getData(entry.getKey()).getTheme());
            ScoreboardUtil.getScoreboard().showScoreboard(
                    entry.getKey(),
                    theme.getScoreboardTitleVictory(this, entry.getKey()),
                    theme.getScoreboardLineVictory(this, entry.getKey())
            );
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
        ArenaTickTask.removeArena(this);

        LinkedList<Player> victoryPlayers = new LinkedList<>();
        LinkedList<Player> defeatPlayers = new LinkedList<>();
        for (Map.Entry<Player, PlayerData> entry : this.getPlayerDataMap().entrySet()) {
            if (entry.getValue().getTeam() == this.victoryTeam) {
                victoryPlayers.add(entry.getKey());
            }else if (entry.getValue().getTeam() != Team.NULL) {
                defeatPlayers.add(entry.getKey());
            }
        }
        Server.getInstance().getScheduler().scheduleDelayedTask(this.crystalWars, () -> {
            if (!victoryPlayers.isEmpty() && !this.crystalWars.getVictoryCmd().isEmpty()) {
                for (Player player : victoryPlayers) {
                    Utils.executeCommand(player, this.crystalWars.getVictoryCmd());
                }
            }
            if (!defeatPlayers.isEmpty() && !this.crystalWars.getDefeatCmd().isEmpty()) {
                for (Player player : defeatPlayers) {
                    Utils.executeCommand(player, this.crystalWars.getDefeatCmd());
                }
            }
        }, 10);

        Server.getInstance().getScheduler().scheduleDelayedTask(this.crystalWars, () -> {
            if (!victoryPlayers.isEmpty() && !this.crystalWars.getVictoryCmd().isEmpty()) {
                for (Player player : victoryPlayers) {
                    Utils.executeCommand(player, this.crystalWars.getVictoryCmd());
                }
            }
            if (!defeatPlayers.isEmpty() && !this.crystalWars.getDefeatCmd().isEmpty()) {
                for (Player player : defeatPlayers) {
                    Utils.executeCommand(player, this.crystalWars.getDefeatCmd());
                }
            }
        }, 10);

        for (Player player : new HashSet<>(this.getPlayerDataMap().keySet())) {
            this.quitRoom(player);
        }
        for (Player player : this.getGameWorld().getPlayers().values()) {
            //不要触发传送事件，防止某些弱智操作阻止我们！
            player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn(), null);
        }
        //因为某些原因无法正常传送走玩家，就全部踹出服务器！
        for (Player player : this.getGameWorld().getPlayers().values()) {
            player.kick("Teleport error!");
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

        this.initData();

        if (oldStatus == ArenaStatus.GAME || oldStatus == ArenaStatus.VICTORY) {
            Language language = CrystalWars.getInstance().getLang();
            this.setArenaStatus(ArenaStatus.LEVEL_NOT_LOADED);
            if (CrystalWars.debug) {
                this.crystalWars.getLogger().info(language.translateString("tips_levelRecovery_start", this.getGameWorldName()));
            }
            Server.getInstance().unloadLevel(this.getGameWorld(), true);
            File levelFile = new File(Server.getInstance().getFilePath() + "/worlds/" + this.getGameWorldName());
            File backup = new File(this.crystalWars.getWorldBackupPath() + this.getGameWorldName());
            if (!backup.exists()) {
                this.crystalWars.getLogger().error(language.translateString("tips_levelRecovery_backupNotFound", this.getGameWorldName()));
                this.crystalWars.unloadArena(this.getGameWorldName());
            }
            CompletableFuture.runAsync(() -> {
                if (FileUtil.deleteFile(levelFile) && FileUtil.copyDir(backup, levelFile)) {
                    Server.getInstance().loadLevel(getGameWorldName());
                    this.gameWorld = Server.getInstance().getLevelByName(getGameWorldName());
                    this.initLevel();
                    setArenaStatus(ArenaStatus.TASK_NEED_INITIALIZED);
                    if (CrystalWars.debug) {
                        this.crystalWars.getLogger().info(language.translateString("tips_levelRecovery_backupSuccess", this.getGameWorldName()));
                    }
                }else {
                    this.crystalWars.getLogger().error(language.translateString("tips_levelRecovery_backupFailed", this.getGameWorldName()));
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
        ArrayList<Team> canUseTeams = this.getCanUseTeams();

        int baseCount = Math.max(1, this.getPlayerCount() / canUseTeams.size());

        HashMap<Team, LinkedList<Player>> teamPlayers = new HashMap<>();
        teamPlayers.put(Team.NULL, new LinkedList<>());
        for (Team team : canUseTeams) {
            teamPlayers.put(team, new LinkedList<>());
        }

        for (Map.Entry<Player, PlayerData> entry : this.getPlayerDataMap().entrySet()) {
            Team team = entry.getValue().getTeam();
            //如果玩家在未启用的队伍，则以无队伍来处理
            if (!teamPlayers.containsKey(team)) {
                team = Team.NULL;
            }
            teamPlayers.get(team).add(entry.getKey());
        }

        //打乱顺序 让队伍分配更随机一些
        for (LinkedList<Player> list : teamPlayers.values()) {
            Collections.shuffle(list, CrystalWars.RANDOM);
        }

        //队伍人数均衡
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
                teamPlayers.get(canUseTeams.get(CrystalWars.RANDOM.nextInt(canUseTeams.size()))).add(cache);
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
     * @return 可以使用的队伍
     */
    private ArrayList<Team> getCanUseTeams() {
        ArrayList<Team> canUseTeams = new ArrayList<>(Arrays.asList(Team.values()));
        canUseTeams.remove(Team.NULL);
        while (canUseTeams.size() > this.getMaxTeamCount()) {
            //如果限制数量，就只用前面的队伍，方便服主配置
            canUseTeams.remove(canUseTeams.get(canUseTeams.size() - 1));
        }
        return canUseTeams;
    }

    /**
     * 玩家死亡
     *
     * @param player 玩家
     */
    public void playerDeath(@NotNull Player player) {
        player.sendTitle("§c死亡");
        PlayerData playerData = this.getPlayerData(player);

        player.getInventory().clearAll();
        player.getUIInventory().clearAll();

        player.setGamemode(Player.SPECTATOR);
        player.teleport(this.getTeamSpawn(playerData.getTeam()).add(0, 2, 0));

        if (this.isTeamCrystalSurviving(playerData.getTeam())) {
            playerData.setPlayerStatus(PlayerData.PlayerStatus.WAIT_SPAWN);
        }else {
            playerData.setPlayerStatus(PlayerData.PlayerStatus.DEATH);
            player.getInventory().setItem(8, ItemManager.get(player, 10000));
        }
        playerData.addDeathCount();
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

        CompoundTag tag;

        Item cap = Item.get(Item.LEATHER_CAP);
        tag = cap.hasCompoundTag() ? cap.getNamedTag() : new CompoundTag();
        tag.putByte("Unbreakable", 1);
        tag.putBoolean("cannotTakeItOff", true);
        cap.setNamedTag(tag);
        player.getInventory().setHelmet(Utils.getTeamColorItem(cap, playerData.getTeam()));

        Item tunic = Item.get(Item.LEATHER_TUNIC);
        tag = tunic.hasCompoundTag() ? tunic.getNamedTag() : new CompoundTag();
        tag.putByte("Unbreakable", 1);
        tag.putBoolean("cannotTakeItOff", true);
        tunic.setNamedTag(tag);
        player.getInventory().setChestplate(Utils.getTeamColorItem(tunic, playerData.getTeam()));

        Item sword = Item.get(Item.WOODEN_SWORD);
        tag = sword.hasCompoundTag() ? sword.getNamedTag() : new CompoundTag();
        tag.putByte("Unbreakable", 1);
        sword.setNamedTag(tag);
        player.getInventory().addItem(sword);

        player.teleport(this.getTeamSpawn(playerData.getTeam()));
        player.setGamemode(Player.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        player.getFoodData().setLevel(player.getFoodData().getMaxLevel());
        playerData.setPlayerStatus(PlayerData.PlayerStatus.SURVIVE);
        playerData.setPlayerInvincibleTime(5); //复活5秒无敌
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

    public CrystalWarsEntityEndCrystal getTeamEntityEndCrystal(Team team) {
        return this.teamEntityEndCrystalMap.get(team);
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

    /**
     * 设置玩家随机皮肤
     *
     * @param player 玩家
     */
    private void setRandomSkin(@NotNull Player player) {
        for (Map.Entry<Integer, Skin> entry : this.crystalWars.getSkins().entrySet()) {
            if (!this.skinNumber.containsValue(entry.getKey())) {
                this.skinCache.put(player, player.getSkin());
                this.skinNumber.put(player, entry.getKey());
                Utils.setHumanSkin(player, entry.getValue());
                return;
            }
        }
    }

    /**
     * 还原玩家皮肤
     *
     * @param player 玩家
     */
    private void restorePlayerSkin(@NotNull Player player) {
        if (this.skinCache.containsKey(player)) {
            Utils.setHumanSkin(player, this.skinCache.get(player));
            this.skinCache.remove(player);
        }
        this.skinNumber.remove(player);
    }
}
