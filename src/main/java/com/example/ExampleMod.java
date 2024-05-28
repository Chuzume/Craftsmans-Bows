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
	public static final String Mod_ID = "hoge";


	@Override
	public void onInitialize() {
		item.init();
		Sound.init();
		initSounds.register();
	}

	public static class SampleGlobal{
		public static float UsingMoveSpeed = 0.0f;
		public static boolean IgnoreSlowdown = true;

	}
}
