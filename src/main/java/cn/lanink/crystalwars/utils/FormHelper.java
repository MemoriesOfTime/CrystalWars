package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.player.PlayerSettingData;
import cn.lanink.crystalwars.player.PlayerSettingDataManager;
import cn.lanink.crystalwars.theme.ThemeManager;
import cn.lanink.gamecore.form.element.ResponseElementButton;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowModal;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.lanink.gamecore.utils.Language;
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
        Language language = CrystalWars.getInstance().getLang();
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(CrystalWars.PLUGIN_NAME);

        simple.addButton(new ResponseElementButton(language.translateString("form_main_joinRandomRoom")).onClicked(
                cp -> Server.getInstance().dispatchCommand(
                        cp,
                        CrystalWars.getInstance().getCmdUser() +  " join"
                )
        ));
        simple.addButton(new ResponseElementButton(language.translateString("form_main_checkRoomList")).onClicked(FormHelper::sendUserArenaListMenu));

        player.showFormWindow(simple);
    }

    /**
     * 显示游戏房间列表菜单
     *
     * @param player 玩家
     */
    public static void sendUserArenaListMenu(@NotNull Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(CrystalWars.PLUGIN_NAME);
        Language language = CrystalWars.getInstance().getLang();
        for (Map.Entry<String, BaseArena> entry : CrystalWars.getInstance().getArenas().entrySet()) {
            simple.addButton(new ResponseElementButton(entry.getKey() + "\n" + language.translateString("form_base_mode") + entry.getValue().getGameMode())
                    .onClicked(cp -> sendArenaConfirmMenu(cp, entry.getKey())));
        }
        simple.addButton(new ResponseElementButton(language.translateString("form_base_return"),
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
        Language language = CrystalWars.getInstance().getLang();
        String returnButtonContent = language.translateString("form_base_return");
        if (arena != null) {
            if (arena.getArenaStatus() == BaseArena.ArenaStatus.LEVEL_NOT_LOADED) {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME, language.translateString("form_confirm_roomNotPrepared", world),
                        returnButtonContent,
                        returnButtonContent);
                modal.onClickedTrue(FormHelper::sendUserArenaListMenu);
                modal.onClickedFalse(FormHelper::sendUserArenaListMenu);
            }else if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME ||
                    arena.getArenaStatus() == BaseArena.ArenaStatus.VICTORY) {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                        language.translateString("form_confirm_roomHasStarted", world),
                        returnButtonContent,
                        returnButtonContent);
                modal.onClickedTrue(FormHelper::sendUserArenaListMenu);
                modal.onClickedFalse(FormHelper::sendUserArenaListMenu);
            }else if (arena.getPlayerCount() >= arena.getMaxPlayers()) {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                        language.translateString("form_confirm_roomIsFull", world),
                        returnButtonContent,
                        returnButtonContent);
            }else {
                modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                        language.translateString("form_confirm_isJoinRoom", world),
                        language.translateString("form_base_confirm"),
                        returnButtonContent);
                modal.onClickedTrue((p) ->
                        Server.getInstance().dispatchCommand(p, CrystalWars.getInstance().getCmdUser() + " join " + arena.getLevelName()));
                modal.onClickedFalse(FormHelper::sendUserArenaListMenu);
            }
        }else {
            modal = new AdvancedFormWindowModal(CrystalWars.PLUGIN_NAME,
                    language.translateString("form_confirm_roomNotFound", world),
                    returnButtonContent,
                    returnButtonContent);
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
        Language language = CrystalWars.getInstance().getLang();
        simple.addButton(new ResponseElementButton(language.translateString("form_base_createRoom"))
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " CreateArena")));
        simple.addButton(new ResponseElementButton(language.translateString("form_base_setRoom"))
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " SetArena")));
        simple.addButton(new ResponseElementButton(language.translateString("form_base_disableRoom"))
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " UnloadArena")));
        simple.addButton(new ResponseElementButton(language.translateString("form_base_reloadConfig"))
                .onClicked(cp -> Server.getInstance().dispatchCommand(cp, CrystalWars.getInstance().getCmdAdmin() + " reload")));

        player.showFormWindow(simple);
    }

    public static void sendAdminCreateArena(@NotNull Player player) {
        Language language = CrystalWars.getInstance().getLang();
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(language.translateString("form_base_createRoom"));

        ArrayList<String> list = new ArrayList<>();
        for (Level level : Server.getInstance().getLevels().values()) {
            String folderName = level.getFolderName();
            if (!CrystalWars.getInstance().getArenaConfigs().containsKey(folderName)) {
                list.add(folderName);
            }
        }
        custom.addElement(new ElementDropdown("\n\n"+language.translateString("form_adminCreateArena_selectLevel"), list)); //0

        custom.onResponded((formResponseCustom, cp) -> Server.getInstance().dispatchCommand(
                cp,
                CrystalWars.getInstance().getCmdAdmin() + " CreateArena " + formResponseCustom.getDropdownResponse(0).getElementContent()
        ));

        player.showFormWindow(custom);
    }

    public static void sendAdminSetArena(@NotNull Player player) {
        Language language = CrystalWars.getInstance().getLang();
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(language.translateString("form_base_setRoom"));

        custom.addElement(new ElementDropdown("\n\n"+language.translateString("form_adminCreateArena_selectRoom"), new ArrayList<>(CrystalWars.getInstance().getArenaConfigs().keySet()))); //0

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
        Language language = CrystalWars.getInstance().getLang();
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(
                language.translateString("form_playerSetting_title"),
                new ArrayList<>(),
                new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_URL, "https://z3.ax1x.com/2021/09/17/4M5vz8.gif")
        );
        PlayerSettingData oldData = PlayerSettingDataManager.getData(player);

        custom.addElement(new ElementLabel(language.translateString("form_playerSetting_title")+"\n")); //0

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
        custom.addElement(new ElementDropdown(language.translateString("form_playerSetting_shopShowType"), Arrays.asList(language.translateString("form_playerSetting_shopShowType_auto"), language.translateString("form_playerSetting_shopShowType_chest"), language.translateString("form_playerSetting_shopShowType_gui")), defaultOptionIndex)); //1

        ArrayList<String> themes = new ArrayList<>(ThemeManager.getTHEME_MAP().keySet());
        defaultOptionIndex = 0;
        for (String theme : themes) {
            if (oldData.getTheme().equals(theme)) {
                break;
            }
            defaultOptionIndex++;
        }
        custom.addElement(new ElementDropdown(language.translateString("form_playerSetting_styleType"), themes, defaultOptionIndex)); //2

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

            data.setTheme(formResponseCustom.getDropdownResponse(2).getElementContent());

            data.save();
        });

        return custom;
    }

}
