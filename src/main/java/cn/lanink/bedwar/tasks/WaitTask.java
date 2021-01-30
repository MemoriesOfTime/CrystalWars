package cn.lanink.bedwar.tasks;

import cn.lanink.bedwar.BedWars;
import cn.lanink.bedwar.event.BadWarRoomStartEvent;
import cn.lanink.bedwar.room.Room;
import cn.lanink.bedwar.utils.Tools;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;

public class WaitTask extends PluginTask<BedWars> {

    private final Room room;

    public WaitTask(BedWars owner, Room room) {
        super(owner);
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 1) {
            this.cancel();
            return;
        }
        if (this.room.getPlayers().size() >= 2) {
            if (this.room.getPlayers().size() == 16 && this.room.waitTime > 10) {
                this.room.waitTime = 10;
            }
            if (this.room.waitTime > 0) {
                this.room.waitTime--;
                if (this.room.waitTime <= 5) {
                    Tools.addSound(this.room, Sound.RANDOM_CLICK);
                }
            }else {
                owner.getServer().getPluginManager().callEvent(new BadWarRoomStartEvent(this.room));
                this.cancel();
            }
        }else if (this.room.getPlayers().size() > 0) {
            if (this.room.waitTime != this.room.getWaitTime()) {
                this.room.waitTime = this.room.getWaitTime();
            }
        }else {
            this.room.endGame();
            this.cancel();
        }
    }

}
