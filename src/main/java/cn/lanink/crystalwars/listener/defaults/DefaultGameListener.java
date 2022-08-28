package cn.lanink.crystalwars.listener.defaults;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.PlayerData;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.items.ItemManager;
import cn.lanink.crystalwars.utils.NukkitTypeUtils;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.gamecore.listener.BaseGameListener;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacketV1;
import cn.nukkit.network.protocol.LevelSoundEventPacketV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LT_Name
 */
@SuppressWarnings("unused")
public class DefaultGameListener extends BaseGameListener<BaseArena> {

    @EventHandler
    public void onPlayerChangeSkin(PlayerChangeSkinEvent event) { //此事件仅玩家主动修改皮肤时触发，不需要针对插件修改特判
        Player player = event.getPlayer();
        BaseArena arena = this.getListenerRoom(player.getLevel());
        if (arena == null || !arena.isPlaying(player)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        BaseArena arena = this.getListenerRoom(player.getLevel());
        if (arena == null) {
            return;
        }

        Item item = event.getItem();
        if (item.hasCompoundTag() && item.getNamedTag().getBoolean(ItemManager.IS_CRYSTALWARS_TAG)) {
            int nowTick = Server.getInstance().getTick();
            CompoundTag tag = item.getNamedTag();
            int lastTick = tag.getInt("lastTick");
            if (lastTick != 0 && nowTick - lastTick < 20) {
                event.setCancelled(true);
                return;
            }
            tag.putInt("lastTick", nowTick);
            item.setNamedTag(tag);
            player.getInventory().setItemInHand(item);
            int internalID = tag.getInt(ItemManager.INTERNAL_ID_TAG_OLD);
            PlayerData playerData = arena.getPlayerData(player);
            if (arena.getArenaStatus() == BaseArena.ArenaStatus.WAIT) {
                event.setCancelled(true);
                Team team;
                switch (internalID) {
                    case 10101:
                        team = Team.RED;
                        break;
                    case 10102:
                        team = Team.YELLOW;
                        break;
                    case 10103:
                        team = Team.BLUE;
                        break;
                    case 10104:
                        team = Team.GREEN;
                        break;
                    default:
                        team = Team.NULL;
                        break;
                }
                if (team != Team.NULL) {
                    playerData.setTeam(team);
                    this.updatePlayerItem(arena, player);
                    arena.getPlayerDataMap().keySet().forEach(p ->
                            p.sendMessage(CrystalWars.getInstance().getLang(p).translateString("tips_join_team", player.getName(), Utils.getShowTeam(p, team)))
                    );
                }
            }else if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME) {
                switch (tag.getString(ItemManager.INTERNAL_ID_TAG)) {
                    case ItemManager.ITEM_INTERNALID_PLATFORM:
                        int minY = NukkitTypeUtils.getNukkitType() == NukkitTypeUtils.NukkitType.POWER_NUKKIT_X ? -62 : 2;
                        if (player.getFloorY() > minY) {
                            //生成平台
                            HashMap<Position, Block> positions = new HashMap<>();
                            Position floor = player.floor();
                            floor.y -= 1;
                            for (int x=-2; x<2; x++) {
                                for (int z=-2; z<2; z++) {
                                    Position position = floor.add(x, 0, z);
                                    //将不可站立的方块换成可以站立的
                                    Block block = player.getLevel().getBlock(position);
                                    if (block.canPassThrough()) {
                                        positions.put(position, block);
                                        player.getLevel().setBlock(position, Utils.getTeamColorItem(Item.get(Item.WOOL), playerData.getTeam()).getBlock());
                                        arena.getPlayerPlaceBlocks().add(position);
                                    }
                                }
                            }
                            player.teleport(floor.add(0, 1, 0));
                            Server.getInstance().getScheduler().scheduleDelayedTask(CrystalWars.getInstance(), () -> {
                                for (Map.Entry<Position, Block> entry : positions.entrySet()) {
                                    entry.getKey().getLevel().setBlock(entry.getKey(), entry.getValue());
                                    arena.getPlayerPlaceBlocks().remove(entry.getKey());
                                }
                            }, 20 * 10);
                            item.setCount(1);
                            player.getInventory().removeItem(item);
                            event.setCancelled(true);
                        }
                        break;
                    //TODO
                }
            }
        }
    }

    private void updatePlayerItem(BaseArena arena, Player player) {
        PlayerData playerData = arena.getPlayerData(player);
        // 队伍选择物品
        ArrayList<Team> canUseTeams = arena.getCanUseTeams();
        Item item;
        if (playerData.getTeam() != Team.RED && canUseTeams.contains(Team.RED)) {
            item = ItemManager.get(player, 10101);
        }else {
            item = ItemManager.get(player, 10100);
        }
        player.getInventory().setItem(2, item);
        if (playerData.getTeam() != Team.YELLOW && canUseTeams.contains(Team.YELLOW)) {
            item = ItemManager.get(player, 10102);
        }else {
            item = ItemManager.get(player, 10100);
        }
        player.getInventory().setItem(3, item);
        if (playerData.getTeam() != Team.BLUE && canUseTeams.contains(Team.BLUE)) {
            item = ItemManager.get(player, 10103);
        }else {
            item = ItemManager.get(player, 10100);
        }
        player.getInventory().setItem(4, item);
        if (playerData.getTeam() != Team.GREEN && canUseTeams.contains(Team.GREEN)) {
            item = ItemManager.get(player, 10104);
        }else {
            item = ItemManager.get(player, 10100);
        }
        player.getInventory().setItem(5, item);

        CompoundTag tag;
        Item cap = Item.get(Item.LEATHER_CAP);
        tag = cap.hasCompoundTag() ? cap.getNamedTag() : new CompoundTag();
        tag.putByte("Unbreakable", 1);
        tag.putBoolean(ItemManager.PROPERTY_CANNOTTAKEITOFF_TAG, true);
        cap.setNamedTag(tag);
        player.getInventory().setHelmet(Utils.getTeamColorItem(cap, playerData.getTeam()));

        Item tunic = Item.get(Item.LEATHER_TUNIC);
        tag = tunic.hasCompoundTag() ? tunic.getNamedTag() : new CompoundTag();
        tag.putByte("Unbreakable", 1);
        tag.putBoolean(ItemManager.PROPERTY_CANNOTTAKEITOFF_TAG, true);
        tunic.setNamedTag(tag);
        player.getInventory().setChestplate(Utils.getTeamColorItem(tunic, playerData.getTeam()));
    }

/*    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        EntityProjectile entity = event.getEntity();
        if (entity.shootingEntity instanceof Player) {
            Player player = (Player) entity.shootingEntity;
            Item item = player.getInventory().getItemInHand();
            if (item.hasCompoundTag() && item.getNamedTag().getBoolean(ItemManager.IS_CRYSTALWARS_TAG)) {
                CompoundTag namedTag = entity.namedTag == null ? new CompoundTag() : entity.namedTag;
                namedTag.putBoolean(ItemManager.IS_CRYSTALWARS_TAG, true)
                        .putString(ItemManager.INTERNAL_ID_TAG, item.getNamedTag().getString(ItemManager.INTERNAL_ID_TAG));
                entity.namedTag = namedTag;
            }
        }
    }*/

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        BaseArena arena = this.getListenerRoom(player.getLevel());
        if (arena == null) {
            return;
        }

        Item item = event.getItem();
        if (item.hasCompoundTag() && item.getNamedTag().getBoolean(ItemManager.IS_CRYSTALWARS_TAG)) {
            switch (item.getNamedTag().getInt(ItemManager.INTERNAL_ID_TAG_OLD)) {
                case 10000:
                    int nowTick = Server.getInstance().getTick();
                    int lastTick = item.getNamedTag().getInt("lastTick");
                    if (lastTick == 0 || nowTick - lastTick > 40) {
                        player.sendTip(CrystalWars.getInstance().getLang(player).translateString("tips_clickAgainToQuitRoom"));
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
                        Player damager = (Player) entityDamageByEntityEvent.getDamager();
                        PlayerData damagerData = arena.getPlayerData(damager);
                        if (!arena.isAllowTeammateDamage() && playerData.getTeam() == damagerData.getTeam()) {
                            event.setCancelled(true);
                            return;
                        }
                        playerData.setLastDamager(damager);
                    }
                }

                if (event.getFinalDamage() + 1 > player.getHealth()) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                            Player damager = (Player) entityDamageByEntityEvent.getDamager();
                            PlayerData damagerData = arena.getPlayerData(damager);
                            if (damagerData.getPlayerStatus() != PlayerData.PlayerStatus.SURVIVE) {
                                event.setCancelled(true);
                                return;
                            }
                            damagerData.addKillCount();
                            playerData.setLastDamager(damager);
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
        if (sourceItem.isArmor() || (sourceItem.hasCompoundTag() && sourceItem.getNamedTag().getBoolean(ItemManager.PROPERTY_CANNOTTAKEITOFF_TAG)) &&
                event.getHeldItem().getId() == 0) {
            event.setCancelled(true);
            return;
        }
        if ((sourceItem.hasCompoundTag() && sourceItem.getNamedTag().getBoolean(ItemManager.PROPERTY_CANNOTCLICKONINVENTORY_TAG)) ||
                (event.getHeldItem().hasCompoundTag() && event.getHeldItem().getNamedTag().getBoolean(ItemManager.PROPERTY_CANNOTCLICKONINVENTORY_TAG))) {
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
