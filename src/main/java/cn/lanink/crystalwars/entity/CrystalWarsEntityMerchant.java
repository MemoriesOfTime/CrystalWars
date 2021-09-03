package cn.lanink.crystalwars.entity;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.Team;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;

/**
 * @author iGxnon
 * @date 2021/9/3
 */
public class CrystalWarsEntityMerchant extends Entity {

    public static final int NETWORK_ID = 15;

    @Getter
    private boolean allowOtherTeamUse = true;

    @Getter
    private final Team team;

    public CrystalWarsEntityMerchant(FullChunk chunk, CompoundTag nbt, Team team) {
        super(chunk, nbt);
        this.team = team;
        this.setMaxHealth(1000);
        this.setHealth(1000F);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("allowOtherTeamUse")) {
            this.allowOtherTeamUse = this.namedTag.getBoolean("allowOtherTeamUse");
        }

        this.fireProof = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true);

        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("allowOtherTeamUse", allowOtherTeamUse);
    }

    public void lock() {
        allowOtherTeamUse = false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public void sendSupplyWindow(Player player, BaseArena arena) {

    }

    /**
     * 手机版玩家打开 ui
     * @param cellPhonePlayer 手机玩家
     */
    private void openGUI(Player cellPhonePlayer) {

    }

    /**
     * win10玩家打开箱子界面
     * @param win10Player win10玩家
     */
    private void openWindow(Player win10Player) {

    }

}
