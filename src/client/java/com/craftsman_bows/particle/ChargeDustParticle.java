package com.craftsman_bows.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class ChargeDustParticle extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;

    protected ChargeDustParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f);
        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;
        this.x = d;
        this.y = e;
        this.z = f;
        this.startX = this.x;
        this.startY = this.y;
        this.startZ = this.z;
        this.scale = 0.2F * this.random.nextFloat();;
        //float j = this.random.nextFloat() * 0.6F + 0.4F;
        //this.red = j * 0.9F;
        //this.green = j * 0.3F;
        //this.blue = j;
        this.maxAge = (int)(Math.random() * 10.0) + 40;

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
    public float getSize(float tickDelta) {
        float f = ((float)this.age + tickDelta) / (float)this.maxAge;
        f = 1.0F - f;
        f *= f;
        f = 1.0F - f;
        return this.scale;
    }

    @Override
    public int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            float f = (float)this.age / (float)this.maxAge;
            float var3 = -f + f * f * 2.0F;
            float var4 = 1.0F - var3;
            this.x = this.startX + this.velocityX * (double)var4;
            this.y = this.startY + this.velocityY * (double)var4;
            this.z = this.startZ + this.velocityZ * (double)var4;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            ChargeDustParticle ChargeDustParticle = new ChargeDustParticle(clientWorld, d, e, f, g, h, i);
            ChargeDustParticle.setSprite(this.spriteProvider);
            return ChargeDustParticle;
        }
    }
}