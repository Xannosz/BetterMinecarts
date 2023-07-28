package hu.xannosz.betterminecarts.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fuel {
	public static final String FUEL_COLOR = "fuelColor";
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
				"\t\t\"" + FUEL_COLOR + "\": " + fuelColor + ",\n" +
				"\t\t\"" + AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS + "\": " + amountInOneItemInMilliBuckets + ",\n" +
				"\t\t\"" + ENERGY_IN_ONE_MILLI_BUCKET + "\": " + energyInOneMilliBucket + ",\n" +
				"\t\t\"" + ITEM_QUALIFIED_NAME + "\": \"" + itemQualifiedName + "\",\n" +
				"\t\t\"" + LEFTOVER_ITEM_QUALIFIED_NAME + "\": \"" + leftoverItemQualifiedName + "\"\n" +
				"\t}\n";
	}
}
