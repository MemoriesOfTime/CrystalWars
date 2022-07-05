package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.gamecore.utils.PlayerDataUtils;
import cn.nukkit.Player;
import cn.nukkit.level.Position;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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

    private int playerInvincibleTime; //玩家无敌时间

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
    private PlayerDataUtils.PlayerData playerData;


    /**
     * 保存玩家加入房间前的一些数据
     */
    public void saveBeforePlayerData() {
        this.beforePos = this.player.clone();
        this.beforeGameMode = this.player.getGamemode();

        File file = new File(CrystalWars.getInstance().getDataFolder() + "/PlayerInventory/" + this.player.getName() + ".json");
        this.playerData = PlayerDataUtils.create(this.player, file);
        this.playerData.saveAll();

        this.player.getInventory().clearAll();
        this.player.getUIInventory().clearAll();
        this.player.getEnderChestInventory().clearAll();
    }

    /**
     * 还原玩家加入房间前的一些数据
     */
    public void restoreBeforePlayerData() {
        this.player.getInventory().clearAll();
        this.player.getUIInventory().clearAll();
        this.player.getEnderChestInventory().clearAll();

        this.playerData.restoreAll();

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
