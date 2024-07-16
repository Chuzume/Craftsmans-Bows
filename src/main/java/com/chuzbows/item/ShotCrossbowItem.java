package com.chuzbows.item;

import com.chuzbows.ChuzBowsCore;
import com.chuzbows.entity.MachineArrowEntity;
import com.chuzbows.init.ModSoundEvents;
import com.chuzbows.item_interface.CustomArmPoseItem;
import com.chuzbows.item_interface.CustomUsingMoveItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class ShotCrossbowItem extends BowItem implements CustomUsingMoveItem, CustomArmPoseItem {
        public ShotCrossbowItem(Settings settings) {
        super(settings);
    }

    //変数
    float MovementSpeed = 2.5f;
    String StandbyArmPose = "CROSSBOW_HOLD";
    String UsingArmPose = "CROSSBOW_CHARGE";

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

    //シンプルに右クリックを推し続けている時間を秒数換算で取得
    public static float getChargeTime(int useTicks) {
        float f = (float) useTicks / 20.0f;
        return f;
    }

    //アイテムを使用しているときの処理？
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        PlayerEntity playerEntity = (PlayerEntity) user;
        MovementSpeed = 2.5f;
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float chargeTime = getChargeTime(i);

        //腕のポーズ変更
        if(chargeTime < 0.9) {
            UsingArmPose = "CROSSBOW_CHARGE";
        }
        else{
            UsingArmPose = "CROSSBOW_HOLD";
        }

        //logger = Logger.getLogger("YourClassName");
        //logger.severe(String.valueOf(chargeTime));

        //
        if(chargeTime == 0.5f) {
            user.playSound(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), 1.0f, 1.0f);

        }
        if(chargeTime == 1.0f) {
            user.playSound(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), 1.0f, 1.5f);
            user.playSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE, 1.0f, 2f);
        }
    }

    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        //ArrowItem arrowItem;
        //Item item = projectileStack.getItem();
        //ArrowItem arrowItem2 = item instanceof ArrowItem ? (arrowItem = (ArrowItem) item) : (ArrowItem) Items.ARROW;
        MachineArrowEntity persistentProjectileEntity = new MachineArrowEntity(world, shooter, weaponStack, projectileStack);

        if (critical) {
            persistentProjectileEntity.setCustomCritical();
        }

        return persistentProjectileEntity;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
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
        if ((double) f < 1) {
            return;
        }

        //ここが放つ処理に見える。
        ChuzBowsCore.Global.UsingMoveSpeed = Float.NaN;
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);

        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (!list.isEmpty() && f < 1) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 0.0f, false, null);
            } else {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.4f, 0.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.4f, 20.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.4f, 20.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.4f, 20.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.4f, 20.0f, f == 1.0f, null);
            }
        }

        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_2, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);

        //腕振る処理
        Hand activeHand = user.getActiveHand();
        if (activeHand == Hand.MAIN_HAND) {
            //現在のアクティブな手がメインハンドなら、メインハンドを振る
            user.swingHand(Hand.MAIN_HAND);
        } else if (activeHand == Hand.OFF_HAND) {
            //現在のアクティブな手がオフハンドなら、オフハンドを振る
            user.swingHand(Hand.OFF_HAND);
        }
    }

    //インターフェース「CustomUsingMoveItem」として必要な処理
    @Override
    public float getMovementSpeed() {
        return MovementSpeed;
    }

    @Override
    public float resetMovementSpeed() {
        MovementSpeed = Float.NaN;
        return MovementSpeed;
    }

    //インターフェス「CustomArmPoseItem」として必要な処理
    @Override
    public String getUsingArmPose() {
        return UsingArmPose;
    }
    @Override
    public String getStandbyArmPose() {
        return StandbyArmPose;
    }
}
