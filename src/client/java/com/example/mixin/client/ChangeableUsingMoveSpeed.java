package com.example.mixin.client;

import com.example.ExampleMod;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class ChangeableUsingMoveSpeed extends AbstractClientPlayerEntity {
    @Shadow public Input input;
    @Shadow protected int ticksLeftToDoubleTapSprint;

    @Shadow public abstract boolean isUsingItem();

    public ChangeableUsingMoveSpeed(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void ChangeableWeaponSlowdown(CallbackInfo ci)
    {
        if (this.isUsingItem() && !this.hasVehicle() && ExampleMod.SampleGlobal.UsingMoveSpeed > 0)
        {
            this.input.movementForward *= ExampleMod.SampleGlobal.UsingMoveSpeed;
            this.input.movementSideways *= ExampleMod.SampleGlobal.UsingMoveSpeed;
            ExampleMod.SampleGlobal.IgnoreSlowdown = false;
        }
    }
}