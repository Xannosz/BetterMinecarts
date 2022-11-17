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
	YELLOW(new Vec3(90.6, 90.6, 16.5), ChatFormatting.YELLOW, "yellow", Items.YELLOW_DYE),
	BROWN(new Vec3(38.8, 17.3, 1.6), ChatFormatting.DARK_RED, "brown", Items.BROWN_DYE),
	GRAY(new Vec3(77.6, 77.6, 77.6), ChatFormatting.GRAY, "gray", Items.LIGHT_GRAY_DYE),
	DARK_GRAY(new Vec3(54.5, 54.5, 54.5), ChatFormatting.DARK_GRAY, "dark_gray", Items.GRAY_DYE),
	RED(new Vec3(90.6, 0, 0), ChatFormatting.RED, "red", Items.RED_DYE);

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

	public Vec3 getFormattedFilter() {
		return getFilter().scale(1 / 100f);
	}
}
