package com.chuzbows;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import static com.chuzbows.init.item.*;

public class CustomModelPredicateProvider {

    public static void registerModModels() {
        registerNewBow(SHORT_BOW);
        registerLongBow();
        registerNewBow(SHOT_CROSSBOW);
        registerNewBow(GATLING_CROSSBOW);
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
        ModelPredicateProviderRegistry.register(com.chuzbows.init.item.LONG_BOW, Identifier.of("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                return 0.0f;
            }
            return (float) (stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 40.0f;
        });
        ModelPredicateProviderRegistry.register(com.chuzbows.init.item.LONG_BOW, Identifier.of("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}
