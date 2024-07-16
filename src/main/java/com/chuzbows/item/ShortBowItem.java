package com.chuzbows.item;

import com.chuzbows.item_interface.CustomUsingMoveItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import com.chuzbows.init.ModSoundEvents;

import java.util.List;

import static java.lang.System.getLogger;

public class ShortBowItem extends BowItem implements CustomUsingMoveItem{
    public ShortBowItem(Settings settings) {
        super(settings);
    }

float MovementSpeed = 5.0f;

//弓を引いた時間を取得する処理のようだ。今回は書き換えて、0.55以上引き絞ったら強制的に1（フルチャージ）になるようにした
    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 0.55f) {
            f = 1f;
        }
        return f;
    }

//最初の使用時のアクション
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean bl;
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl2 = bl = !user.getProjectileType(itemStack).isEmpty();
        if (user.isInCreativeMode() || bl) {
            user.setCurrentHand(hand);
            user.playSound(ModSoundEvents.BOW_CHARGE, 1.0f, 1.25f);
            return TypedActionResult.consume(itemStack);

        }
        return TypedActionResult.fail(itemStack);
    }

//アイテムを使用しているときの処理？
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        PlayerEntity playerEntity = (PlayerEntity) user;
        MovementSpeed = 5.0f;
    }

//使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity)) {
            return;
        }

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
        //ChuzBowsCore.Global.UsingMoveSpeed = Float.NaN;
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (!list.isEmpty()) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 5.0f, f == 1.0f, null);
            }
            if (f < 1) {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);
            } else {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_1, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + 0.9f);
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);
            }
        }
    }

//インターフェースとして持っておくべき処理
    @Override
    public float getMovementSpeed() {
        return MovementSpeed;
    }

    @Override
    public float resetMovementSpeed() {
        MovementSpeed = Float.NaN;
        return MovementSpeed;
    }
}