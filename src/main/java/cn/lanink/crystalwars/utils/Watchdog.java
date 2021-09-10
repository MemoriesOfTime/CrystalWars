package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.nukkit.scheduler.PluginTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class Watchdog extends PluginTask<CrystalWars> {

    private static final ConcurrentHashMap<BaseArena, Integer> ARENA_RUN_TIME = new ConcurrentHashMap<>();
    private final int outTime;

    public Watchdog(CrystalWars crystalWars, int outTime) {
        super(crystalWars);
        this.outTime = outTime;
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
                            this.owner.unloadArena(entry.getKey().getGameWorldName());
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

    public static void addArena(BaseArena arena) {
        ARENA_RUN_TIME.put(arena, 0);
    }

    public static void removeArena(BaseArena arena) {
        ARENA_RUN_TIME.remove(arena);
    }

    public static void clearAll() {
        ARENA_RUN_TIME.clear();
    }

}
