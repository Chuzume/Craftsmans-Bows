package com.chuzbows.item;

import com.chuzbows.init.ModSoundEvents;
import com.chuzbows.item_interface.CustomArmPoseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class GatlingBowItem extends BowItem implements CustomArmPoseItem {
    public GatlingBowItem(Item.Settings settings) {
        super(settings);
    }

//変数の定義
    int RapidShot = 0;

//弓を引いた時間を取得する処理のようだ。今回は書き換えて、0.55以上引き絞ったら強制的に1（フルチャージ）になるようにした
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 0.55f) {
            f = 1f;
        }
        return f;
    }

    //シンプルに右クリックを推している間Tickで取る
    public static float getChargeTime(int useTicks) {
        float f = (float) useTicks;
        return f;
    }

//最初の使用時のアクション
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(ModSoundEvents.BOW_CHARGE, 1.0f, 1.25f);
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

//アイテムを使用しているときの処理？
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        //PlayerEntity playerEntity = (PlayerEntity) user;
        if (world instanceof ServerWorld) {
            RapidShot += 1;
        }
        user.sendMessage(Text.literal("現在のTick:" + RapidShot));
        //RapidShot = this.getMaxUseTime(stack) - remainingUseTicks;
        //user.sendMessage(Text.literal(String.valueOf(RapidShot)));
        //playerEntity.getItemCooldownManager().set(this, 20);

        //if (pullTime >= 10){
        //    this.GatingShot(world,user,stack,remainingUseTicks);
        //}

        //if (playerEntity.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            //user.sendMessage(Text.literal("クールダウン中だ！"));
        //}
    //クールタイム入ってないときの処理
        //else {
        //   this.GatlingShot(world,user,stack,remainingUseTicks);
        //    playerEntity.getItemCooldownManager().set(this, RapidShot);
        //}
    }

//アイテムを放り込んでいるときの処理
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        PlayerEntity playerEntity = (PlayerEntity) entity;
        //playerEntity.sendMessage(Text.literal(String.valueOf(RapidShot)));

    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    public void GatlingShot(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {

    //連射力周り
        //if(RapidShot <= RapidLimit && !world.isClient()) {
        //    RapidShot = RapidShot - 1;
        //}

    //音を鳴らす処理
        user.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.25f);

    //プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        PlayerEntity playerEntity = (PlayerEntity) user;
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return;
        }

    //弓につがえた矢を取得している？
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
    //ワールドがサーバーなら？
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (!list.isEmpty()) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 1.6f, 1.0f, false, null);
            }
        }
    }

//使用をやめたとき、つまりクリックを離したときの処理だ。
   @Override
   public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
       if (!(user instanceof PlayerEntity playerEntity)) {
           return;
       }
       RapidShot = 0;
       playerEntity.getItemCooldownManager().set(this, 20);
       user.playSound(SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, 1.0f, 1.25f);
    }

    @Override
    public String getStandbyArmPose() {
        return "ITEM";
    }

    @Override
    public String getUsingArmPose() {
        return "CROSSBOW_HOLD";
    }
}
