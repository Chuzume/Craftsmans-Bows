package com.craftsman_bows.item;

import com.craftsman_bows.init.ModParticleTypes;
import com.craftsman_bows.init.ModSoundEvents;
import com.craftsman_bows.interfaces.item.CustomArmPoseItem;
import com.craftsman_bows.interfaces.item.CustomFirstPersonRender;
import com.craftsman_bows.interfaces.item.CustomUsingMoveItem;
import com.craftsman_bows.interfaces.item.ZoomItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class RepeaterCrossbowItem extends BowItem implements CustomArmPoseItem, CustomUsingMoveItem, CustomFirstPersonRender , ZoomItem {
    public RepeaterCrossbowItem(Item.Settings settings) {
        super(settings);
    }

    // 変数の定義
    float movementSpeed = 5.0f;
    float fov;

    // 最初の使用時のアクション
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {

        // サウンド
        user.playSound(ModSoundEvents.DUNGEONS_COG_CROSSBOW_PICKUP, 0.4f, 2.0f);
        user.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 1.0f, 1.5f);
        user.playSound(SoundEvents.BLOCK_IRON_DOOR_OPEN, 1.0f, 2f);

        // 変数リセット
        movementSpeed = 3.0f;
        fov = Float.NaN;

        // 腕振る処理
        Hand activeHand = user.getActiveHand();
        if (activeHand == Hand.MAIN_HAND) {
            user.swingHand(Hand.OFF_HAND);
        } else if (activeHand == Hand.OFF_HAND) {
            user.swingHand(Hand.MAIN_HAND);
        }

        // 値を返す
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    // 右クリックを押し続けているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int useTick = this.getMaxUseTime(stack, user) - remainingUseTicks;

        // 徐々に移動速度が下がっていく
        movementSpeed = 3.0f - (useTick * 0.1f);

        // 移動速度が負になると操作方向が逆になるので、0未満にならないようにする
        if (movementSpeed <= 0) {
            movementSpeed = 0.0f;
        }

        if (useTick >= 30) {
            fov = 0.8f;
        }

        // チャージ演出
        if (useTick <= 32) {
            // プレイヤーの視線方向を取得
            Vec3d lookDirection = user.getRotationVec(1.0F);

            // 出現位置の範囲を設定
            double rangeX = 1.5;
            double rangeY = 1.5;
            double rangeZ = 1.5;

            // オフセット
            double offsetUp = -0.15; // 上に0.1ブロック分オフセット

            // ベクトルを取得
            Vec3d rightDirection = lookDirection.crossProduct(new Vec3d(0, 1, 0)).normalize();
            Vec3d verticalDirection = rightDirection.crossProduct(lookDirection).normalize();

            // プレイヤーの視線先の位置を計算
            double distance = 2.0;

            // 目標位置（収束先）を設定
            double targetX = user.getX() + lookDirection.x + verticalDirection.x * offsetUp * distance;
            double targetY = user.getEyeY() + lookDirection.y + verticalDirection.y * offsetUp * distance;
            double targetZ = user.getZ() + lookDirection.z + verticalDirection.z * offsetUp * distance;

            double particleX = user.getX() + lookDirection.x * 2.0
                    + verticalDirection.x * offsetUp
                    + (world.random.nextDouble() - 0.5) * rangeX;

            double particleY = user.getEyeY() + lookDirection.y * 2.0
                    + verticalDirection.y * offsetUp
                    + (world.random.nextDouble() - 0.5) * rangeY;

            double particleZ = user.getZ() + lookDirection.z * 2.0
                    + verticalDirection.z * offsetUp
                    + (world.random.nextDouble() - 0.5) * rangeZ;

            // 視線の先にパーティクルを追加
            world.addParticle(ModParticleTypes.CHARGE_DUST, particleX, particleY, particleZ, targetX, targetY, targetZ);
        }

        if (useTick == 15) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f);
        }
        if (useTick == 20) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.5f);

        }
        if (useTick == 30) {
            user.playSound(ModSoundEvents.DUNGEONS_COG_CROSSBOW_PICKUP, 0.4f, 2.0f);
            user.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0f, 1.0f);
        }
        if (useTick == 31) {
            user.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0f, 1.5f);

        }
        if (useTick == 32) {
            user.playSound(SoundEvents.BLOCK_PISTON_EXTEND, 1.0f, 2.0f);
        }
        if (useTick == 40) {
            user.playSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE, 1.0f, 2f);
            user.playSound(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), 1.0f, 1.5f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_3, 1.0f, 2.0f);

            // プレイヤーの視線方向を取得
            Vec3d lookDirection = user.getRotationVec(1.0F);

            // オフセット
            double offsetUp = -0.15; // 上に0.1ブロック分オフセット

            // ベクトルを取得
            Vec3d rightDirection = lookDirection.crossProduct(new Vec3d(0, 1, 0)).normalize();
            Vec3d verticalDirection = rightDirection.crossProduct(lookDirection).normalize();

            // プレイヤーの視線先の位置を計算
            double distance = 2.0;
            double particleX = user.getX() + lookDirection.x + verticalDirection.x * offsetUp * distance;
            double particleY = user.getEyeY() + lookDirection.y + verticalDirection.y * offsetUp * distance; // 目の高さ
            double particleZ = user.getZ() + lookDirection.z + verticalDirection.z * offsetUp * distance;

            // パーティクルを複数発生させるループ
            for (int i = 0; i < 1; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 1;
                double offsetY = (world.random.nextDouble() - 0.5) * 1;
                double offsetZ = (world.random.nextDouble() - 0.5) * 1;

                // 視線の先にパーティクルを追加
                world.addParticle(ModParticleTypes.CHARGE_END,
                        particleX, particleY, particleZ,
                        offsetX, offsetY, offsetZ);
            }
        }
        // 完了して一拍置いてから射撃開始
        if (useTick >= 50) {
            this.GatlingShot(world, user, stack);
        }
        // あんまり長いこと撃ってると煙を吹き出す
        if (useTick == 82) {
            user.playSound(ModSoundEvents.DUNGEONS_COG_CROSSBOW_PICKUP, 1.0f, 1.5f);
            user.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 2.0f);
        }
        if (useTick >= 82) {
            // もくもく警告パーティクル

            // プレイヤーの視線方向を取得
            Vec3d lookDirection = user.getRotationVec(1.0F);

            // オフセット
            double offsetUp = -0.15; // 上に0.1ブロック分オフセット

            // ベクトルを取得
            Vec3d rightDirection = lookDirection.crossProduct(new Vec3d(0, 1, 0)).normalize();
            Vec3d verticalDirection = rightDirection.crossProduct(lookDirection).normalize();

            // プレイヤーの視線先の位置を計算
            double distance = 2.0;
            double particleX = user.getX() + lookDirection.x + verticalDirection.x * offsetUp * distance;
            double particleY = user.getEyeY() + lookDirection.y + verticalDirection.y * offsetUp * distance; // 目の高さ
            double particleZ = user.getZ() + lookDirection.z + verticalDirection.z * offsetUp * distance;

            // パーティクルを複数発生させるループ
            double offsetX = (world.random.nextDouble() - 0.5) * 0.3;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.3;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.3;

            // 視線の先にパーティクルを追加
            world.addParticle(ParticleTypes.SMOKE,
                    particleX, particleY, particleZ,
                    offsetX, offsetY, offsetZ);
        }
        // そろそろやばいぞ！
        if (useTick == 98) {
            user.playSound(ModSoundEvents.DUNGEONS_COG_CROSSBOW_PICKUP, 1.0f, 1.5f);
            user.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 2.0f);
        }
        if (useTick >= 98) {
            // アチアチパーティクル

            // プレイヤーの視線方向を取得
            Vec3d lookDirection = user.getRotationVec(1.0F);

            // オフセット
            double offsetUp = -0.15; // 上に0.1ブロック分オフセット

            // ベクトルを取得
            Vec3d rightDirection = lookDirection.crossProduct(new Vec3d(0, 1, 0)).normalize();
            Vec3d verticalDirection = rightDirection.crossProduct(lookDirection).normalize();

            // プレイヤーの視線先の位置を計算
            double distance = 2.0;
            double particleX = user.getX() + lookDirection.x + verticalDirection.x * offsetUp * distance;
            double particleY = user.getEyeY() + lookDirection.y + verticalDirection.y * offsetUp * distance; // 目の高さ
            double particleZ = user.getZ() + lookDirection.z + verticalDirection.z * offsetUp * distance;

            // パーティクルを複数発生させるループ
            double offsetX = (world.random.nextDouble() - 0.5) * 0.3;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.3;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.3;

            // 視線の先にパーティクルを追加
            world.addParticle(ParticleTypes.LAVA,
                    particleX, particleY, particleZ,
                    offsetX, offsetY, offsetZ);
        }
        // それでも撃ち続けるとオーバーヒートする
        if (useTick == 113) {
            // サウンド
            user.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 1.5f);
            user.playSound(ModSoundEvents.DUNGEONS_COG_CROSSBOW_PLACE, 1.0f, 1f);

            // 吹っ飛ぶ
            float g = user.getYaw();
            float h = user.getPitch();
            float j = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
            float k = -MathHelper.sin(h * (float) (Math.PI / 180.0));
            float l = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
            float m = MathHelper.sqrt(j * j + k * k + l * l);
            j *= (1 / m) * -1;
            k *= (1 / m) * -1;
            l *= (1 / m) * -1;
            user.addVelocity(j, k, l);

            // プレイヤーの視線方向を取得
            Vec3d lookDirection = user.getRotationVec(1.0F);

            // オフセット
            double offsetUp = -0.15; // 上に0.1ブロック分オフセット

            // ベクトルを取得
            Vec3d rightDirection = lookDirection.crossProduct(new Vec3d(0, 1, 0)).normalize();
            Vec3d verticalDirection = rightDirection.crossProduct(lookDirection).normalize();

            // プレイヤーの視線先の位置を計算
            double distance = 2.0;
            double particleX = user.getX() + lookDirection.x + verticalDirection.x * offsetUp * distance;
            double particleY = user.getEyeY() + lookDirection.y + verticalDirection.y * offsetUp * distance; // 目の高さ
            double particleZ = user.getZ() + lookDirection.z + verticalDirection.z * offsetUp * distance;

            // パーティクルを複数発生させるループ
            for (int i = 0; i < 10; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 0.3;
                double offsetY = (world.random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (world.random.nextDouble() - 0.5) * 0.3;

                // 視線の先にパーティクルを追加
                world.addParticle(ParticleTypes.LARGE_SMOKE,
                        particleX, particleY, particleZ,
                        offsetX, offsetY, offsetZ);

            // クールダウンに突入
            if (!(user instanceof PlayerEntity playerEntity)) {
                return;
            }
            playerEntity.getItemCooldownManager().set(stack, 60);
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    // 矢を発射する処理
    public void GatlingShot(World world, LivingEntity user, ItemStack stack) {

        // プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        PlayerEntity playerEntity = (PlayerEntity) user;
        ItemStack itemStack = playerEntity.getProjectileType(stack);

        //　弾切れ時の処理
        if (itemStack.isEmpty()) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f);
            return;
        }

        // プレイヤーの視線方向を取得
        Vec3d lookDirection = user.getRotationVec(1.0F);

        // オフセット
        double offsetUp = -0.15; // 上に0.1ブロック分オフセット

        // ベクトルを取得
        Vec3d rightDirection = lookDirection.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d verticalDirection = rightDirection.crossProduct(lookDirection).normalize();

        // プレイヤーの視線先の位置を計算
        double distance = 2.0;
        double particleX = user.getX() + lookDirection.x + verticalDirection.x * offsetUp * distance;
        double particleY = user.getEyeY() + lookDirection.y + verticalDirection.y * offsetUp * distance; // 目の高さ
        double particleZ = user.getZ() + lookDirection.z + verticalDirection.z * offsetUp * distance;

        // パーティクルを複数発生させるループ
        for (int i = 0; i < 1; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 1;
            double offsetY = (world.random.nextDouble() - 0.5) * 1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 1;

            // 視線の先にパーティクルを追加
            world.addParticle(ParticleTypes.CRIT,
                    particleX, particleY, particleZ,
                    offsetX, offsetY, offsetZ);
        }

        // 後ろに下がっていく
        float g = playerEntity.getYaw();
        float h = playerEntity.getPitch();
        float j = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
        float k = -MathHelper.sin(h * (float) (Math.PI / 180.0));
        float l = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
        float m = MathHelper.sqrt(j * j + k * k + l * l);
        j *= (float) (0.02 / m) * -1;
        k *= (float) (0.02 / m) * -1;
        l *= (float) (0.02 / m) * -1;
        playerEntity.addVelocity(j, k, l);


        // 音を鳴らす処理
        user.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.2f);

        // 弓につがえた矢を取得している？
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);

        // ワールドがサーバーなら？
        if (world instanceof ServerWorld serverWorld) {
            if (!list.isEmpty()) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 2.7f, 3.0f, false, null);
            }
        }
    }

    // 矢の生成処理
    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        Item item = projectileStack.getItem();
        ArrowItem arrowItem2 = item instanceof ArrowItem ? (ArrowItem) item : (ArrowItem) Items.ARROW;
        PersistentProjectileEntity persistentProjectileEntity = arrowItem2.createArrow(world, projectileStack, shooter, weaponStack);
        persistentProjectileEntity.setBypassDamageCooldown();
        return persistentProjectileEntity;
    }

    // 使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        }
        fov = Float.NaN;

        // 使用時間に応じたクールタイムがかかる
        int useTick = this.getMaxUseTime(stack, user) - remainingUseTicks;

            if (useTick <= 82){
                playerEntity.getItemCooldownManager().set(stack, 20);
            }

            if (useTick >= 82 && useTick <= 114){
                playerEntity.getItemCooldownManager().set(stack, 30);
            }

            if (useTick == 114){
                playerEntity.getItemCooldownManager().set(stack, 60);
            }

        user.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 1.0f, 1.5f);
        user.playSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE, 1.0f, 2f);


        // 腕振る処理
        Hand activeHand = user.getActiveHand();
        if (activeHand == Hand.MAIN_HAND) {
            user.swingHand(Hand.MAIN_HAND);
        } else if (activeHand == Hand.OFF_HAND) {
            user.swingHand(Hand.OFF_HAND);
        }

        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 114;
    }

    // インターフェースが欲しがってる処理
    @Override
    public String getStandbyArmPose() {
        return "ITEM";
    }

    @Override
    public String getUsingArmPose() {
        return "CROSSBOW_HOLD";
    }

    @Override
    public float getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public void resetMovementSpeed() {
    }

    @Override
    public String getUsingFirstPersonRender() {
        return "CROSSBOW_HOLD";
    }

    @Override
    public String getStandbyFirstPersonRender() {
        return null;
    }

    @Override
    public float getFov() {
        return this.fov;
    }

    @Override
    public void resetFov() {
        fov = Float.NaN;
    }
}