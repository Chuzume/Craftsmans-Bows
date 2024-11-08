package com.craftsman_bows;

import com.craftsman_bows.init.*;
import net.fabricmc.api.ModInitializer;


public class CraftsmanBows implements ModInitializer {
	public static final String Mod_ID = "craftsman_bows";

	@Override
	public void onInitialize() {
		item.init();
		ModParticle.init();
		ModSoundEvents.init();
	}
}
