package cn.lanink.crystalwars.utils;

import cn.lanink.crystalwars.entity.CrystalWarsEntityEndCrystal;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author LT_Name
 */
public class Utils {

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

}
