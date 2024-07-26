package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.CustomUsingMoveItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class ChangeableUsingMoveSpeed extends AbstractClientPlayerEntity {

    @Unique
    ClientPlayerEntity target = (ClientPlayerEntity) (Object) this;

    @Shadow
    public Input input;

    public ChangeableUsingMoveSpeed(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    // アイテムの使用の設定
    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void ChangeableWeaponSlowdown(CallbackInfo ci) {
        ItemStack itemStack = target.getActiveItem();
        if (itemStack.getItem() instanceof CustomUsingMoveItem customUsingMoveItem) {
            float movementSpeed = customUsingMoveItem.getMovementSpeed();
            this.input.movementForward *= movementSpeed;
            this.input.movementSideways *= movementSpeed;
            customUsingMoveItem.resetMovementSpeed();
        }
    }
}