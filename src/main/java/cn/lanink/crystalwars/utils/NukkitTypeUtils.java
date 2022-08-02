package cn.lanink.crystalwars.utils;

import cn.nukkit.Server;
import lombok.Getter;

/**
 * @author LT_Name
 */
public class NukkitTypeUtils {

    @Getter
    private static final NukkitType nukkitType;

    static {
        String codename = Server.getInstance().getCodename();
        if ("PowerNukkitX".equalsIgnoreCase(codename)) {
            nukkitType = NukkitType.POWER_NUKKIT_X;
        }else if ("PowerNukkit".equalsIgnoreCase(codename)) {
            nukkitType = NukkitType.POWER_NUKKIT;
        }else {
            nukkitType = NukkitType.NUKKITX;
        }
    }

    public enum NukkitType {

        NUKKITX,

        POWER_NUKKIT,

        POWER_NUKKIT_X;

    }

}
