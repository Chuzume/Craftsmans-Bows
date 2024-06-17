package com.example;

import com.example.init.Entity;
import com.example.render.ShotArrowEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
//import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CustomModelPredicateProvider.registerModModels();

		//EntityRendererRegistry.register(Entity.SHOTARROW, ShotArrowEntity);
		EntityRendererRegistry.register(Entity.SHOTARROW, ShotArrowEntityRenderer::new);
	}
}