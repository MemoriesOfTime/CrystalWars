package cn.lanink.crystalwars.entity;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.listener.inventory.MerchantInventoryClickListener;
import cn.lanink.crystalwars.utils.RuntimeIdHolder;
import cn.lanink.gamecore.api.Info;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;

/**
 * @author iGxnon
 * @date 2021/9/3
 */
public abstract class CrystalWarsEntityBaseMerchant extends EntityVillager implements InventoryHolder {

    @Getter
    private boolean allowOtherTeamUse = true;

    @Getter
    private final Team team;

    @Getter
    private final ImmutableMap<MerchantInventory.Slot, MerchantInventory> inventoryMap = ImmutableMap.<MerchantInventory.Slot, MerchantInventory>builder()
            .put(MerchantInventory.Slot.FIRST, new MerchantInventory(this))
            .put(MerchantInventory.Slot.SECOND, new MerchantInventory(this))
            .put(MerchantInventory.Slot.THIRD, new MerchantInventory(this))
            .put(MerchantInventory.Slot.FOURTH, new MerchantInventory(this))
            .put(MerchantInventory.Slot.FIFTH, new MerchantInventory(this))
            .put(MerchantInventory.Slot.SIXTH, new MerchantInventory(this))
            .put(MerchantInventory.Slot.SEVENTH, new MerchantInventory(this))
            .put(MerchantInventory.Slot.EIGHTH, new MerchantInventory(this))
            .put(MerchantInventory.Slot.NINTH, new MerchantInventory(this))
            .build();

    public CrystalWarsEntityBaseMerchant(FullChunk chunk, CompoundTag nbt, Team team, BaseArena arena) {
        super(chunk, nbt);
        this.team = team;
        this.setMaxHealth(1000);
        this.setHealth(1000F);
        updateMerchantInventory(arena);
        registerInventoryClickListener();
    }

    /**
     * 刷新商人的背包
     */
    public void updateMerchantInventory(BaseArena arena) {

    }

    public void registerInventoryClickListener() {
        MerchantInventoryClickListener listener = new MerchantInventoryClickListener(this);
        inventoryMap.forEach(((slot, merchantInventory) -> listener.addToListen(merchantInventory.getRid(), merchantInventory)));
        Server.getInstance().getPluginManager().registerEvents(listener, CrystalWars.getInstance());
    }

    @Override
    @Info("商人的背包不止一个，请勿使用该方法")
    @Deprecated
    public Inventory getInventory() {
        return null;
    }

    /**
     * @param slot 容器角标 or 按钮角标
     * @return 背包
     */
    public MerchantInventory getInventory(MerchantInventory.Slot slot) {
        return inventoryMap.get(slot);
    }


    @Override
    public void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("allowOtherTeamUse")) {
            this.allowOtherTeamUse = this.namedTag.getBoolean("allowOtherTeamUse");
        }

        this.fireProof = true;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true);

        this.getDataProperties().putInt(Entity.DATA_CONTAINER_TYPE, 10)
                .putInt(Entity.DATA_CONTAINER_BASE_SIZE, InventoryType.CHEST.getDefaultSize())
                .putInt(Entity.DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH, 0);

        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("allowOtherTeamUse", allowOtherTeamUse);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    /**
     * 将商店发给发送给玩家 win10玩家是箱子商店，pe玩家是GUI界面
     * @param player 玩家
     * @param arena 战局
     */
    public void sendSupplyWindow(Player player, BaseArena arena) {

    }

    public static class MerchantInventory extends ContainerInventory implements RuntimeIdHolder {

        @Override
        public long getRid() {
            return CrystalWars.inventoryRuntimeId ++;
        }

        public MerchantInventory(InventoryHolder holder) {
            super(holder, InventoryType.CHEST);
        }

        @Override
        public InventoryHolder getHolder() {
            return super.getHolder();
        }

        public CrystalWarsEntityBaseMerchant getOwner() {
            return (CrystalWarsEntityBaseMerchant) getHolder();
        }

        public enum Slot {
            FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NINTH
        }

    }

}
