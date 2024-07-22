package com.chuzbows;

import net.fabricmc.api.ClientModInitializer;

public class ChuzBowsCoreClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CustomModelPredicateProvider.registerModModels();
	}
}