package cn.lanink.bedwars.arena;

import cn.lanink.bedwars.BedWars;
import cn.nukkit.scheduler.PluginTask;

import java.util.ArrayList;

/**
 * @author LT_Name
 */
public class ArenaTickTask extends PluginTask<BedWars> {

    private static final ArrayList<BaseArena> ARENAS = new ArrayList<>();

    public ArenaTickTask(BedWars bedWars) {
        super(bedWars);
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

}
