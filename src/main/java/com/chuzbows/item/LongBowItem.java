package com.chuzbows.item;

import com.chuzbows.ChuzBowsCore;
import com.chuzbows.init.ModSoundEvents;
import com.chuzbows.item_interface.ZoomItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

import static java.lang.Float.NaN;

public class LongBowItem
        extends BowItem implements ZoomItem {
    public LongBowItem(Settings settings) {
        super(settings);
    }

    float chargeTime;
    float pullTime;
    float FOV;

    //弓を引いた時間を取得する処理
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 3.0f) {
            f = 3f;
        }
        return f;
    }

    //シンプルに右クリックを推し続けている時間を秒数換算で取得
    public static float getChargeTime(int useTicks) {
        float f = (float) useTicks / 20.0f;
        return f;
    }

    //最初の使用時のアクション
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(ModSoundEvents.BOW_CHARGE, 1.0f, 0.7f);
        FOV = 1f;
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    //アイテムを使用しているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        chargeTime = getChargeTime(i);
        //user.sendMessage(Text.literal(String.valueOf(FOV)));

        //チャージ完了に近づくにつれて移動が遅くなる
        float moveSpeed = 5.0f - (2.0f + chargeTime);
        //ChuzBowsCore.Global.UsingMoveSpeed = Math.max(moveSpeed, 0.0f);

        //ズーム処理
        FOV = 1.0f - chargeTime / 4;

        //ズーム停止
        if (FOV <= 0.5) {
            FOV = 0.5f;
        }

        //フルチャージ
        if (chargeTime == 2.2f) {
            user.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
        }
    }

    //インターフェースとして持っておくべきやつ
    @Override
    public float resetFOV() {
        FOV = Float.NaN;
        return this.FOV;
    }

    @Override
    public float getFOV() {
        return this.FOV;
    }


    //使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity)) {
            return;
        }

        FOV = Float.NaN;

        //プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        PlayerEntity playerEntity = (PlayerEntity) user;
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return;
        }

        //使用時間0.1未満では使用をキャンセルする処理のようだ
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i);
        if ((double) f < 0.1) {
            return;
        }

        //ここが放つ処理に見える。
        ChuzBowsCore.Global.UsingMoveSpeed = NaN;
        //ChuzBowsCore.Global.CustomFOV = Float.NaN;
        //user.sendMessage(Text.literal(String.valueOf(FOV)));
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (!list.isEmpty() && f >= 3) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 2f, 0.0f, true, null);
            } else {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 0.5f, 0.0f, f == 0.0f, null);
            }
            if (f < 3) {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 0.8f);
            } else {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_2, SoundCategory.PLAYERS, 1.0f, 1.0f);
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);
            }
        }
    }
}