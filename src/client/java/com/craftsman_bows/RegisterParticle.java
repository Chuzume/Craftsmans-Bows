package com.craftsman_bows;

import com.craftsman_bows.particle.ChargeDustParticle;
import com.craftsman_bows.particle.ChargedParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.EndRodParticle;

import static com.craftsman_bows.init.ModParticle.*;

public class RegisterParticle
{
    public static void init() {
        ParticleFactoryRegistry.getInstance().register(SPARKLE_PARTICLE, EndRodParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CHARGE_DUST, ChargeDustParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOOT, ChargedParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CHARGE_END, ChargedParticle.Factory::new);
    }
}
