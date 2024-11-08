package com.craftsman_bows.item;

import com.craftsman_bows.init.ModParticle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CraftsmanBowItem extends BowItem {

    public CraftsmanBowItem(Settings settings) {
        super(settings);
    }

    // チャージ完了パーティクル
    void chargeEndParticle(World world, LivingEntity player) {
        // プレイヤーの視線方向を取得
        Vec3d lookDirection = player.getRotationVec(1.0F);

        // プレイヤーの視線先の位置を計算
        double distance = 2.0;
        double particleX = player.getX() + lookDirection.x * distance;
        double particleY = player.getEyeY() + lookDirection.y * distance; // 目の高さ
        double particleZ = player.getZ() + lookDirection.z * distance;

        // パーティクルを複数発生させるループ
        for (int i = 0; i < 1; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.1;

            // 視線の先にパーティクルを追加
            world.addParticle(ModParticle.CHARGE_END,
                    particleX, particleY, particleZ,
                    offsetX, offsetY, offsetZ);
        }
    }

    // 発射パーティクル
    void shootParticle(World world, LivingEntity player) {
        // プレイヤーの視線方向を取得
        Vec3d lookDirection = player.getRotationVec(1.0F);

        // プレイヤーの視線先の位置を計算
        double distance = 2.0;
        double particleX = player.getX() + lookDirection.x * distance;
        double particleY = player.getEyeY() + lookDirection.y * distance; // 目の高さ
        double particleZ = player.getZ() + lookDirection.z * distance;

        // パーティクルを複数発生させるループ
        for (int i = 0; i < 1; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.1;

            // 視線の先にパーティクルを追加
            world.addParticle(ModParticle.SHOOT,
                    particleX, particleY, particleZ,
                    offsetX, offsetY, offsetZ);
        }
    }

    // 発射パーティクル
    void chargingParticle(World world, LivingEntity player) {
        // プレイヤーの視線方向を取得
        Vec3d lookDirection = player.getRotationVec(1.0F);

        // プレイヤーの視線先の位置を計算
        double distance = 2.0;
        double particleX = player.getX() + lookDirection.x * distance;
        double particleY = player.getEyeY() + lookDirection.y * distance; // 目の高さ
        double particleZ = player.getZ() + lookDirection.z * distance;

        // パーティクルを複数発生させるループ
        for (int i = 0; i < 1; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.1;

            // 視線の先にパーティクルを追加
            world.addParticle(ModParticle.CHARGE_DUST,
                    particleX, particleY, particleZ,
                    offsetX, offsetY, offsetZ);
        }
    }
}
