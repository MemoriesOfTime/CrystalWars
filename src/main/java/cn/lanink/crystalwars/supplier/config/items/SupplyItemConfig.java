package cn.lanink.crystalwars.supplier.config.items;

import cn.lanink.crystalwars.arena.Team;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author iGxnon
 * @date 2021/9/4
 */
@Getter
@AllArgsConstructor
@ToString
public class SupplyItemConfig {

    private String name;

    private boolean dynamicTeamItem;

    private Item item;

    private int count;

    private String title;

    private String subTitle;

    private List<Item> cost;

    private Map<Team, Item> dynamicTeamItems;

    public SupplyItemConfig(@NotNull String name, @NotNull Config config) {
        this.dynamicTeamItem = config.getBoolean("dynamicTeamItem");
        this.count = config.getInt("count");
        this.title = config.getString("title");
        this.subTitle = config.getString("subTitle");
        this.name = name;

        cost = config.getStringList("cost").stream()
                .filter(costStr -> Pattern.compile("\\d:\\d#\\d").matcher(costStr).find())
                .map(filterStr -> {
                    Item item = Item.fromString(filterStr.split("#")[0]);
                    item.setCount(Integer.parseInt(filterStr.split("#")[1]));
                    return item;
                }).collect(Collectors.toList());

        String[] lore = config.getStringList("lore").toArray(new String[0]);

        if(this.dynamicTeamItem) {
            this.dynamicTeamItems = new HashMap<>();

            Item redItem = Item.fromString(config.getString("redId"));
            redItem.setCount(this.count);
            redItem.setLore(lore);

            Item blueItem = Item.fromString(config.getString("blueId"));
            blueItem.setCount(this.count);
            blueItem.setLore(lore);

            Item greenItem = Item.fromString(config.getString("greenId"));
            greenItem.setCount(this.count);
            greenItem.setLore(lore);

            Item yellowItem = Item.fromString(config.getString("yellowId"));
            yellowItem.setCount(this.count);
            greenItem.setLore(lore);

            this.dynamicTeamItems.put(Team.RED, redItem);
            this.dynamicTeamItems.put(Team.BLUE, blueItem);
            this.dynamicTeamItems.put(Team.GREEN, greenItem);
            this.dynamicTeamItems.put(Team.YELLOW, yellowItem);
        }else {
            this.item = Item.fromString(config.getString("id"));
            this.item.setCount(this.count);
            this.item.setLore(lore);
        }
    }


}
