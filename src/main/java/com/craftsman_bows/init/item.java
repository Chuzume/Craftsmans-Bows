package com.craftsman_bows.init;

import com.craftsman_bows.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static com.craftsman_bows.CraftsmanBows.Mod_ID;

public class item {

    public static Item register(String id, Item item) {
        // アイテムの識別子を作成
        Identifier itemID = Identifier.of(Mod_ID, id);

        // 登録したアイテムを返す
        return Registry.register(Registries.ITEM, itemID, item);

    }


    // 各アイテムを異なるクラスで生成し、設定済みで登録
    public static final Item SHORT_BOW = register("shortbow", new ShortBowItem(new Item.Settings().maxCount(1).maxDamage(576)));
    public static final Item LONG_BOW = register("longbow", new LongBowItem(new Item.Settings().maxCount(1).maxDamage(576)));
    public static final Item SHOT_CROSSBOW = register("shot_crossbow", new ShotCrossbowItem(new Item.Settings().maxCount(1).maxDamage(576)));
    public static final Item REPEATER_CROSSBOW = register("repeater_crossbow", new RepeaterCrossbowItem(new Item.Settings().maxCount(1).maxDamage(640).fireproof()));
    public static final Item BURST_ARBALEST = register("burst_arbalest", new BurstArbalestItem(new Item.Settings().maxCount(1).maxDamage(900).fireproof()));

    public static void init() {

        // アイテムのグループを武器に
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(SHORT_BOW);
            content.add(LONG_BOW);
            content.add(SHOT_CROSSBOW);
            content.add(REPEATER_CROSSBOW);
            content.add(BURST_ARBALEST);
        });
    }
}