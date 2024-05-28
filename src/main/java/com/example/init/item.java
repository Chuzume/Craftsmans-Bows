package com.example.init;

import com.example.item.LongBowItem;
import com.example.item.ShortBowItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class item {

// アイテムその1
    // 新しいアイテムのインスタンス
    public static final Item SHORT_BOW = new ShortBowItem(new Item.Settings().maxCount(1).maxDamage(500));;
    public static final Item LONG_BOW = new LongBowItem(new Item.Settings().maxCount(1).maxDamage(500));;

    // アイテム追加処理

    public static void init() {
        Registry.register(Registries.ITEM, new Identifier("example", "shortbow"), SHORT_BOW);
        Registry.register(Registries.ITEM, new Identifier("example", "long_bow"), LONG_BOW);

        // アイテムのグループを武器に
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {content.add(SHORT_BOW);});
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {content.add(LONG_BOW);});

    }

}