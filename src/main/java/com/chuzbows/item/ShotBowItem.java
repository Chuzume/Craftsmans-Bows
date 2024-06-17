package com.chuzbows.item;

import com.chuzbows.ChuzBowsCore;
import com.chuzbows.entity.ShotArrowEntity;
import com.chuzbows.init.ModSoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShotBowItem extends BowItem {
    public ShotBowItem(Settings settings) {
        super(settings);
    }

//弓を引いた時間を取得する処理のようだ。今回は書き換えて、0.55以上引き絞ったら強制的に1（フルチャージ）になるようにした
    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 1f) {
            f = 1f;
        }
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
        PlayerEntity playerEntity = (PlayerEntity) user;
        ChuzBowsCore.Global.UsingMoveSpeed = 2.5f;
    }

    //public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
    //    //ArrowEntity arrowEntity = new ArrowEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1), null);
    //    ShotArrowEntity ArrowEntity = new ShotArrowEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1), null);
    //    SpectralArrowEntity spectralArrowEntity = new SpectralArrowEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount(1), null);
//
    //    //arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
    //    //return arrowEntity;
    //}
    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        ArrowItem arrowItem;
        Item item = projectileStack.getItem();
        ArrowItem arrowItem2 = item instanceof ArrowItem ? (arrowItem = (ArrowItem)item) : (ArrowItem)Items.ARROW;
        PersistentProjectileEntity persistentProjectileEntity = HogeArrow(world, projectileStack, shooter, weaponStack);

        if (critical) {
            persistentProjectileEntity.setCritical(true);
        }
        return persistentProjectileEntity;
    }


    public PersistentProjectileEntity HogeArrow(World world, ItemStack stack, LivingEntity shooter, @Nullable ItemStack shotFrom) {
        return new ShotArrowEntity(world, shooter, stack.copyWithCount(1), shotFrom);
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
            ServerWorld serverWorld = (ServerWorld)world;
            if (!list.isEmpty()) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 0.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 20.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 20.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 20.0f, f == 1.0f, null);
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 20.0f, f == 1.0f, null);
            }
        }
        if (f < 1) {
            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        } else {
            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_2, SoundCategory.PLAYERS, 1.0f, 1.0f);
            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);
        }
    }
}
