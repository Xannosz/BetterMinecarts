package hu.xannosz.betterminecarts.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fuel {
	private int fuelColorRed;
	private int fuelColorGreen;
	private int fuelColoBlue;
	private int fuelColorAlpha;
	private int amountInOneItemInMilliBuckets;
	private int energyInOneMilliBucket;
	private String itemQualifiedName;
	private String leftoverItemQualifiedName;
}
