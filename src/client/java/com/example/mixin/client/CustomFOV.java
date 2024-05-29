package com.example.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.network.AbstractClientPlayerEntity.class)

public class CustomFOV {
    @Inject(method = "getFovMultiplier",at = @At(value = "TAIL"), cancellable = true)
    public void getFovMultiplier(CallbackInfoReturnable<Float> cir) {
        float f = 1.5f; // float fを希望の値に変更
        cir.setReturnValue(f);
    }
}
