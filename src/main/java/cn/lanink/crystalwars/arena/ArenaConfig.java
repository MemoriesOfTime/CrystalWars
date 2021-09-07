package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
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

    private final int setWaitTime;
    private final int setGameTime;
    private final int setOvertime;

    private final int minPlayers;
    private final int maxPlayers;

    private final Vector3 waitSpawn;
    private final Map<Team, Vector3> teamSpawn = new HashMap<>();
    private final Map<Team, Vector3> teamCrystal = new HashMap<>();
    private final Map<Team, Vector3> teamShop = new HashMap<>();
    private final ArrayList<ResourceGeneration> resourceGenerations = new ArrayList<>();
    private final Supply supply;

    public ArenaConfig(@NotNull Config config) throws ArenaLoadException {
        try {
            this.setWaitTime = config.getInt("waitTime");
            this.setGameTime = config.getInt("gameTime");
            this.setOvertime = config.getInt("overtime");

            this.minPlayers = config.getInt("minPlayers");
            this.maxPlayers = config.getInt("maxPlayers");

            this.waitSpawn = Utils.stringToVector3(config.getString("waitSpawn"));

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
                    this.resourceGenerations.add(
                            new ResourceGeneration(
                                    ItemGenerationConfigManager.getItemGenerationConfig((String) map.get("itemGenerationConfigName")),
                                    Utils.mapToVector3(map)
                            )
                    );
                } catch (Exception e) {
                    CrystalWars.getInstance().getLogger().error("加载资源生成点时出现错误：", e);
                }
            }
            if(!SupplyConfigManager.getSUPPLY_CONFIG_MAP().containsKey(config.getString("supply"))) {
                CrystalWars.getInstance().getLogger().error("加载商店时出现错误：无 " + config.getString("supply") + " 商店供给配置！");
                this.supply = null;
            }else {
                this.supply = new Supply(SupplyConfigManager.getSupplyConfig(config.getString("supply")));

            }

            if (CrystalWars.debug) {
                CrystalWars.getInstance().getLogger().info("[debug] 资源生成点:" + this.resourceGenerations);
                CrystalWars.getInstance().getLogger().info("[debug] 商店供给:" + this.supply);
            }
        }catch (Exception e) {
            throw new ArenaLoadException("游戏房间配置读取错误！", e);
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

    @Override
    public Map<String, Object> toSaveMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("waitTime", this.getSetWaitTime());
        map.put("gameTime", this.getSetGameTime());

        map.put("waitSpawn", Utils.vector3ToString(this.getWaitSpawn()));
        map.put("spawn", this.getSavePosMap(this.getTeamSpawn()));
        map.put("crystal", this.getSavePosMap(this.getTeamCrystal()));
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
