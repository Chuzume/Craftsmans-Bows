package com.chuzbows.entity;

import com.chuzbows.init.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class MachineArrowEntity
        extends PersistentProjectileEntity {

    private int duration = 200;
    //private boolean CUSTOM_CRITICAL = false;
    private static final TrackedData<Boolean> CUSTOM_CRITICAL = DataTracker.registerData(MachineArrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public MachineArrowEntity(EntityType<? extends MachineArrowEntity> entityType, World world) {
        super((EntityType<? extends PersistentProjectileEntity>)entityType, world);
    }

    public MachineArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(Entity.SHOTARROW, owner, world, stack, shotFrom);
    }

    public void setCustomCritical() {
        this.dataTracker.set(CUSTOM_CRITICAL,true);
    }

    public boolean getCustomCritical() {
        return this.dataTracker.get(CUSTOM_CRITICAL);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CUSTOM_CRITICAL, false);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d vec3d = this.getVelocity();
        double e = vec3d.x;
        double f = vec3d.y;
        double g = vec3d.z;

        if (this.inGround) {
            this.discard();
        }

        if (this.getWorld().isClient && !this.inGround && getCustomCritical()) {
                playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.0f);
            for (int i = 0; i < 4; ++i) {
                    this.getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK, this.getX() + e * (double)i / 4.0, this.getY() + f * (double)i / 4.0, this.getZ() + g * (double)i / 4.0, -e, -f + 0.2, -g);
                    //this.getWorld().addParticle(ParticleTypes.CRIT, this.getX() + e * (double)i / 4.0, this.getY() + f * (double)i / 4.0, this.getZ() + g * (double)i / 4.0, -e, -f + 0.2, -g);
                }
            }
        }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        net.minecraft.entity.Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity) {
            entity.timeUntilRegen = 0;
        }
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
