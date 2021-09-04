package cn.lanink.crystalwars.supplier.config.items;

import cn.lanink.crystalwars.arena.Team;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author iGxnon
 * @date 2021/9/4
 */
@Getter
@AllArgsConstructor
@ToString
public class SupplyItemConfig {

    private boolean dynamicTeamItem;

    private Item item;

    private int count;

    private Map<Team, Item> dynamicTeamItems;

    public SupplyItemConfig(@NotNull String name, @NotNull Config config) {
        this.dynamicTeamItem = config.getBoolean("dynamicTeamItem");
        this.count = config.getInt("count");
        if(this.dynamicTeamItem) {
            this.dynamicTeamItems = new HashMap<>();

            Item redItem = Item.fromString(config.getString("redId"));
            redItem.setCount(this.count);

            Item blueItem = Item.fromString(config.getString("blueId"));
            blueItem.setCount(this.count);

            Item greenItem = Item.fromString(config.getString("greenId"));
            greenItem.setCount(this.count);

            Item yellowItem = Item.fromString(config.getString("yellowId"));
            yellowItem.setCount(this.count);

            this.dynamicTeamItems.put(Team.RED, redItem);
            this.dynamicTeamItems.put(Team.BLUE, blueItem);
            this.dynamicTeamItems.put(Team.GREEN, greenItem);
            this.dynamicTeamItems.put(Team.YELLOW, yellowItem);
        }else {
            this.item = Item.fromString(config.getString("id"));
            this.item.setCount(this.count);
        }
    }


}
