package cn.lanink.bedwar.command;

import cn.lanink.bedwar.BedWars;
import cn.lanink.bedwar.room.Room;
import cn.lanink.bedwar.ui.GuiCreate;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class UserCommand extends Command {

    BedWars murderMystery = BedWars.getInstance();
    public final String name;

    public UserCommand(String name) {
        super(name, "BedWar 游戏命令", "/" + name + " help");
        this.name = name;
        this.setPermission("BedWar.all");
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            if (strings.length > 0) {
                switch (strings[0]) {
                    case "join": case "加入":
                        if (murderMystery.getRooms().size() > 0) {
                            for (Room room : murderMystery.getRooms().values()) {
                                if (room.isPlaying(player)) {
                                    commandSender.sendMessage(murderMystery.getLanguage().joinRoomOnRoom);
                                    return true;
                                }
                            }
                            if (player.riding != null) {
                                commandSender.sendMessage(murderMystery.getLanguage().joinRoomOnRiding);
                                return true;
                            }
                            if (strings.length < 2) {
                                for (Room room : murderMystery.getRooms().values()) {
                                    if (room.getMode() == 0 || room.getMode() == 1) {
                                        room.joinRoom(player);
                                        commandSender.sendMessage(murderMystery.getLanguage().joinRandomRoom);
                                        return true;
                                    }
                                }
                            }else if (murderMystery.getRooms().containsKey(strings[1])) {
                                Room room = murderMystery.getRooms().get(strings[1]);
                                if (room.getMode() == 2 || room.getMode() == 3) {
                                    commandSender.sendMessage(murderMystery.getLanguage().joinRoomIsPlaying);
                                }else if (room.getPlayers().values().size() > 15) {
                                    commandSender.sendMessage(murderMystery.getLanguage().joinRoomIsFull);
                                } else {
                                    room.joinRoom(player);
                                }
                                return true;
                            }else {
                                commandSender.sendMessage(murderMystery.getLanguage().joinRoomIsNotFound);
                                return true;
                            }
                        }
                        commandSender.sendMessage(murderMystery.getLanguage().joinRoomNotAvailable);
                        return true;
                    case "quit": case "退出":
                        for (Room room : murderMystery.getRooms().values()) {
                            if (room.isPlaying(player)) {
                                room.quitRoom(player, true);
                                commandSender.sendMessage(murderMystery.getLanguage().quitRoom);
                                return true;
                            }
                        }
                        commandSender.sendMessage(murderMystery.getLanguage().quitRoomNotInRoom);
                        return true;
                    case "list": case "列表":
                        StringBuilder list = new StringBuilder();
                        for (String string : murderMystery.getRooms().keySet()) {
                            list.append(string).append(" ");
                        }
                        commandSender.sendMessage(
                                murderMystery.getLanguage().listRoom.replace("%list%", String.valueOf(list)));
                        return true;
                    default:
                        commandSender.sendMessage(
                                murderMystery.getLanguage().userHelp.replace("%cmdName%", this.name));
                        return true;
                }
            }else {
                GuiCreate.sendUserMenu(player);
                return true;
            }
        }else {
            commandSender.sendMessage(murderMystery.getLanguage().useCmdInCon);
            return true;
        }
    }

}
