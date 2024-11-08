package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.ZoomItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Fovをいじくりまわす処理

@Mixin(net.minecraft.client.network.AbstractClientPlayerEntity.class)

public class FovMixin {

    @Unique
    AbstractClientPlayerEntity target = (AbstractClientPlayerEntity) (Object) this;

    @Inject(method = "getFovMultiplier", at = @At(value = "HEAD"), cancellable = true)
    public void getFovMultiplier(CallbackInfoReturnable<Float> cir) {
        ItemStack itemStack = target.getActiveItem();
        if (itemStack.getItem() instanceof ZoomItem zoomItem) {
            float fov = zoomItem.getFov();
            if (!Float.isNaN(fov)) {
                cir.setReturnValue(fov);
                zoomItem.resetFov();
            }
        }
    }
}