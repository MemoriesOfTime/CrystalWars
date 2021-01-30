package cn.lanink.bedwar.event;

import cn.lanink.bedwar.room.Room;
import cn.nukkit.event.Event;

public abstract class BadWarRoomEvent extends Event {

    protected Room room;

    public BadWarRoomEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
