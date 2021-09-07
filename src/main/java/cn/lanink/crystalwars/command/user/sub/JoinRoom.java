package cn.lanink.crystalwars.command.user.sub;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.command.BaseSubCommand;
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
        return new String[] { "加入" };
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.crystalWars.getArenas().isEmpty()) {
            Player player = (Player) sender;
            if (player.riding != null) {
                sender.sendMessage("你不能在骑乘状态下加入房间！");
                return true;
            }
            for (BaseArena arena : this.crystalWars.getArenas().values()) {
                if (arena.isPlaying(player)) {
                    sender.sendMessage("你已经加入一个游戏房间了！");
                    return true;
                }
            }
            if (args.length < 2) {
                LinkedList<BaseArena> arenas = new LinkedList<>();
                for (BaseArena arena : this.crystalWars.getArenas().values()) {
                    if (arena.canJoin()) {
                        if (arena.getPlayerDataMap().size() > 0) {
                            arena.joinRoom(player);
                            sender.sendMessage("§a已为你随机分配房间！");
                            return true;
                        }
                        arenas.add(arena);
                    }
                }
                if (arenas.size() > 0) {
                    BaseArena arena = arenas.get(CrystalWars.RANDOM.nextInt(arenas.size()));
                    arena.joinRoom(player);
                    sender.sendMessage("§a已为你随机分配房间！");
                    return true;
                }
            }else {
                String[] s = args[1].split(":");
                if (s.length == 2 && "mode".equals(s[0].toLowerCase().trim())) {
                    String modeName = s[1].toLowerCase().trim();
                    LinkedList<BaseArena> arenas = new LinkedList<>();
                    for (BaseArena room : this.crystalWars.getArenas().values()) {
                        if (room.canJoin() && room.getGameMode().equals(modeName)) {
                            if (room.getPlayerDataMap().size() > 0) {
                                room.joinRoom(player);
                                sender.sendMessage("§a已为你随机分配房间！");
                                return true;
                            }
                            arenas.add(room);
                        }
                    }
                    if (arenas.size() > 0) {
                        BaseArena room = arenas.get(CrystalWars.RANDOM.nextInt(arenas.size()));
                        room.joinRoom(player);
                        sender.sendMessage("§a已为你随机分配房间！");
                        return true;
                    }
                    sender.sendMessage("§a暂无符合条件的房间！");
                    return true;
                }else {
                    String world = args[1];
                    BaseArena arena = this.crystalWars.getArenas().get(world);
                    if (arena != null) {
                        if (arena.getArenaStatus() == BaseArena.ArenaStatus.LEVEL_NOT_LOADED) {
                            sender.sendMessage("§a房间初始化中，请稍后...");
                        }else if (arena.getArenaStatus() == BaseArena.ArenaStatus.GAME ||
                                arena.getArenaStatus() == BaseArena.ArenaStatus.VICTORY) {
                            sender.sendMessage("§a该房间正在游戏中，请稍后...");
                        }else if (arena.getPlayerDataMap().size() >= arena.getMaxPlayers()) {
                            sender.sendMessage("§a该房间已满人，请稍后...");
                        }else {
                            arena.joinRoom(player);
                        }
                    }else {
                        sender.sendMessage("§a暂无符合条件的房间！");
                    }
                    return true;
                }
            }
        }
        sender.sendMessage("§a暂无可用的房间！");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] { CommandParameter.newType("roomName", CommandParamType.TEXT) };
    }

}
