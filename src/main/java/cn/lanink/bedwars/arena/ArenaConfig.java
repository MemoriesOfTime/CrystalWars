package cn.lanink.bedwars.arena;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lt_name
 */
@Getter
class ArenaConfig {

    private final int setWaitTime, setGameTime;

    private final String gameWorldName;

    private final Map<Team, Vector3> teamSpawn = new HashMap<>();

    private final Map<Team, Vector3> teamBed = new HashMap<>();

    protected ArenaConfig(@NotNull Config config) {
        this.setWaitTime = config.getInt("waitTime");
        this.setGameTime = config.getInt("waitTime");

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
        return teamSpawn.get(team);
    }

    public Vector3 getTeamBed(@NotNull Team team) {
        return this.teamBed.get(team);
    }

}
