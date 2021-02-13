package cn.lanink.bedwars.arena;

import cn.lanink.bedwars.BedWars;
import cn.lanink.bedwars.utils.exception.ArenaLoadException;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public abstract class BaseArena extends ArenaConfig {

    protected BedWars bedWars = BedWars.getInstance();

    @Setter
    @Getter
    private ArenaStatus arenaStatus;

    @Getter
    private Level gameWorld;

    private final Map<Player, PlayerData> playerData = new ConcurrentHashMap<>();

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

    public Map<Player, PlayerData> getPlayerData() {
        return this.playerData;
    }

    public PlayerData getPlayerData(@NotNull Player player) {
        return this.playerData.get(player);
    }

    public enum ArenaStatus {
        LEVEL_NOT_LOADED,
        TASK_NEED_INITIALIZED,
        WAIT,
        GAME,
        VICTORY
    }

}
