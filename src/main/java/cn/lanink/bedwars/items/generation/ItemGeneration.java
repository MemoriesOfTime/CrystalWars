package cn.lanink.bedwars.items.generation;

import cn.lanink.bedwars.utils.ISaveConfig;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lt_name
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ItemGeneration implements ISaveConfig {

    private final Item item;

    private final String showName;

    private final int spawnTime;
    private final int spawnCount;

    private final boolean canSpawnOnDay;
    private final boolean canSpawnOnNight;

    public ItemGeneration(@NotNull Config config) {
        this.item = Item.fromString(config.getString("itemID"));

        this.showName = config.getString("showName");
        if (!"".equals(this.showName)) {
            this.item.setCustomName(this.showName);
        }

        this.spawnTime = config.getInt("spawnTime(s)");
        this.spawnCount = config.getInt("spawnCount");

        this.canSpawnOnDay = config.getBoolean("canSpawnOnDay");
        this.canSpawnOnNight = config.getBoolean("canSpawnOnNight");
    }

    public Item getItem() {
        return this.item.clone();
    }

    @Override
    public Map<String, Object> getSaveMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("itemID", this.getItem().getId() + ":" + this.getItem().getDamage());

        map.put("showName", this.getItem().getCustomName());

        map.put("spawnTime(s)", this.getSpawnTime());
        map.put("spawnCount", this.getSpawnCount());

        map.put("canSpawnOnDay", this.isCanSpawnOnDay());
        map.put("canSpawnOnNight", this.isCanSpawnOnNight());

        return map;
    }

}
