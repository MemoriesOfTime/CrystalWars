package cn.lanink.bedwars.arena;

import cn.lanink.bedwars.utils.exception.ArenaLoadException;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class BaseArena extends ArenaConfig {

    private ArenaStatus arenaStatus;

    private Level gameWorld;

    private final ConcurrentHashMap<Player, Team> players = new ConcurrentHashMap<>();

    public BaseArena(@NotNull Config config) throws ArenaLoadException {
        super(config);
        //TODO Init Arena
        if (!Server.getInstance().loadLevel(this.getGameWorldName())) {
            throw new ArenaLoadException("世界: " + this.getGameWorldName() + " 加载失败！");
        }
        this.gameWorld = Server.getInstance().getLevelByName(this.getGameWorldName());

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
