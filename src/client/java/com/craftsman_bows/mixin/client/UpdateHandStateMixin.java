package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.IsCustomItem;
import com.craftsman_bows.interfaces.item.CustomArmPoseItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)

public class UpdateHandStateMixin{

    @Inject(method = "updateHandState", at = @At("TAIL"))
    private void updateHandState(AbstractClientPlayerEntity player, PlayerEntityRenderState.HandState handState, Hand hand, CallbackInfo ci) {
        ItemStack itemStack = player.getStackInHand(hand);

        // 特定のアイテムかどうかをチェック
        if (itemStack.getItem() instanceof CustomArmPoseItem customArmPoseItem && player.getItemUseTimeLeft() > 0) {
            boolean isCustom = true;
            // インターフェースを通してフィールドにアクセス
            ((IsCustomItem) handState).setCustomItem(isCustom);
        }
        else {
            boolean isCustom = false;
            ((IsCustomItem) handState).setCustomItem(isCustom);
        }
    }
}