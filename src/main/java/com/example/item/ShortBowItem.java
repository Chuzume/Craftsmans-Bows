package com.example.item;

import com.example.ExampleMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ShortBowItem extends BowItem {
    public ShortBowItem(Settings settings) {
        super(settings);
    }

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
        user.playSound(SoundEvents.ITEM_CROSSBOW_LOADING_START, 1.0f, 1.25f);
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

//インベントリ内に存在する限り実行され続ける処理のようだ。それを選んでいるかどうか、つまり持っているだけで実行する処理もここに入れるのかな？
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        PlayerEntity playerEntity = (PlayerEntity) entity;
    //まずアイテムを選んでいるかチェック
        //if (playerEntity.isUsingItem() && selected) {
        if (selected) {
            ExampleMod.SampleGlobal.IgnoreSlowdown = true;
        }
    }

//使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity)) {
            return;
        }

    //プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        PlayerEntity playerEntity = (PlayerEntity)user;
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return;
        }

    //使用時間0.1未満では使用をキャンセルする処理のようだ
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i);
        if ((double)f < 0.1) {
            return;
        }

    //ここが放つ処理に見える。
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
        if (!world.isClient() && !list.isEmpty()) {
            this.shootAll(world, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 0.0f, f == 1.0f, null);
        }
        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 100.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);

        //腕振る処理
        //Hand activeHand = playerEntity.getActiveHand();
        //if (activeHand == Hand.MAIN_HAND) {
        //    // プレイヤーはメインの手を使用しています
        //    playerEntity.swingHand(Hand.MAIN_HAND);
        //} else if (activeHand == Hand.OFF_HAND) {
        //    // プレイヤーはオフハンドを使用しています
        //    playerEntity.swingHand(Hand.OFF_HAND);
        //}
    }
}