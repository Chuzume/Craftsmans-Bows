package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.CanSprintWhileUsing;
import com.craftsman_bows.interfaces.item.CustomUsingMoveItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
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
    protected abstract boolean isWalking();

    @Shadow
    public Input input = new Input();

    @Shadow
    protected abstract boolean canVehicleSprint(Entity vehicle);

    @Shadow
    public abstract void tick();

    public CustomUsingMoveSpeedMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    // アイテム使用時には移動速度が0.2倍になるので、5倍すれば元の速度に戻る。そこからもう一度倍率をかける。
    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void ChangeableWeaponSlowdown(CallbackInfo ci) {
        ItemStack itemStack = target.getActiveItem();
        if (itemStack.getItem() instanceof CustomUsingMoveItem customUsingMoveItem) {
            float movementSpeed = customUsingMoveItem.getMovementSpeed();
            this.input.movementForward *= 5.0f * movementSpeed;
            this.input.movementSideways *= 5.0f * movementSpeed;
            customUsingMoveItem.resetMovementSpeed();
        }
    }

    // ダッシュが開始できるかどうかのメソッドの後ろに処理を付け足して、「CanSprintWhileUsing」インターフェースのアイテムならダッシュ開始できるようにした
    @Inject(method = "canStartSprinting", at = @At("TAIL"), cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (this.isIgnoreSlowdown()) {
            cir.setReturnValue(canStartUsingSprinting());
        }
    }

    // アイテム使用中はダブルタップでダッシュできなくなるって処理を「CanSprintWhileUsing」インターフェースのアイテムなら無視するようにした。もっといいやり方がある気がする。
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean cancelWeaponSlowdown(ClientPlayerEntity instance) {
        if (isIgnoreSlowdown()) return false;
        else return instance.isUsingItem();
    }

    // 使用中でも移動速度が下がらないアイテムを使っている
    @Unique
    private boolean isIgnoreSlowdown() {
        ItemStack itemStack = target.getActiveItem();
        return (itemStack.getItem() instanceof CanSprintWhileUsing);
    }

    // CanStartSprintをちょっといじったもの。アイテム使用中でもダッシュできるようにするのに使う。
    @Unique
    private boolean canStartUsingSprinting() {
        return !this.isSprinting()
                && this.isWalking()
                && this.canSprint()
                && !this.hasStatusEffect(StatusEffects.BLINDNESS)
                && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle()))
                && !this.isFallFlying();
    }
}