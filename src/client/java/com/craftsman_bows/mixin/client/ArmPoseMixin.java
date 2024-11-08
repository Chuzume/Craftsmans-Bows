package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.IsCustomItem;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)

public abstract class ArmPoseMixin{
    @Inject(method = "getArmPose(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState$HandState;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;", at = @At("TAIL"), cancellable = true)
    private static void getArmPose(PlayerEntityRenderState state, PlayerEntityRenderState.HandState handState, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir ) {
        // インターフェース経由でカスタムアイテム判定を取得
        if (((IsCustomItem) handState).isCustomItem()) {
            // カスタムのArmPoseを返す
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD); // 任意のポーズ
        }
    }
}