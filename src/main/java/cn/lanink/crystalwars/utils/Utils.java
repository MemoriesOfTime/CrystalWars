package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.lanink.crystalwars.supplier.config.SupplyConfigManager;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LT_Name
 */
public class Utils {

    private Utils() {
        throw new RuntimeException("哎呀！你不能实例化这个类！");
    }

    public static String getShowTeam(Team team) {
        switch (team) {
            case RED:
                return team.getStringColor() + "§l红队§r";
            case YELLOW:
                return team.getStringColor() + "§l黄队§r";
            case BLUE:
                return team.getStringColor() + "§l蓝队§r";
            case GREEN:
                return team.getStringColor() + "§l绿队§r";
            case NULL:
            default:
                return team.getStringColor() + "§l未加入队伍§r";
        }
    }

    /**
     * 获取水晶实体百分比显示的血量
     *
     * @param crystal 水晶实体
     * @return 百分比显示的血量
     */
    public static String getShowHealth(CrystalWarsEntityEndCrystal crystal) {
        if (crystal.isClosed() || crystal.getHealth() < 1) {
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
     * GitHub：https://github.com/PetteriM1/FireworkShow
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

    public static Item getTeamColorItem(Item defaultItem, Team team) {
        Item air = Item.get(Item.AIR);
        air.setNamedTag(defaultItem.getNamedTag());
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
        newItem.setNamedTag(defaultItem.getNamedTag());
        return newItem;
    }

}
