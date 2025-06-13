package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.CanSprintWhileUsing;
import com.craftsman_bows.interfaces.item.CustomUsingMoveItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 右クリックの長押し中だろうがダッシュできるアイテムに関する処理

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class CustomUsingMoveSpeedMixin extends AbstractClientPlayerEntity {

    // これはイマイチわかっていない
    @Unique
    ClientPlayerEntity target = (ClientPlayerEntity) (Object) this;

    // @Shadowと付けると継承元のメソッドを呼び出してもエラーが出なくなる。理由はよくわかっていない。
    @Shadow
    protected abstract boolean canSprint();

    @Shadow
    protected abstract boolean isBlind();

    @Shadow
    public Input input = new Input();

    @Shadow
    public abstract boolean shouldSlowDown();

    @Shadow
    protected abstract boolean canVehicleSprint(Entity vehicle);

    @Shadow
    protected int ticksLeftToDoubleTapSprint;

    public CustomUsingMoveSpeedMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Unique
    // 使用中でも移動速度が下がらないアイテムを使っている
    private boolean isIgnoreSlowdown() {
        ItemStack itemStack = target.getActiveItem();
        return (itemStack.getItem() instanceof CanSprintWhileUsing);
    }

    // アイテム使用時には移動速度が0.2倍になるので、5倍すれば元の速度に戻るってわけだ
    @Inject(method = "tickMovementInput", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;forwardSpeed:F", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void onSetMovementSpeed(CallbackInfo ci) {
        if (this.isIgnoreSlowdown()) {
            this.sidewaysSpeed *= 5.0F;
            this.forwardSpeed *= 5.0F;
        }
        // 移動速度を変更できるものは、5倍したあとに倍率かけて速度を変更する
            ItemStack itemStack = target.getActiveItem();
        if (itemStack.getItem() instanceof CustomUsingMoveItem customUsingMoveItem) {
            float movementSpeed = customUsingMoveItem.getMovementSpeed();
            this.sidewaysSpeed *= 5.0f * movementSpeed;
            this.forwardSpeed *= 5.0f * movementSpeed;
            customUsingMoveItem.resetMovementSpeed();
        }
    }

    // 特定のアイテムを持っていればアイテム使用中でもダッシュができるように
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canStartSprinting()Z"))
        private boolean canStartDoubleTapSprint(ClientPlayerEntity instance) {
        if (ticksLeftToDoubleTapSprint > 0) {
            System.out.println("ticksLeftToDoubleTapSprint: " + ticksLeftToDoubleTapSprint);
        }

        // 移動速度下がらないアイテムを使っている場合
        if (this.isIgnoreSlowdown()) {
            return !this.isSprinting()
            && this.input.hasForwardMovement()
            && this.canSprint()
            && !this.isBlind()
            && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle()))
            && (!this.isGliding() || this.isSubmergedInWater())
            && (!this.shouldSlowDown() || this.isSubmergedInWater())
            && (!this.isTouchingWater() || this.isSubmergedInWater());
        }

        // そうでない場合、元のメソッドの処理をそっくりそのまま実行する。
        else return !this.isSprinting()
                && this.input.hasForwardMovement()
                && this.canSprint()
                && !this.isUsingItem()
                && !this.isBlind()
                && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle()))
                && (!this.isGliding() || this.isSubmergedInWater())
                && (!this.shouldSlowDown() || this.isSubmergedInWater())
                && (!this.isTouchingWater() || this.isSubmergedInWater());
    }

    // 移動速度下がらないアイテムを使っている場合、アイテム使用中はticksLeftToDoubleTapSprintが0になるのを無効化する
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean cancelWeaponSlowdown(ClientPlayerEntity instance) {
        if (this.isIgnoreSlowdown()) {
            return false;
        }
        else return instance.isUsingItem();
    }
}