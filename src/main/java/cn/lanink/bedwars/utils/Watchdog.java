package cn.lanink.bedwars.utils;

import cn.lanink.bedwars.BedWars;
import cn.lanink.bedwars.arena.BaseArena;
import cn.nukkit.scheduler.PluginTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class Watchdog extends PluginTask<BedWars> {

    private static final ConcurrentHashMap<BaseArena, Integer> ARENA_RUN_TIME = new ConcurrentHashMap<>();
    private final int outTime = 10;

    public Watchdog(BedWars bedWars) {
        super(bedWars);
    }

    @Override
    public void onRun(int i) {
        for (Map.Entry<BaseArena, Integer> entry : ARENA_RUN_TIME.entrySet()) {
            int runTime = entry.getValue() + 1;
            entry.setValue(runTime);
            switch (entry.getKey().getArenaStatus()) {
                case WAIT:
                case GAME:
                case VICTORY:
                    if (runTime > this.outTime) {
                        try {
                            this.owner.getLogger().warning("[Watchdog] Room[" + entry.getKey().getGameWorldName() + "] stuck error! Try to close...");
                            entry.setValue(0);
                            entry.getKey().gameEnd();
                        } catch (Exception e) {
                            //this.owner.unloadRoom(entry.getKey().getLevelName());
                            this.owner.getLogger().error("[Watchdog] The room[" + entry.getKey().getGameWorldName() + "] cannot end the game error", e);
                        }
                    }
                    break;
                default:
                    entry.setValue(0);
                    break;
            }
        }
    }

    @Override
    public void onCancel() {
        ARENA_RUN_TIME.clear();
    }

    public static void resetTime(BaseArena baseRoom) {
        if (ARENA_RUN_TIME.containsKey(baseRoom)) {
            ARENA_RUN_TIME.put(baseRoom, 0);
        }
    }

    public static void add(BaseArena arena) {
        ARENA_RUN_TIME.put(arena, 0);
    }

    public static void remove(BaseArena arena) {
        ARENA_RUN_TIME.remove(arena);
    }

}
