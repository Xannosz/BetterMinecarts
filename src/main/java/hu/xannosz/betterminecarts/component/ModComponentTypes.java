package hu.xannosz.betterminecarts.component;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

public class ModComponentTypes {
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
			DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, BetterMinecarts.MOD_ID);

	public static final RegistryObject<DataComponentType<String>> TOP_COLOR_TAG = registerString("topColor");
	public static final RegistryObject<DataComponentType<String>> BOTTOM_COLOR_TAG = registerString("bottomColor");
	public static final RegistryObject<DataComponentType<String>> MODE_TAG = registerString("mode");
	public static final RegistryObject<DataComponentType<Integer>> FIRST_CART_ID_TAG = register("firstCartId",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT));
	public static final RegistryObject<DataComponentType<Integer>> FUEL_COLOR_KEY = register("BETTER_MINECARTS_FUEL_COLOR",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT));
	public static final RegistryObject<DataComponentType<Integer>> AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS_KEY = register("BETTER_MINECARTS_AMOUNT_IN_ONE_ITEM_IN_MILLI_BUCKETS",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT));
	public static final RegistryObject<DataComponentType<Integer>> ENERGY_IN_ONE_MILLI_BUCKET_KEY = register("BETTER_MINECARTS_ENERGY_IN_ONE_MILLI_BUCKET",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT));
	public static final RegistryObject<DataComponentType<String>> LEFTOVER_ITEM_KEY = registerString("BETTER_MINECARTS_LEFTOVER_ITEM");

	private static RegistryObject<DataComponentType<String>> registerString(String name) {
		return register(name, builder -> builder.persistent(ExtraCodecs.NON_EMPTY_STRING));
	}

	private static <T> RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return DATA_COMPONENT_TYPES.register(name, () -> builder.apply(DataComponentType.builder()).build());
	}
}
