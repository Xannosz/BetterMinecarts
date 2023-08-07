package hu.xannosz.betterminecarts.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fuel {
	public static final String FUEL_COLOR_R = "fuelColorRed";
	public static final String FUEL_COLOR_G = "fuelColorGreen";
	public static final String FUEL_COLOR_B = "fuelColorBlue";
	public static final String FUEL_COLOR_A = "fuelColorAlpha";
	public static final String AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS = "amountInOneItemInMilliBuckets";
	public static final String ENERGY_IN_ONE_MILLI_BUCKET = "energyInOneMilliBucket";
	public static final String ITEM_QUALIFIED_NAME = "itemQualifiedName";
	public static final String LEFTOVER_ITEM_QUALIFIED_NAME = "leftoverItemQualifiedName";

	private int fuelColor;
	private int amountInOneItemInMilliBuckets;
	private int energyInOneMilliBucket;
	private String itemQualifiedName;
	private String leftoverItemQualifiedName;

	public String toJson() {
		return "\t{\n" +
				"\t\t\"" + FUEL_COLOR_R + "\": " + (fuelColor >> 24 & 0xff) + ",\n" +
				"\t\t\"" + FUEL_COLOR_G + "\": " + ((fuelColor & 0xff0000) >> 16) + ",\n" +
				"\t\t\"" + FUEL_COLOR_B + "\": " + ((fuelColor & 0xff00) >> 8) + ",\n" +
				"\t\t\"" + FUEL_COLOR_A + "\": " + (fuelColor & 0xff) + ",\n" +
				"\t\t\"" + AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS + "\": " + amountInOneItemInMilliBuckets + ",\n" +
				"\t\t\"" + ENERGY_IN_ONE_MILLI_BUCKET + "\": " + energyInOneMilliBucket + ",\n" +
				"\t\t\"" + ITEM_QUALIFIED_NAME + "\": \"" + itemQualifiedName + "\",\n" +
				"\t\t\"" + LEFTOVER_ITEM_QUALIFIED_NAME + "\": \"" + leftoverItemQualifiedName + "\"\n" +
				"\t}\n";
	}
}
