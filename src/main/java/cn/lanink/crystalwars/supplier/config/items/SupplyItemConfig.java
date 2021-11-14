package cn.lanink.crystalwars.supplier.config.items;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.supplier.config.SupplyConfigManager;
import cn.lanink.crystalwars.utils.Utils;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

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
                .filter(rawStr -> rawStr.matches("\\d{1,3}:\\d{1,4}x\\d{1,3}"))
                .map(rawStr -> {
                    Item item = Item.fromString(rawStr.split("x")[0]);
                    item.setCount(Utils.toInt(rawStr.split("x")[1]));
                    return item;
                }).toArray(Item[]::new);
        if (this.cost.length == 0) {
            CrystalWars.getInstance().getLogger().warning("物品：" + this.fileName + " 未设置成本！玩家可以免费获取！");
        }

        this.item = Item.fromString(config.getString("item"));
        this.item.setCount(config.getInt("count"));
        this.item.setLore(this.lore.toArray(new String[0]));
        this.item.setCustomName(this.title);

        for (Map map : config.getMapList("enchantment")) {
            try {
                int id = (int) map.getOrDefault("id", 17);
                int level = (int) map.getOrDefault("level", 1);
                Enchantment enchantment = Enchantment.get(id);
                enchantment.setLevel(level);
                this.item.addEnchantment(enchantment);
            } catch (Exception e) {
                CrystalWars.getInstance().getLogger().error("加载物品附魔时出现错误！物品：" + this.fileName, e);
            }
        }
    }

    public Item getItem() {
        return item.clone();
    }

    public boolean isTeamChangeItem() {
        return SupplyConfigManager.TEAM_CHANGE_ITEM_IDS.contains(this.item.getId());
    }


}
