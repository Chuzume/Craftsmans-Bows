package com.chuzbows.entity;

import com.chuzbows.init.Entity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShotArrowEntity
        extends PersistentProjectileEntity {
    private int duration = 200;


    public ShotArrowEntity(EntityType<? extends ShotArrowEntity> entityType, World world) {
        super((EntityType<? extends PersistentProjectileEntity>)entityType, world);
    }

    public ShotArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(Entity.SHOTARROW, owner, world, stack, shotFrom);
    }

    public ShotArrowEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(Entity.SHOTARROW, x, y, z, world, stack, shotFrom);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient && !this.inGround) {
            this.getWorld().addParticle(ParticleTypes.EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected double getGravity() {
        return 0.07;
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.GLOWING, this.duration, 0);
        target.addStatusEffect(statusEffectInstance, this.getEffectCause());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        ServerWorld serverWorld;
        net.minecraft.entity.Entity entity = entityHitResult.getEntity();
        float f = 8.0f;
        DamageSource damageSource = this.getDamageSources().magic();
        World world = this.getWorld();
        if (world instanceof ServerWorld) {
            serverWorld = (ServerWorld)world;
            f = EnchantmentHelper.getDamage(serverWorld, this.getWeaponStack(), entity, damageSource, f);
        }
        if (entity.damage(damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            world = this.getWorld();
            if (world instanceof ServerWorld) {
                serverWorld = (ServerWorld)world;
                EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource, this.getWeaponStack());
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                this.knockback(livingEntity, damageSource);
                this.onHit(livingEntity);
            }
        }
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Duration")) {
            this.duration = nbt.getInt("Duration");
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Duration", this.duration);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }
}
