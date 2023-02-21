package hu.xannosz.betterminecarts.screen;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BetterMinecarts.MOD_ID);

	public static final RegistryObject<MenuType<ElectricLocomotiveMenu>> ELECTRIC_LOCOMOTIVE_MENU =
			registerMenuType(ElectricLocomotiveMenu::new, "electric_locomotive_menu");

	public static final RegistryObject<MenuType<SteamLocomotiveMenu>> STEAM_LOCOMOTIVE_MENU =
			registerMenuType(SteamLocomotiveMenu::new, "steam_locomotive_menu");

	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
		return MENUS.register(name, () -> IForgeMenuType.create(factory));
	}
}
