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

import static hu.xannosz.betterminecarts.component.ModComponentTypes.*;

@Slf4j
public class FuelHolder {
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
		if (item.has(ENERGY_IN_ONE_MILLI_BUCKET_KEY.get())) {
			return true;
		}
		return getFuelFromList(item) != null;
	}

	@SuppressWarnings("ConstantConditions")
	public Item getLeftover(ItemStack item) {
		if (item.has(LEFTOVER_ITEM_KEY.get())) {
			return getItemFromQualifiedName(item.get(LEFTOVER_ITEM_KEY.get()));
		}
		if (!isFuel(item)) {
			return Items.AIR;
		}
		return getItemFromQualifiedName(getFuelFromList(item).getLeftoverItemQualifiedName());
	}

	@SuppressWarnings("ConstantConditions")
	public int getFuelAmount(ItemStack item) {
		if (item.has(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY.get())) {
			return item.get(AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY.get());
		}
		if (!isFuel(item)) {
			return 0;
		}
		return getFuelFromList(item).getAmountInOneItemInMilliBuckets();
	}

	@SuppressWarnings("ConstantConditions")
	public int getFuelPower(ItemStack item) {
		if (item.has(ENERGY_IN_ONE_MILLI_BUCKET_KEY.get())) {
			return item.get(ENERGY_IN_ONE_MILLI_BUCKET_KEY.get());
		}
		if (!isFuel(item)) {
			return 0;
		}
		return getFuelFromList(item).getEnergyInOneMilliBucket();
	}

	@SuppressWarnings("ConstantConditions")
	public int getFuelColor(ItemStack item) {
		if (item.has(FUEL_COLOR_KEY.get())) {
			return item.get(FUEL_COLOR_KEY.get());
		}
		if (!isFuel(item)) {
			return 0xFFFFFFFF;
		}
		Fuel fuel = getFuelFromList(item);
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

	private Item getItemFromQualifiedName(String name) {
		return BuiltInRegistries.ITEM.get(ResourceLocation.parse(name));
	}
}
