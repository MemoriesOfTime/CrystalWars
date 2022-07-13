package cn.lanink.crystalwars.command.user.sub;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.command.BaseSubCommand;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

import java.util.LinkedList;

/**
 * @author LT_Name
 */
public class JoinRoom extends BaseSubCommand {

    public JoinRoom(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isPlayer();
    }

    @Override
    public String[] getAliases() {
        return new String[] { "加入", "join" };
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Language language = this.crystalWars.getLang(sender);
        if (!this.crystalWars.getArenas().isEmpty()) {
            Player player = (Player) sender;
            if (player.riding != null) {
                sender.sendMessage(language.translateString("tips_canNotJoinWhenRiding"));
                return true;
            }
            for (BaseArena arena : this.crystalWars.getArenas().values()) {
                if (arena.isPlaying(player)) {
                    sender.sendMessage(language.translateString("tips_isInRoom"));
                    return true;
                }
            }
            if (args.length < 2) {
                LinkedList<BaseArena> arenas = new LinkedList<>();
                for (BaseArena arena : this.crystalWars.getArenas().values()) {
                    if (arena.canJoin()) {
                        if (arena.getPlayerCount() > 0) {
                            arena.joinRoom(player);
                            sender.sendMessage(language.translateString("tips_joinRandomRoom"));
                            return true;
                        }
                        arenas.add(arena);
                    }
                }
                if (arenas.size() > 0) {
                    int index = CrystalWars.RANDOM.nextInt(arenas.size());
                    BaseArena arena = arenas.get(index);
                    arena.joinRoom(player);
                    sender.sendMessage(language.translateString("tips_joinRandomRoom"));
                    return true;
                }
            }else {
                String[] s = args[1].split(":");
                if (s.length == 2 && "mode".equals(s[0].toLowerCase().trim())) {
                    String modeName = s[1].toLowerCase().trim();
                    LinkedList<BaseArena> arenas = new LinkedList<>();
                    for (BaseArena room : this.crystalWars.getArenas().values()) {
                        if (room.canJoin() && room.getGameMode().equals(modeName)) {
                            if (room.getPlayerCount() > 0) {
                                room.joinRoom(player);
                                sender.sendMessage(language.translateString("tips_joinRandomRoom"));
                                return true;
                            }
                            arenas.add(room);
                        }
                    }
                    if (arenas.size() > 0) {
                        BaseArena room = arenas.get(CrystalWars.RANDOM.nextInt(arenas.size()));
                        room.joinRoom(player);
                        sender.sendMessage(language.translateString("tips_joinRandomRoom"));
                        return true;
                    }
                    sender.sendMessage(language.translateString("tips_canNotFoundAvailableRoom"));
                    return true;
                }else {
                    String world = args[1];
                    BaseArena arena = this.crystalWars.getArenas().get(world);
                    if (arena != null) {
                        if (arena.getArenaStatus() == BaseArena.ArenaStatus.LEVEL_NOT_LOADED) {
                            sender.sendMessage(language.translateString("tips_roomPreparing"));
                        }else if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME ||
                                arena.getArenaStatus() == BaseArena.ArenaStatus.VICTORY) {
                            sender.sendMessage(language.translateString("tips_roomStarted"));
                        }else if (arena.getPlayerCount() >= arena.getMaxPlayers()) {
                            sender.sendMessage(language.translateString("tips_roomFull"));
                        }else {
                            arena.joinRoom(player);
                        }
                    }else {
                        sender.sendMessage(language.translateString("tips_canNotFoundAvailableRoom"));
                    }
                    return true;
                }
            }
        }
        sender.sendMessage(language.translateString("tips_noAvailableRoom"));
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("roomName", CommandParamType.TEXT) };
    }

}
