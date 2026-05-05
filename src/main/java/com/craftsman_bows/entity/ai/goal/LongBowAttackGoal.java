package com.craftsman_bows.entity.ai.goal;

import com.craftsman_bows.init.item;
import com.craftsman_bows.item.LongBowItem;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.HostileEntity;

public class LongBowAttackGoal<T extends HostileEntity & RangedAttackMob> extends CraftsmanBowAttackGoal<T> {

    public LongBowAttackGoal(T actor, double speed, int attackInterval, float range, float power, int canShoot) {
        super(actor, speed, attackInterval, range, power, canShoot);
        super.holdingItem = item.LONG_BOW;
    }

    @Override
    protected float getPullProgress(int i) {
        return LongBowItem.getPullProgress(i);
    }
}
