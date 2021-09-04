package cn.lanink.crystalwars.supplier.config;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.entity.CrystalWarsEntityBaseMerchant;
import cn.lanink.crystalwars.supplier.config.items.SupplyItemConfig;
import cn.nukkit.utils.Config;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author iGxnon
 * @date 2021/9/3
 */
@Getter
@AllArgsConstructor
@ToString
public class SupplyConfig {

    private String name;

    private final SupplySlot[] slots;

    public SupplyConfig(@NotNull String name, @NotNull Config config) {
        slots = new SupplySlot[config.getAll().size()];
        this.name = name;
        int index = 0;
        for (Map.Entry<String, Object> entry : config.getAll().entrySet()) {
            String slotStr = entry.getKey();
            String title = (String) ((Map<String, Object>) entry.getValue()).get("title");
            String icon = (String) ((Map<String, Object>) entry.getValue()).get("icon");
            List<String> items = (List<String>) ((Map<String, Object>) entry.getValue()).get("items");
            SupplySlot slot = new SupplySlot();
            slot.setTitle(title);
            slot.setIcon(icon);
            slot.setType(CrystalWarsEntityBaseMerchant.MerchantInventory.Slot.valueFormStr(slotStr));
            Map<Integer, SupplyItemConfig> itemConfigs = new HashMap<>();
            items.forEach(foo -> {
                final String itemName = foo.split(":")[0];
                String itemFile = CrystalWars.getInstance().getDataFolder() + "/Supply/Items/" + itemName + ".yml";
                if(!new File(itemFile).exists()) {
                    CrystalWars.getInstance().getLogger().warning("Item " + itemName + " does not exist!");
                    return;
                }
                int itemIndex;
                try {
                    itemIndex = Integer.parseInt(foo.split(":")[1]);
                }catch (NumberFormatException e) {
                    CrystalWars.getInstance().getLogger().warning("Item " + itemName + " index format exception!");
                    return;
                }
                itemConfigs.put(itemIndex, new SupplyItemConfig(itemName, new Config(new File(itemFile), Config.YAML)));
            });
            slot.setItemConfigs(itemConfigs);
            this.slots[index] = slot;
            index ++;
        }
    }

    @Data
    @ToString
    public static class SupplySlot {

        private CrystalWarsEntityBaseMerchant.MerchantInventory.Slot type;

        private String title;

        private String icon;

        private Map<Integer, SupplyItemConfig> itemConfigs;
    }
}
