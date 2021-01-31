package cn.lanink.bedwars.arena;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
@Getter
class ArenaConfig {

    private int setWaitTime,
            setGameTime;
    //spawn
    private Vector3 redSpawn,
            yellowSpawn,
            blueSpawn,
            greenSpawn;
    //bed
    private Vector3 redBed,
            yellowBed,
            blueBed,
            greenBed;

    protected ArenaConfig(@NotNull Config config) {
        //TODO load Config
    }

}
