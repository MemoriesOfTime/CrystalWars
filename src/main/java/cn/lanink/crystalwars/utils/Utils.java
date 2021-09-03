package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.Team;
import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.nukkit.entity.item.EntityFirework;
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

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author LT_Name
 */
public class Utils {

    public static String getShowTeam(Team team) {
        switch (team) {
            case RED:
                return "红队";
            case YELLOW:
                return "黄队";
            case BLUE:
                return "蓝队";
            case GREEN:
                return "绿队";
            case NULL:
            default:
                return "未加入队伍";
        }
    }

    public static String getShowHealth(CrystalWarsEntityEndCrystal crystalWarsEntityEndCrystal) {
        if (crystalWarsEntityEndCrystal.isClosed() || crystalWarsEntityEndCrystal.getHealth() < 1) {
            return "X";
        }
        return String.valueOf(NukkitMath.round(crystalWarsEntityEndCrystal.getHealth(), 2));
    }

    public static String vector3ToString(Vector3 vector3) {
        return vector3.x + ":" + vector3.y + ":" +vector3.z;
    }

    public static Vector3 stringToVector3(String string) {
        String[] split = string.split(":");
        return new Vector3(
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2])
        );
    }

    public static String formatCountdown(int time) {
        DecimalFormat format = new DecimalFormat("00");
        return format.format(time/60) + ":" + format.format(time%60);
    }

    public static String getSpace(List<?> list){
        return getSpace(list.size() + 1);
    }

    /**
     * 填充空格
     *
     * @param size 数量
     * @return 空格
     */
    public static String getSpace(int size){
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

}
