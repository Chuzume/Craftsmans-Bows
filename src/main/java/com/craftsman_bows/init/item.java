package com.craftsman_bows.init;

import com.craftsman_bows.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.craftsman_bows.CraftsmanBows.Mod_ID;

public class item {
    // 新しいアイテムのインスタンス
    public static final Item LONG_BOW = new LongBowItem(new Item.Settings().maxCount(1).maxDamage(576));
    public static final Item SHORT_BOW = new ShortBowItem(new Item.Settings().maxCount(1).maxDamage(576));
    public static final Item SHOT_CROSSBOW = new ShotCrossbowItem(new Item.Settings().maxCount(1).maxDamage(600));
    public static final Item REPEATER_CROSSBOW = new RepeaterCrossbowItem(new Item.Settings().maxCount(1).maxDamage(600));

    // アイテム追加処理
    public static void init() {
        Registry.register(Registries.ITEM, Identifier.of(Mod_ID, "longbow"), LONG_BOW);
        Registry.register(Registries.ITEM, Identifier.of(Mod_ID, "shortbow"), SHORT_BOW);
        Registry.register(Registries.ITEM, Identifier.of(Mod_ID, "shot_crossbow"), SHOT_CROSSBOW);
        Registry.register(Registries.ITEM, Identifier.of(Mod_ID, "repeater_crossbow"), REPEATER_CROSSBOW);

        // アイテムのグループを武器に
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(SHORT_BOW);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(LONG_BOW);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(SHOT_CROSSBOW);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(REPEATER_CROSSBOW);
        });
    }

}