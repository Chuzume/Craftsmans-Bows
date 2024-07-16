package com.chuzbows.init;

import com.chuzbows.entity.MachineArrowEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.chuzbows.ChuzBowsCore.Mod_ID;

public class Entity {
    public static final EntityType<MachineArrowEntity> SHOTARROW = Registry.register(Registries.ENTITY_TYPE, Identifier.of(Mod_ID,"machine_arrow"), EntityType.Builder.<MachineArrowEntity>create(MachineArrowEntity::new, SpawnGroup.MISC).dimensions(0.5f, 0.5f).eyeHeight(0.13f).maxTrackingRange(4).trackingTickInterval(20).build("machine_arrow"));
    public static void init() {
    }
}

