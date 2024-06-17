package com.chuzbows.init;

import com.chuzbows.entity.ShotArrowEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Entity {
    public static final EntityType<ShotArrowEntity> SHOTARROW = Registry.register(Registries.ENTITY_TYPE, Identifier.of("example","shotarrow"), EntityType.Builder.<ShotArrowEntity>create(ShotArrowEntity::new, SpawnGroup.MISC).dimensions(0.5f, 0.5f).eyeHeight(0.13f).maxTrackingRange(4).trackingTickInterval(20).build("shotarrow"));
    public static void init() {
    }
}
