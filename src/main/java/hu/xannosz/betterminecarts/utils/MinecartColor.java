package hu.xannosz.betterminecarts.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

@Getter
@AllArgsConstructor
public enum MinecartColor {
	YELLOW(new Vec3(227, 227, 41), ChatFormatting.YELLOW, "yellow", Items.YELLOW_DYE),
	BROWN(new Vec3(99, 44, 4), ChatFormatting.GOLD, "brown", Items.BROWN_DYE),
	LIGHT_GRAY(new Vec3(109, 109, 109), ChatFormatting.GRAY, "light_gray", Items.LIGHT_GRAY_DYE),
	GRAY(new Vec3(59, 59, 59), ChatFormatting.DARK_GRAY, "gray", Items.GRAY_DYE),
	RED(new Vec3(166, 5, 5), ChatFormatting.RED, "red", Items.RED_DYE),
	LIGHT_BLUE(new Vec3(52, 135, 255), ChatFormatting.BLUE, "light_blue", Items.LIGHT_BLUE_DYE),
	LIME(new Vec3(123, 207, 17), ChatFormatting.GREEN, "lime", Items.LIME_DYE),
	ORANGE(new Vec3(227, 144, 44), ChatFormatting.GOLD, "orange", Items.ORANGE_DYE),
	MAGENTA(new Vec3(203, 105, 197), ChatFormatting.LIGHT_PURPLE, "magenta", Items.MAGENTA_DYE),
	WHITE(new Vec3(255, 255, 255), ChatFormatting.WHITE, "white", Items.WHITE_DYE),
	PURPLE(new Vec3(130, 48, 178), ChatFormatting.DARK_PURPLE, "purple", Items.PURPLE_DYE),
	PINK(new Vec3(242, 171, 207), ChatFormatting.LIGHT_PURPLE, "pink", Items.PINK_DYE),
	GREEN(new Vec3(56, 84, 14), ChatFormatting.DARK_GREEN, "green", Items.GREEN_DYE),
	CYAN(new Vec3(28, 97, 132), ChatFormatting.DARK_AQUA, "cyan", Items.CYAN_DYE),
	BLUE(new Vec3(0, 24, 171), ChatFormatting.DARK_BLUE, "blue", Items.BLUE_DYE),
	BLACK(new Vec3(15, 15, 15), ChatFormatting.DARK_GRAY, "black", Items.BLACK_DYE);

	private final Vec3 filter;
	private final ChatFormatting labelColor;
	private final String label;
	private final Item dye;

	public static MinecartColor getFromLabel(String label) {
		for (MinecartColor color : values()) {
			if (color.label.equals(label)) {
				return color;
			}
		}
		return null;
	}

	public static MinecartColor getFromItem(Item dye) {
		for (MinecartColor color : values()) {
			if (color.dye.equals(dye)) {
				return color;
			}
		}
		return null;
	}

	public Vec3 getFormattedFilter() {
		return getFilter().scale(1 / 255f);
	}
}
