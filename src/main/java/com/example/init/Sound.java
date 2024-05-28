package com.example.init;

import com.example.ExampleMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class Sound {

    public static final SoundEvent LEGACY_BOW_SHOOT_1 = registerSoundEvent("legacy_arrow_1");
    public static final SoundEvent LEGACY_BOW_SHOOT_2 = registerSoundEvent("legacy_arrow_2");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(ExampleMod.Mod_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

        public static void init() {
    }
}