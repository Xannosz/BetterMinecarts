package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static hu.xannosz.betterminecarts.BetterMinecarts.generateNameFromData;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BetterMinecarts.MOD_ID);

	public static final Map<String, RegistryObject<AbstractLocomotiveItem>> LOCOMOTIVE_ITEMS = createLocomotiveItems();
	public static final Set<RegistryObject<BlockItem>> BLOCK_ITEMS = new HashSet<>();

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> CROWBAR = ITEMS.register("crowbar",
			() -> new Crowbar(new Item.Properties().stacksTo(1)));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> CRAFTING_MINECART_ITEM = ITEMS.register("crafting_minecart_item", CraftingMinecartItem::new);

	private static Map<String, RegistryObject<AbstractLocomotiveItem>> createLocomotiveItems() {
		Map<String, RegistryObject<AbstractLocomotiveItem>> result = new HashMap<>();
		for (MinecartColor topColor : MinecartColor.values()) {
			for (MinecartColor bottomColor : MinecartColor.values()) {
				result.put(generateNameFromData(topColor, bottomColor, true),
						ITEMS.register(generateNameFromData(topColor, bottomColor, true),
								() -> new AbstractLocomotiveItem(topColor, bottomColor, true)));
				result.put(generateNameFromData(topColor, bottomColor, false),
						ITEMS.register(generateNameFromData(topColor, bottomColor, false),
								() -> new AbstractLocomotiveItem(topColor, bottomColor, false)));
			}
		}

		return result;
	}
}
