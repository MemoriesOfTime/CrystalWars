package cn.lanink.crystalwars.supplier.config.items;


import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;


/**
 * @author iGxnon
 * @date 2021/9/4
 */
@Getter
@ToString
public class SupplyItemConfig {

    private final String fileName;

    private final Config config;

    public SupplyItemConfig(@NotNull String fileName, @NotNull Config config) {
        this.fileName = fileName;
        this.config = config;
    }
}
