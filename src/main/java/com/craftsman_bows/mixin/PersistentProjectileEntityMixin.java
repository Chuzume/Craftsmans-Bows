package com.craftsman_bows.mixin;

import com.craftsman_bows.interfaces.entity.BypassCooldown;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.entity.projectile.PersistentProjectileEntity.class)

public abstract class PersistentProjectileEntityMixin extends Entity implements BypassCooldown{

    public PersistentProjectileEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract void initDataTracker(DataTracker.Builder builder);

    @Override
    public void setBypassDamageCooldown() {
        this.dataTracker.set(BYPASS_DAMAGE_COOLDOWN, true);
    }

    @Override
    public boolean getBypassDamageCooldown() {
        return this.dataTracker.get(BYPASS_DAMAGE_COOLDOWN);
    }

    @Override
    public void setWeakKnockback() {
        this.dataTracker.set(WEAK_KNOCKBACK, true);
    }

    @Override
    public boolean getWeakKnockback() {
        return this.dataTracker.get(WEAK_KNOCKBACK);
    }

    @Unique
    private static final TrackedData<Boolean> BYPASS_DAMAGE_COOLDOWN = DataTracker.registerData(PersistentProjectileEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Unique
    private static final TrackedData<Boolean> WEAK_KNOCKBACK = DataTracker.registerData(PersistentProjectileEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    // ヒット時に無敵時間を剥がす
    @Inject(method = "onEntityHit", at = @At(value = "HEAD"))
    public void forceBypassCooldown(EntityHitResult entityHitResult, CallbackInfo ci) {
        net.minecraft.entity.Entity entity = entityHitResult.getEntity();
        if (getBypassDamageCooldown()) {
            if (entity instanceof LivingEntity) {
                entity.timeUntilRegen = 0;
            }
        }
    }

    @Redirect(method = "knockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;modifyKnockback(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;F)F"))
    public float weakKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseKnockback) {
        return baseKnockback;
    }

    // データトラッカーくんを呼び出す処理
    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(BYPASS_DAMAGE_COOLDOWN, false);
        builder.add(WEAK_KNOCKBACK, false);
    }

    // 地面に刺さったらオフ
    @Inject(method = "onBlockHit", at = @At(value = "TAIL"))
    protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        this.dataTracker.set(BYPASS_DAMAGE_COOLDOWN, false);
        this.dataTracker.set(WEAK_KNOCKBACK, false);
    }

    // NBTに書き込む処理
    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("BypassDamageCooldown", getBypassDamageCooldown());
        nbt.putBoolean("WeakKnockback", getWeakKnockback());
    }
}