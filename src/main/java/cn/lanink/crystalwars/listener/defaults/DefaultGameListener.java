package cn.lanink.crystalwars.listener.defaults;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.PlayerData;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.items.ItemManager;
import cn.lanink.gamecore.listener.BaseGameListener;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerGameModeChangeEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacketV1;
import cn.nukkit.network.protocol.LevelSoundEventPacketV2;

/**
 * @author LT_Name
 */
@SuppressWarnings("unused")
public class DefaultGameListener extends BaseGameListener<BaseArena> {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        BaseArena arena = this.getListenerRoom(player.getLevel());
        if (arena == null) {
            return;
        }

        Item item = event.getItem();
        if (item.hasCompoundTag() && item.getNamedTag().getBoolean(ItemManager.IS_CRYSTALWARS_TAG)) {
            switch (item.getNamedTag().getInt(ItemManager.INTERNAL_ID_TAG)) {
                case 10000:
                    int nowTick = Server.getInstance().getTick();
                    int lastTick = item.getNamedTag().getInt("lastTick");
                    if (lastTick == 0 || nowTick - lastTick > 40) {
                        player.sendTip(CrystalWars.getInstance().getLang().translateString("tips_clickAgainToQuitRoom"));
                        CompoundTag tag = item.getNamedTag();
                        tag.putInt("lastTick", nowTick);
                        item.setNamedTag(tag); //防止tag更改不生效，无法退出房间
                        player.getInventory().setItem(8, item);
                        player.getInventory().setHeldItemIndex(7);
                        event.setCancelled(true);
                    }else {
                        arena.quitRoom(player);
                    }
                default:
                    break;
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            BaseArena arena = this.getListenerRoom(player.getLevel());
            if (arena == null) {
                return;
            }
            PlayerData playerData = arena.getPlayerData(player);
            if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME && playerData.getPlayerStatus() == PlayerData.PlayerStatus.SURVIVE) {
                if (playerData.getPlayerInvincibleTime() > 0) {
                    event.setDamage(0);
                    event.setCancelled(true);
                    return;
                }

                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                    if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                        PlayerData damagerData = arena.getPlayerData((Player) entityDamageByEntityEvent.getDamager());
                        if (!arena.isAllowTeammateDamage() && playerData.getTeam() == damagerData.getTeam()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                if (event.getFinalDamage() + 1 > player.getHealth()) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                            PlayerData damagerData = arena.getPlayerData((Player) entityDamageByEntityEvent.getDamager());
                            if (damagerData.getPlayerStatus() != PlayerData.PlayerStatus.SURVIVE) {
                                event.setCancelled(true);
                                return;
                            }
                            damagerData.addKillCount();
                        }
                    }
                    arena.playerDeath(player);
                    for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
                        event.setDamage(0, modifier);
                    }
                }
            } else {
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME) {
                        player.teleport(arena.getTeamSpawn(playerData.getTeam()));
                    }else {
                        player.teleport(arena.getWaitSpawn());
                    }
                }
                for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
                    event.setDamage(0, modifier);
                }
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
        }else if (event.getEntity() instanceof CrystalWarsEntityMerchant) {
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
            CrystalWarsEntityMerchant crystalWarsEntityMerchant = (CrystalWarsEntityMerchant) event.getEntity();
            BaseArena arena = this.getListenerRoom(toucher.getLevel());
            if(arena == null) {
                return;
            }
            PlayerData playerData = arena.getPlayerData(toucher);
            if(playerData.getPlayerStatus() != PlayerData.PlayerStatus.SURVIVE) {
                return;
            }
            if(playerData.getTeam() == crystalWarsEntityMerchant.getTeam() || crystalWarsEntityMerchant.isAllowOtherTeamUse()) {
                crystalWarsEntityMerchant.sendSupplyWindow(toucher);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        BaseArena baseArena = this.getListenerRoom(player.getLevel());
        if (baseArena == null) {
            return;
        }
        if (baseArena.getArenaStatus() != BaseArena.ArenaStatus.GAME) {
            event.setCancelled(true);
            return;
        }
        baseArena.getPlayerPlaceBlocks().add(event.getBlock().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        BaseArena baseArena = this.getListenerRoom(player.getLevel());
        if (baseArena == null) {
            return;
        }
        if (baseArena.getArenaStatus() != BaseArena.ArenaStatus.GAME) {
            event.setCancelled(true);
            return;
        }
        if (!baseArena.getPlayerPlaceBlocks().contains(event.getBlock())) {
            event.setCancelled(true);
        }
        baseArena.getPlayerPlaceBlocks().remove(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        BaseArena arena = this.getListenerRoom(event.getEntity().getLevel());
        if (arena == null) {
            return;
        }
        event.getBlockList().removeIf(block -> !arena.getPlayerPlaceBlocks().contains(block));
    }

    @EventHandler(priority = EventPriority.MONITOR)
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && this.getListenerRooms().containsKey(level.getFolderName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Level level = event.getPlayer() == null ? null : event.getPlayer().getLevel();
        if (level != null && this.getListenerRooms().containsKey(level.getFolderName())) {
            event.setCancelled(false);
        }
    }

    /**
     * 玩家点击背包栏格子事件
     * @param event 事件
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getInventory() == null) {
            return;
        }
        BaseArena arena = this.getListenerRoom(player.getLevel());
        if (arena == null || !arena.isPlaying(player)) {
            return;
        }
        Item sourceItem = event.getSourceItem();
        //TODO 全使用NBT判断
        if (sourceItem.isArmor() || (sourceItem.hasCompoundTag() && sourceItem.getNamedTag().getBoolean("cannotTakeItOff")) &&
                event.getHeldItem().getId() == 0) {
            event.setCancelled(true);
            return;
        }
        if ((sourceItem.hasCompoundTag() && sourceItem.getNamedTag().getBoolean("cannotClickOnInventory")) ||
                (event.getHeldItem().hasCompoundTag() && event.getHeldItem().getNamedTag().getBoolean("cannotClickOnInventory"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDataPacketReceive(DataPacketReceiveEvent event) {
        if (event.getPacket() instanceof LevelSoundEventPacket ||
                event.getPacket() instanceof LevelSoundEventPacketV1 ||
                event.getPacket() instanceof LevelSoundEventPacketV2) {
            Player player = event.getPlayer();
            BaseArena arena = this.getListenerRoom(player.getLevel());
            if (arena == null || !arena.isPlaying(player)) {
                return;
            }
            PlayerData playerData = arena.getPlayerData(player);
            if (playerData.getPlayerStatus() != PlayerData.PlayerStatus.SURVIVE) {
                player.dataPacket(event.getPacket());
                event.setCancelled(true);
            }
        }
    }

}
