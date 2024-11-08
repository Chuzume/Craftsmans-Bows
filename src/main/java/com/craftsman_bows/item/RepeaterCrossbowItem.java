package com.craftsman_bows.item;

import com.craftsman_bows.init.ModSoundEvents;
import com.craftsman_bows.interfaces.entity.BypassCooldown;
import com.craftsman_bows.interfaces.item.CustomArmPoseItem;
import com.craftsman_bows.interfaces.item.CustomFirstPersonRender;
import com.craftsman_bows.interfaces.item.CustomUsingMoveItem;
import com.craftsman_bows.interfaces.item.ZoomItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class RepeaterCrossbowItem extends BowItem implements CustomArmPoseItem, CustomUsingMoveItem, CustomFirstPersonRender , ZoomItem {
    public RepeaterCrossbowItem(Item.Settings settings) {
        super(settings);
    }

    // 変数の定義
    int useTick = 0;
    float movementSpeed = 5.0f;
    float fov;

    // 最初の使用時のアクション
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {

        // サウンド
        user.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 1.0f, 1.5f);
        user.playSound(SoundEvents.BLOCK_IRON_DOOR_OPEN, 1.0f, 2f);

        // 変数リセット
        useTick = 0;
        movementSpeed = 3.0f;
        fov = Float.NaN;

        // 値を返す
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    // 右クリックを押し続けているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world instanceof ServerWorld) {
            useTick += 1;
        }

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
        if (useTick == 15) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f);
        }
        if (useTick == 20) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.5f);
        }
        if (useTick == 30) {
            user.playSound(ModSoundEvents.BOW_CHARGE, 1.0f, 1.25f);
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
        }

        // 完了してから一拍置いてから射撃開始
        if (useTick >= 50) {
            this.GatlingShot(world, user, stack);
        }

        // あんまり長いこと撃ってると煙を吹き出す
        if (useTick >= 82){
            this.spawnAlertParticles(world,user);
        }

        // それでも撃ち続けるとオーバーヒートする
        if (useTick >= 92){
            this.overHeatEnd(stack,user);
        }
    }

    // もくもく警告パーティクル
    private void spawnAlertParticles(World world, LivingEntity player) {
        // プレイヤーの視線方向を取得
        Vec3d lookDirection = player.getRotationVec(1.0F);

        // プレイヤーの視線先の位置を計算
        double distance = 2.0;
        double particleX = player.getX() + lookDirection.x * distance;
        double particleY = player.getEyeY() + lookDirection.y * distance; // 目の高さ
        double particleZ = player.getZ() + lookDirection.z * distance;

        // パーティクルを複数発生させるループ
        for (int i = 0; i < 2; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetY = (world.random.nextDouble() - 0.5) * 1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.1;

            // 視線の先にパーティクルを追加
            world.addParticle(ParticleTypes.SMOKE,
                    particleX, particleY, particleZ,
                    offsetX, offsetY, offsetZ);
        }
    }

    // オーバーヒート！
    public void overHeatEnd(ItemStack stack, LivingEntity user){
        if (!(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        fov = Float.NaN;
        useTick = 0;
        playerEntity.getItemCooldownManager().set(stack, 60);
        user.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 1.0f, 1.5f);
        user.playSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE, 1.0f, 2f);
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

        // 音を鳴らす処理
        user.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.2f);

        // 弓につがえた矢を取得している？
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);

        // ワールドがサーバーなら？
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            if (!list.isEmpty()) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 2.7f, 3.0f, false, null);
            }
        }
    }

    // 矢の生成処理
    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        ProjectileEntity entity = super.createArrowEntity(world, shooter, weaponStack, projectileStack, critical);
        ((BypassCooldown) entity).setBypassDamageCooldown();
        return entity;
    }

    // 使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        }
        fov = Float.NaN;
        useTick = 0;
        playerEntity.getItemCooldownManager().set(stack, 20);
        user.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 1.0f, 1.5f);
        user.playSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE, 1.0f, 2f);
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 92;
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
    public String getFirstPersonRender() {
        if (useTick >= 1) {
            return "CROSSBOW_HOLD";
        } else {
            return null;
        }
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