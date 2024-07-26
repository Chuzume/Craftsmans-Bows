package com.craftsman_bows.interfaces.entity;

public interface BypassCooldown {

    default void setBypassDamageCooldown() {
    }

    default boolean getBypassDamageCooldown() {
        return false;
    }
}