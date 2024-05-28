package com.example.init;

import com.example.ExampleMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class initSounds {
    public static final Identifier SOUND_GOAT = new Identifier(ExampleMod.Mod_ID, "legacy_arrow_1");
    public static SoundEvent SOUND_GOAT_EVENT = SoundEvent.of(SOUND_GOAT);

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, SOUND_GOAT, SOUND_GOAT_EVENT);
    }
}