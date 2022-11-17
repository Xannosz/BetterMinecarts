package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.button.ButtonId;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ElectricLocomotive extends AbstractLocomotive {

	public static final int POWER_KEY = 3;
	public static final int MAX_POWER = 20;
	public static final int DATA_SIZE = 4;

	private int power = 0;
	private BlockPos lastBlockPos;

	public ElectricLocomotive(EntityType<?> entityType, Level level) {
		super(entityType, level, MinecartColor.YELLOW, MinecartColor.BROWN, DATA_SIZE);
	}

	public ElectricLocomotive(Level level, double x, double y, double z, MinecartColor top, MinecartColor bottom) {
		super(BetterMinecarts.ELECTRIC_LOCOMOTIVE.get(), x, y, z, level, top, bottom, DATA_SIZE);
	}

	@Override
	protected @NotNull Item getDropItem() {
		return BetterMinecarts.LOCOMOTIVE_ITEMS.get(
				BetterMinecarts.generateNameFromData(getTopFilter(), getBottomFilter(), false)).get();
	}

	@Override
	protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv) {
		updateData();
		return new ElectricLocomotiveMenu(containerId, inv, this, data);
	}

	@Override
	protected boolean canPush() {
		return power > 0;
	}

	@Override
	public void updateData() {
		data.set(POWER_KEY, power);
		super.updateData();
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Power", power);
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		power = compoundTag.getInt("Power");
		updateData();
	}

	@Override
	protected void moveAlongTrack(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		super.moveAlongTrack(blockPos, blockState);
		if (!blockPos.equals(lastBlockPos)) {
			lastBlockPos = blockPos;
			power--;
		}
		if (power < 0) {
			power = 0;
			xPush = 0;
			zPush = 0;
			activeButton = ButtonId.STOP;
		}
		if (blockState.getBlock().equals(Blocks.POWERED_RAIL) && blockState.getValue(PoweredRailBlock.POWERED)) {
			power = MAX_POWER;
		}
	}
}
