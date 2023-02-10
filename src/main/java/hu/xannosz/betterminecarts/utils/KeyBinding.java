package hu.xannosz.betterminecarts.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
	private static final String KEY_CATEGORY_BETTER_MINECARTS = "key.category.betterminecarts";

	private static final String KEY_INCREASE = "key.betterminecarts.increase";
	private static final String KEY_DECREASE = "key.betterminecarts.decrease";
	private static final String KEY_LAMP = "key.betterminecarts.lamp";
	private static final String KEY_WHISTLE = "key.betterminecarts.whistle";
	private static final String KEY_REDSTONE = "key.betterminecarts.redstone";
	private static final String KEY_DATA = "key.betterminecarts.data";

	public static final KeyMapping INCREASE_KEY = new KeyMapping(KEY_INCREASE, KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, KEY_CATEGORY_BETTER_MINECARTS);
	public static final KeyMapping DECREASE_KEY = new KeyMapping(KEY_DECREASE, KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY_BETTER_MINECARTS);
	public static final KeyMapping LAMP_KEY = new KeyMapping(KEY_LAMP, KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KEY_CATEGORY_BETTER_MINECARTS);
	public static final KeyMapping WHISTLE_KEY = new KeyMapping(KEY_WHISTLE, KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, KEY_CATEGORY_BETTER_MINECARTS);
	public static final KeyMapping REDSTONE_KEY = new KeyMapping(KEY_REDSTONE, KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, KEY_CATEGORY_BETTER_MINECARTS);
	public static final KeyMapping DATA_KEY = new KeyMapping(KEY_DATA, KeyConflictContext.IN_GAME,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, KEY_CATEGORY_BETTER_MINECARTS);
}
