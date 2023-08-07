package hu.xannosz.betterminecarts.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static hu.xannosz.betterminecarts.utils.Fuel.*;

@Slf4j
public class FuelHolder {
	public static final String FUEL_COLOR_KEY = "BETTER_MINECARTS_FUEL_COLOR";
	public static final String AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY = "BETTER_MINECARTS_AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS";
	public static final String ENERGY_IN_ONE_MILLI_BUCKET_KEY = "BETTER_MINECARTS_ENERGY_IN_ONE_MILLI_BUCKET";
	public static final String LEFTOVER_ITEM_KEY = "BETTER_MINECARTS_LEFTOVER_ITEM";

	@Getter
	private static final FuelHolder INSTANCE = new FuelHolder();

	private final List<Fuel> fuels = new ArrayList<>();

	private FuelHolder() {
		Path configPath = FMLPaths.GAMEDIR.get().resolve("config/betterminecartsfuel.json");

		if (!configPath.toFile().exists()) {
			try {
				final String content = "[\n" +
						new Fuel(0x9491467F, 200, 10,
								"betterminecarts:bio_diesel_fuel", "minecraft:glass_bottle").toJson() +
						"]\n";
				Files.writeString(configPath, content);
			} catch (IOException e) {
				log.error("Problem with fuel json parsing", e);
			}
		}
		try {
			for (JsonObject value : Json.createReader(new FileInputStream(configPath.toFile()))
					.readArray().getValuesAs(JsonObject.class)) {
				fuels.add(new Fuel(
						getFuelColorValue(value),
						value.getInt(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS),
						value.getInt(ENERGY_IN_ONE_MILLI_BUCKET),
						value.getString(ITEM_QUALIFIED_NAME),
						value.getString(LEFTOVER_ITEM_QUALIFIED_NAME)
				));
			}
		} catch (IOException e) {
			log.error("Problem with fuel json parsing", e);
		}
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
		if (!isFuel(item)) {
			return Items.AIR;
		}
		return getItemFromQualifiedName(getFuelFromList(item).getLeftoverItemQualifiedName());
	}

	public int getFuelAmount(ItemStack item) {
		if (item.getOrCreateTag().contains(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY)) {
			return item.getOrCreateTag().getInt(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY);
		}
		if (!isFuel(item)) {
			return 0;
		}
		return getFuelFromList(item).getAmountInOneItemInMilliBuckets();
	}

	public int getFuelPower(ItemStack item) {
		if (item.getOrCreateTag().contains(ENERGY_IN_ONE_MILLI_BUCKET_KEY)) {
			return item.getOrCreateTag().getInt(ENERGY_IN_ONE_MILLI_BUCKET_KEY);
		}
		if (!isFuel(item)) {
			return 0;
		}
		return getFuelFromList(item).getEnergyInOneMilliBucket();
	}

	public int getFuelColor(ItemStack item) {
		if (item.getOrCreateTag().contains(FUEL_COLOR_KEY)) {
			return item.getOrCreateTag().getInt(FUEL_COLOR_KEY);
		}
		if (!isFuel(item)) {
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

	private int getFuelColorValue(JsonObject value){
		int a = value.getInt(FUEL_COLOR_A);
		int r = value.getInt(FUEL_COLOR_R);
		int g = value.getInt(FUEL_COLOR_G);
		int b = value.getInt(FUEL_COLOR_B);
		return r << 24 | g << 16 | b << 8 | a;
	}
}
