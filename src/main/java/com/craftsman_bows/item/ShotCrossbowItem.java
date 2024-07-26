package com.craftsman_bows.item;

import com.craftsman_bows.interfaces.entity.BypassCooldown;
import com.craftsman_bows.init.ModSoundEvents;
import com.craftsman_bows.interfaces.item.CustomArmPoseItem;
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
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class ShotCrossbowItem extends BowItem implements CustomUsingMoveItem, CustomArmPoseItem {
    public ShotCrossbowItem(Settings settings) {
        super(settings);
    }

    // 変数
    boolean fullCharged;
    float movementSpeed = 2.5f;
    String standbyArmPose = "CROSSBOW_HOLD";
    String usingArmPose = "CROSSBOW_CHARGE";

    // 最初の使用時のアクション
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        boolean bl = !user.getProjectileType(itemStack).isEmpty();
        if (user.isInCreativeMode() || bl) {
            fullCharged = false;
            user.setCurrentHand(hand);
            user.playSound(ModSoundEvents.BOW_CHARGE, 1.0f, 1.25f);
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    // アイテムを使用しているときの処理？
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        movementSpeed = 2.5f;
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;

        // 腕のポーズ変更
        if (getPullProgress(i) < 1.0) {
            usingArmPose = "CROSSBOW_CHARGE";
        } else {
            usingArmPose = "CROSSBOW_HOLD";
        }
        if (getPullProgress(i) > 0.9 && !fullCharged) {
            fullCharged = true;
            user.playSound(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value(), 1.0f, 1.5f);
            user.playSound(SoundEvents.BLOCK_IRON_DOOR_CLOSE, 1.0f, 2f);
        }
    }

    protected ProjectileEntity createShotArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean pickup) {
        Item item = projectileStack.getItem();
        ArrowItem arrowItem2 = item instanceof ArrowItem ? (ArrowItem) item : (ArrowItem) Items.ARROW;
        PersistentProjectileEntity persistentProjectileEntity = arrowItem2.createArrow(world, projectileStack, shooter, weaponStack);
        ((BypassCooldown) persistentProjectileEntity).setBypassDamageCooldown();
        persistentProjectileEntity.setCritical(true);
        if (!pickup) {
            persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
        }
        return persistentProjectileEntity;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

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
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity)) {
            return;
        }

        // プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        PlayerEntity playerEntity = (PlayerEntity) user;
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return;
        }

        // 使用時間0.1未満では使用をキャンセルする処理のようだ
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i);
        if ((double) f < 1) {
            return;
        }

        // ここが放つ処理に見える。
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);


        if (world instanceof ServerWorld serverWorld) {
            if (!list.isEmpty()) {
                this.shootArrow(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 0.0f, true);
                this.shootArrow(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 15.0f, false);
                this.shootArrow(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 15.0f, false);
                this.shootArrow(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 15.0f, false);
                this.shootArrow(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 15.0f, false);
            }
        }

        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_2, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.3f);

        // 腕振る処理
        Hand activeHand = user.getActiveHand();
        if (activeHand == Hand.MAIN_HAND) {
            // 現在のアクティブな手がメインハンドなら、メインハンドを振る
            user.swingHand(Hand.MAIN_HAND);
        } else if (activeHand == Hand.OFF_HAND) {
            // 現在のアクティブな手がオフハンドなら、オフハンドを振る
            user.swingHand(Hand.OFF_HAND);
        }
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

    // インターフェス「CustomArmPoseItem」として必要な処理
    @Override
    public String getUsingArmPose() {
        return usingArmPose;
    }

    @Override
    public String getStandbyArmPose() {
        return standbyArmPose;
    }
}
