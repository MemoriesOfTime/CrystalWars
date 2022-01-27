package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.items.generation.ItemGenerationConfig;
import cn.lanink.crystalwars.items.generation.ItemGenerationConfigManager;
import cn.lanink.crystalwars.supplier.Supply;
import cn.lanink.crystalwars.supplier.config.SupplyConfigManager;
import cn.lanink.crystalwars.utils.ISaveConfig;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.crystalwars.utils.exception.ArenaLoadException;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lt_name
 */
@AllArgsConstructor
@Getter
public class ArenaConfig implements ISaveConfig {

    private CrystalWars crystalWars = CrystalWars.getInstance();

    private Config config;

    private boolean isSet;

    //游戏规则
    /**
     * 是否允许队友伤害
     */
    private boolean allowTeammateDamage;
    /**
     * 水晶默认血量
     */
    private int defaultEndCrystalHealth;

    //时间参数
    private int setWaitTime;
    private int setGameTime;
    private int setOvertime;
    private int setVictoryTime;

    /**
     * 最大团队数量
     * 最小不能小于2 最大受限于Team类
     */
    private int maxTeamCount;

    private int minPlayers;
    private int maxPlayers;

    private Vector3 waitSpawn;

    protected final Map<Team, Vector3> teamSpawn = new HashMap<>();
    protected final Map<Team, Vector3> teamCrystal = new HashMap<>();
    protected final Map<Team, Vector3> teamShop = new HashMap<>();

    private final ArrayList<ResourceGeneration> resourceGenerations = new ArrayList<>();

    private Supply supply;

    public ArenaConfig(@NotNull Config config) throws ArenaLoadException {
        this(config, false);
    }

    public ArenaConfig(@NotNull Config config, boolean isSet) throws ArenaLoadException {
        try {
            this.config = config;
            this.isSet = isSet;

            this.allowTeammateDamage = config.getBoolean("allowTeammateDamage", true);
            this.defaultEndCrystalHealth = config.getInt("defaultEndCrystalHealth", 100);

            this.setWaitTime = config.getInt("waitTime", this.isSet ? 0 : 60);
            this.setGameTime = config.getInt("gameTime", this.isSet ? 0 : 600);
            this.setOvertime = config.getInt("overtime", this.isSet ? 0 : 180);
            this.setVictoryTime = config.getInt("victoryTime", this.isSet ? 0 : 10);

            this.maxTeamCount = Math.min(Team.values().length - 1, Math.max(2, config.getInt("maxTeamCount", Team.values().length - 1)));

            this.minPlayers = config.getInt("minPlayers", this.isSet ? 0 : 2);
            this.maxPlayers = config.getInt("maxPlayers", this.isSet ? 0 : 16);

            this.waitSpawn = Utils.stringToVector3(config.getString("waitSpawn", "0:-100:0"));

            Map<String, Map<String, Double>> spawn = config.get("spawn", new HashMap<>());
            for (Map.Entry<String, Map<String, Double>> entry : spawn.entrySet()) {
                this.teamSpawn.put(Team.valueOf(entry.getKey().toUpperCase()),
                        Utils.mapToVector3(entry.getValue()));
            }

            Map<String, Map<String, Double>> crystal = config.get("crystal", new HashMap<>());
            for (Map.Entry<String, Map<String, Double>> entry : crystal.entrySet()) {
                this.teamCrystal.put(Team.valueOf(entry.getKey().toUpperCase()),
                        Utils.mapToVector3(entry.getValue()));
            }

            Map<String, Map<String, Double>> shop = config.get("shop", new HashMap<>());
            for (Map.Entry<String, Map<String, Double>> entry : shop.entrySet()) {
                this.teamShop.put(Team.valueOf(entry.getKey().toUpperCase()),
                        Utils.mapToVector3(entry.getValue()));
            }

            for (Map map : config.getMapList("resourceGenerations")) {
                try {
                    String name = (String) map.get("itemGenerationConfigName");
                    ItemGenerationConfig generationConfig = ItemGenerationConfigManager.getItemGenerationConfig(name);
                    if (generationConfig == null) {
                        throw new RuntimeException(this.crystalWars.getLanguage().translateString("plugin_arenaConfig_ResourceNotExist", name));
                    }
                    this.resourceGenerations.add(
                            new ResourceGeneration(generationConfig, Utils.mapToVector3(map))
                    );
                } catch (Exception e) {
                    CrystalWars.getInstance().getLogger().error(this.crystalWars.getLanguage().translateString("plugin_arenaConfig_ResourceError"), e);
                }
            }

            String supplyName = config.getString("supply");
            if(!SupplyConfigManager.getSUPPLY_CONFIG_MAP().containsKey(supplyName) && !isSet) {
                CrystalWars.getInstance().getLogger().error(this.crystalWars.getLanguage().translateString("plugin_arenaConfig_SupplyNotExist", supplyName));
            }
            this.supply = new Supply(SupplyConfigManager.getSupplyConfig(supplyName));

            if (CrystalWars.debug) {
                CrystalWars.getInstance().getLogger().info("[debug] 资源生成点:" + this.resourceGenerations);
                CrystalWars.getInstance().getLogger().info("[debug] 商店供给:" + this.supply);
            }
        }catch (Exception e) {
            throw new ArenaLoadException(this.crystalWars.getLanguage().translateString("plugin_arenaConfig_LoadError"), e);
        }
    }

    public Vector3 getTeamSpawn(@NotNull Team team) {
        return this.teamSpawn.get(team);
    }

    public Vector3 getTeamCrystal(@NotNull Team team) {
        return this.teamCrystal.get(team);
    }

    public Vector3 getTeamShop(@NotNull Team team) {
        return this.teamShop.get(team);
    }

    public void setAllowTeammateDamage(boolean allowTeammateDamage) {
        if (!this.isSet) {
            return;
        }
        this.allowTeammateDamage = allowTeammateDamage;
    }

    public void setDefaultEndCrystalHealth(int defaultEndCrystalHealth) {
        if (!this.isSet) {
            return;
        }
        this.defaultEndCrystalHealth = defaultEndCrystalHealth;
    }

    public void setWaitTime(int newTime) {
        if (!this.isSet) {
            return;
        }
        this.setWaitTime = newTime;
    }

    public void setGameTime(int newTime) {
        if (!this.isSet) {
            return;
        }
        this.setGameTime = newTime;
    }

    public void setOvertime(int newTime) {
        if (!this.isSet) {
            return;
        }
        this.setOvertime = newTime;
    }

    public void setVictoryTime(int newTime) {
        if (!this.isSet) {
            return;
        }
        this.setVictoryTime = newTime;
    }

    public void setMaxTeamCount(int newCount) {
        if (!this.isSet) {
            return;
        }
        this.maxTeamCount = newCount;
    }

    public void setMinPlayers(int newCount) {
        if (!this.isSet) {
            return;
        }
        this.minPlayers = Math.min(2, newCount);
    }

    public void setMaxPlayers(int newCount) {
        if (!this.isSet) {
            return;
        }
        this.maxPlayers = Math.max(2, newCount);
    }

    public void setWaitSpawn(Vector3 newPos) {
        if (!this.isSet) {
            return;
        }
        this.waitSpawn = newPos.clone();
    }

    public void setSupply(Supply newSupply) {
        if (!this.isSet) {
            return;
        }
        this.supply = newSupply;
    }

    @Override
    public void save() {
        this.saveConfig(this.config);
    }

    @Override
    public Map<String, Object> toSaveMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("allowTeammateDamage", this.isAllowTeammateDamage());
        map.put("defaultEndCrystalHealth", this.getDefaultEndCrystalHealth());

        map.put("waitTime", this.getSetWaitTime());
        map.put("gameTime", this.getSetGameTime());
        map.put("overtime", this.getSetOvertime());
        map.put("victoryTime", this.getSetVictoryTime());

        map.put("maxTeamCount", this.getMaxTeamCount());

        map.put("minPlayers", this.getMinPlayers());
        map.put("maxPlayers", this.getMaxPlayers());

        map.put("supply", this.supply.getSupplyConfig().getDirName());

        map.put("waitSpawn", Utils.vector3ToString(this.getWaitSpawn()));

        map.put("spawn", this.getSavePosMap(this.getTeamSpawn()));
        map.put("crystal", this.getSavePosMap(this.getTeamCrystal()));
        map.put("shop", this.getSavePosMap(this.getTeamShop()));

        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (ResourceGeneration resourceGeneration : this.resourceGenerations) {
            LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("itemGenerationConfigName", resourceGeneration.getConfig().getName());
            linkedHashMap.put("x", resourceGeneration.getVector3().getX());
            linkedHashMap.put("y", resourceGeneration.getVector3().getY());
            linkedHashMap.put("z", resourceGeneration.getVector3().getZ());
            maps.add(linkedHashMap);
        }
        map.put("resourceGenerations", maps);

        return map;
    }

    private LinkedHashMap<String, LinkedHashMap<String, Double>> getSavePosMap(Map<Team, Vector3> pos) {
        LinkedHashMap<String, LinkedHashMap<String, Double>> posMap = new LinkedHashMap<>();
        for (Map.Entry<Team, Vector3> entry : pos.entrySet()) {
            posMap.put(entry.getKey().name().toLowerCase(), Utils.vector3ToMap(entry.getValue()));
        }
        return posMap;
    }

}
