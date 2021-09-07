package cn.lanink.crystalwars.utils;

import cn.nukkit.Server;

/**
 * @author iGxnon
 * @date 2021/9/4
 */
public interface RuntimeIdHolder {

    default long getRid() {
        return Server.getInstance().getTick();
    }

}
