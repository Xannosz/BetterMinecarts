package hu.xannosz.betterminecarts.utils;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FuelHolder {
	public static final String FUEL_COLOR_KEY = "BETTER_MINECARTS_FUEL_COLOR";
	public static final String AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY = "BETTER_MINECARTS_AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS";
	public static final String ENERGY_IN_ONE_MILLI_BUCKET_KEY = "BETTER_MINECARTS_ENERGY_IN_ONE_MILLI_BUCKET";
	public static final String LEFTOVER_ITEM_KEY = "BETTER_MINECARTS_LEFTOVER_ITEM";

	@Getter
	private static final FuelHolder INSTANCE = new FuelHolder();

	private final List<Fuel> fuels = new ArrayList<>();

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private FuelHolder() {
		final Path configPath = FMLPaths.GAMEDIR.get().resolve("config/betterminecartsfuel.json");

		if (!configPath.toFile().exists()) {
			fuels.add(new Fuel(0x94, 0x91, 0x46, 0x7F, 200, 10,
					"betterminecarts:bio_diesel_fuel", "minecraft:glass_bottle"));
			try {
				configPath.toFile().createNewFile();
				FileUtils.writeStringToFile(configPath.toFile(), new GsonBuilder().setPrettyPrinting().create().toJson(this.fuels), Charsets.UTF_8);
			} catch (Exception ex) {
				log.error("Problem with fuel json parsing", ex);
			}
		} else {
			try {
				JsonElement dataObject = JsonParser.parseString(FileUtils.readFileToString(configPath.toFile(), Charsets.UTF_8));
				fuels.addAll(Arrays.asList(new Gson().fromJson(dataObject, Fuel[].class)));
			} catch (Exception ex) {
				log.error("Problem with fuel json parsing", ex);
			}
		}
	}

	public boolean isFuel(ItemStack item) {
		if (item.getOrCreateTag().contains(ENERGY_IN_ONE_MILLI_BUCKET_KEY)) {
			return true;
		}
		return getFuelFromList(item) != null;
	}

	@SuppressWarnings("ConstantConditions")
	public Item getLeftover(ItemStack item) {
		if (item.getOrCreateTag().contains(LEFTOVER_ITEM_KEY)) {
			return getItemFromQualifiedName(item.getOrCreateTag().getString(LEFTOVER_ITEM_KEY));
		}
		if (!isFuel(item)) {
			return Items.AIR;
		}
		return getItemFromQualifiedName(getFuelFromList(item).getLeftoverItemQualifiedName());
	}

	@SuppressWarnings("ConstantConditions")
	public int getFuelAmount(ItemStack item) {
		if (item.getOrCreateTag().contains(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY)) {
			return item.getOrCreateTag().getInt(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY);
		}
		if (!isFuel(item)) {
			return 0;
		}
		return getFuelFromList(item).getAmountInOneItemInMilliBuckets();
	}

	@SuppressWarnings("ConstantConditions")
	public int getFuelPower(ItemStack item) {
		if (item.getOrCreateTag().contains(ENERGY_IN_ONE_MILLI_BUCKET_KEY)) {
			return item.getOrCreateTag().getInt(ENERGY_IN_ONE_MILLI_BUCKET_KEY);
		}
		if (!isFuel(item)) {
			return 0;
		}
		return getFuelFromList(item).getEnergyInOneMilliBucket();
	}

	@SuppressWarnings("ConstantConditions")
	public int getFuelColor(ItemStack item) {
		if (item.getOrCreateTag().contains(FUEL_COLOR_KEY)) {
			return item.getOrCreateTag().getInt(FUEL_COLOR_KEY);
		}
		if (!isFuel(item)) {
			return 0xFFFFFFFF;
		}
		Fuel fuel =  getFuelFromList(item);
		return fuel.getFuelColorRed() << 24 | fuel.getFuelColorGreen() << 16 | fuel.getFuelColoBlue() << 8 | fuel.getFuelColorAlpha();
	}

	private Fuel getFuelFromList(ItemStack item) {
		for (Fuel fuel : fuels) {
			if (getItemFromQualifiedName(fuel.getItemQualifiedName()).equals(item.getItem())) {
				return fuel;
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	private Item getItemFromQualifiedName(String name) {
		return BuiltInRegistries.ITEM.get(new ResourceLocation(name));
	}
}
