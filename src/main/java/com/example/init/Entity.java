package com.example.init;

import com.example.entity.ShotArrowEntity;
import com.example.ExampleMod;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.example.ExampleMod.Mod_ID;
import static net.fabricmc.loader.impl.FabricLoaderImpl.MOD_ID;

public class Entity {
    public static final EntityType<ShotArrowEntity> SHOTARROW = Registry.register(Registries.ENTITY_TYPE, Identifier.of("example","shotarrow"), EntityType.Builder.<ShotArrowEntity>create(ShotArrowEntity::new, SpawnGroup.MISC).dimensions(0.5f, 0.5f).eyeHeight(0.13f).maxTrackingRange(4).trackingTickInterval(20).build("shotarrow"));

    public static void init() {
    }
}
