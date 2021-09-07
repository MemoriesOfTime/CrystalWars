package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.nukkit.scheduler.PluginTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author LT_Name
 */
public class ArenaTickTask extends PluginTask<CrystalWars> {

    private static final List<BaseArena> ARENAS = new CopyOnWriteArrayList<>();

    public ArenaTickTask(CrystalWars crystalWars) {
        super(crystalWars);
    }

    @Override
    public void onRun(int i) {
        for (BaseArena arena : ARENAS) {
            arena.onUpdate(i);
        }
    }

    public static void addArena(BaseArena arena) {
        ARENAS.add(arena);
    }

    public static void removeArena(BaseArena arena) {
        ARENAS.remove(arena);
    }

    public static void clearAll() {
        ARENAS.clear();
    }

}
