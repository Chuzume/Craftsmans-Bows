package com.chuzbows.mixin.client;

import com.chuzbows.item_interface.CustomUsingMoveItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Logger;

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class ChangeableUsingMoveSpeed extends AbstractClientPlayerEntity {

    @Unique
    ClientPlayerEntity target = (ClientPlayerEntity) (Object) this;

    @Shadow
    public Input input;
    @Shadow
    protected int ticksLeftToDoubleTapSprint;

    @Shadow
    protected abstract boolean isWalking();

    @Shadow
    public abstract boolean isUsingItem();

    public ChangeableUsingMoveSpeed(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "canStartSprinting", at = @At(value = "TAIL"), cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
    }

    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void ChangeableWeaponSlowdown(CallbackInfo ci) {
        ItemStack itemStack = target.getActiveItem();

        if (itemStack.getItem() instanceof CustomUsingMoveItem customUsingMoveItem) {

            float movementSpeed = customUsingMoveItem.getMovementSpeed();
            this.input.movementForward *= movementSpeed;
            this.input.movementSideways *= movementSpeed;

            //Logger logger = Logger.getLogger("YourClassName");
            //logger.severe(String.valueOf(movementSpeed));

            customUsingMoveItem.resetMovementSpeed();
        }
    }
}