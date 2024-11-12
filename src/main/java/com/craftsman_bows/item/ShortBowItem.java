package com.craftsman_bows.item;

import com.craftsman_bows.interfaces.item.CanSprintWhileUsing;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import com.craftsman_bows.init.ModSoundEvents;

import java.util.List;

public class ShortBowItem extends CraftsmanBowItem implements CanSprintWhileUsing {
    public ShortBowItem(Settings settings) {
        super(settings);
    }

    boolean fullCharged;

    // 弓を引いた時間を取得する処理のようだ。今回は書き換えて、0.55以上引き絞ったら強制的に1（フルチャージ）になるようにした
    public static float getPullProgress(int useTicks) {
        float f = (float) useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 0.55f) {
            f = 1f;
        }
        return f;
    }

    // アイテムを使用しているときの処理
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;

        // チャージ中
        if (getPullProgress(i) <= 1.0F && !fullCharged) {
            chargingParticle(world, user);  // パーティクル生成の処理
        }

        // チャージが完了しているか確認し、完了時に一度だけ処理を実行
        if (getPullProgress(i) >= 1.0F && !fullCharged) {
            fullCharged = true;  // 一度だけ実行するためにフラグを設定
            chargeEndParticle(world, user);  // パーティクル生成の処理
        }
    }

    // 最初の使用時のアクション
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.playSound(ModSoundEvents.DUNGEONS_BOW_CHARGE, 1.0f, 1.2f);
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    // 使用をやめたとき、つまりクリックを離したときの処理だ。
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {

        if (!(user instanceof PlayerEntity playerEntity)) {
            return false;
        }

        // チャージリセット
        fullCharged = false;

        // パーティクル
        shootParticle(world, user);

        // プレイヤーを定義する処理のようだ。後は…手持ちの矢の種類を取得する処理？
        ItemStack itemStack = playerEntity.getProjectileType(stack);
        if (itemStack.isEmpty()) {
            return false;
        }

        // 使用時間0.1未満では使用をキャンセルする処理のようだ
        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        float f = getPullProgress(i);
        if ((double) f < 0.1) {
            return false;
        }

        // ここが放つ処理に見える。
        List<ItemStack> list = BowItem.load(stack, itemStack, playerEntity);
        if (world instanceof ServerWorld serverWorld) {
            if (!list.isEmpty()) {
                this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 1.6f, 1.0f, f == 1.0f, null);
            }
            if (f < 1) {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);
            } else {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.LEGACY_BOW_SHOOT_1, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + 0.9f);
                //world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.5f);
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), ModSoundEvents.DUNGEONS_BOW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.4f);
            }
        }
        return true;
    }
}