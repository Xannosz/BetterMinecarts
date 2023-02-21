package hu.xannosz.betterminecarts.blocks;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static hu.xannosz.betterminecarts.item.ModItems.ITEMS;

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BetterMinecarts.MOD_ID);

	@SuppressWarnings("unused")
	public static final RegistryObject<Block> CROSSED_RAIL = registerBlock("crossed_rail",
			CrossedRailBlock::new
	);

	@SuppressWarnings("unused")
	public static final RegistryObject<Block> SIGNAL_RAIL = registerBlock("signal_rail",
			SignalRailBlock::new
	);

	public static final RegistryObject<Block> GLOWING_RAIL = BLOCKS.register("glowing_rail",
			GlowingRailBlock::new
	);

	@SuppressWarnings("unused")
	public static final RegistryObject<Block> DEAD_END = registerBlock("dead_end",
			DeadEndBlock::new
	);

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockCreator) {
		RegistryObject<T> block = BLOCKS.register(name, blockCreator);
		registerBlockItem(name, block);
		return block;
	}

	private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
		ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));
	}
}
