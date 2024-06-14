package com.example.mixin.client;

import com.example.ExampleMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.network.AbstractClientPlayerEntity.class)

public class CustomFOV {
    @Inject(method = "getFovMultiplier",at = @At(value = "HEAD"), cancellable = true)
    public void getFovMultiplier(CallbackInfoReturnable<Float> cir) {
        if (!Float.isNaN(ExampleMod.SampleGlobal.CustomFOV)){
            float f = ExampleMod.SampleGlobal.CustomFOV; // float fを希望の値に変更
            cir.setReturnValue(f);
            ExampleMod.SampleGlobal.CustomFOV = Float.NaN;
        }
    }
}
