package com.example.item;

import com.example.ExampleMod;
import com.example.init.ModSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GatlingBowItem extends BowItem {
    public GatlingBowItem(Item.Settings settings) {
        super(settings);
    }

//変数の定義
    float pullTime;

//弓を引いた時間を取得する処理のようだ。今回は書き換えて、0.55以上引き絞ったら強制的に1（フルチャージ）になるようにした
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0f;
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

//アイテムを使用しているときの処理？
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    //    if (!world.isClient) {
//
    //    //プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
    //        PlayerEntity playerEntity = (PlayerEntity) user;
    //        ItemStack itemStack = playerEntity.getProjectileType(stack);
    //        if (itemStack.isEmpty()) {
    //            return;
    //        }
//
    //        user.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.25f);
//
    //        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
    //        if (!world.isClient() && !list.isEmpty()) {
    //            this.shootAll(world, user, user.getActiveHand(), stack, list, 1.6f, 5.0f, true, null);
    //        }
    //    }
    }

//使用をやめたとき、つまりクリックを離したときの処理だ。
   @Override
   public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
       if (!(user instanceof PlayerEntity)) {
           return;
       }
       user.playSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, 1.0f, 1.25f);
    }
}
