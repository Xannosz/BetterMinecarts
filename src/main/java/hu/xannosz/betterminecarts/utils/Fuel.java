package hu.xannosz.betterminecarts.utils;

import lombok.Data;

@Data
public class Fuel {
	private int fuelColor;
	private int amountInOneItemInMilliBuckets;
	private int energyInOneMilliBucket;
	private String itemQualifiedName;
	private String leftoverItemQualifiedName;
}
