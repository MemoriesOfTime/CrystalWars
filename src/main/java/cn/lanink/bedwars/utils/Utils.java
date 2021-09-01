package cn.lanink.bedwars.utils;

import cn.nukkit.math.Vector3;

/**
 * @author LT_Name
 */
public class Utils {

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

}
