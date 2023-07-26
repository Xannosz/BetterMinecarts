package hu.xannosz.betterminecarts.utils;

import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class FuelHolder {
	public static final String FUEL_COLOR_KEY = "BETTER_MINECARTS_FUEL_COLOR";
	public static final String AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY = "BETTER_MINECARTS_AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS";
	public static final String ENERGY_IN_ONE_MILLI_BUCKET_KEY = "BETTER_MINECARTS_ENERGY_IN_ONE_MILLI_BUCKET";
	public static final String LEFTOVER_ITEM_KEY = "BETTER_MINECARTS_LEFTOVER_ITEM";

	@Getter
	private static final FuelHolder INSTANCE = new FuelHolder();

	private final List<Fuel> fuels = new ArrayList<>();

	private FuelHolder() {
		Fuel fuel = new Fuel();
		fuel.setFuelColor(0xAFAFAFFF);
		fuel.setAmountInOneItemInMilliBuckets(200);
		fuel.setEnergyInOneMilliBucket(3);
		fuel.setItemQualifiedName("minecraft:honey_bottle");
		fuel.setLeftoverItemQualifiedName("minecraft:glass_bottle");
		fuels.add(fuel);
	}

	public boolean isFuel(ItemStack item) {
		if (item.getOrCreateTag().contains(ENERGY_IN_ONE_MILLI_BUCKET_KEY)) {
			return true;
		}
		return getFuelFromList(item) != null;
	}

	public Item getLeftover(ItemStack item) {
		if (item.getOrCreateTag().contains(LEFTOVER_ITEM_KEY)) {
			return getItemFromQualifiedName(item.getOrCreateTag().getString(LEFTOVER_ITEM_KEY));
		}
		if(!isFuel(item)){
			return Items.AIR;
		}
		return getItemFromQualifiedName(getFuelFromList(item).getLeftoverItemQualifiedName());
	}

	public int getFuelAmount(ItemStack item) {
		if (item.getOrCreateTag().contains(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY)) {
			return item.getOrCreateTag().getInt(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY);
		}
		if(!isFuel(item)){
			return 0;
		}
		return getFuelFromList(item).getAmountInOneItemInMilliBuckets();
	}

	public int getFuelPower(ItemStack item) {
		if (item.getOrCreateTag().contains(ENERGY_IN_ONE_MILLI_BUCKET_KEY)) {
			return item.getOrCreateTag().getInt(ENERGY_IN_ONE_MILLI_BUCKET_KEY);
		}
		if(!isFuel(item)){
			return 0;
		}
		return getFuelFromList(item).getEnergyInOneMilliBucket();
	}

	public int getFuelColor(ItemStack item) {
		if (item.getOrCreateTag().contains(FUEL_COLOR_KEY)) {
			return item.getOrCreateTag().getInt(FUEL_COLOR_KEY);
		}
		if(!isFuel(item)){
			return 0xFFFFFFFF;
		}
		return getFuelFromList(item).getFuelColor();
	}

	private Fuel getFuelFromList(ItemStack item) {
		for (Fuel fuel : fuels) {
			if (getItemFromQualifiedName(fuel.getItemQualifiedName()).equals(item.getItem())) {
				return fuel;
			}
		}
		return null;
	}

	private Item getItemFromQualifiedName(String name) {
		return BuiltInRegistries.ITEM.get(new ResourceLocation(name));
	}
}
