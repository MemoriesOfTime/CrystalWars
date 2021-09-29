package cn.lanink.crystalwars.event;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.nukkit.event.player.PlayerEvent;

/**
 * @author LT_Name
 */
public class CrystalWarsArenaPlayerEvent extends PlayerEvent {

    protected BaseArena arena;

    public BaseArena getArena() {
        return this.arena;
    }

}
