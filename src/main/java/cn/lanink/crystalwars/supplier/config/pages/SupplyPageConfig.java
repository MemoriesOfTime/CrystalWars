package cn.lanink.crystalwars.supplier.config.pages;

import cn.lanink.crystalwars.CrystalWars;
import cn.lanink.crystalwars.arena.BaseArena;
import cn.lanink.crystalwars.entity.CrystalWarsEntityMerchant;
import cn.lanink.crystalwars.supplier.config.SupplyConfig;
import cn.lanink.crystalwars.supplier.config.items.SupplyItemConfig;
import cn.lanink.crystalwars.utils.Utils;
import cn.lanink.crystalwars.utils.inventory.ui.advanced.AdvancedBuyItem;
import cn.lanink.crystalwars.utils.inventory.ui.advanced.AdvancedInventory;
import cn.lanink.crystalwars.utils.inventory.ui.advanced.AdvancedPageLinkItem;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.lanink.gamecore.utils.Language;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
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
                        return value.get("pos").matches("\\d{1,2}");
                    }).forEach(stringMapEntry -> {
                        Map<String, String> value = stringMapEntry.getValue();
                        int slotPos = Integer.parseInt(value.get("pos"));
                        LinkItem linkItem = new LinkItem(
                                Item.fromString(stringMapEntry.getKey()),
                                slotPos,
                                value.get("link"),
                                value.containsKey("afterClick") ? Item.fromString(value.get("afterClick")) : null
                        );
                        linkItemBuilder.put(slotPos, linkItem);
                    });

            this.linkItems = linkItemBuilder.build();
        }else {
            this.linkItems = null;
        }

        ArrayList<Integer> list = new ArrayList<>();
        config.getStringList("items").stream()
                .filter(item -> this.parent.getItemConfigMap().containsKey(item))
                .forEach(item -> {
                    final SupplyItemConfig supplyItemConfig = this.parent.getItemConfigMap().get(item);
                    int slotPos = supplyItemConfig.getSlotPos();
                    while (list.contains(slotPos)) {
                        slotPos++;
                    }
                    itemBuilder.put(slotPos, supplyItemConfig);
                    list.add(slotPos);
                });
        this.items = itemBuilder.build();
    }

    public AdvancedInventory generateWindow(@NotNull CrystalWarsEntityMerchant holder) {
        AdvancedInventory advancedInventory = new AdvancedInventory(holder, this.title);
        if(linkItems != null) {
            linkItems.forEach((slotPos, linkItem) -> {
                final SupplyPageConfig supplyPageConfig = getParent().getPageConfigMap().get(linkItem.getPageFileName());
                advancedInventory.setItem(slotPos, new AdvancedPageLinkItem(linkItem.getItem().setCustomName(supplyPageConfig.getTitle()), supplyPageConfig));
            });
        }
        items.forEach((slotPos, item) -> {
            advancedInventory.setItem(slotPos, new AdvancedBuyItem(item.getItem().setCustomName(item.getTitle() + "§r\n" + item.getSubTitle()), item));
        });
        return advancedInventory;
    }

    public AdvancedInventory generateWindow(@NotNull AdvancedInventory advancedInventory) {
        advancedInventory.clearAll();
        if(this.linkItems != null) {
            linkItems.forEach((slotPos, linkItem) -> {
                final SupplyPageConfig supplyPageConfig = getParent().getPageConfigMap().get(linkItem.getPageFileName());
                advancedInventory.setItem(slotPos, new AdvancedPageLinkItem(linkItem.getItem().setCustomName(supplyPageConfig.getTitle()), supplyPageConfig));
            });
        }
        this.items.forEach((slotPos, item) -> {
            advancedInventory.setItem(slotPos, new AdvancedBuyItem(item.getItem().setCustomName(item.getTitle() + "§r\n" + item.getSubTitle()), item));
        });
        return advancedInventory;
    }

    public AdvancedFormWindowSimple generateForm(@NotNull AdvancedFormWindowSimple parent) {
        Language language = CrystalWars.getInstance().getLang();
        AdvancedFormWindowSimple advancedFormWindowSimple = new AdvancedFormWindowSimple(this.title);
        advancedFormWindowSimple.addButton(language.translateString("buyItem_ReturnToMainPage"), player -> {
            player.showFormWindow(parent);
        });
        this.items.forEach((slotPos, itemConfig) -> {
            advancedFormWindowSimple.addButton(itemConfig.getTitle() + "§r\n" + itemConfig.getSubTitle(), player -> {
                BaseArena arena = CrystalWars.getInstance().getArenas().get(player.getLevel().getFolderName());
                if(!player.getInventory().canAddItem(itemConfig.getItem())) {
                    player.sendTip(language.translateString("buyItem_inventoryFull"));
                    return;
                }
                for (Item cost : itemConfig.getCost()) {
                    if(!player.getInventory().contains(cost)) {
                        player.sendTip(language.translateString("buyItem_lackOfNeededItems"));
                        return;
                    }
                }
                if (!itemConfig.isOvertimeCanBuy() && arena.isOvertime()) {
                    player.sendTip("此物品不能在加时赛时购买！");
                    return;
                }
                for (Item cost : itemConfig.getCost()) {
                    player.getInventory().removeItem(cost);
                }
                Item item = itemConfig.getItem();
                if(itemConfig.isTeamChangeItem()) {
                    if(arena == null) {
                        player.sendMessage(language.translateString("buyItem_notInRoom"));
                        return;
                    }
                    item = Utils.getTeamColorItem(item, arena.getPlayerData(player).getTeam());
                }
                player.getInventory().addItem(item);
                player.sendTip(language.translateString("buyItem_success"));
            });
        });
        return advancedFormWindowSimple;
    }

}
