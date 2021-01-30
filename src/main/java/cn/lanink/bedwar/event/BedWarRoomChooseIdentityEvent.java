package cn.lanink.bedwar.event;

import cn.lanink.bedwar.room.Room;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class BedWarRoomChooseIdentityEvent extends BadWarRoomEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BedWarRoomChooseIdentityEvent(Room room) {
        this.room = room;
    }

}
