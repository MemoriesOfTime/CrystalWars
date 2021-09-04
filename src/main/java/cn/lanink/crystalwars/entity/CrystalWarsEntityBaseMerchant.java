package cn.lanink.crystalwars.entity;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.listener.inventory.MerchantInventoryClickListener;
import cn.lanink.crystalwars.supplier.SupplyWindowGenerator;
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
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

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
    private final ImmutableMap<MerchantInventory.Slot, MerchantInventory> inventoryWindowMap = ImmutableMap.<MerchantInventory.Slot, MerchantInventory>builder()
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

    @Getter
    private ImmutableMap<MerchantInventory.Slot, FormWindowSimple> guiWindowMap;

    @Getter
    private FormWindowSimple parentGUI;

    public CrystalWarsEntityBaseMerchant(FullChunk chunk, CompoundTag nbt, @NotNull Team team, @NotNull BaseArena arena) {
        super(chunk, nbt);
        this.team = team;
        this.setMaxHealth(1000);
        this.setHealth(1000F);
        updateMerchantInventory(arena);
        registerInventoryClickListener(); // TODO 在 GameCore 内制作一个类似 GUI快速构建 的'箱子界面交互模块'，并抛弃此 Listener
        generateGUI(arena);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        source.setCancelled(true);
        return super.attack(source);
    }

    /**
     * 生成 Gui 界面
     */
    public void generateGUI(BaseArena arena) {
        ImmutableMap.Builder<MerchantInventory.Slot, FormWindowSimple> builder = ImmutableMap.builder();
        getInventoryWindowMap().forEach((slot, inventory) -> {
            if(!inventory.isEmpty()) {
                builder.put(slot, SupplyWindowGenerator.generatePage(inventory));
            }
        });
        guiWindowMap = builder.build();
        parentGUI = new AdvancedFormWindowSimple(getNameTag());
        // TODO 从战局内获取到 Supply 信息
    }

    /**
     * 刷新商人的背包
     */
    public void updateMerchantInventory(BaseArena arena) {

    }

    public void registerInventoryClickListener() {
        MerchantInventoryClickListener listener = new MerchantInventoryClickListener(this);
        inventoryWindowMap.forEach(((slot, merchantInventory) -> listener.addToListen(merchantInventory.getRid(), merchantInventory)));
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
        return inventoryWindowMap.get(slot);
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
    public void sendSupplyWindow(Player player, BaseArena arena) {

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

        public CrystalWarsEntityBaseMerchant getOwner() {
            return (CrystalWarsEntityBaseMerchant) getHolder();
        }

        public enum Slot {
            FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NINTH;

            public static Slot valueFormStr(String str) {
                str = str.toLowerCase(Locale.ROOT);
                switch (str) {
                    case "first":
                        return Slot.FIRST;
                    case "second":
                        return Slot.SECOND;
                    case "third":
                        return Slot.THIRD;
                    case "fourth":
                        return Slot.FOURTH;
                    case "fifth":
                        return Slot.FIFTH;
                    case "sixth":
                        return Slot.SIXTH;
                    case "seventh":
                        return Slot.SEVENTH;
                    case "eighth":
                        return Slot.EIGHTH;
                    case "ninth":
                        return Slot.NINTH;
                    default:
                        return null;
                }
            }
        }

    }

}
