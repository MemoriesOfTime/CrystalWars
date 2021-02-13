package cn.lanink.bedwars.arena.classic;

import cn.lanink.bedwars.arena.BaseArena;
import cn.lanink.bedwars.utils.exception.ArenaLoadException;
import cn.nukkit.utils.Config;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public class ClassicArena extends BaseArena {

    public ClassicArena(@NotNull Config config) throws ArenaLoadException {
        super(config);
    }

}
