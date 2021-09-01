package cn.lanink.bedwars.arena;

import cn.lanink.bedwars.BedWars;
import cn.lanink.gamecore.utils.SavePlayerInventory;
import cn.nukkit.Player;
import cn.nukkit.level.Position;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

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

    public PlayerData(@NotNull Player player) {
        this.player = player;
        this.playerStatus = PlayerStatus.WAIT_SPAWN;
        this.team = Team.NULL;
        this.killCount = 0;
        this.deathCount = 0;
        this.waitSpawnTime = 0;
    }

    private Position beforePos;
    private int beforeGameMode;

    /**
     * 保存玩家加入房间前的一些数据
     */
    public void saveBeforePlayerData() {
        this.beforePos = this.player.clone();
        this.beforeGameMode = this.player.getGamemode();
        SavePlayerInventory.save(BedWars.getInstance(), player);
    }

    /**
     * 还原玩家加入房间前的一些数据
     */
    public void restoreBeforePlayerData() {
        SavePlayerInventory.restore(BedWars.getInstance(), player);
        this.player.teleport(this.beforePos);
        this.player.setGamemode(this.beforeGameMode);
    }

    public void addKillCount() {
        this.killCount++;
    }

    public void addDeathCount() {
        this.deathCount++;
    }

    public enum PlayerStatus {

        /**
         * 存活
         */
        SURVIVE,

        /**
         * 等待重生
         */
        WAIT_SPAWN,

        /**
         * 死亡
         */
        DEATH;

    }

}
