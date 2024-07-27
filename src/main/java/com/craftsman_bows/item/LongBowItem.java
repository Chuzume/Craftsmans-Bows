package com.craftsman_bows.item;

import com.craftsman_bows.init.ModSoundEvents;
import com.craftsman_bows.interfaces.item.ZoomItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class LongBowItem
        extends BowItem implements ZoomItem {
    public LongBowItem(Settings settings) {
        super(settings);
    }

    float fov;

    // 弓を引いた時間を取得する処理
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 40.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }


    // 最初の使用時のアクション
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(ModSoundEvents.BOW_CHARGE, 1.0f, 0.7f);
        fov = 1f;
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    // アイテムを使用しているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;

        // ズーム処理
        fov = 1.0f - getPullProgress(i) / 2.0f;

    }

    // 使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }

        fov = Float.NaN;

        // プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return;
        }

        // 使用時間0.1未満では使用をキャンセルする処理のようだ
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i);
        if ((double) f < 0.1) {
            return;
        }

        // ここが放つ処理に見える。
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
        if (world instanceof ServerWorld serverWorld) {
            if (!list.isEmpty() && f >= 1) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 6.0f, 0.0f, true, null);
            } else {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 2.0f, 0.0f, f == 0.0f, null);
            }
            if (f < 1) {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 0.8f);
            } else {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_1, SoundCategory.PLAYERS, 1.0f, 1.4f);
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_2, SoundCategory.PLAYERS, 1.0f, 0.7f);
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);
            }
        }
    }

    // インターフェースとして持っておくべきやつ
    @Override
    public void resetFov() {
        fov = Float.NaN;
    }

    @Override
    public float getFov() {
        return this.fov;
    }
}