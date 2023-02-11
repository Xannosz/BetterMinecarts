package hu.xannosz.betterminecarts.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DeadEndBlock extends Block {
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

	public DeadEndBlock() {
		super(BlockBehaviour.Properties.of(Material.HEAVY_METAL).noOcclusion()
				.strength(1.5F, 6.0F).sound(SoundType.METAL));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
		blockStateBuilder.add(FACING);
	}

	@SuppressWarnings("deprecation")
	public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
		switch (blockState.getValue(FACING)) {
			case WEST -> {
				return Shapes.or(
						Block.box(8, 0, 0, 16, 6, 16),
						Block.box(0, 0, 0, 8, 12, 16)
				);
			}
			case EAST -> {
				return Shapes.or(
						Block.box(8, 0, 0, 16, 12, 16),
						Block.box(0, 0, 0, 8, 6, 16)
				);
			}
			case NORTH -> {
				return Shapes.or(
						Block.box(0, 0, 8, 16, 6, 16),
						Block.box(0, 0, 0, 16, 12, 8)
				);
			}
			case SOUTH -> {
				return Shapes.or(
						Block.box(0, 0, 8, 16, 12, 16),
						Block.box(0, 0, 0, 16, 6, 8)
				);
			}
		}

		return Shapes.block();
	}
}
