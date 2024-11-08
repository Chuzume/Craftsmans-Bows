package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.CanSprintWhileUsing;
import com.mojang.authlib.GameProfile;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 右クリックの長押し中だろうがダッシュできるアイテムに関する処理

@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public abstract class NoUsingSlowdownMixin extends AbstractClientPlayerEntity {

    // これはイマイチわかっていない
    @Unique
    ClientPlayerEntity target = (ClientPlayerEntity) (Object) this;

    // @Shadowと付けると継承元のメソッドを呼び出してもエラーが出なくなる。理由はよくわかっていない。
    @Shadow
    protected abstract boolean isWalking();

    @Shadow
    protected abstract boolean canSprint();

    @Shadow
    protected abstract boolean canVehicleSprint(Entity vehicle);

    public NoUsingSlowdownMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    // ダッシュが開始できるかどうかのメソッドの後ろに処理を付け足して、「CanSprintWhileUsing」インターフェースのアイテムならダッシュ開始できるようにした
    @Inject(method = "canStartSprinting", at = @At("TAIL"), cancellable = true)
    private void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = target.getActiveItem();
        cir.setReturnValue(!this.isSprinting() && this.isWalking() && this.canSprint() && (!this.isUsingItem() || itemStack.getItem() instanceof CanSprintWhileUsing) && !this.hasStatusEffect(StatusEffects.BLINDNESS) && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle())) && !this.isGliding());
    }

    // アイテム使用中はダブルタップでダッシュできなくなるって処理を「CanSprintWhileUsing」インターフェースのアイテムなら無視するようにした。もっといいやり方がある気がする。
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean cancelWeaponSlowdown(ClientPlayerEntity instance) {
        ItemStack itemStack = target.getActiveItem();
        if (itemStack.getItem() instanceof CanSprintWhileUsing) return false;
        else return instance.isUsingItem();
    }
}