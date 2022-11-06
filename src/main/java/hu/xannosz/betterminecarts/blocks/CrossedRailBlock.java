package hu.xannosz.betterminecarts.blocks;

import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class CrossedRailBlock extends BaseRailBlock {
	public static final EnumProperty<RailShape> SHAPE = EnumProperty.create("shape", RailShape.class, shape -> shape != RailShape.ASCENDING_NORTH && shape != RailShape.ASCENDING_EAST && shape != RailShape.ASCENDING_SOUTH && shape != RailShape.ASCENDING_WEST && shape != RailShape.NORTH_EAST && shape != RailShape.NORTH_WEST && shape != RailShape.SOUTH_EAST && shape != RailShape.SOUTH_WEST);

	public CrossedRailBlock() {
		super(true, BlockBehaviour.Properties.copy(Blocks.RAIL));
		registerDefaultState(stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, false));
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, WATERLOGGED);
	}
}
