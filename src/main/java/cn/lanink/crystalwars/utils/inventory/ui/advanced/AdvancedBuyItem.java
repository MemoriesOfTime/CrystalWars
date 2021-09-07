package cn.lanink.crystalwars.utils.inventory.ui.advanced;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.supplier.config.items.SupplyItemConfig;
import cn.lanink.crystalwars.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
public class AdvancedBuyItem extends AdvancedClickItem{

    private final SupplyItemConfig itemConfig;

    public AdvancedBuyItem(@NotNull Item item, @NotNull SupplyItemConfig itemConfig) {
        super(item.getId(), item.getDamage(), item.getCount(), item.getName());
        this.setCustomName(item.getCustomName());
        this.itemConfig = itemConfig;
    }

    @Override
    public void callClick(InventoryClickEvent clickEvent, Player player) {
        if(!player.getInventory().canAddItem(this.itemConfig.getItem())) {
            player.sendTip("你的背包满了！");
            return;
        }
        for (Item cost : this.itemConfig.getCost()) {
            if(!player.getInventory().contains(cost)) {
                player.sendTip("你还没有足够的物品来兑换");
                return;
            }
        }
        for (Item cost : this.itemConfig.getCost()) {
            player.getInventory().removeItem(cost);
        }
        Item item = this.itemConfig.getItem();
        if(this.itemConfig.isTeamChangeItem()) {
            BaseArena arena = CrystalWars.getInstance().getArenas().get(player.getLevel().getFolderName());
            if(arena == null) {
                player.sendMessage("§c[警告] 你没有加入任何游戏！");
                return;
            }
            item = Utils.getTeamColorItem(item, arena.getPlayerData(player).getTeam());
        }
        player.getInventory().addItem(item);
        player.sendTip("购买成功！");
    }

}
