package cn.lanink.bedwar.tasks;

import cn.lanink.bedwar.BedWars;
import cn.lanink.bedwar.room.Room;
import cn.lanink.bedwar.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.PluginTask;

import java.util.Map;


public class VictoryTask extends PluginTask<BedWars> {

    private final Room room;
    private int victoryTime;

    public VictoryTask(BedWars owner, Room room, int victory) {
        super(owner);
        this.room = room;
        this.victoryTime = 10;
        this.room.victory = victory;
    }

    @Override
    public void onRun(int i) {
        if (this.room.getMode() != 3) {
            this.cancel();
        }
        if (this.victoryTime < 1) {
            this.room.endGame();
            this.cancel();
        }else {
            this.victoryTime--;
            owner.getServer().getScheduler().scheduleAsyncTask(BedWars.getInstance(), new AsyncTask() {
                @Override
                public void onRun() {
                    for (Map.Entry<Player, Integer> entry : room.getPlayers().entrySet()) {
                        if (entry.getValue() != 0) {
                            if (room.victory == 1 && entry.getValue() == 3) {
                                continue;
                            }
                            Tools.spawnFirework(entry.getKey());
                        }
                    }
                }
            });
        }
    }



}
