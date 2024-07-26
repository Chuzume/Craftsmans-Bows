package com.chuzbows.mixin;

import com.chuzbows.interfaces.entity.BypassCooldown;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.entity.projectile.PersistentProjectileEntity.class)

public abstract class PersistentProjectileEntityMixin extends Entity implements BypassCooldown {

    public PersistentProjectileEntityMixin(EntityType<?> type, World world, boolean bypassDamageCooldown) {
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

    @Unique
    private static final TrackedData<Boolean> BYPASS_DAMAGE_COOLDOWN = DataTracker.registerData(PersistentProjectileEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    // ヒット時に無敵時間を剥がす
    @Inject(method = "onEntityHit", at = @At(value = "HEAD"), cancellable = true)
    public void forceBypassCooldown(EntityHitResult entityHitResult, CallbackInfo ci) {
        net.minecraft.entity.Entity entity = entityHitResult.getEntity();
        if (getBypassDamageCooldown()) {
            if (entity instanceof LivingEntity) {
                entity.timeUntilRegen = 0;
            }
        }
    }

    // データトラッカーくんを呼び出す処理
    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    protected void initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(BYPASS_DAMAGE_COOLDOWN, false);
    }

    // 地面に刺さったらオフ
    @Inject(method = "onBlockHit", at = @At(value = "TAIL"))
    protected void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        this.dataTracker.set(BYPASS_DAMAGE_COOLDOWN, false);
    }

    // NBTに書き込む処理
    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("BypassDamageCooldown", getBypassDamageCooldown());
    }
}

