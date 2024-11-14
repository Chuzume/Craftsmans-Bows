package com.craftsman_bows.item;

import com.craftsman_bows.init.ModSoundEvents;
import com.craftsman_bows.interfaces.item.CustomArmPoseItem;
import com.craftsman_bows.interfaces.item.CustomFirstPersonRender;
import com.craftsman_bows.interfaces.item.CustomUsingMoveItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.craftsman_bows.init.ModParticleTypes;

import java.util.List;

public class ShotCrossbowItem extends CraftsmanBowItem implements CustomUsingMoveItem, CustomArmPoseItem , CustomFirstPersonRender{
    public ShotCrossbowItem(Settings settings) {
        super(settings);
    }

    // 変数
    // 変数の定義
    int shootStack = 0;
    float movementSpeed = 2.5f;

    // 最初の使用時のアクション
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getProjectileType(itemStack).isEmpty();
        if (user.isInCreativeMode() || bl) {
            shootStack = 0;
            user.setCurrentHand(hand);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_LOAD, 1.0f, 1.25f);
            return ActionResult.CONSUME;
        }
        return ActionResult.FAIL;
    }

    // アイテムを使用しているときの処理？
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {

        movementSpeed = 2.5f;
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;

        // チャージ演出
        if (i < 20) {
            // プレイヤーの視線方向を取得
            Vec3d lookDirection = user.getRotationVec(1.0F);

            // オフセット
            double offsetUp = -0.3; // 上に0.1ブロック分オフセット

            // ベクトルを取得
            Vec3d rightDirection = lookDirection.crossProduct(new Vec3d(0, 1, 0)).normalize();
            Vec3d verticalDirection = rightDirection.crossProduct(lookDirection).normalize();

            // 出現位置の範囲を設定
            double rangeX = 1.5;
            double rangeY = 1.5;
            double rangeZ = 1.5;

            // 目標位置（収束先）を設定
            double distanceToTarget = 0.9; // プレイヤーから目標地点までの距離
            double targetX = user.getX() + lookDirection.x * distanceToTarget + verticalDirection.x * offsetUp;
            double targetY = user.getEyeY() + lookDirection.y * distanceToTarget + verticalDirection.y * offsetUp;
            double targetZ = user.getZ() + lookDirection.z * distanceToTarget + verticalDirection.x * offsetUp;

            // 複数のパーティクルを発生させるループ
            for (int i2 = 0; i2 < 1; i2++) {
                // 視線方向に基づいた初期位置にランダムな偏差を加える
                double particleX = user.getX() + lookDirection.x * 2.0
                        + verticalDirection.x * offsetUp
                        + (world.random.nextDouble() - 0.5) * rangeX;

                double particleY = user.getEyeY() + lookDirection.y * 2.0
                        + verticalDirection.y * offsetUp
                        + (world.random.nextDouble() - 0.5) * rangeY;

                double particleZ = user.getZ() + lookDirection.z * 2.0
                        + verticalDirection.z * offsetUp
                        + (world.random.nextDouble() - 0.5) * rangeZ;

                // パーティクルを追加し、収束先を設定
                world.addParticle(ModParticleTypes.CHARGE_DUST, particleX, particleY, particleZ, targetX, targetY, targetZ);
            }
        }

        // 途中が寂しいので…
        if (i == 10) {
            user.playSound(SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE.value(), 1.0f, 1.0f);
        }

        if (i == 20) {
            shootStack = 4;
            user.playSound(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), 1.0f, 1.5f);
            user.playSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE, 1.0f, 2f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_1, 1.0f, 1.1f);

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

            // パーティクル発生
            double offsetX = 0;
            double offsetY = 0;
            double offsetZ = 0;

            // 視線の先にパーティクルを追加
            world.addParticle(ModParticleTypes.CHARGE_END, particleX, particleY, particleZ, offsetX, offsetY, offsetZ);
        }
    }

    protected ProjectileEntity createShotArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean pickup) {
        Item item = projectileStack.getItem();
        ArrowItem arrowItem2 = item instanceof ArrowItem ? (ArrowItem) item : (ArrowItem) Items.ARROW;
        PersistentProjectileEntity persistentProjectileEntity = arrowItem2.createArrow(world, projectileStack, shooter, weaponStack);
        persistentProjectileEntity.setBypassDamageCooldown();
        persistentProjectileEntity.setCritical(true);
        if (!pickup) {
            persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        }
        return persistentProjectileEntity;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {return UseAction.NONE;}

    protected void shootArrow(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack stack, List<ItemStack> projectiles, float divergence, boolean pickup) {
        float f = EnchantmentHelper.getProjectileSpread(world, stack, shooter, 0.0f);
        float g = projectiles.size() == 1 ? 0.0f : 2.0f * f / (float) (projectiles.size() - 1);
        float h = (float) ((projectiles.size() - 1) % 2) * g / 2.0f;
        float i = 1.0f;
        for (int j = 0; j < projectiles.size(); ++j) {
            ItemStack itemStack = projectiles.get(j);
            if (itemStack.isEmpty()) continue;
            float k = h + i * (float) ((j + 1) / 2) * g;
            i = -i;
            ProjectileEntity projectileEntity = this.createShotArrowEntity(world, shooter, stack, itemStack, pickup);
            this.shoot(shooter, projectileEntity, j, 1.2f, divergence, k, null);
            world.spawnEntity(projectileEntity);
            stack.damage(this.getWeaponStackDamage(itemStack), shooter, LivingEntity.getSlotForHand(hand));
            if (stack.isEmpty()) break;
        }
    }

    // 使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        }

        // プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return false;
        }

        // 使用時間0.1未満では使用をキャンセルする処理のようだ
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i);
        if ((double) f < 1) {
            return false;
        }

        // ここが放つ処理に見える。
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);

        if (world instanceof ServerWorld serverWorld) {
            if (!list.isEmpty()) {
                this.shootArrow(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 0.0f, true);
                for (int i2 = 0; i2< shootStack; i2++) {
                    this.shootArrow(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 15.0f, false);
                }
                shootStack = 0;
            }
        }

        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_2, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.DUNGEONS_BOW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);

        return true;
    }

    // インターフェース「CustomUsingMoveItem」として必要な処理
    @Override
    public float getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public void resetMovementSpeed() {
        movementSpeed = Float.NaN;
    }

    @Override
    public String getUsingFirstPersonRender() {
        return "CROSSBOW_HOLD";
    }

    @Override
    public String getStandbyFirstPersonRender() {
        return null;
    }

    // インターフェス「CustomArmPoseItem」として必要な処理
    @Override
    public String getUsingArmPose() {
        return "CROSSBOW_HOLD";
    }

    @Override
    public String getStandbyArmPose() {
        return null;
    }
}
