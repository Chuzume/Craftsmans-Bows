package com.craftsman_bows.item;

import com.craftsman_bows.init.ModSoundEvents;
import com.craftsman_bows.interfaces.item.CustomUsingMoveItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import com.craftsman_bows.init.ModComponents;

import java.util.List;


public class BurstArbalestItem extends CraftsmanBowItem implements CustomUsingMoveItem {
    public BurstArbalestItem(Item.Settings settings) {
        super(settings);
    }

    // 最初の使用時のアクション
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {


        ItemStack stack = user.getStackInHand(hand);

        // サウンド
        //user.playSound(ModSoundEvents.DUNGEONS_COG_CROSSBOW_PICKUP, 0.4f, 1.5f);
        user.playSound(ModSoundEvents.DUNGEONS_BOW_LOAD, 1.0f, 1.0f);
        //user.playSound(SoundEvents.BLOCK_PISTON_CONTRACT, 1.0f, 1.5f);
        //user.playSound(SoundEvents.BLOCK_IRON_DOOR_OPEN, 1.0f, 2f);

        // 腕振る処理
        Hand activeHand = user.getActiveHand();
        if (activeHand == Hand.MAIN_HAND) {
            user.swingHand(Hand.OFF_HAND);
        } else if (activeHand == Hand.OFF_HAND) {
            user.swingHand(Hand.MAIN_HAND);
        }

        stack.set(ModComponents.BURST_STACK, 0);
        stack.set(ModComponents.BURST_COUNT, 0);

        // 値を返す
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    // 右クリックを押し続けているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int useTick = this.getMaxUseTime(stack, user) - remainingUseTicks;

        // クライアント、サーバーともに行う処理
        chargingParticle(world, user);
        if (useTick <= 70) {
            chargingParticle(world, user);
        }

        // チャージ段階ごと
        if (useTick == 20 | useTick == 45 |useTick == 70) {
            chargeEndParticle(world, user);
        }

        // チャージ1
        if (useTick == 10) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.1f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_1, 1.0f, 1.0f);

        }
        if (useTick == 15) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.2f);
        }
        if (useTick == 20) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.3f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_3, 1.0f, 1.0f);
        }

        // チャージ2
        if (useTick == 35) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.4f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_1, 1.0f, 1.25f);

        }
        if (useTick == 40) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.5f);
        }
        if (useTick == 45) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.6f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_3, 1.0f, 1.5f);
        }

        // チャージ3
        if (useTick == 60) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.7f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_1, 1.0f, 1.5f);

        }
        if (useTick == 65) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.8f);
        }
        if (useTick == 70) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 2.0f);
            user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE_3, 1.0f, 2.0f);
        }

        // サーバーのみ
        if (world instanceof ServerWorld) {
            // チャージカウントが進む
            if (useTick == 10 | useTick == 15 |useTick == 20 | useTick == 35 | useTick == 40 | useTick == 45 | useTick == 60 | useTick == 65 |useTick == 70 ) {
                int count = stack.getOrDefault(ModComponents.BURST_STACK, 0);
                stack.set(ModComponents.BURST_STACK, ++count);
            }
        }
    }

    // 矢を発射する処理
    public void burstShot(World world, LivingEntity user, ItemStack stack) {

        // プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        PlayerEntity playerEntity = (PlayerEntity) user;
        ItemStack itemStack = playerEntity.getProjectileType(stack);

        //　弾切れ時の処理
        if (itemStack.isEmpty()) {
            user.playSound(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f);
            return;
        }

        // 弓につがえた矢を取得している？
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);

        // 音を鳴らす処理
        user.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.2f);

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

        // ワールドがサーバーなら
        if (world instanceof ServerWorld serverWorld) {
            if (!list.isEmpty()) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, 3.0f, 2.0f, false, null);
            }
        }
    }

    // 持ってる間の処理…？
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity user) {
            if (selected | user.getOffHandStack().equals(stack)) {

                int burstCount = stack.getOrDefault(ModComponents.BURST_COUNT, 0);

                //stack.set(ModComponents.MY_COMPONENT_TYPE, --burstCount2);

                if (burstCount >= 1) {
                    burstShot(world, user, stack);

                    // 更新した値を stack.set() に渡して保存
                    stack.set(ModComponents.BURST_COUNT, burstCount - 1);
                }
            }
        }
    }

    // 矢の生成処理
    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical) {
        ProjectileEntity entity = super.createArrowEntity(world, shooter, weaponStack, projectileStack, critical);
        (entity).setBypassDamageCooldown();
        return entity;
    }

    // 使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        }

        int burstStack = stack.getOrDefault(ModComponents.BURST_STACK, 0);
        stack.set(ModComponents.BURST_COUNT, burstStack);
        stack.remove(ModComponents.BURST_STACK);

        playerEntity.getItemCooldownManager().set(stack, 20);
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
    public float getMovementSpeed() {
        return 2.5f;
    }

    @Override
    public void resetMovementSpeed() {
    }
}