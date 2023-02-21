package hu.xannosz.betterminecarts.blockentity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static hu.xannosz.betterminecarts.blocks.ModBlocks.GLOWING_RAIL;

public class ModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BetterMinecarts.MOD_ID);

	public static final RegistryObject<BlockEntityType<GlowingRailBlockEntity>> GLOWING_RAIL_BLOCK_ENTITY =
			BLOCK_ENTITIES.register("glowing_rail_block_entity", () ->
					BlockEntityType.Builder.of(
							GlowingRailBlockEntity::new,
							GLOWING_RAIL.get()).build(null));
}
