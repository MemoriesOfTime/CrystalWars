package cn.lanink.crystalwars.arena;

import cn.lanink.crystalwars.items.generation.ItemGenerationConfig;
import cn.nukkit.math.Vector3;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * @author LT_Name
 */
@Data
@EqualsAndHashCode
public class ResourceGeneration {

    private ItemGenerationConfig config;
    private Vector3 vector3;

    @EqualsAndHashCode.Exclude
    private int coolDownTime = 0;

    public ResourceGeneration(@NotNull ItemGenerationConfig config, @NotNull Vector3 vector3) {
        this.config = config;
        this.vector3 = vector3;
    }

}
