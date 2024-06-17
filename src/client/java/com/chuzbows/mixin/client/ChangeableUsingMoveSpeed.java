package com.chuzbows.mixin.client;

import com.chuzbows.ChuzBowsCore;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class ChangeableUsingMoveSpeed extends AbstractClientPlayerEntity {
    @Shadow public Input input;
    @Shadow protected int ticksLeftToDoubleTapSprint;
    @Shadow protected abstract boolean isWalking();

    @Shadow public abstract boolean isUsingItem();

    public ChangeableUsingMoveSpeed(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    @Inject(method = "canStartSprinting",at = @At(value = "TAIL"),cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!this.isSprinting() && this.isWalking());
    }
    //@Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void ChangeableWeaponSlowdown(CallbackInfo ci)
    {
        if (this.isUsingItem() && !this.hasVehicle() && !Float.isNaN(ChuzBowsCore.Global.UsingMoveSpeed))
        {
            this.input.movementForward *= ChuzBowsCore.Global.UsingMoveSpeed;
            this.input.movementSideways *= ChuzBowsCore.Global.UsingMoveSpeed;
            ChuzBowsCore.Global.UsingMoveSpeed = Float.NaN;
            //ExampleMod.SampleGlobal.IgnoreSlowdown = false;
        }
    }
}