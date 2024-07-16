package com.chuzbows.init;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import static com.chuzbows.init.ModDamageTypes.*;

public class ModDamageSources {
    public final Registry<DamageType> registry;

    public ModDamageSources(Registry<DamageType> registry) {
        this.registry = registry;
    }

    public DamageSource machine_arrow(Entity source, @Nullable Entity attacker) {
        return create(MACHINE_ARROW, source, attacker);
    }

    public DamageSource create(RegistryKey<DamageType> key, @Nullable Entity source, @Nullable Entity attacker) {
        return new DamageSource(this.registry.entryOf(key), source, attacker);
    }

    public static void init() {
    }
}
