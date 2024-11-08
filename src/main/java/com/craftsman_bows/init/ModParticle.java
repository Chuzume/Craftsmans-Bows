package com.craftsman_bows.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.craftsman_bows.CraftsmanBows.Mod_ID;

public class ModParticle {

    public static final SimpleParticleType SPARKLE_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType CHARGE_DUST = FabricParticleTypes.simple();
    public static final SimpleParticleType CHARGE_END = FabricParticleTypes.simple();
    public static final SimpleParticleType SHOOT = FabricParticleTypes.simple();

    public static void init() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Mod_ID, "test"), SPARKLE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Mod_ID, "charge_dust"), CHARGE_DUST);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Mod_ID, "charge_end"), CHARGE_END);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Mod_ID, "shoot"), SHOOT);
    }
}