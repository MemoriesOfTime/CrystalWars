package cn.lanink.crystalwars.listener.defaults;

import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.PlayerData;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.entity.CrystalWarsEntityBaseMerchant;
import cn.lanink.gamecore.listener.BaseGameListener;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerGameModeChangeEvent;
import cn.nukkit.level.Level;

/**
 * @author LT_Name
 */
@SuppressWarnings("unused")
public class DefaultGameListener extends BaseGameListener<BaseArena> {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            BaseArena arena = this.getListenerRoom(player.getLevel());
            if (arena == null) {
                return;
            }
            if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME) {
                if (event.getFinalDamage() + 1 > player.getHealth()) {
                    arena.playerDeath(player);
                    event.setDamage(0);
                }
            } else {
                event.setCancelled(true);
            }
        }else if (event.getEntity() instanceof CrystalWarsEntityEndCrystal) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                    Player damager = (Player) entityDamageByEntityEvent.getDamager();
                    BaseArena arena = this.getListenerRoom(damager.getLevel());
                    if (arena == null) {
                        return;
                    }
                    CrystalWarsEntityEndCrystal entityCrystal = (CrystalWarsEntityEndCrystal) event.getEntity();
                    if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME) {
                        PlayerData playerData = arena.getPlayerData(damager);
                        if (playerData.getPlayerStatus() == PlayerData.PlayerStatus.SURVIVE && playerData.getTeam() != entityCrystal.getTeam()) {
                            return;
                        }
                    }
                }
            }
            event.setCancelled(true);
        }else if (event.getEntity() instanceof CrystalWarsEntityBaseMerchant) {
            if(!event.isCancelled()) {
                event.setCancelled(true);
            }
            if(!(event instanceof EntityDamageByEntityEvent)) {
                return;
            }
            if(!(((EntityDamageByEntityEvent) event).getDamager() instanceof Player)) {
                return;
            }
            Player toucher = (Player) ((EntityDamageByEntityEvent) event).getDamager();
            CrystalWarsEntityBaseMerchant crystalWarsEntityMerchant = (CrystalWarsEntityBaseMerchant) event.getEntity();
            BaseArena arena = this.getListenerRoom(toucher.getLevel());
            if(arena == null) {
                return;
            }
            PlayerData playerData = arena.getPlayerData(toucher);
            if(playerData.getPlayerStatus() != PlayerData.PlayerStatus.SURVIVE) {
                return;
            }
            if(playerData.getTeam() == crystalWarsEntityMerchant.getTeam() || crystalWarsEntityMerchant.isAllowOtherTeamUse()) {
                crystalWarsEntityMerchant.sendSupplyWindow(toucher, arena);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (this.getListenerRoom(player.getLevel()) != null) {
            if (event.getFoodLevel() < player.getFoodData().getLevel() ||
                    event.getFoodSaturationLevel() < player.getFoodData().getFoodSaturationLevel()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && this.getListenerRooms().containsKey(level.getFolderName())) {
            event.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && this.getListenerRooms().containsKey(level.getFolderName())) {
            event.setCancelled(false);
        }
    }

}
