package com.craftsman_bows.interfaces.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

public interface CraftsmanBowUser {

    void craftsmanBowShootAt(LivingEntity target, float pullProgress, Item item, float power);
}
