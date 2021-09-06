package cn.lanink.crystalwars.supplier.config.items;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

/**
 * @author iGxnon
 * @date 2021/9/4
 */
@Getter
@ToString
public class SupplyItemConfig {

    private final String fileName;

    private final Config config;

    private final String title;

    private final String subTitle;

    private final Item item;

    private final int slotPos;

    private final List<String> lore;

    private final Item[] cost;

    public SupplyItemConfig(@NotNull String fileName, @NotNull File fileConfig) {
        this.fileName = fileName;
        this.config = new Config(fileConfig, Config.YAML);
        this.title = config.getString("title");
        this.subTitle = config.getString("subTitle");
        this.slotPos = config.getInt("pos");
        this.lore = config.getStringList("lore");

        this.cost = config.getStringList("cost").stream()
                .filter(rawStr -> rawStr.matches("\\d{1,3}:\\d{1,4}x\\d{1,2}"))
                .map(rawStr -> {
                    Item item = Item.fromString(rawStr.split("x")[0]);
                    item.setCount(Integer.parseInt(rawStr.split("x")[1]));
                    return item;
                }).toArray(Item[]::new);

        this.item = Item.fromString(config.getString("item"));
        this.item.setCount(config.getInt("count"));
    }

    public Item getItem() {
        return item.clone();
    }

}
