package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.entity.EntityText;
import cn.lanink.crystalwars.items.ItemManager;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.crystalwars.utils.exception.ArenaLoadException;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author LT_Name
 */
public class ArenaSet extends ArenaConfig {

    protected CrystalWars crystalWars = CrystalWars.getInstance();

    @Getter
    protected final String worldName;
    @Getter
    protected final Level world;
    @Getter
    protected final Player player;

    private final int beforeGameMode;
    private final Map<Integer, Item> playerInventory;
    private final Item offHandItem;

    @Getter
    private int setRoomSchedule = 100;
    @Getter
    private int backRoomSchedule = 100;
    @Getter
    private int nextRoomSchedule = 150;
    private int particleEffectTick = 0;


    private EntityText waitSpawnText;
    private final HashMap<Team, EntityText> spawnTextMap = new HashMap<>();
    private final HashMap<Team, EntityText> crystalTextMap = new HashMap<>();
    private final HashMap<Team, EntityText> shopTextMap = new HashMap<>();
    private final HashMap<ResourceGeneration, EntityText> resourceGenerationTextMap = new HashMap<>();

    private boolean isExit = false;

    public ArenaSet(@NotNull String worldName, @NotNull Config config, @NotNull Player player) throws ArenaLoadException {
        super(config, true);

        this.worldName = worldName;
        this.player = player;

        this.world = Server.getInstance().getLevelByName(this.worldName);

        this.beforeGameMode = player.getGamemode();
        this.playerInventory = player.getInventory().getContents();
        this.offHandItem = player.getOffhandInventory().getItem(0);
        player.setGamemode(Player.CREATIVE);
        player.getInventory().clearAll();
        player.getUIInventory().clearAll();

        Server.getInstance().getScheduler().scheduleRepeatingTask(this.crystalWars, new Task() {
            @Override
            public void onRun(int i) {
                if (!onUpdate(i)) {
                    this.cancel();
                }
            }
        }, 1);
    }

    public boolean onUpdate(int tick) {
        if (this.isExit ||
                !this.player.isOnline() ||
                this.player.getLevel() != this.world ||
                !this.crystalWars.getArenaSetMap().containsKey(this.player)) {
            this.exit();
            return false;
        }
        if (tick%10 == 0) {
            if (this.setRoomSchedule > 100) {
                this.player.getInventory().setItem(0, ItemManager.get(this.player, 11001));
            }else {
                this.player.getInventory().clear(0);
            }
            boolean canNext = false;
            Item item;
            switch (this.setRoomSchedule) {
                case 100: //设置游戏模式
                    this.backRoomSchedule = 100;
                    this.nextRoomSchedule = 150;

                    this.player.sendTitle("", "设置游戏模式", 0, 30, 10);

                    item = ItemManager.get(this.player, 11004);
                    item.setCustomName(this.crystalWars.getLanguage().translateString("plugin_arenaSet_item_SetGameMode"));
                    this.player.getInventory().setItem(4, item);

                    if (CrystalWars.getARENA_CLASS().containsKey(this.getConfig().getString("gameMode"))) {
                        canNext = true;
                    }
                    break;
                case 150: //设置等待出生点
                    this.backRoomSchedule = 100;
                    this.nextRoomSchedule = 200;

                    this.player.sendTitle("", "设置等待出生点", 0, 30, 10);

                    item = ItemManager.get(this.player, 11005);
                    item.setCustomName(this.crystalWars.getLanguage().translateString("plugin_arenaSet_item_SetWaitPosition"));
                    this.player.getInventory().setItem(4, item);

                    if (this.isSet(this.getWaitSpawn())) {
                        canNext = true;
                    }
                    break;
                case 200: //设置各队出生点
                    this.backRoomSchedule = 150;
                    this.nextRoomSchedule = 250;

                    this.player.sendTitle("", "设置各队出生点", 0, 30, 10);

                    int indexSpawn = 2;
                    for (Team team : Team.values()) {
                        if (team == Team.NULL) {
                            continue;
                        }
                        item = Utils.getTeamColorItem(ItemManager.get(this.player, 11006), team);
                        item.getNamedTag().putString("CrystalWarsTeam", team.name());
                        item.setCustomName(this.crystalWars.getLanguage().translateString("plugin_arenaSet_item_SetTeamSpawnPosition", Utils.getShowTeam(team)));
                        this.player.getInventory().setItem(indexSpawn, item);
                        indexSpawn++;
                    }

                    int okCountSpawn = 0;
                    for (Team team : Team.values()) {
                        if (team == Team.NULL) {
                            continue;
                        }
                        if (this.isSet(this.getTeamSpawn(team))) {
                            okCountSpawn++;
                        }
                    }
                    if (okCountSpawn >= Team.values().length - 1) {
                        canNext = true;
                    }
                    break;
                case 250: //设置各队水晶位置
                    this.backRoomSchedule = 200;
                    this.nextRoomSchedule = 300;

                    this.player.sendTitle("", "设置各队水晶位置", 0, 30, 10);

                    int indexCrystal = 2;
                    for (Team team : Team.values()) {
                        if (team == Team.NULL) {
                            continue;
                        }
                        item = Utils.getTeamColorItem(ItemManager.get(this.player, 11006), team);
                        item.getNamedTag().putString("CrystalWarsTeam", team.name());
                        item.setCustomName(this.crystalWars.getLanguage().translateString("plugin_arenaSet_item_SetCrystalPosition", Utils.getShowTeam(team)));
                        this.player.getInventory().setItem(indexCrystal, item);
                        indexCrystal++;
                    }

                    int okCountCrystal = 0;
                    for (Team team : Team.values()) {
                        if (team == Team.NULL) {
                            continue;
                        }
                        if (this.isSet(this.getTeamCrystal(team))) {
                            okCountCrystal++;
                        }
                    }
                    if (okCountCrystal >= Team.values().length - 1) {
                        canNext = true;
                    }
                    break;
                case 300: //设置各队商店位置
                    this.backRoomSchedule = 250;
                    this.nextRoomSchedule = 350;

                    this.player.sendTitle("", "设置各队商店位置", 0, 30, 10);

                    int indexShop = 2;
                    for (Team team : Team.values()) {
                        if (team == Team.NULL) {
                            continue;
                        }
                        item = Utils.getTeamColorItem(ItemManager.get(this.player, 11006), team);
                        item.getNamedTag().putString("CrystalWarsTeam", team.name());
                        item.setCustomName("设置" + Utils.getShowTeam(team) + "商店位置");
                        this.player.getInventory().setItem(indexShop, item);
                        indexShop++;
                    }

                    int okCountShop = 0;
                    for (Team team : Team.values()) {
                        if (team == Team.NULL) {
                            continue;
                        }
                        if (this.isSet(this.getTeamShop(team))) {
                            okCountShop++;
                        }
                    }
                    if (okCountShop >= Team.values().length - 1) {
                        canNext = true;
                    }
                    break;
                case 350: //设置资源点
                    this.backRoomSchedule = 300;
                    this.nextRoomSchedule = 400;

                    this.player.sendTitle("", "设置资源生成点", 0, 30, 10);

                    this.player.getInventory().setItem(3, ItemManager.get(this.player, 11007));
                    this.player.getInventory().setItem(5, ItemManager.get(this.player, 11008));

                    if (!this.getResourceGenerations().isEmpty()) {
                        canNext = true;
                    }
                    break;
                case 400: //设置其他参数
                    this.backRoomSchedule = 350;
                    this.nextRoomSchedule = 1000;

                    this.player.sendTitle("", "设置其他参数", 0, 30, 10);

                    item = ItemManager.get(this.player, 11004);
                    item.setCustomName("设置其他参数");
                    this.player.getInventory().setItem(4, item);

                    if (this.getMinPlayers() >= 2 &&
                            this.getMaxPlayers() >= this.getMinPlayers() &&
                            this.getSetWaitTime() > 0 &&
                            this.getSetGameTime() > 0 &&
                            this.getSetOvertime() > 0 &&
                            this.getSetVictoryTime() > 0 &&
                            this.getSupply().getSupplyConfig() != null) {
                        canNext = true;
                    }
                    break;
            }
            if (canNext) {
                if (this.nextRoomSchedule == 1000) {
                    this.player.getInventory().setItem(8, ItemManager.get(this.player, 11003));
                }else {
                    this.player.getInventory().setItem(8, ItemManager.get(this.player, 11002));
                }
            }else {
                this.player.getInventory().clear(8);
            }

            //显示已设置的点
            this.particleEffectTick++;
            if (this.particleEffectTick >= 10) {
                this.particleEffectTick = 0;
            }

            this.particleEffect(this.getWaitSpawn());
            for (Vector3 vector3 : this.teamSpawn.values()) {
                this.particleEffect(vector3);
            }
            for (Vector3 vector3 : this.teamCrystal.values()) {
                this.particleEffect(vector3);
            }
            for (Vector3 vector3 : this.teamShop.values()) {
                this.particleEffect(vector3);
            }

            Position waitSpawn = Position.fromObject(this.getWaitSpawn(), this.world);
            if (this.waitSpawnText == null || this.waitSpawnText.isClosed()) {
                this.waitSpawnText = new EntityText(waitSpawn, "");
                this.waitSpawnText.spawnToAll();
            }
            this.waitSpawnText.setPosition(waitSpawn);
            this.waitSpawnText.setNameTag("等待出生点");
            for (Team team : Team.values()) {
                if (team == Team.NULL) {
                    continue;
                }
                Position position = Position.fromObject(this.getTeamSpawn(team), this.world);
                EntityText text = this.spawnTextMap.get(team);
                if (text == null || text.isClosed()) {
                    text = new EntityText(position, "");
                    text.spawnToAll();
                    this.spawnTextMap.put(team, text);
                }
                text.setPosition(position);
                text.setNameTag(Utils.getShowTeam(team) + "出生点");

                position = Position.fromObject(this.getTeamCrystal(team), this.world);
                text = this.crystalTextMap.get(team);
                if (text == null || text.isClosed()) {
                    text = new EntityText(position, "");
                    text.spawnToAll();
                    this.crystalTextMap.put(team, text);
                }
                text.setPosition(position);
                text.setNameTag(Utils.getShowTeam(team) + "水晶位置");

                position = Position.fromObject(this.getTeamShop(team), this.world);
                text = this.shopTextMap.get(team);
                if (text == null || text.isClosed()) {
                    text = new EntityText(position, "");
                    text.spawnToAll();
                    this.shopTextMap.put(team, text);
                }
                text.setPosition(position);
                text.setNameTag(Utils.getShowTeam(team) + "商店位置");
            }
            Iterator<Map.Entry<ResourceGeneration, EntityText>> iterator = this.resourceGenerationTextMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ResourceGeneration, EntityText> next = iterator.next();
                if (!this.getResourceGenerations().contains(next.getKey())) {
                    next.getValue().close();
                    iterator.remove();
                }
            }
            for (ResourceGeneration generation : this.getResourceGenerations()) {
                Position position = Position.fromObject(generation.getVector3(), this.world);
                EntityText entityText = this.resourceGenerationTextMap.get(generation);
                if (entityText == null) {
                    entityText = new EntityText(position, "");
                    entityText.spawnToAll();
                    this.resourceGenerationTextMap.put(generation, entityText);
                }
                entityText.setPosition(position);
                entityText.setNameTag("资源生成点\n物品生成配置: " + generation.getConfig().getName());
            }
        }

        return true;
    }

    public boolean isSet(@NotNull Vector3 vector3) {
        return vector3.getFloorX() != 0 ||
                (vector3.getFloorY() != 0 && vector3.getFloorY() != -100) ||
                vector3.getFloorZ() != 0;
    }

    public void setTeamSpawn(@NotNull Team team, @NotNull Vector3 vector3) {
        super.teamSpawn.put(team, vector3.clone());
    }

    public void setTeamCrystal(@NotNull Team team, @NotNull Vector3 vector3) {
        super.teamCrystal.put(team, vector3.clone());
    }

    public void setTeamShop(@NotNull Team team, @NotNull Vector3 vector3) {
        super.teamShop.put(team, vector3.clone());
    }

    public void setRoomSchedule(int setRoomSchedule) {
        this.setRoomSchedule = setRoomSchedule;
        this.player.getInventory().clearAll();
    }

    /**
     * 退出设置
     */
    public void exit() {
        this.isExit = true;

        this.crystalWars.getArenaSetMap().remove(this.player);

        this.player.setGamemode(this.beforeGameMode);
        this.player.getInventory().setContents(this.playerInventory);
        this.player.getOffhandInventory().setItem(0, this.offHandItem);

        this.waitSpawnText.close();
        for (EntityText entityText : this.spawnTextMap.values()) {
            entityText.close();
        }
        for (EntityText entityText : this.crystalTextMap.values()) {
            entityText.close();
        }
        for (EntityText entityText : this.shopTextMap.values()) {
            entityText.close();
        }
        for (EntityText entityText : this.resourceGenerationTextMap.values()) {
            entityText.close();
        }
    }

    private void particleEffect(Vector3 center) {
        if (this.particleEffectTick%5 != 0) {
            return;
        }
        Server.getInstance().getScheduler().scheduleAsyncTask(this.crystalWars, new AsyncTask() {
            @Override
            public void onRun() {
                Vector3 v = center.clone();
                v.x += 0.8;
                double x = v.x - center.x;
                double z = v.z - center.z;
                for (int i = 0; i < 360; i += 10) {
                    world.addParticleEffect(
                            new Vector3(
                                    x * Math.cos(i) - z * Math.sin(i) + center.x,
                                    center.y + (i * 0.0055),
                                    x * Math.sin(i) + z * Math.cos(i) + center.z),
                            ParticleEffect.REDSTONE_TORCH_DUST);
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
