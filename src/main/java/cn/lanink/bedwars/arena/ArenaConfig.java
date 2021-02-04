package cn.lanink.bedwars.arena;

import cn.lanink.bedwars.utils.ISaveConfig;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lt_name
 */
@AllArgsConstructor
@Getter
class ArenaConfig implements ISaveConfig {

    private final int setWaitTime;
    private final int setGameTime;

    private final String gameWorldName;

    private final Map<Team, Vector3> teamSpawn = new HashMap<>();
    private final Map<Team, Vector3> teamBed = new HashMap<>();

    protected ArenaConfig(@NotNull Config config) {
        this.setWaitTime = config.getInt("waitTime");
        this.setGameTime = config.getInt("gameTime");

        this.gameWorldName = config.getString("gameWorld");

        Map<String, Map<String, Double>> spawn = config.get("spawn", new HashMap<>());
        for (Map.Entry<String, Map<String, Double>> entry : spawn.entrySet()) {
            this.teamSpawn.put(Team.valueOf(entry.getKey().toUpperCase()),
                    new Vector3(entry.getValue().get("x"), entry.getValue().get("y"), entry.getValue().get("z")));
        }
        spawn = config.get("bed", new HashMap<>());
        for (Map.Entry<String, Map<String, Double>> entry : spawn.entrySet()) {
            this.teamBed.put(Team.valueOf(entry.getKey().toUpperCase()),
                    new Vector3(entry.getValue().get("x"), entry.getValue().get("y"), entry.getValue().get("z")));
        }
    }

    public Vector3 getTeamSpawn(@NotNull Team team) {
        return this.teamSpawn.get(team);
    }

    public Vector3 getTeamBed(@NotNull Team team) {
        return this.teamBed.get(team);
    }

    @Override
    public Map<String, Object> getSaveMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("waitTime", this.getSetWaitTime());
        map.put("gameTime", this.getSetGameTime());

        map.put("gameWorld", this.getGameWorldName());

        map.put("spawn", this.getSavePosMap(this.getTeamSpawn()));
        map.put("bed", this.getSavePosMap(this.getTeamBed()));
        return map;
    }

    private LinkedHashMap<String, LinkedHashMap<String, Double>> getSavePosMap(Map<Team, Vector3> pos) {
        LinkedHashMap<String, LinkedHashMap<String, Double>> posMap = new LinkedHashMap<>();
        for (Map.Entry<Team, Vector3> entry : pos.entrySet()) {
            LinkedHashMap<String, Double> map = new LinkedHashMap<>();
            map.put("x", entry.getValue().getX());
            map.put("y", entry.getValue().getY());
            map.put("z", entry.getValue().getZ());
            posMap.put(entry.getKey().name().toLowerCase(), map);
        }
        return posMap;
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

    public static ArenaConfig fromJsonString(@NotNull String jsonString) {
        return new Gson().fromJson(jsonString, ArenaConfig.class);
    }

}
