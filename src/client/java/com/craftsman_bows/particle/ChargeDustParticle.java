package com.craftsman_bows.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class ChargeDustParticle extends AnimatedParticle {
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    protected ChargeDustParticle(ClientWorld clientWorld,
                                 double startX, double startY, double startZ,
                                 double targetX, double targetY, double targetZ,
                                 SpriteProvider spriteProvider) {
        super(clientWorld, startX, startY, startZ,spriteProvider, 0.0125F);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.scale = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
        float j = this.random.nextFloat() * 0.2F + 0.8F;
        this.red = j * 0.9f;
        this.green = j * 0.7f;
        this.blue = j * 0.2f;
        this.maxAge = 5;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            double directionX = targetX - this.x;
            double directionY = targetY - this.y;
            double directionZ = targetZ - this.z;

            double distance = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);

            if (distance > 0.1) {
                directionX /= distance;
                directionY /= distance;
                directionZ /= distance;

                // 収束する速度
                double speed = 0.2;
                this.x += directionX * speed;
                this.y += directionY * speed;
                this.z += directionZ * speed;
            } else {
                this.x = targetX;
                this.y = targetY;
                this.z = targetZ;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world, double startX, double startY, double startZ, double targetX, double targetY, double targetZ) {
            ChargeDustParticle particle = new ChargeDustParticle(world, startX, startY, startZ, targetX, targetY, targetZ,spriteProvider);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}