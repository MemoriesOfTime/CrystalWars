package cn.lanink.crystalwars.arena.classic;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.utils.exception.ArenaLoadException;
import cn.nukkit.utils.Config;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public class ClassicArena extends BaseArena {

    public ClassicArena(@NotNull String gameWorldName, @NotNull Config config) throws ArenaLoadException {
        super(gameWorldName, config);
    }

}
