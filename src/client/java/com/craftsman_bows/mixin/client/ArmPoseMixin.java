package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.CustomArmPoseItem;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)

public abstract class ArmPoseMixin {
    @Inject(method = "getArmPose(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;", at = @At("TAIL"), cancellable = true)
    private static void getArmPose(PlayerEntity player, ItemStack stack, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {

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