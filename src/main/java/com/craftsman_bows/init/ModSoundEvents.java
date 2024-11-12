package com.craftsman_bows.init;

import com.craftsman_bows.CraftsmanBows;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    public static final net.minecraft.sound.SoundEvent LEGACY_BOW_SHOOT_1 = registerSoundEvent("legacy_arrow_1");
    public static final net.minecraft.sound.SoundEvent LEGACY_BOW_SHOOT_2 = registerSoundEvent("legacy_arrow_2");
    public static final net.minecraft.sound.SoundEvent DUNGEONS_BOW_CHARGE = registerSoundEvent("dungeons_bow_load");
    public static final net.minecraft.sound.SoundEvent DUNGEONS_BOW_SHOOT = registerSoundEvent("dungeons_bow_shoot");
    public static final net.minecraft.sound.SoundEvent DUNGEONS_COG_CROSSBOW_PICKUP = registerSoundEvent("dungeons_cog_crossbow_pickup");
    public static final net.minecraft.sound.SoundEvent DUNGEONS_COG_CROSSBOW_SHOOT = registerSoundEvent("dungeons_cog_crossbow_shoot");
    public static final net.minecraft.sound.SoundEvent DUNGEONS_COG_CROSSBOW_PLACE = registerSoundEvent("dungeons_cog_crossbow_place");

    private static net.minecraft.sound.SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(CraftsmanBows.Mod_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, net.minecraft.sound.SoundEvent.of(id));
    }

        public static void init() {
    }
}