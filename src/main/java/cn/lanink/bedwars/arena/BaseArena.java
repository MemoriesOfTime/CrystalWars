package cn.lanink.bedwars.arena;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class BaseArena extends ArenaConfig {

    private ArenaStatus arenaStatus;

    private final ConcurrentHashMap<Player, Team> players = new ConcurrentHashMap<>();

    public BaseArena(@NotNull Config config) {
        super(config);
        //TODO Init Arena
    }

    public boolean joinRoom(@NotNull Player player) {
        //TODO
        return false;
    }

    public boolean quitRoom(@NotNull Player player) {
        //TODO
        return false;
    }

    public enum ArenaStatus {
        LEVEL_NOT_LOADED,
        TASK_NEED_INITIALIZED,
        WAIT,
        GAME,
        VICTORY
    }

}
