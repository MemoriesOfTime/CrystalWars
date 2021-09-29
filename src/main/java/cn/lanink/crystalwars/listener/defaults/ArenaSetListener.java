package cn.lanink.crystalwars.listener.defaults;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.ArenaSet;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.items.ItemManager;
import cn.lanink.crystalwars.supplier.Supply;
import cn.lanink.crystalwars.supplier.config.SupplyConfigManager;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;

import java.util.ArrayList;

/**
 * @author LT_Name
 */
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
        event.setCancelled(true);

        Item item = event.getItem();
        if (!item.hasCompoundTag() || !item.getNamedTag().getBoolean(ItemManager.IS_CRYSTALWARS_TAG)) {
            return;
        }
        switch (item.getNamedTag().getInt(ItemManager.INTERNAL_ID_TAG)) {
            case 11001: //上一步
                arenaSet.setRoomSchedule(arenaSet.getBackRoomSchedule());
                break;
            case 11002: //下一步
                arenaSet.setRoomSchedule(arenaSet.getNextRoomSchedule());
                break;
            case 11003: //保存配置
                this.crystalWars.unloadArena(arenaSet.getWorldName());
                arenaSet.save();
                arenaSet.exit();
                this.crystalWars.loadArena(arenaSet.getWorldName());
                player.sendTitle("", "配置已保存！", 10, 40, 10);
                break;
            case 11004:
            case 11005:
            case 11006:
                if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR ||
                        event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
                    return;
                }
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
                        Block block = event.getBlock();
                        arenaSet.setWaitSpawn(block);
                        break;
                    case 200: //设置各队出生点
                        arenaSet.setTeamSpawn(Team.valueOf(item.getNamedTag().getString("CrystalWarsTeam")), event.getBlock());
                        break;
                    case 250: //设置各队水晶位置
                        arenaSet.setTeamCrystal(Team.valueOf(item.getNamedTag().getString("CrystalWarsTeam")), event.getBlock());
                        break;
                    case 300: //设置各队商店位置
                        arenaSet.setTeamShop(Team.valueOf(item.getNamedTag().getString("CrystalWarsTeam")), event.getBlock());
                        break;
                    case 350: //设置资源点
                        //TODO
                        break;
                    case 400: //设置其他参数
                        AdvancedFormWindowCustom custom2 = new AdvancedFormWindowCustom("设置其他参数");

                        custom2.addElement(new ElementInput("房间开始游戏最小人数", "2", "2")); //0
                        custom2.addElement(new ElementInput("房间最大人数", "16", "16")); //1
                        custom2.addElement(new ElementInput("游戏开始等待时间", "60", "60")); //2
                        custom2.addElement(new ElementInput("游戏时间（超出将进入加时赛，水晶会直接爆炸）", "300", "300")); //3
                        custom2.addElement(new ElementInput("加时赛时间", "180", "180")); //4
                        custom2.addElement(new ElementInput("胜利结算时间", "10", "10")); //5
                        custom2.addElement(new ElementDropdown("供给物品配置(商店)", new ArrayList<>(SupplyConfigManager.getSUPPLY_CONFIG_MAP().keySet()))); //6

                        custom2.onResponded((formResponseCustom, cp) -> {
                            arenaSet.setMinPlayers(Utils.toInt(formResponseCustom.getInputResponse(0)));
                            arenaSet.setMaxPlayers(Utils.toInt(formResponseCustom.getInputResponse(1)));
                            arenaSet.setWaitTime(Utils.toInt(formResponseCustom.getInputResponse(2)));
                            arenaSet.setGameTime(Utils.toInt(formResponseCustom.getInputResponse(3)));
                            arenaSet.setOvertime(Utils.toInt(formResponseCustom.getInputResponse(4)));
                            arenaSet.setVictoryTime(Utils.toInt(formResponseCustom.getInputResponse(5)));
                            arenaSet.setSupply(new Supply(SupplyConfigManager.getSupplyConfig(formResponseCustom.getDropdownResponse(6).getElementContent())));
                        });

                        player.showFormWindow(custom2);
                        break;
                }
                break;
        }
    }

}