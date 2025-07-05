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
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    public abstract boolean shouldSlowDown();

    @Shadow
    protected abstract boolean canVehicleSprint(Entity vehicle);

    @Shadow
    protected int ticksLeftToDoubleTapSprint;

    @Shadow public abstract void tick();

    public CustomUsingMoveSpeedMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldSlowDown()Z" , shift = At.Shift.AFTER))
    private void ChangeableWeaponSlowdown(CallbackInfo ci) {
        if (this.isIgnoreSlowdown()) {
            this.sidewaysSpeed *= 5.0F;
            this.forwardSpeed *= 5.0F;
        }
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

    @Unique
    private boolean wasPressingForwardKeyLastTick = false;

    // アイテム使用しつつも前進キー2回でダッシュ可能になる
    @Inject(method = "tickMovement", at = @At(value = "TAIL"))
    private void canStartDoubleTapSprint(CallbackInfo ci) {

        // 今回のフレームの状態
        boolean isPressingForward = this.canStartUsingSprinting();

        // キーが「今押されていて、前は押されていなかった」時だけ処理する
        if (this.isIgnoreSlowdown() && isPressingForward && !wasPressingForwardKeyLastTick) {

            // 1回だけ実行される処理（押した瞬間）
            if (this.ticksLeftToDoubleTapSprint > 0) {
                this.setSprinting(true);
            } else {
                this.ticksLeftToDoubleTapSprint = 7;
            }
        }

        // 使用中でもダッシュ可能なアイテムを使ってるなら、キーでダッシュ開始できる
        if (this.isIgnoreSlowdown() && this.input.playerInput.sprint()) {
            this.setSprinting(true);
        }

        // 状態を記録（次の tick のために）
        wasPressingForwardKeyLastTick = isPressingForward;
    }


    // 移動速度下がらないアイテムを使っている場合、アイテム使用中はticksLeftToDoubleTapSprintが0になるのを無効化する
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean cancelWeaponSlowdown(ClientPlayerEntity instance) {
        if (this.isIgnoreSlowdown()) {
            return false;
        } else return instance.isUsingItem();
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
                && !this.isUsingItem()
                && !this.hasStatusEffect(StatusEffects.BLINDNESS)
                && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle()))
                && !this.isFallFlying();
    }
}