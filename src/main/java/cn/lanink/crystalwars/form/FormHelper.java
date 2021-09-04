package cn.lanink.crystalwars.form;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.gamecore.form.element.ResponseElementButton;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowModal;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButtonImageData;
import org.jetbrains.annotations.NotNull;

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
            }else if (arena.getPlayerDataMap().size() >= arena.getMaxPlayers()) {
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

}
