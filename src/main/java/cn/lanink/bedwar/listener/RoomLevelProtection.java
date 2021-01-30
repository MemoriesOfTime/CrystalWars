package cn.lanink.bedwar.listener;

import cn.lanink.bedwar.BedWars;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.StartBrewEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.level.Level;


/**
 * 游戏世界保护 禁止除游戏规则外的其他事件
 * 仅判断是否在游戏世界，并撤回操作
 * 其他判断请移步至 PlayerGameListener
 * @author lt_name
 */
public class RoomLevelProtection implements Listener {

    /**
     * 物品合成事件
     * @param event 事件
     */
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 开始酿造事件
     * @param event 事件
     */
    @EventHandler
    public void onStartBrew(StartBrewEvent event) {
        Level level = event.getBrewingStand() == null ? null : event.getBrewingStand().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 实体爆炸事件
     * @param event 事件
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Level level = event.getEntity() == null ? null : event.getEntity().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 物品展示框丢出事件
     * @param event 事件
     */
    @EventHandler
    public void onFrameDropItem(ItemFrameDropItemEvent event) {
        Level level = event.getItemFrame() == null ? null : event.getItemFrame().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 饥饿值变化事件
     * @param event 事件
     */
    @EventHandler
    public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 丢出物品事件
     * @param event 事件
     */
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 伤害事件
     * @param event 事件
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Level level = event.getEntity() == null ? null : event.getEntity().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 收起发射出去的箭事件
     * @param event 事件
     */
    @EventHandler
    public void onPickupArrow(InventoryPickupArrowEvent event) {
        Level level = event.getArrow() == null ? null : event.getArrow().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setCancelled();
        }
    }

    /**
     * 当一个抛射物击中物体时
     * @param event 事件
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Level level = event.getEntity() == null ? null : event.getEntity().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.getEntity().close();
        }
    }

    /**
     * 玩家死亡事件
     * @param event 事件
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Level level = event.getEntity() == null ? null : event.getEntity().getLevel();
        if (level != null && BedWars.getInstance().getRooms().containsKey(level.getName())) {
            event.setKeepInventory(true);
            event.setKeepExperience(true);
        }
    }

}
