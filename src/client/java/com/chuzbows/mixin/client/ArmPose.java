package com.chuzbows.mixin.client;

import com.chuzbows.item_interface.CustomArmPoseItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.chuzbows.ChuzBowsCore.Mod_ID;

@Mixin(net.minecraft.client.render.entity.PlayerEntityRenderer.class)

public class ArmPose {
    @Inject(method = "getArmPose", at = @At("TAIL"), cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir ) {

        ItemStack itemStack = player.getStackInHand(hand);
        ItemStack activeUItemStack = player.getActiveItem();

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