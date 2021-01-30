package cn.lanink.bedwar.event;

import cn.lanink.bedwar.room.Room;
import cn.nukkit.event.player.PlayerEvent;


public abstract class BadWarRoomPlayerEvent extends PlayerEvent {

    protected Room room;

    public BadWarRoomPlayerEvent() {

    }

    public Room getRoom() {
        return this.room;
    }

}
