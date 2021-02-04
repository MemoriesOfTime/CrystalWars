package cn.lanink.bedwars.utils;

import cn.nukkit.utils.Config;
import lombok.NonNull;

import java.io.File;
import java.util.Map;

/**
 * @author lt_name
 */
public interface ISaveConfig {

    Map<String, Object> getSaveMap();

    default void saveConfig(@NonNull File file) {
        this.saveConfig(new Config(file, Config.YAML));
    }

    default void saveConfig(@NonNull Config config) {
        for (Map.Entry<String, Object> entry : this.getSaveMap().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        config.save();
    }

}
