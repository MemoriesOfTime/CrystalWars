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
        //TODO load Config

        this.setWaitTime = config.getInt("waitTime");
        this.setGameTime = config.getInt("waitTime");

        this.gameWorldName = config.getString("gameWorld");



    }

    public Vector3 getTeamSpawn(@NotNull Team team) {
        return teamSpawn.get(team);
    }

    public Vector3 getTeamBed(@NotNull Team team) {
        return this.teamBed.get(team);
    }

}
