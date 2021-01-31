package cn.lanink.bedwars.arena;

import cn.nukkit.Player;
import lombok.Data;

/**
 * @author lt_name
 */
@Data
public class PlayerData {

    private final Player player;

    private PlayerStatus playerStatus;

    private Team team;

    private int killCount;
    private int deathCount;

    private int waitSpawnTime;

    public PlayerData(Player player) {
        this.player = player;
        this.playerStatus = PlayerStatus.SURVIVE;
        this.team = Team.NULL;
        this.killCount = 0;
        this.deathCount = 0;
        this.waitSpawnTime = 0;
    }

    public void addKillCount() {
        this.killCount++;
    }

    public void addDeathCount() {
        this.deathCount++;
    }

    public enum PlayerStatus {

        SURVIVE,
        WAIT_SPAWN,
        DEATH;

    }

}
