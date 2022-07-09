package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.gamecore.utils.PlayerDataUtils;
import cn.nukkit.Player;
import cn.nukkit.Server;
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

    private Player lastDamager; //最后一次攻击的玩家
    private int lastDamagerTime; //最后一次攻击的时间

    public PlayerData(@NotNull Player player) {
        this.player = player;
        this.playerStatus = PlayerStatus.WAIT_SPAWN;
        this.team = Team.NULL;
        this.killCount = 0;
        this.deathCount = 0;
        this.waitSpawnTime = 0;
    }
    private PlayerDataUtils.PlayerData playerData;


    /**
     * 保存玩家加入房间前的一些数据
     */
    public void saveBeforePlayerData() {
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
    }

    public void addKillCount() {
        this.killCount++;
    }

    public void addDeathCount() {
        this.deathCount++;
    }

    public void setLastDamager(Player lastDamager) {
        this.lastDamager = lastDamager;
        this.lastDamagerTime = Server.getInstance().getTick();
    }

    public Player getLastDamager() {
        //让最后攻击玩家参数只在一段时间内有效
        if (Server.getInstance().getTick() - this.lastDamagerTime > 100) {
            this.lastDamager = null;
        }
        return this.lastDamager;
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
