package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.IsCustomItem;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.HandState.class)

public class PlayerEntityRenderStateMixin implements IsCustomItem {
    @Unique
    private boolean isCustomItem;

    @Override
    public boolean isCustomItem() {
        return isCustomItem;
    }

    @Override
    public void setCustomItem(boolean customItem) {
        isCustomItem = customItem;
    }
}