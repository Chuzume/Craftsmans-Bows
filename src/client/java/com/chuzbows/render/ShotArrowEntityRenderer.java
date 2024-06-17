package com.chuzbows.render;

import net.minecraft.util.Identifier;
import com.chuzbows.entity.ShotArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;

import static com.chuzbows.ChuzBowsCore.Mod_ID;

public class ShotArrowEntityRenderer extends ProjectileEntityRenderer<ShotArrowEntity> {
    public static final Identifier TEXTURE = Identifier.of(Mod_ID,"textures/entity/projectiles/shot_arrow.png");

    public ShotArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ShotArrowEntity ShotArrowEntity) {
        return TEXTURE;
    }
}