package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BetterMinecarts.MOD_ID);

	public static final Set<RegistryObject<BlockItem>> BLOCK_ITEMS = new HashSet<>();

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> CROWBAR = ITEMS.register("crowbar",
			() -> new Crowbar(new Item.Properties().stacksTo(1)));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> ELECTRIC_LOCOMOTIVE = ITEMS.register("electric_locomotive",
			() -> new AbstractLocomotiveItem(false));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> STEAM_LOCOMOTIVE = ITEMS.register("steam_locomotive",
			() -> new AbstractLocomotiveItem(true));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> CRAFTING_MINECART_ITEM = ITEMS.register("crafting_minecart_item", CraftingMinecartItem::new);
}
