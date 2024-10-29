package com.craftsman_bows.init;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static com.craftsman_bows.CraftsmanBows.Mod_ID;

public class item {

    public static Item register(String id, Item.Settings settings) {
        // アイテムの識別子を作成
        Identifier itemID = Identifier.of(Mod_ID, id);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, itemID);

        // `Item.Settings` に `registryKey` を設定
        settings.registryKey(itemKey);

        // 設定済みの `Item` インスタンスを生成
        Item item = new Item(settings);

        // アイテムを登録し、登録したアイテムを返す
        return Registry.register(Registries.ITEM, itemID, item);
    }

    // 各アイテムを生成し、設定済みで登録
    public static final Item SHORT_BOW = register("shortbow", new Item.Settings().maxCount(1).maxDamage(576));
    public static final Item LONG_BOW = register("longbow", new Item.Settings().maxCount(1).maxDamage(576));
    public static final Item SHOT_CROSSBOW = register("shot_crossbow", new Item.Settings().maxCount(1).maxDamage(600));
    public static final Item REPEATER_CROSSBOW = register("repeater_crossbow", new Item.Settings().maxCount(1).maxDamage(600));

    // アイテム追加処理
    public static void init() {

        // アイテムのグループを武器に
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(SHORT_BOW);
            content.add(LONG_BOW);
            content.add(SHOT_CROSSBOW);
            content.add(REPEATER_CROSSBOW);
        });
    }
}