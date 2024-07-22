package com.chuzbows;

import com.chuzbows.init.*;
import net.fabricmc.api.ModInitializer;



public class ChuzBowsCore implements ModInitializer {
	public static final String Mod_ID = "chuzbows";

	@Override
	public void onInitialize() {
		item.init();
		ModSoundEvents.init();
	}
}
