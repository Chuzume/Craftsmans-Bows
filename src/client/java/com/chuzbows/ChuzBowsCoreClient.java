package com.chuzbows;

import com.chuzbows.render.ShotArrowEntityRenderer;
import com.chuzbows.init.Entity;
import net.fabricmc.api.ClientModInitializer;
//import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ChuzBowsCoreClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CustomModelPredicateProvider.registerModModels();

		//EntityRendererRegistry.register(Entity.SHOTARROW, ShotArrowEntity);
		EntityRendererRegistry.register(Entity.SHOTARROW, ShotArrowEntityRenderer::new);
	}
}