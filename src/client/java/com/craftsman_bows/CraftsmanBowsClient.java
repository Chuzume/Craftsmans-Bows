package com.craftsman_bows;

import net.fabricmc.api.ClientModInitializer;

public class CraftsmanBowsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		CustomModelPredicateProvider.registerModModels();
	}
}