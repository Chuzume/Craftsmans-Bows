package com.chuzbows.item;

import com.chuzbows.ChuzBowsCore;
import com.chuzbows.init.ModSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class LongBowItem
        extends BowItem {
    public LongBowItem(Settings settings) {
        super(settings);
    }

    float pullTime;
    float FOV;

    //弓を引いた時間を取得する処理のようだ。今回は書き換えて、0.55以上引き絞ったら強制的に1（フルチャージ）になるようにした
    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 3.0f) {
            f = 3f;
        }
        return f;
    }

//最初の使用時のアクション
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(ModSoundEvents.BOW_CHARGE, 1.0f, 0.7f);
    //腕振る処理
        //Hand activeHand = user.getActiveHand();
        //if (activeHand == Hand.MAIN_HAND) {
        //// プレイヤーはメインの手を使用しています
        //    user.swingHand(Hand.OFF_HAND);
        //} else if (activeHand == Hand.OFF_HAND) {
        //// プレイヤーはオフハンドを使用しています
        //    user.swingHand(Hand.MAIN_HAND);
        //}
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

//インベントリ内に存在する限り実行され続ける処理のようだ。それを選んでいるかどうか、つまり持っているだけで実行する処理もここに入れるのかな？
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        //playerEntity.sendMessage(Text.literal(String.valueOf(ExampleMod.SampleGlobal.UsingMoveSpeed)));
        //playerEntity.sendMessage(Text.literal(String.valueOf(PullTime)));
    }

//アイテムを使用しているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        pullTime = getPullProgress(i);
        //user.sendMessage(Text.literal(String.valueOf(pullTime)));
        user.sendMessage(Text.literal(String.valueOf(FOV)));

        //ExampleMod.SampleGlobal.UsingMoveSpeed = 5.0f - (2.0f + pullTime);
        FOV = 1.0f - pullTime/3;
        ChuzBowsCore.Global.CustomFOV = FOV;

    // ズーム停止
        if (FOV <= 0.5 ){
            ChuzBowsCore.Global.CustomFOV = 0.5f;
        }

    //フルチャージ
        if (pullTime == 3.0f) {
            user.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
        }
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
        ChuzBowsCore.Global.UsingMoveSpeed = Float.NaN;
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (!list.isEmpty() && f >= 3) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 2f, 0.0f, true, null);
            } else {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 0.5f, 0.0f, f == 0.0f, null);
            }
            if (f < 3) {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);
            } else {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_2, SoundCategory.PLAYERS, 1.0f, 1.0f);
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);
            }
        }
    }
}