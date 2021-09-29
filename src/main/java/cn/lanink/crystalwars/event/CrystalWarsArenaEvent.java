package cn.lanink.crystalwars.event;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.nukkit.event.Event;

/**
 * @author LT_Name
 */
public abstract class CrystalWarsArenaEvent extends Event {

    protected BaseArena arena;

    public BaseArena getArena() {
        return this.arena;
    }

}
