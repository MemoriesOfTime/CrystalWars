package cn.lanink.crystalwars.supplier.config.pages;

import cn.lanink.crystalwars.supplier.config.SupplyConfig;
import cn.lanink.crystalwars.supplier.config.items.SupplyItemConfig;
import cn.lanink.crystalwars.utils.inventory.ui.advanced.AdvancedInventory;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author iGxnon
 * @date 2021/9/6
 */
@Getter
@ToString
public class SupplyPageConfig {

    private final String fileName;

    private final Config config;

    private final String title;

    @ToString.Exclude // 不然会导致栈溢出
    private final SupplyConfig parent;

    // slotPos -> Item
    private final @Nullable ImmutableMap<Integer, LinkItem> linkItems;
    // slotPos -> Item
    private final @NotNull ImmutableMap<Integer, SupplyItemConfig> items;

    public SupplyPageConfig(@NotNull String fileName, @NotNull File fileConfig, @NotNull SupplyConfig parent) {
        this.fileName = fileName;
        this.parent = parent;
        this.config = new Config(fileConfig, Config.YAML);
        this.title = config.getString("title");
        Map<String, Map<String, String>> rawLinkItemData = (Map<String, Map<String, String>>) config.get("linkItems");

        ImmutableMap.Builder<Integer, LinkItem> linkItemBuilder = new ImmutableMap.Builder<>();
        ImmutableMap.Builder<Integer, SupplyItemConfig> itemBuilder = new ImmutableMap.Builder<>();

        if(rawLinkItemData != null) {
            rawLinkItemData.entrySet().stream()
                    .filter(stringMapEntry -> {
                        String idAndMeta = stringMapEntry.getKey();
                        if (!idAndMeta.matches("\\d{1,3}:\\d{1,4}")) {
                            return false;
                        }
                        Map<String, String> value = stringMapEntry.getValue();
                        List<String> authorizedKey = Arrays.asList("pos", "link", "afterClick");
                        // 可以不包含 afterClick
                        if (value.size() != authorizedKey.size()) {
                            if (value.containsKey("afterClick")) {
                                return false;
                            }
                        }
                        for (Map.Entry<String, String> secondEntry : value.entrySet()) {
                            if (!authorizedKey.contains(secondEntry.getKey())) {
                                return false;
                            }
                        }
                        return value.get("pos").matches("[0-26]");
                    }).forEach(stringMapEntry -> {
                        Map<String, String> value = stringMapEntry.getValue();
                        int slotPos = Integer.parseInt(value.get("pos"));
                        LinkItem linkItem = new LinkItem(
                                Item.fromString(stringMapEntry.getKey()),
                                slotPos,
                                value.get("link"),
                                Item.fromString(value.get("afterClick"))
                        );
                        linkItemBuilder.put(slotPos, linkItem);
                    });

            this.linkItems = linkItemBuilder.build();
        }else {
            this.linkItems = null;
        }

        config.getStringList("items").forEach(item -> {
            final SupplyItemConfig supplyItemConfig = this.parent.getItemConfigMap().get(item);
            itemBuilder.put(supplyItemConfig.getSlotPos(), supplyItemConfig);
        });
        this.items = itemBuilder.build();
    }

    public AdvancedInventory generateWindow() {
        return null;
    }

    public AdvancedFormWindowSimple generateForm() {
        return null;
    }

}
