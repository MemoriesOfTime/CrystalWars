package cn.lanink.crystalwars.items.generation;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * 物品生成配置（游戏内资源点）
 *
 * @author LT_Name
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "name")
public class ItemGenerationConfig {

    private final String name;
    private final Config config;

    private final Item item;

    private final String showName;

    private final int spawnTime;
    private final int spawnCount;

    /**
     * 可以在白天生成
     */
    private final boolean canSpawnOnDay;
    /**
     * 可以在晚上生成
     */
    private final boolean canSpawnOnNight;
    /**
     * 可以在加时赛生成
     */
    private final boolean canSpawnOnOvertime;

    public ItemGenerationConfig(@NotNull String name, @NotNull Config config) {
        this.name = name;
        this.config = config;

        this.item = Item.fromString(config.getString("itemID"));

        this.showName = config.getString("showName");

        this.spawnTime = config.getInt("spawnTime(s)");
        this.spawnCount = config.getInt("spawnCount");

        this.canSpawnOnDay = config.getBoolean("canSpawnOnDay");
        this.canSpawnOnNight = config.getBoolean("canSpawnOnNight");
        this.canSpawnOnOvertime = config.getBoolean("canSpawnOnOvertime");
    }

    public Item getItem() {
        return this.item.clone();
    }

    public void save() {
        this.save(false);
    }

    public void save(boolean async) {
        this.config.set("itemID", this.getItem().getId() + ":" + this.getItem().getDamage());

        this.config.set("showName", this.getItem().getCustomName());

        this.config.set("spawnTime(s)", this.getSpawnTime());
        this.config.set("spawnCount", this.getSpawnCount());

        this.config.set("canSpawnOnDay", this.isCanSpawnOnDay());
        this.config.set("canSpawnOnNight", this.isCanSpawnOnNight());
        this.config.set("canSpawnOnOvertime", this.isCanSpawnOnOvertime());

        this.config.save(async);
    }

}
