package com.craftsman_bows;

import com.craftsman_bows.item.BurstArbalestItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import static com.craftsman_bows.init.item.*;

public class CustomModelPredicateProvider {

    public static void registerModModels() {
        registerNewBow(SHORT_BOW);
        registerNewBow(SHOT_CROSSBOW);
        registerLongBow();
        registerRepeaterCrossbow();
        registerNewBow(BURST_ARBALEST);
    }

    // 最大チャージレベルが1の弓を登録
    private static void registerNewBow(Item bow) {
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                return 0.0f;
            }
            return (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0f;
        });
        ModelPredicateProviderRegistry.register(bow, Identifier.of("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }

    // ロングボウ用の専用登録処理録
    private static void registerLongBow() {
        ModelPredicateProviderRegistry.register(com.craftsman_bows.init.item.LONG_BOW, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                return 0.0f;
            }
            return (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 30.0f;
        });
        ModelPredicateProviderRegistry.register(com.craftsman_bows.init.item.LONG_BOW, Identifier.of("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }

    // ガトリングボウ用の専用登録処理録
    private static void registerRepeaterCrossbow() {
        ModelPredicateProviderRegistry.register(REPEATER_CROSSBOW, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0;
            }
            if (entity.getActiveItem() != stack) {
                return 0;
            }
            return (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 50.0f;
        });
        ModelPredicateProviderRegistry.register(com.craftsman_bows.init.item.REPEATER_CROSSBOW, Identifier.of("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }

    // バーストアルバレスト用の専用登録処理録
    private static void registerBurstArbalest() {
        ModelPredicateProviderRegistry.register(BURST_ARBALEST, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                return 0.0f;
            }
            return (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0f;
        });
        ModelPredicateProviderRegistry.register(BURST_ARBALEST, Identifier.of("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}
