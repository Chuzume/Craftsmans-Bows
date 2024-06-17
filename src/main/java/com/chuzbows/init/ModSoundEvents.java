package com.chuzbows.init;

import com.chuzbows.ChuzBowsCore;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    public static final net.minecraft.sound.SoundEvent LEGACY_BOW_SHOOT_1 = registerSoundEvent("legacy_arrow_1");
    public static final net.minecraft.sound.SoundEvent LEGACY_BOW_SHOOT_2 = registerSoundEvent("legacy_arrow_2");
    public static final net.minecraft.sound.SoundEvent BOW_CHARGE = registerSoundEvent("bow_charge");

    private static net.minecraft.sound.SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(ChuzBowsCore.Mod_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, net.minecraft.sound.SoundEvent.of(id));
    }

        public static void init() {
    }
}