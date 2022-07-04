package cn.lanink.crystalwars.listener.defaults;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.ArenaSet;
import cn.lanink.crystalwars.arena.ResourceGeneration;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.items.ItemManager;
import cn.lanink.crystalwars.items.generation.ItemGenerationConfigManager;
import cn.lanink.crystalwars.supplier.Supply;
import cn.lanink.crystalwars.supplier.config.SupplyConfigManager;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;

import java.util.ArrayList;

/**
 * @author LT_Name
 */
@SuppressWarnings("unused")
public class ArenaSetListener implements Listener {

    private final CrystalWars crystalWars;

    public ArenaSetListener(CrystalWars crystalWars) {
        this.crystalWars = crystalWars;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ArenaSet arenaSet = this.crystalWars.getArenaSetMap().get(player);
        if (arenaSet == null) {
            return;
        }

        Item item = event.getItem();
        if (!item.hasCompoundTag() || !item.getNamedTag().getBoolean(ItemManager.IS_CRYSTALWARS_TAG)) {
            return;
        }
        switch (item.getNamedTag().getInt(ItemManager.INTERNAL_ID_TAG)) {
            case 11001: //上一步
                event.setCancelled(true);
                arenaSet.setRoomSchedule(arenaSet.getBackRoomSchedule());
                break;
            case 11002: //下一步
                event.setCancelled(true);
                arenaSet.setRoomSchedule(arenaSet.getNextRoomSchedule());
                break;
            case 11003: //保存配置
                event.setCancelled(true);
                this.crystalWars.unloadArena(arenaSet.getWorldName());
                arenaSet.save();
                arenaSet.exit();
                this.crystalWars.loadArena(arenaSet.getWorldName());
                player.sendTitle("", CrystalWars.getInstance().getLang().translateString("arenaSet_configSaved"), 10, 40, 10);
                break;
            case 11004:
            case 11005:
            case 11006:
            case 11007:
            case 11008:
                break;
            default:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.onPlaceOrBreakBlock(event, event.getPlayer(), event.getItem(), event.getBlock());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        this.onPlaceOrBreakBlock(event, event.getPlayer(), event.getItem(), event.getBlock());
    }

    public void onPlaceOrBreakBlock(Event event, Player player, Item item, Block block) {
        ArenaSet arenaSet = this.crystalWars.getArenaSetMap().get(player);
        if (arenaSet == null) {
            return;
        }
        event.setCancelled(true);

        Vector3 newVector3 = new Vector3(block.getX(), block.getY(), block.getZ());
        newVector3.x = newVector3.getFloorX() + 0.5;
        newVector3.z = newVector3.getFloorZ() + 0.5;
        switch (arenaSet.getSetRoomSchedule()) {
            case 100: //设置游戏模式
                AdvancedFormWindowCustom custom1 = new AdvancedFormWindowCustom("设置游戏模式");
                custom1.addElement(new ElementDropdown("游戏模式", new ArrayList<>(CrystalWars.getARENA_CLASS().keySet()))); //0
                custom1.onResponded((formResponseCustom, cp) -> {
                    Config config = arenaSet.getConfig();
                    config.set("gameMode", formResponseCustom.getDropdownResponse(0).getElementContent());
                });
                player.showFormWindow(custom1);
                break;
            case 150: //设置等待出生点
                arenaSet.setWaitSpawn(newVector3);
                break;
            case 200: //设置各队出生点
                arenaSet.setTeamSpawn(Team.valueOf(item.getNamedTag().getString("CrystalWarsTeam")), newVector3);
                break;
            case 250: //设置各队水晶位置
                arenaSet.setTeamCrystal(Team.valueOf(item.getNamedTag().getString("CrystalWarsTeam")), newVector3);
                break;
            case 300: //设置各队商店位置
                arenaSet.setTeamShop(Team.valueOf(item.getNamedTag().getString("CrystalWarsTeam")), newVector3);
                break;
            case 350: //设置资源点
                if (item.getNamedTag().getInt(ItemManager.INTERNAL_ID_TAG) == 11007) {
                    AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(CrystalWars.getInstance().getLang().translateString("arenaSet_selectItemSpawnInit"));
                    custom.addElement(new ElementDropdown(
                            CrystalWars.getInstance().getLang().translateString("arenaSet_ItemSpawnInit"),
                            new ArrayList<>(ItemGenerationConfigManager.getITEM_GENERATION_CONFIG_MAP().keySet())
                    )); //0
                    custom.onResponded((formResponseCustom, cp) -> {
                        arenaSet.getResourceGenerations().add(
                                new ResourceGeneration(
                                        ItemGenerationConfigManager.getITEM_GENERATION_CONFIG_MAP()
                                                .get(formResponseCustom.getDropdownResponse(0).getElementContent()),
                                        newVector3)
                        );
                    });
                    player.showFormWindow(custom);
                }else {
                    arenaSet.getResourceGenerations().removeIf(next -> newVector3.floor().equals(next.getVector3().floor()));
                }
                break;
            case 400: //设置其他参数
                Language language = CrystalWars.getInstance().getLang();
                AdvancedFormWindowCustom custom2 = new AdvancedFormWindowCustom(language.translateString("arenaSet_setOtherParameters"));

                String canUseTeamCount = "2-" + (Team.values().length - 1);
                custom2.addElement(new ElementInput(language.translateString("arenaSet_availableTeamCount"), canUseTeamCount, canUseTeamCount)); //0
                custom2.addElement(new ElementInput(language.translateString("arenaSet_minPlayers"), "2", "2")); //1
                custom2.addElement(new ElementInput(language.translateString("arenaSet_maxPlayers"), "16", "16")); //2
                custom2.addElement(new ElementInput(language.translateString("arenaSet_waitTime"), "60", "60")); //3
                custom2.addElement(new ElementInput(language.translateString("arenaSet_gameTime"), "300", "300")); //4
                custom2.addElement(new ElementInput(language.translateString("arenaSet_overTime"), "180", "180")); //5
                custom2.addElement(new ElementInput(language.translateString("arenaSet_ceremonyTime"), "10", "10")); //6
                custom2.addElement(new ElementDropdown(language.translateString("arenaSet_shopSupplyConfig"), new ArrayList<>(SupplyConfigManager.getSUPPLY_CONFIG_MAP().keySet()))); //7
                custom2.addElement(new ElementToggle(language.translateString("arenaSet_isAllowedToDamageTeammates"), true)); //8
                custom2.addElement(new ElementInput(language.translateString("arenaSet_defaultCrystalHealth"), "100", "100")); //9

                custom2.onResponded((formResponseCustom, cp) -> {
                    arenaSet.setMaxTeamCount(Utils.toInt(formResponseCustom.getInputResponse(0)));
                    arenaSet.setMinPlayers(Utils.toInt(formResponseCustom.getInputResponse(1)));
                    arenaSet.setMaxPlayers(Utils.toInt(formResponseCustom.getInputResponse(2)));
                    arenaSet.setWaitTime(Utils.toInt(formResponseCustom.getInputResponse(3)));
                    arenaSet.setGameTime(Utils.toInt(formResponseCustom.getInputResponse(4)));
                    arenaSet.setOvertime(Utils.toInt(formResponseCustom.getInputResponse(5)));
                    arenaSet.setVictoryTime(Utils.toInt(formResponseCustom.getInputResponse(6)));
                    arenaSet.setSupply(new Supply(SupplyConfigManager.getSupplyConfig(formResponseCustom.getDropdownResponse(7).getElementContent())));
                    arenaSet.setAllowTeammateDamage(formResponseCustom.getToggleResponse(8));
                    arenaSet.setDefaultEndCrystalHealth(Utils.toInt(formResponseCustom.getInputResponse(9)));
                });

                player.showFormWindow(custom2);
                break;
        }
    }

}
