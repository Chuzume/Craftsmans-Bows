package com.chuzbows;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.chuzbows.init.item.*;

public class CustomModelPredicateProvider {

    public static void registerModModels() {
        registerNewBow(SHORT_BOW);
        registerNewBow(SHOT_CROSSBOW);
        registerLongBow();
        registerRepeaterCrossbow();
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
        ModelPredicateProviderRegistry.register(com.chuzbows.init.item.REPEATER_CROSSBOW, Identifier.of("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
}
