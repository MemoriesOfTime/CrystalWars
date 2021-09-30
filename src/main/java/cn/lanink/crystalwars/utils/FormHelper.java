package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.player.PlayerSettingData;
import cn.lanink.crystalwars.player.PlayerSettingDataManager;
import cn.lanink.gamecore.form.element.ResponseElementButton;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowModal;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @author LT_Name
 */
public class FormHelper {

    private FormHelper() {
        throw new RuntimeException("哎呀！你不能实例化这个类！");
    }

    /**
     * 显示用户主菜单
     *
     * @param player 玩家
     */
    public static void sendUserMainMenu(@NotNull Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(CrystalWars.PLUGIN_NAME);

        simple.addButton(new ResponseElementButton("加入随机游戏房间").onClicked(
                cp -> Server.getInstance().dispatchCommand(
                        cp,
                        CrystalWars.getInstance().getCmdUser() +  " join"
                )
        ));
        simple.addButton(new ResponseElementButton("查看游戏房间列表").onClicked(FormHelper::sendUserArenaListMenu));

        player.showFormWindow(simple);
    }

    /**
     * 显示游戏房间列表菜单
     *
     * @param player 玩家
     */
    public static void sendUserArenaListMenu(@NotNull Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(CrystalWars.PLUGIN_NAME);
        
        for (Map.Entry<String, BaseArena> entry : CrystalWars.getInstance().getArenas().entrySet()) {
            simple.addButton(new ResponseElementButton(entry.getKey() + "\n游戏模式: " + entry.getValue().getGameMode())
                    .onClicked(cp -> sendArenaConfirmMenu(cp, entry.getKey())));
        }
        simple.addButton(new ResponseElementButton("返回", 
                new ElementButtonImageData("path", "textures/ui/cancel")).onClicked(FormHelper::sendUserMainMenu)
        );
        
        player.showFormWindow(simple);
    }

    /**
     * 显示确认加入游戏房间菜单
     *
     * @param player 玩家
     * @param world 游戏房间世界
     */
    public static void sendArenaConfirmMenu(@NotNull Player player, @NotNull String world) {
        AdvancedFormWindowModal modal;
        BaseArena arena = CrystalWars.getInstance().getArenas().get(world);
        if (arena != null) {
            if (arena.getArenaStatus() == BaseArena.ArenaStatus.LEVEL_NOT_LOADED) {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                        "游戏房间: " + world + " 正在准备中，无法加入！",
                        "返回",
                        "返回");
                modal.onClickedTrue(FormHelper::sendUserArenaListMenu);
                modal.onClickedFalse(FormHelper::sendUserArenaListMenu);
            }else if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME ||
                    arena.getArenaStatus() == BaseArena.ArenaStatus.VICTORY) {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                        "游戏房间: " + world + " 正在游戏中，无法加入！",
                        "返回",
                        "返回");
                modal.onClickedTrue(FormHelper::sendUserArenaListMenu);
                modal.onClickedFalse(FormHelper::sendUserArenaListMenu);
            }else if (arena.getPlayerCount() >= arena.getMaxPlayers()) {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                        "游戏房间: " + world + " 人数已满，无法加入！",
                        "返回",
                        "返回");
            }else {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                        "确定要加入游戏房价: " + world + " ?",
                        "确认",
                        "返回");
                modal.onClickedTrue((p) ->
                        Server.getInstance().dispatchCommand(p, CrystalWars.getInstance().getCmdUser() + " join " + arena.getLevelName()));
                modal.onClickedFalse(FormHelper::sendUserArenaListMenu);
            }
        }else {
            modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                    "游戏房间: " + world + " 不存在！无法加入！",
                    "返回", 
                    "返回");
            modal.onClickedTrue(FormHelper::sendUserArenaListMenu);
            modal.onClickedFalse(FormHelper::sendUserArenaListMenu);
        }
        
        player.showFormWindow(modal);
    }


    /**
     * 显示管理主菜单
     *
     * @param player 玩家
     */
    public static void sendAdminMainMenu(@NotNull Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(CrystalWars.PLUGIN_NAME);

        simple.addButton(new ResponseElementButton("创建游戏房间")
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " CreateArena")));
        simple.addButton(new ResponseElementButton("设置游戏房间")
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " SetArena")));
        simple.addButton(new ResponseElementButton("卸载所有游戏房间")
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " UnloadArena")));
        simple.addButton(new ResponseElementButton("重载配置")
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " reload")));

        player.showFormWindow(simple);
    }

    public static void sendAdminCreateArena(@NotNull Player player) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("创建游戏房间");

        ArrayList<String> list = new ArrayList<>();
        for (Level level : Server.getInstance().getLevels().values()) {
            String folderName = level.getFolderName();
            if (!CrystalWars.getInstance().getArenaConfigs().containsKey(folderName)) {
                list.add(folderName);
            }
        }
        custom.addElement(new ElementDropdown("\n\n请选择地图", list)); //0

        custom.onResponded((formResponseCustom, cp) -> Server.getInstance().dispatchCommand(
                cp,
                CrystalWars.getInstance().getCmdAdmin() + " CreateArena " + formResponseCustom.getDropdownResponse(0).getElementContent()
        ));

        player.showFormWindow(custom);
    }

    public static void sendAdminSetArena(@NotNull Player player) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom("设置游戏房间");

        custom.addElement(new ElementDropdown("\n\n请选择要设置的游戏房间", new ArrayList<>(CrystalWars.getInstance().getArenaConfigs().keySet()))); //0

        custom.onResponded((formResponseCustom, cp) -> Server.getInstance().dispatchCommand(
                cp,
                CrystalWars.getInstance().getCmdAdmin() + " SetArena " + formResponseCustom.getDropdownResponse(0).getElementContent()
        ));

        player.showFormWindow(custom);
    }

    /**
     * 获取玩家个性化设置界面
     *
     * @param player 玩家
     * @return 个性化设置界面
     */
    public static AdvancedFormWindowCustom getPlayerSetting(@NotNull Player player) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(
                "CrystalWars - 个性化设置",
                new ArrayList<>(),
                new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, "https://z3.ax1x.com/2021/09/17/4M5vz8.gif")
        );
        PlayerSettingData oldData = PlayerSettingDataManager.getData(player);

        custom.addElement(new ElementLabel("CrystalWars - 个性化设置\n")); //0

        int defaultOptionIndex;
        switch (oldData.getShopType()) {
            case CHEST:
                defaultOptionIndex = 1;
                break;
            case GUI:
                defaultOptionIndex = 2;
                break;
            case AUTO:
            default:
                defaultOptionIndex = 0;
                break;
        }
        custom.addElement(new ElementDropdown("商店界面类型", Arrays.asList("自动", "箱子商店", "GUI商店"), defaultOptionIndex)); //1

        custom.onResponded((formResponseCustom, cp) -> {
            PlayerSettingData data = PlayerSettingDataManager.getData(cp);

            switch (formResponseCustom.getDropdownResponse(1).getElementID()) {
                case 1:
                    data.setShopType(PlayerSettingData.ShopType.CHEST);
                    break;
                case 2:
                    data.setShopType(PlayerSettingData.ShopType.GUI);
                    break;
                case 0:
                default:
                    data.setShopType(PlayerSettingData.ShopType.AUTO);
                    break;
            }

            data.save();
        });

        return custom;
    }

}
