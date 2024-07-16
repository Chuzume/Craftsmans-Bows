package com.chuzbows.init;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import static com.chuzbows.ChuzBowsCore.Mod_ID;

public class ModDamage {
    RegistryKey<DamageType> MACHINE_ARROW = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Mod_ID, "machine_arrow"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
        static void init() {
        }
    }
