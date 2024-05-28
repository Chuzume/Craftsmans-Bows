package com.example.mixin.client;

import com.example.ExampleMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class DisableItemUsingSlowdown extends AbstractClientPlayerEntity {
        @Shadow protected abstract boolean isWalking();

        @Shadow protected abstract boolean canSprint();


    public DisableItemUsingSlowdown(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "canStartSprinting",at = @At(value = "HEAD"),cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
            cir.setReturnValue(!this.isSprinting() && this.isWalking());

    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean cancelWeaponSlowdown(ClientPlayerEntity instance) {
        if (ExampleMod.SampleGlobal.IgnoreSlowdown){
            ExampleMod.SampleGlobal.IgnoreSlowdown = false;
            return false;
}
            else return instance.isUsingItem();
        // ExampleMod.SampleGlobal.IgnoreSlowdown = false;
    }
}