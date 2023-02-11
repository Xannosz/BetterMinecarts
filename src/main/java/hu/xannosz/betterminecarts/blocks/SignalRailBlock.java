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

	private void checkPressed(Level level, BlockPos blockPos, BlockState inputBlockState) {
		if (this.canSurvive(inputBlockState, level, blockPos)) {
			boolean isPowered = inputBlockState.getValue(POWERED);
			boolean locomotiveOnRail = false;
			boolean minecartOnRail = false;
			List<AbstractMinecart> list = this.getInteractingMinecartOfType(level,
					blockPos, AbstractMinecart.class, (entity) -> true);
			for (AbstractMinecart locomotive : list) {
				if (locomotive instanceof AbstractLocomotive abstractLocomotive && abstractLocomotive.popSignal()) {
					locomotiveOnRail = true;
				}
				minecartOnRail = true;
			}

			if (locomotiveOnRail && !isPowered) {
				BlockState blockState = inputBlockState.setValue(POWERED, Boolean.TRUE);
				level.setBlock(blockPos, blockState, 3);
				this.updatePowerToConnected(level, blockPos, blockState, true);
				level.updateNeighborsAt(blockPos, this);
				level.updateNeighborsAt(blockPos.below(), this);
				level.setBlocksDirty(blockPos, inputBlockState, blockState);
			}

			if (!locomotiveOnRail && isPowered && !minecartOnRail) {
				BlockState blockState = inputBlockState.setValue(POWERED, Boolean.FALSE);
				level.setBlock(blockPos, blockState, 3);
				this.updatePowerToConnected(level, blockPos, blockState, false);
				level.updateNeighborsAt(blockPos, this);
				level.updateNeighborsAt(blockPos.below(), this);
				level.setBlocksDirty(blockPos, inputBlockState, blockState);
			}

			if (locomotiveOnRail || minecartOnRail) {
				level.scheduleTick(blockPos, this, 28);
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
		return new AABB((double) blockPos.getX() - 0.2D,
				blockPos.getY(),
				(double) blockPos.getZ() - 0.2D,
				(double) (blockPos.getX() + 1) + 0.2D,
				(double) (blockPos.getY() + 1) - 0.2D,
				(double) (blockPos.getZ() + 1) + 0.2D);
	}
}
