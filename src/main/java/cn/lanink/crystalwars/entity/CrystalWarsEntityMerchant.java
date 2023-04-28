package cn.lanink.crystalwars.entity;

import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.player.PlayerSettingDataManager;
import cn.lanink.crystalwars.supplier.SupplyConfig;
import cn.lanink.crystalwars.utils.inventory.ui.advanced.AdvancedInventory;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.lanink.gamecore.utils.EntityUtils;
import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.event.entity.EntityDamageEvent;
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
    private final SupplyConfig supply;

    @Getter
    private AdvancedInventory indexInventory;

    public CrystalWarsEntityMerchant(FullChunk chunk, CompoundTag nbt, @NotNull Team team, @NotNull SupplyConfig supply) {
        super(chunk, nbt);
        this.team = team;
        this.supply = supply;
        this.setMaxHealth(100);
        this.setHealth(100F);
        this.setNameTag(team.getStringColor() + "村民商店");
        generateMerchantInventory();
        generateGui();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        source.setCancelled(true);
        return super.attack(source);
    }

    /**
     * 生成 Gui 界面
     */
    public AdvancedFormWindowSimple generateGui() {
        AdvancedFormWindowSimple advancedFormWindowSimple = new AdvancedFormWindowSimple(this.getNameTag());
        if (this.supply != null) {
            this.supply.getPageConfigMap().forEach((ignore, pageConfig) -> {
                advancedFormWindowSimple.addButton(pageConfig.getTitle(), player -> {
                    player.showFormWindow(pageConfig.generateForm(advancedFormWindowSimple));
                });
            });
        }
        return advancedFormWindowSimple;
    }

    /**
     * 生成 背包 界面
     */
    public void generateMerchantInventory() {
        if (this.supply == null) {
            this.indexInventory = new AdvancedInventory(this, "null");
            return;
        }
        this.indexInventory = this.supply.getDefaultPageConfig().generateWindow(this);
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
        this.namedTag.putBoolean("allowOtherTeamUse", this.allowOtherTeamUse);
    }

    /**
     * 将商店发给发送给玩家 win10玩家是箱子商店，pe玩家是GUI界面
     * @param player 玩家
     */
    public void sendSupplyWindow(@NotNull Player player) {
        switch (PlayerSettingDataManager.getData(player).getShopType()) {
            case AUTO:
                if (player.getLoginChainData().getDeviceOS() == 7) { //Win10
                    int id = player.getWindowId(this.indexInventory);
                    if (id == -1) {
                        player.addWindow(this.indexInventory);
                    } else {
                        Inventory inventory = player.getWindowById(id);
                        inventory.open(player);
                    }
                } else {
                    player.showFormWindow(this.generateGui());
                }
                break;
            case CHEST:
                int id = player.getWindowId(this.indexInventory);
                if (id == -1) {
                    player.addWindow(this.indexInventory);
                } else {
                    Inventory inventory = player.getWindowById(id);
                    inventory.open(player);
                }
                break;
            case GUI:
            default:
                player.showFormWindow(this.generateGui());
                break;
        }
    }

}
