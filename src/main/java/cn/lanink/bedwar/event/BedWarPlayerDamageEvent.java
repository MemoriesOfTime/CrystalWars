package cn.lanink.bedwar.event;

import cn.lanink.bedwar.room.Room;
import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BedWarPlayerDamageEvent extends BadWarRoomPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Player damage;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BedWarPlayerDamageEvent(Room room, Player damage, Player player) {
        this.room = room;
        this.damage = damage;
        this.player = player;
    }

    public Player getDamage() {
        return this.damage;
    }

}
