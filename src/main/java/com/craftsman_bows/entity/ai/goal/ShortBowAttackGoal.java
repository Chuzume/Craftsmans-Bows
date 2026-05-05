package com.craftsman_bows.entity.ai.goal;

import com.craftsman_bows.init.item;
import com.craftsman_bows.item.ShortBowItem;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.HostileEntity;

public class ShortBowAttackGoal<T extends HostileEntity & RangedAttackMob> extends CraftsmanBowAttackGoal<T> {

    public ShortBowAttackGoal(T actor, double speed, int attackInterval, float range, float power, int canShoot) {
        super(actor, speed, attackInterval, range, power, canShoot, item.SHORT_BOW);
    }

    @Override
    protected float getPullProgress(int i) {
        return ShortBowItem.getPullProgress(i);
    }
}
