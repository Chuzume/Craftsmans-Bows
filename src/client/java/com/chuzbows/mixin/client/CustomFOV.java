package com.chuzbows.mixin.client;

import com.chuzbows.item_interface.ZoomItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.network.AbstractClientPlayerEntity.class)

public class CustomFOV {

    @Unique
    AbstractClientPlayerEntity target = (AbstractClientPlayerEntity) (Object) this;

    @Inject(method = "getFovMultiplier", at = @At(value = "HEAD"), cancellable = true)
    public void getFovMultiplier(CallbackInfoReturnable<Float> cir) {
        float fov_result = 1.0f;


        ItemStack itemStack = target.getActiveItem();
        //ItemStack itemStack = target.getStackInHand(Hand.);

        if (itemStack.getItem() instanceof ZoomItem zoomItem) {

            float fov = zoomItem.getFOV();

            if (!Float.isNaN(fov)) {
                cir.setReturnValue(fov);
                zoomItem.resetFOV();
            }
        }
    }
}