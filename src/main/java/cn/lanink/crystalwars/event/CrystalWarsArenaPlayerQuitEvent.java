package cn.lanink.crystalwars.event;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author LT_Name
 */
public class CrystalWarsArenaPlayerQuitEvent extends CrystalWarsArenaPlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public CrystalWarsArenaPlayerQuitEvent(@NotNull BaseArena arena, @NotNull Player player) {
        this.arena = arena;
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
