package com.example;

import com.example.init.*;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final String Mod_ID = "example";


	@Override
	public void onInitialize() {
		item.init();
		Entity.init();
		ModSoundEvents.init();
	}

	public static class SampleGlobal{
		public static float UsingMoveSpeed = Float.NaN;
		public static float CustomFOV = Float.NaN;
		public static boolean IgnoreSlowdown = true;

	}
}
