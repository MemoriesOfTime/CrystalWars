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
        dynamicTeamItem = config.getBoolean("dynamicTeamItem");
        count = config.getInt("count");
        if(dynamicTeamItem) {
            dynamicTeamItems = new HashMap<>();
            Item redItem = Item.get(config.getInt("redId"), config.getInt("redMeta"), count);
            Item blueItem = Item.get(config.getInt("blueId"), config.getInt("blueMeta"), count);
            Item greenItem = Item.get(config.getInt("greenId"), config.getInt("greenMeta"), count);
            Item yellowItem = Item.get(config.getInt("yellowId"), config.getInt("yellowMeta"), count);
            dynamicTeamItems.put(Team.RED, redItem);
            dynamicTeamItems.put(Team.BLUE, blueItem);
            dynamicTeamItems.put(Team.GREEN, greenItem);
            dynamicTeamItems.put(Team.YELLOW, yellowItem);
        }else {
            item = Item.get(config.getInt("id"), config.getInt("meta"), count);
        }
    }


}
