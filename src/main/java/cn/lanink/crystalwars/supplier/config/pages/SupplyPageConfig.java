package cn.lanink.crystalwars.supplier.config.pages;

import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
@Getter
@ToString
public class SupplyPageConfig {

    private final String fileName;

    private final Config config;

    public SupplyPageConfig(@NotNull String fileName, @NotNull Config config) {
        this.fileName = fileName;
        this.config = config;
    }

}
