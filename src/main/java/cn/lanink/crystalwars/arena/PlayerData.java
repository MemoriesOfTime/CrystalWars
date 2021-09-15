package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.gamecore.utils.SavePlayerInventory;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
    private int beforeFoodLevel;
    //TODO 使用文件保存，防止因崩服导致的数据丢失
    private Map<Integer, Item> enderChestContents;

    /**
     * 保存玩家加入房间前的一些数据
     */
    public void saveBeforePlayerData() {
        this.beforePos = this.player.clone();
        this.beforeGameMode = this.player.getGamemode();
        this.beforeFoodLevel = this.player.getFoodData().getLevel();
        SavePlayerInventory.save(CrystalWars.getInstance(), this.player);
        this.enderChestContents = this.player.getEnderChestInventory().getContents();
        this.player.getEnderChestInventory().clearAll();
    }

    /**
     * 还原玩家加入房间前的一些数据
     */
    public void restoreBeforePlayerData() {
        SavePlayerInventory.restore(CrystalWars.getInstance(), this.player);
        this.player.teleport(this.beforePos);
        this.player.setGamemode(this.beforeGameMode);
        this.player.getFoodData().setLevel(this.beforeFoodLevel);
        this.player.getEnderChestInventory().clearAll();
        this.player.getEnderChestInventory().setContents(this.enderChestContents);
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
