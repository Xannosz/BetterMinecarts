package hu.xannosz.betterminecarts.blocks;

import hu.xannosz.betterminecarts.blockentity.GlowingRailBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static hu.xannosz.betterminecarts.blockentity.ModBlockEntities.GLOWING_RAIL_BLOCK_ENTITY;

public class GlowingRailBlock extends RailBlock implements EntityBlock {
	public GlowingRailBlock() {
		super(BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL).lightLevel(bs -> 15));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		return new GlowingRailBlockEntity(blockPos, blockState);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
		return blockEntityType == GLOWING_RAIL_BLOCK_ENTITY.get() ?
				(level2, pos, state, blockEntity) ->
						GlowingRailBlockEntity.tick(level2, pos, state,
								(GlowingRailBlockEntity) blockEntity) : null;
	}
}
