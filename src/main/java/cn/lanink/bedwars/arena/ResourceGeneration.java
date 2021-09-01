package cn.lanink.bedwars.arena;

import cn.lanink.bedwars.items.generation.ItemGenerationConfig;
import cn.nukkit.math.Vector3;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @author LT_Name
 */
@Data
public class ResourceGeneration {

    private ItemGenerationConfig config;
    private Vector3 vector3;

    private int coolDownTime = 0;

    public ResourceGeneration(@NotNull ItemGenerationConfig config, @NotNull Vector3 vector3) {
        this.config = config;
        this.vector3 = vector3;
    }

}
