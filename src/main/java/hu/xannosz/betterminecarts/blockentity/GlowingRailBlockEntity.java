package hu.xannosz.betterminecarts.blockentity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.BaseRailBlock.WATERLOGGED;
import static net.minecraft.world.level.block.RailBlock.SHAPE;

public class GlowingRailBlockEntity extends BlockEntity {

	@Setter
	private int count = 0;

	public GlowingRailBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BetterMinecarts.GLOWING_RAIL_BLOCK_ENTITY.get(), blockPos, blockState);
	}

	@SuppressWarnings("unused")
	public static void tick(Level level, BlockPos pos, BlockState state, GlowingRailBlockEntity blockEntity) {
		blockEntity.tick(pos, state);
	}

	private void tick(BlockPos pos, BlockState state) {
		if (level == null || level.isClientSide()) {
			return;
		}
		count++;
		if (count > 5) {
			level.setBlock(pos, Blocks.RAIL.defaultBlockState().setValue(SHAPE, state.getValue(SHAPE))
							.setValue(WATERLOGGED,state.getValue(WATERLOGGED)),
					2, 0);
		}
	}
}
