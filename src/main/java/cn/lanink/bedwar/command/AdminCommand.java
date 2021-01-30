package cn.lanink.bedwar.command;

import cn.lanink.bedwar.BedWars;
import cn.lanink.bedwar.ui.GuiCreate;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class AdminCommand extends Command {

    BedWars murderMystery = BedWars.getInstance();
    private final String name;

    public AdminCommand(String name) {
        super(name, "BedWar 管理命令", "/" + name + " help");
        this.name = name;
        this.setPermission("BedWar.op");
        this.setPermissionMessage("§c你没有权限");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            if (player.isOp()) {
                if (strings.length > 0) {
                    switch (strings[0]) {
                        case "设置出生点": case "setspawn":
                            murderMystery.roomSetSpawn(player, murderMystery.getRoomConfig(player.getLevel()));
                            commandSender.sendMessage(murderMystery.getLanguage().adminSetSpawn);
                            return true;
                        case "添加随机出生点": case "addrandomspawn":
                            murderMystery.roomAddRandomSpawn(player, murderMystery.getRoomConfig(player.getLevel()));
                            commandSender.sendMessage(murderMystery.getLanguage().adminAddRandomSpawn);
                            return true;
                        case "添加金锭生成点": case "addgoldspawn":
                            murderMystery.roomAddGoldSpawn(player, murderMystery.getRoomConfig(player.getLevel()));
                            commandSender.sendMessage(murderMystery.getLanguage().adminAddGoldSpawn);
                            return true;
                        case "设置金锭产出间隔": case "setgoldspawntime":
                            if (strings.length == 2) {
                                if (strings[1].matches("[0-9]*")) {
                                    murderMystery.roomSetGoldSpawnTime(Integer.valueOf(strings[1]), murderMystery.getRoomConfig(player.getLevel()));
                                    commandSender.sendMessage(
                                            murderMystery.getLanguage().adminSetGoldSpawnTime.replace("%time%", strings[1]));
                                }else {
                                    commandSender.sendMessage(murderMystery.getLanguage().adminNotNumber);
                                }
                            }else {
                                commandSender.sendMessage(murderMystery.getLanguage().cmdHelp.replace("%cmdName%", this.name));
                            }
                            return true;
                        case "设置等待时间": case "setwaittime":
                            if (strings.length == 2) {
                                if (strings[1].matches("[0-9]*")) {
                                    murderMystery.roomSetWaitTime(Integer.valueOf(strings[1]), murderMystery.getRoomConfig(player.getLevel()));
                                    commandSender.sendMessage(murderMystery.getLanguage().adminSetWaitTime.replace("%time%", strings[1]));
                                }else {
                                    commandSender.sendMessage(murderMystery.getLanguage().adminNotNumber);
                                }
                            }else {
                                commandSender.sendMessage(murderMystery.getLanguage().cmdHelp.replace("%cmdName%", this.name));
                            }
                            return true;
                        case "设置游戏时间": case "setgametime":
                            if (strings.length == 2) {
                                if (strings[1].matches("[0-9]*")) {
                                    if (Integer.parseInt(strings[1]) > 60) {
                                        murderMystery.roomSetGameTime(Integer.valueOf(strings[1]), murderMystery.getRoomConfig(player.getLevel()));
                                        commandSender.sendMessage(murderMystery.getLanguage().adminSetGameTime.replace("%time%", strings[1]));
                                    } else {
                                        commandSender.sendMessage(murderMystery.getLanguage().adminSetGameTimeShort);
                                    }
                                }else {
                                    commandSender.sendMessage(murderMystery.getLanguage().adminNotNumber);
                                }
                            }else {
                                commandSender.sendMessage(murderMystery.getLanguage().cmdHelp.replace("%cmdName%", this.name));
                            }
                            return true;
                        case "reload": case "重载":
                            murderMystery.reLoadRooms();
                            commandSender.sendMessage(murderMystery.getLanguage().adminReload);
                            return true;
                        case "unload":
                            murderMystery.unloadRooms();
                            commandSender.sendMessage(murderMystery.getLanguage().adminUnload);
                            return true;
                        default:
                            commandSender.sendMessage(
                                    murderMystery.getLanguage().adminHelp.replace("%cmdName%", this.name));
                            return true;
                    }
                }else {
                    GuiCreate.sendAdminMenu(player);
                    return true;
                }
            }else {
                commandSender.sendMessage(murderMystery.getLanguage().noPermission);
                return true;
            }
        }else {
            if(strings.length > 0 && strings[0].equals("reload")) {
                murderMystery.reLoadRooms();
                commandSender.sendMessage(murderMystery.getLanguage().adminReload);
                return true;
            }else if(strings.length > 0 && strings[0].equals("unload")) {
                murderMystery.unloadRooms();
                commandSender.sendMessage(murderMystery.getLanguage().adminUnload);
                return true;
            }else {
                commandSender.sendMessage(murderMystery.getLanguage().useCmdInCon);
            }
            return true;
        }
    }

}
