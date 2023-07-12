package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.PluginTask;

public class WaitTask extends PluginTask<CrystalWars> {

    private final BaseArena arena;

    public WaitTask(CrystalWars owner, BaseArena arena) {
        super(owner);
        this.arena = arena;
    }

    @Override
    public void onRun(int i) {
        if (this.arena.getArenaStatus() != BaseArena.ArenaStatus.WAIT) {
            this.cancel();
            return;
        }

        if (this.arena.getPlayerDataMap().size() >= this.arena.getMinPlayers()) {
            if (this.arena.getPlayerDataMap().size() == this.arena.getMaxPlayers() && this.arena.waitTime > 10) {
                this.arena.waitTime = 10;
            }
            if (this.arena.waitTime > 0) {
                String title = "§e";
                if (this.arena.waitTime <= 10) {
                    if (this.arena.waitTime <= 3) {
                        title = "§c";
                        Utils.playSound(this.arena, Sound.NOTE_HARP);
                    } else {
                        Utils.playSound(this.arena, Sound.NOTE_BASSATTACK);
                    }
                    title += this.arena.waitTime;
                    for (Player player : this.arena.getPlayerDataMap().keySet()) {
                        player.sendTitle(title, "", 0, 15, 5);
                    }
                }
            }else {
                this.arena.gameStart();
                Server.getInstance().getScheduler().scheduleDelayedTask(this.owner,
                        () -> Utils.playSound(this.arena, Sound.NOTE_FLUTE), 2, true);
                this.cancel();
            }
        }else if (this.arena.getPlayerDataMap().size() > 0) {
            if (this.arena.waitTime != this.arena.getSetWaitTime()) {
                this.arena.waitTime = this.arena.getSetWaitTime();
            }
        }else {
            this.arena.gameEnd();
            this.cancel();
        }
    }
}
