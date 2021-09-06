package cn.lanink.crystalwars.entity;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.listener.inventory.MerchantInventoryClickListener;
import cn.lanink.crystalwars.supplier.Supply;
import cn.lanink.crystalwars.utils.RuntimeIdHolder;
import cn.lanink.gamecore.api.Info;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.lanink.gamecore.utils.EntityUtils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


/**
 * @author iGxnon
 * @date 2021/9/3
 */
public class CrystalWarsEntityMerchant extends EntityVillager implements InventoryHolder {

    @Getter
    private boolean allowOtherTeamUse = true;

    @Getter
    private final Team team;

    @Getter
    private final Supply supply;

    @Getter
    private FormWindowSimple parentGui;

    public CrystalWarsEntityMerchant(FullChunk chunk, CompoundTag nbt, @NotNull Team team, @NotNull Supply supply) {
        super(chunk, nbt);
        this.team = team;
        this.supply = supply;
        this.setMaxHealth(100);
        this.setHealth(100F);
        updateMerchantInventory();
        registerInventoryClickListener(); // TODO 在 GameCore 内制作一个类似 GUI快速构建 的'箱子界面交互模块'，并抛弃此 Listener
        generateGUI();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        source.setCancelled(true);
        return super.attack(source);
    }

    /**
     * 生成 Gui 界面
     */
    public void generateGUI() {
        // TODO 根据 Supply 生成 parentGui
    }

    /**
     * 刷新商人的背包
     */
    public void updateMerchantInventory() {

    }

    public void registerInventoryClickListener() {

    }

    @Override
    @Info("商人的背包不止一个，请勿使用该方法")
    @Deprecated
    public Inventory getInventory() {
        return null;
        // TODO
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("allowOtherTeamUse")) {
            this.allowOtherTeamUse = this.namedTag.getBoolean("allowOtherTeamUse");
        }

        this.fireProof = true;
        this.setDataFlag(
                EntityUtils.getEntityField("DATA_FLAGS", DATA_FLAGS),
                EntityUtils.getEntityField("DATA_FLAG_FIRE_IMMUNE", DATA_FLAG_FIRE_IMMUNE),
                true
        );

        this.getDataProperties()
                .putInt(EntityUtils.getEntityField("DATA_CONTAINER_TYPE", DATA_CONTAINER_TYPE), 10)
                .putInt(EntityUtils.getEntityField("DATA_CONTAINER_BASE_SIZE", DATA_CONTAINER_BASE_SIZE), InventoryType.CHEST.getDefaultSize())
                .putInt(EntityUtils.getEntityField("DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH", DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH), 0);

        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("allowOtherTeamUse", allowOtherTeamUse);
    }

    /**
     * 将商店发给发送给玩家 win10玩家是箱子商店，pe玩家是GUI界面
     * @param player 玩家
     * @param arena 战局
     */
    public void sendSupplyWindow(Player player) {
        if (player.getLoginChainData().getDeviceOS() == 7) { //Win10

        }else {

        }
    }

    public static class MerchantInventory extends ContainerInventory implements RuntimeIdHolder {

        private final long runtimeId = CrystalWars.inventoryRuntimeId ++;

        @Override
        public long getRid() {
            return runtimeId;
        }

        public MerchantInventory(InventoryHolder holder) {
            super(holder, InventoryType.CHEST);
        }

        @Override
        public InventoryHolder getHolder() {
            return super.getHolder();
        }

        public CrystalWarsEntityMerchant getOwner() {
            return (CrystalWarsEntityMerchant) getHolder();
        }



    }

}
