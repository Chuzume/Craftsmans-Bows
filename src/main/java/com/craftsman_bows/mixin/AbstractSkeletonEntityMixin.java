package com.craftsman_bows.mixin;

import com.craftsman_bows.entity.ai.goal.LongBowAttackGoal;
import com.craftsman_bows.entity.ai.goal.ShortBowAttackGoal;
import com.craftsman_bows.init.ModSoundEvents;
import com.craftsman_bows.init.item;
import com.craftsman_bows.interfaces.entity.CraftsmanBowUser;
import com.craftsman_bows.item.CraftsmanBowItem;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity implements CraftsmanBowUser {

    @Shadow
    protected abstract PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom);

    @Shadow @Nullable public abstract EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData);

    @Unique
    AbstractSkeletonEntity self = (AbstractSkeletonEntity) (Object) this;

    @Unique
    private final ShortBowAttackGoal<AbstractSkeletonEntity> shortBowAttackGoal = new ShortBowAttackGoal<>(self, 1.025F, 10, 10F, 0.8F, 20);

    @Unique
    private final LongBowAttackGoal<AbstractSkeletonEntity> longBowAttackGoal = new LongBowAttackGoal<>(self, 0.5F, 60, 50.0F, 2.0F, 40);

    protected AbstractSkeletonEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super((EntityType<? extends AbstractSkeletonEntity>) entityType, world);
    }

    @Inject(
            method = "updateAttackType()V",
            at = @At("TAIL")
    )
    private void onUpdateAttackType(CallbackInfo ci) {

        if (self.getWorld() != null && !self.getWorld().isClient) {
            // 一度攻撃手段をリセット
            super.goalSelector.remove(this.shortBowAttackGoal);
            super.goalSelector.remove(this.longBowAttackGoal);

            // アイテムチェック
                // ショートボウ
                ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, item.SHORT_BOW));
                if (itemStack.isOf(item.SHORT_BOW)) {
                    this.shortBowAttackType();
                    return;
                }
                // ロングボウ
                itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, item.LONG_BOW));
                if (itemStack.isOf(item.LONG_BOW)) {
                    this.longBowAttackType();
                }
        }
    }

    @Unique
    private void shortBowAttackType() {
        // 難易度によって弓を引く時間を変える
        int i = this.getShortHardAttackInterval();
        if (self.getWorld().getDifficulty() != Difficulty.HARD) {
            i = this.getShortRegularAttackInterval();
        } // 殴る処理はバニラの方が決めてくれてるんだよなぁ！

        // 弓を持っているので弓AIを使いま～す
        this.shortBowAttackGoal.setAttackInterval(i);
        super.goalSelector.add(4, this.shortBowAttackGoal);
    }

    @Unique
    private int getShortHardAttackInterval() {
        return 10;
    }

    @Unique
    private int getShortRegularAttackInterval() {
        return 20;
    }

    @Unique
    private void longBowAttackType() {
        // 難易度によって弓を引く時間を変える
        int i = this.getLongHardAttackInterval();
        if (self.getWorld().getDifficulty() != Difficulty.HARD) {
            i = this.getLongRegularAttackInterval();
        } // 殴る処理はバニラの方が決めてくれてるんだよなぁ！

        // 弓を持っているので弓AIを使いま～す
        this.longBowAttackGoal.setAttackInterval(i);
        super.goalSelector.add(4, this.longBowAttackGoal);
    }

    @Unique
    private int getLongHardAttackInterval() {
        return 50;
    }

    @Unique
    private int getLongRegularAttackInterval() {
        return 60;
    }

    // 持ってるアイテムがバニラの弓限定はきついからな、変えさせてもらうぜ！
    @Redirect(
            method = "shootAt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileUtil;getHandPossiblyHolding(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/Item;)Lnet/minecraft/util/Hand;"
            )
    )
    private Hand redirectHand(LivingEntity entity, Item item) {

        for (Hand hand : Hand.values()) {
            ItemStack stack = entity.getStackInHand(hand);

            // このmod製弓の持ってる位置を取得
            if (stack.isOf(item) || stack.getItem() instanceof CraftsmanBowItem) {
                return hand;
            }
        }

        return Hand.MAIN_HAND;
    }

    @Override
    public void craftsmanBowShootAt(LivingEntity target, float pullProgress, Item weapon, float power) {
        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, weapon));
        ItemStack itemStack2 = this.getProjectileType(itemStack);
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        World var15 = this.getWorld();
        if (var15 instanceof ServerWorld serverWorld) {
            ProjectileEntity.spawnWithVelocity(persistentProjectileEntity, serverWorld, itemStack2, d, e + g * (double)0.2F, f, power, (float)(14 - serverWorld.getDifficulty().getId() * 4));
        }

        // 演出
        if (weapon == item.SHORT_BOW) {
            this.playSound(ModSoundEvents.LEGACY_BOW_SHOOT_1, 1.0F, 0.8F / (this.getRandom().nextFloat() * 0.4f + 1.2f) + pullProgress * 0.5f);
            this.playSound(ModSoundEvents.DUNGEONS_BOW_SHOOT, 1.0f, 1.4f);
            return;
        }
        if (weapon == item.LONG_BOW) {
            this.playSound(ModSoundEvents.LEGACY_BOW_SHOOT_1, 1.0f, 1.3f);
            this.playSound(ModSoundEvents.DUNGEONS_BOW_SHOOT, 1.0f, 1.0f);
        }
    }
}