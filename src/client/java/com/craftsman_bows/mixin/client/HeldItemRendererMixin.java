package com.craftsman_bows.mixin.client;

import com.craftsman_bows.interfaces.item.CustomFirstPersonRender;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(net.minecraft.client.render.item.HeldItemRenderer.class)

public class HeldItemRendererMixin {


    @Shadow
    private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {

    }

    @Shadow
    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
    }

    @Shadow
    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    }

    // 手持ちのアイテムの「getFirstPersonReder」の結果に従ってレンダリングする
    @Inject(method = "renderFirstPersonItem", at = @At(value = "HEAD"), cancellable = true)
    public void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem() instanceof CustomFirstPersonRender customFirstPersonRender) {
            // クロスボウ（チャージ後）の構え
            if (Objects.equals(customFirstPersonRender.getFirstPersonRender(), "CROSSBOW_HOLD")) {
                boolean bl = hand == Hand.MAIN_HAND;
                Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
                boolean bl3 = arm == Arm.RIGHT;
                int i = bl3 ? 1 : -1;
                float f = -0.4f * MathHelper.sin((float) (MathHelper.sqrt((float) swingProgress) * (float) Math.PI));
                float g = 0.2f * MathHelper.sin((float) (MathHelper.sqrt((float) swingProgress) * ((float) Math.PI * 2)));
                float h = -0.2f * MathHelper.sin((float) (swingProgress * (float) Math.PI));
                matrices.translate((float) i * f, g, h);
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.applySwingOffset(matrices, arm, swingProgress);
                if (swingProgress < 0.001f && bl) {
                    matrices.translate((float) i * -0.641864f, 0.0f, 0.0f);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) i * 10.0f));
                }
                this.renderItem((LivingEntity) player, item, bl3 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
                // これ実行するとターゲットメソッドの処理が中断される…？
                ci.cancel();
            }
        }
    }
}
