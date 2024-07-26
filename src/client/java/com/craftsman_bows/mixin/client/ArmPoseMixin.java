package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.CustomArmPoseItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)

public class ArmPoseMixin {
    @Inject(method = "getArmPose", at = @At("TAIL"), cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir ) {

        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.getItem() instanceof CustomArmPoseItem customArmPoseItem) {
            String standbyArmPose = customArmPoseItem.getStandbyArmPose();
            cir.setReturnValue(BipedEntityModel.ArmPose.valueOf(standbyArmPose));
        }

        if (itemStack.getItem() instanceof CustomArmPoseItem customArmPoseItem && player.getItemUseTimeLeft() > 0) {
            String standbyArmPose = customArmPoseItem.getUsingArmPose();
            cir.setReturnValue(BipedEntityModel.ArmPose.valueOf(standbyArmPose));
        }
    }
}