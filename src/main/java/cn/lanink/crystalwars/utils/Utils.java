package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.supplier.SupplyConfigManager;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author LT_Name
 */
public class Utils {

    private Utils() {
        throw new RuntimeException(CrystalWars.getInstance().getLang().translateString("tips_canNotInstantiateClass"));
    }

    public static void clearEntity(BaseArena arena) {
        for (Entity entity : arena.getLevel().getEntities()) {
            if (!(entity instanceof Player)) {
                entity.close();
            }
        }
    }

    /**
     * 广播消息
     *
     * @param message 消息
     * @param baseArena 需要广播消息的房间
     */
    public static void broadcastMessage(@NotNull String message, @NotNull BaseArena baseArena) {
        broadcastMessage(message, baseArena.getPlayerDataMap().keySet());
    }

    public static void broadcastMessage(@NotNull String message, Collection<Player> players) {
        players.forEach(player -> player.sendMessage(message));
    }

    public static void executeCommand(@NotNull Player player, List<String> cmds) {
        for (String cmd : cmds) {
            String[] c = cmd.split("&");
            String command = c[0];
            if (command.startsWith("/")) {
                command = command.replaceFirst("/", "");
            }
            command = command.replace("{player}", player.getName())
                    .replace("@p", player.getName());
            if (c.length > 1 && "con".equals(c[1])) {
                try {
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), command);
                } catch (Exception e) {
                    CrystalWars.getInstance().getLogger().error(
                            "控制台权限执行命令时出现错误！" +
                                    " 玩家:" + player.getName() +
                                    " 错误:", e);
                }
                continue;
            }
            try {
                Server.getInstance().dispatchCommand(player, command);
            } catch (Exception e) {
                CrystalWars.getInstance().getLogger().error(
                        "玩家权限执行命令时出现错误！" +
                                " 玩家:" + player.getName() +
                                " 错误:", e);
            }
        }
    }

    /**
     * 设置Human实体皮肤
     *
     * @param human 实体
     * @param skin 皮肤
     */
    public static void setHumanSkin(EntityHuman human, Skin skin) {
        PlayerSkinPacket packet = new PlayerSkinPacket();
        packet.skin = skin;
        packet.newSkinName = skin.getSkinId();
        packet.oldSkinName = human.getSkin().getSkinId();
        packet.uuid = human.getUniqueId();
        HashSet<Player> players = new HashSet<>(human.getViewers().values());
        if (human instanceof Player) {
            players.add((Player) human);
        }
        if (!players.isEmpty()) {
            Server.broadcastPacket(players, packet);
        }
        human.setSkin(skin);
    }

    public static String getShowTeam(Team team) {
        return getShowTeam(null, team);
    }

    public static String getShowTeam(Player player, @NotNull Team team) {
        switch (team) {
            case RED:
                return team.getStringColor() + CrystalWars.getInstance().getLang(player).translateString("teams_name_red");
            case YELLOW:
                return team.getStringColor() + CrystalWars.getInstance().getLang(player).translateString("teams_name_yellow");
            case BLUE:
                return team.getStringColor() + CrystalWars.getInstance().getLang(player).translateString("teams_name_blue");
            case GREEN:
                return team.getStringColor() + CrystalWars.getInstance().getLang(player).translateString("teams_name_green");
            case NULL:
            default:
                return team.getStringColor() + CrystalWars.getInstance().getLang(player).translateString("teams_name_noTeam");
        }
    }

    /**
     * 获取水晶实体百分比显示的血量
     *
     * @param crystal 水晶实体
     * @return 百分比显示的血量
     */
    public static String getShowHealth(CrystalWarsEntityEndCrystal crystal) {
        if (crystal == null || crystal.isClosed() || crystal.getHealth() < 1) {
            return "§c§l✘";
        }
        return NukkitMath.round((crystal.getHealth()/crystal.getMaxHealth()) * 100, 1) + "% ";
    }

    /**
     * 获取水晶实体显示的血量条
     *
     * @param crystal 水晶实体
     * @return 血量条
     */
    public static String getEntityShowHealth(CrystalWarsEntityEndCrystal crystal) {
        return getProgressBar((int) crystal.getHealth(), crystal.getMaxHealth()) + " §e" + getShowHealth(crystal);
    }

    /**
     * 进度条
     *
     * @param now 现在
     * @param max 最大
     * @return 进度条
     */
    public static String getProgressBar(int now, int max) {
        int needShow = Math.max(10, now) / (Math.max(10, max) / 10);
        StringBuilder string = new StringBuilder();
        for (int j = 0; j < 10; j++) {
            if (j < needShow) {
                string.append("§a▍");
            }else {
                string.append("§c▍");
            }
        }
        return string.toString();
    }

    public static double toDouble(Object object) {
        return new BigDecimal(object.toString()).doubleValue();
    }

    public static int toInt(Object object) {
        return new BigDecimal(object.toString()).intValue();
    }

    /**
     * Vector3 转为 String
     *
     * @param vector3 Vector3
     * @return String
     */
    public static String vector3ToString(Vector3 vector3) {
        return vector3.x + ":" + vector3.y + ":" +vector3.z;
    }

    /**
     * Vector3 转为 Map
     *
     * @param vector3 Vector3
     * @return Map
     */
    public static LinkedHashMap<String, Double> vector3ToMap(Vector3 vector3) {
        LinkedHashMap<String, Double> map = new LinkedHashMap<>();
        map.put("x", vector3.getX());
        map.put("y", vector3.getY());
        map.put("z", vector3.getZ());
        return map;
    }

    /**
     * String 转为 Vector3
     *
     * @param string 字符串
     * @return Vector3
     */
    public static Vector3 stringToVector3(String string) {
        String[] split = string.split(":");
        return new Vector3(
                toDouble(split[0]),
                toDouble(split[1]),
                toDouble(split[2])
        );
    }

    /**
     * Map 转为 Vector3
     *
     * @param map Map
     * @return Vector3
     */
    @SuppressWarnings("rawtypes")
    public static Vector3 mapToVector3(Map map) {
        return new Vector3(
                toDouble(map.get("x")),
                toDouble(map.get("y")),
                toDouble(map.get("z"))
        );
    }

    /**
     * 格式化倒计时 0 -> 00:00
     * @param time 时间
     * @return 格式化后的时间
     */
    public static String formatCountdown(int time) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(time/60) + ":" + format.format(time%60);
    }

    public static String getSpace(List<?> list) {
        return getSpace(list.size() + 1);
    }

    /**
     * 填充空格
     *
     * @param size 数量
     * @return 空格
     */
    public static String getSpace(int size) {
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < size;i++){
            s.append(" ");
        }
        return s.toString();
    }

    /**
     * 放烟花
     *
     * GitHub：<a href="https://github.com/PetteriM1/FireworkShow">https://github.com/PetteriM1/FireworkShow</a>
     * @param position 位置
     */
    public static void spawnFirework(Position position) {
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        CompoundTag ex = new CompoundTag();
        ex.putByteArray("FireworkColor",new byte[]{
                (byte) DyeColor.values()[CrystalWars.RANDOM.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].getDyeData()
        });
        ex.putByteArray("FireworkFade",new byte[0]);
        ex.putBoolean("FireworkFlicker", CrystalWars.RANDOM.nextBoolean());
        ex.putBoolean("FireworkTrail",CrystalWars.RANDOM.nextBoolean());
        ex.putByte("FireworkType",ItemFirework.FireworkExplosion.ExplosionType.values()
                [CrystalWars.RANDOM.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].ordinal());
        tag.putCompound("Fireworks",(new CompoundTag("Fireworks"))
                .putList(new ListTag<CompoundTag>("Explosions").add(ex)).putByte("Flight",1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",position.x+0.5D))
                .add(new DoubleTag("",position.y+0.5D))
                .add(new DoubleTag("",position.z+0.5D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("",0.0F))
                .add(new FloatTag("",0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        EntityFirework entity = new EntityFirework(position.getLevel().getChunk((int)position.x >> 4, (int)position.z >> 4), nbt);
        entity.spawnToAll();
    }

    /**
     * 根据队伍获取对应颜色的物品
     *
     * @param defaultItem 默认物品
     * @param team 队伍
     * @return 对应颜色的物品
     */
    public static Item getTeamColorItem(Item defaultItem, Team team) {
        Item air = Item.get(Item.AIR);
        if (defaultItem.hasCompoundTag()) {
            air.setNamedTag(defaultItem.getNamedTag());
        }
        if(!defaultItem.hasMeta()) {
            return air;
        }
        if(!SupplyConfigManager.TEAM_CHANGE_ITEM_IDS.contains(defaultItem.getId())) {
            return air;
        }

        //皮革护甲染色
        Item item = Item.get(defaultItem.getId(), defaultItem.getDamage(), defaultItem.getCount());
        if (item instanceof ItemColorArmor) {
            ItemColorArmor colorArmor = (ItemColorArmor) item;
            if (defaultItem.hasCompoundTag()) {
                colorArmor.setNamedTag(defaultItem.getNamedTag());
            }
            colorArmor.setColor(team.getBlockColor());
            return colorArmor;
        }

        String colorCode = team.getStringColor().split("§")[1];
        int id = defaultItem.getId();
        int meta;
        switch (colorCode) {
            case "a":
                meta = 5;
                break;
            case "b":
                meta = 3;
                break;
            case "c":
                meta = 6;
                break;
            case "d":
            case "9":
                meta = 2;
                break;
            case "e":
            case "6":
                meta = 4;
                break;
            case "f":
                meta = 0;
                break;
            case "0":
                meta = 15;
                break;
            case "1":
                meta = 11;
                break;
            case "2":
                meta = 13;
                break;
            case "3":
                meta = 9;
                break;
            case "4":
                meta = 14;
                break;
            case "5":
                meta = 10;
                break;
            case "7":
                meta = 8;
                break;
            case "8":
                meta = 7;
                break;
            default:
                return air;
        }
        Item newItem = Item.get(id, meta, defaultItem.getCount());
        if (defaultItem.hasCompoundTag()) {
            newItem.setNamedTag(defaultItem.getNamedTag());
        }
        return newItem;
    }

    /**
     * 播放声音
     * @param arena 房间
     * @param sound 声音
     */
    public static void playSound(BaseArena arena, Sound sound) {
        arena.getPlayerDataMap().keySet().forEach(player -> playSound(player, sound));
    }

    /**
     * 发包方式播放声音
     * @param player 玩家
     * @param sound 声音
     */
    public static void playSound(Player player, Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.name = sound.getSound();
        packet.volume = 1.0F;
        packet.pitch = 1.0F;
        packet.x = player.getFloorX();
        packet.y = player.getFloorY();
        packet.z = player.getFloorZ();
        player.dataPacket(packet);
    }
}
