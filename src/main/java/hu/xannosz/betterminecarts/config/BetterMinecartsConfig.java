package hu.xannosz.betterminecarts.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BetterMinecartsConfig {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Float> MINECART_DAMAGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> FURNACE_MINECARTS_LOAD_CHUNKS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> MOBS_PANIC_AFTER_WHISTLE;
	public static final ForgeConfigSpec.ConfigValue<Integer> MOBS_PANIC_AFTER_WHISTLE_RANGE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> KEY_CONTROL_FROM_THE_WHOLE_TRAIN;
	public static final ForgeConfigSpec.ConfigValue<Boolean> WHISTLE_USE_STEAM_ON_STEAM_LOCOMOTIVE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> LOCOMOTIVE_EXPLODE_AFTER_FALL_DAMAGE;

	static {
		BUILDER.push("Configs for Flying Ships Mod");

		MINECART_DAMAGE = BUILDER.comment("Damage Dealt by Minecarts")
				.define("minecartDamage", 20F);
		FURNACE_MINECARTS_LOAD_CHUNKS = BUILDER.comment("Should Locomotives be able to load chunks?")
				.define("furnaceMinecartsLoadChunks", true);
		MOBS_PANIC_AFTER_WHISTLE = BUILDER.comment("Should Mobs panic after whistle?")
				.define("mobsPanicAfterWhistle", true);
		MOBS_PANIC_AFTER_WHISTLE_RANGE = BUILDER.comment("Whistle panic range")
				.defineInRange("mobsPanicAfterWhistleRange", 30, 5, 60);
		KEY_CONTROL_FROM_THE_WHOLE_TRAIN = BUILDER.comment("Can you control the locomotive from the whole train?")
				.define("keyControlFromTheWholeTrain", true);
		WHISTLE_USE_STEAM_ON_STEAM_LOCOMOTIVE = BUILDER.comment("Should steam whistle use steam?")
				.define("whistleUseSteamOnSteamLocomotive", true);
		LOCOMOTIVE_EXPLODE_AFTER_FALL_DAMAGE = BUILDER.comment("Should Locomotives explode after fall damage?")
				.define("locomotiveExplodeAfterFallDamage", true);

		BUILDER.pop();
		SPEC = BUILDER.build();
	}
}
