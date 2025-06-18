package com.craftsman_bows.init;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.craftsman_bows.CraftsmanBows.Mod_ID;

public class ModComponents {
    public static final ComponentType<Integer> BURST_COUNT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Mod_ID, "burst_count"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final ComponentType<Integer> BURST_STACK = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Mod_ID, "burst_stack"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    public static void init() {
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }
}