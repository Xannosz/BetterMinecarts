package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.LocomotiveType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
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
			() -> new Crowbar(Tiers.IRON, 2, -2.4F, new Item.Properties().stacksTo(1)));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> ELECTRIC_LOCOMOTIVE = ITEMS.register("electric_locomotive",
			() -> new AbstractLocomotiveItem(LocomotiveType.ELECTRIC));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> STEAM_LOCOMOTIVE = ITEMS.register("steam_locomotive",
			() -> new AbstractLocomotiveItem(LocomotiveType.STEAM));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> DIESEL_LOCOMOTIVE = ITEMS.register("diesel_locomotive",
			() -> new AbstractLocomotiveItem(LocomotiveType.DIESEL));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> BIO_DIESEL_FUEL = ITEMS.register("bio_diesel_fuel",
			() -> new Item(new Item.Properties().stacksTo(16)));

	@SuppressWarnings("unused")
	public static final RegistryObject<Item> CRAFTING_MINECART_ITEM = ITEMS.register("crafting_minecart_item", CraftingMinecartItem::new);
}
