package hu.xannosz.betterminecarts.blocks;

import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class SignalRailBlock extends DetectorRailBlock {
	public SignalRailBlock() {
		super(BlockBehaviour.Properties.copy(Blocks.DETECTOR_RAIL));
	}

	@Override
	public void entityInside(@NotNull BlockState blockState, Level level,
							 @NotNull BlockPos blockPos, @NotNull Entity entity) {
		if (!level.isClientSide) {
			if (!blockState.getValue(POWERED)) {
				this.checkPressed(level, blockPos, blockState);
			}
		}
	}

	@Override
	public void tick(BlockState blockState, @NotNull ServerLevel serverLevel, @NotNull BlockPos blockPos,
					 @NotNull RandomSource randomSource) {
		if (blockState.getValue(POWERED)) {
			this.checkPressed(serverLevel, blockPos, blockState);
		}
	}

	@Override
	public void onPlace(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos,
						BlockState bottomBlock, boolean update) {
		if (!bottomBlock.is(blockState.getBlock())) {
			BlockState blockstate = this.updateState(blockState, level, blockPos, update);
			this.checkPressed(level, blockPos, blockstate);
		}
	}

	private void checkPressed(Level level, BlockPos blockPos, BlockState blockState) {
		if (this.canSurvive(blockState, level, blockPos)) {
			boolean flag = blockState.getValue(POWERED);
			boolean flag1 = false;
			List<AbstractMinecart> list = this.getInteractingMinecartOfType(level,
					blockPos, AbstractMinecart.class, (entity) -> true);
			for (AbstractMinecart locomotive : list) {
				if (locomotive instanceof AbstractLocomotive && ((AbstractLocomotive) locomotive).popSignal()) {
					flag1 = true;
				}
			}

			if (flag1 && !flag) {
				BlockState blockstate = blockState.setValue(POWERED, Boolean.TRUE);
				level.setBlock(blockPos, blockstate, 3);
				this.updatePowerToConnected(level, blockPos, blockstate, true);
				level.updateNeighborsAt(blockPos, this);
				level.updateNeighborsAt(blockPos.below(), this);
				level.setBlocksDirty(blockPos, blockState, blockstate);
			}

			if (!flag1 && flag) {
				BlockState blockState1 = blockState.setValue(POWERED, Boolean.FALSE);
				level.setBlock(blockPos, blockState1, 3);
				this.updatePowerToConnected(level, blockPos, blockState1, false);
				level.updateNeighborsAt(blockPos, this);
				level.updateNeighborsAt(blockPos.below(), this);
				level.setBlocksDirty(blockPos, blockState, blockState1);
			}

			if (flag1) {
				level.scheduleTick(blockPos, this, 20);
			}

			level.updateNeighbourForOutputSignal(blockPos, this);
		}
	}

	@SuppressWarnings("SameParameterValue")
	private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level level, BlockPos blockPos,
																			  Class<T> clazz,
																			  Predicate<Entity> predicate) {
		return level.getEntitiesOfClass(clazz, this.getSearchBB(blockPos), predicate);
	}

	private AABB getSearchBB(BlockPos blockPos) {
		return new AABB((double) blockPos.getX() + 0.2D,
				blockPos.getY(),
				(double) blockPos.getZ() + 0.2D,
				(double) (blockPos.getX() + 1) - 0.2D,
				(double) (blockPos.getY() + 1) - 0.2D,
				(double) (blockPos.getZ() + 1) - 0.2D);
	}
}
