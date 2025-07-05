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
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class LongBowItem
        extends CraftsmanBowItem implements ZoomItem {

    public LongBowItem(Settings settings) {
        super(settings);
    }

    float fov;

    // 弓を引いた時間を取得する処理
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 30.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    // 最初の使用時のアクション
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getProjectileType(itemStack).isEmpty();
        if (!user.isInCreativeMode() && !bl) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_LOAD, 1.0f, 1.0f);
            fov = 1f;
            return TypedActionResult.consume(itemStack);
        }
    }

    // アイテムを使用しているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;

        // 途中が寂しいので…
        if (i == 10) {
            user.playSound(SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE.value(), 1.0f, 1.2f);
        }

        // チャージ中
        if (i < 29) {
            chargingParticle(world, user);  // パーティクル生成の処理
        }

        // チャージ完了
        if (i == 29) {
            chargeEndParticle(world, user);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_1, 1.0f, 1.0f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_4, 1.0f, 1.2f);
        }

        // ズーム処理
        fov = 1.0f - getPullProgress(i) / 3f;
    }

    // 使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            fov = Float.NaN;
            ItemStack itemStack = playerEntity.getProjectileType(stack);
            if (!itemStack.isEmpty()) {
                int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
                float f = getPullProgress(i);
                if (!(f < 0.1)) {
                    List<ItemStack> list = load(stack, itemStack, playerEntity);
                    if (world instanceof ServerWorld serverWorld && !list.isEmpty()) {
                        if (f >= 1) {
                            this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 4.0f, 0.0f, true, null);
                        } else {
                            this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 2.0f, 0.0f, f == 0.0f, null);
                        }
                        if (f < 1) {
                            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 0.8f);
                        } else {
                            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_1, SoundCategory.PLAYERS, 1.0f, 1.3f);
                            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.DUNGEONS_BOW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        }

                        // パーティクル
                        if (f >= 1) {
                            shootParticle(world, user);
                        }
                    }
                }
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