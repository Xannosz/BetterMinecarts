package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.button.ButtonId;
import hu.xannosz.betterminecarts.button.ButtonUser;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ElectricLocomotive extends AbstractMinecartContainer implements ButtonUser {//AbstractMinecartContainer

	public static final int ID_KEY = 0;
	public static final int POWER_KEY = 1;
	public static final int ACTIVE_BUTTON_KEY = 2;
	public static final int DATA_SIZE = 3;

	public static final int MAX_POWER = 20;

	private ButtonId activeButton = ButtonId.STOP;
	private int power = 0;
	private double xPush = 0;
	private double zPush = 0;
	private int speed = 0;
	private BlockPos lastBlockPos;

	private final ContainerData data = new SimpleContainerData(DATA_SIZE);

	public ElectricLocomotive(EntityType<?> p_38087_, Level p_38088_) {
		super(p_38087_, p_38088_);
		updateData();
	}

	public ElectricLocomotive(Level level, double x, double y, double z) {
		super(BetterMinecarts.ELECTRIC_LOCOMOTIVE.get(), x, y, z, level);
		updateData();
	}

	@Override
	protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv) {
		updateData();
		return new ElectricLocomotiveMenu(containerId, inv, this, data);
	}

	@Override
	protected @NotNull Item getDropItem() {
		return BetterMinecarts.ELECTRIC_LOCOMOTIVE_ITEM.get();
	}

	@Override
	public @NotNull Type getMinecartType() {
		return Type.FURNACE;
	}

	@Override
	public int getContainerSize() {
		return DATA_SIZE;
	}

	public void updateData() {
		data.set(ID_KEY, this.getId());
		data.set(POWER_KEY, power);
		data.set(ACTIVE_BUTTON_KEY, activeButton.getId());
	}

	@Override
	protected double getMaxSpeed() {
		return (this.isInWater() ? 1.5D : 2.0D) * speed / 20.0D;
	}

	@Override
	protected void moveAlongTrack(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		double d0 = 1.0E-4D;
		double d1 = 0.001D;
		super.moveAlongTrack(blockPos, blockState);
		Vec3 vec3 = this.getDeltaMovement();
		double d2 = vec3.horizontalDistanceSqr();
		double d3 = this.xPush * this.xPush + this.zPush * this.zPush;
		if (d3 > d0 && d2 > d1) {
			double d4 = Math.sqrt(d2);
			double d5 = Math.sqrt(d3);
			this.xPush = vec3.x / d4 * d5;
			this.zPush = vec3.z / d4 * d5;
		}
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
		updateData();
	}

	@Override
	protected void applyNaturalSlowdown() {
		double d0 = this.xPush * this.xPush + this.zPush * this.zPush;
		if (d0 > 1.0E-7D) {
			d0 = Math.sqrt(d0);
			this.xPush /= d0;
			this.zPush /= d0;
			Vec3 vec3 = this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush);
			if (this.isInWater()) {
				vec3 = vec3.scale(0.1D);
			}

			this.setDeltaMovement(vec3);
		} else {
			this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 0.0D, 0.6D));
		}

		final Vec3 dMovement = getDeltaMovement();
		setDeltaMovement(getNormalizedSpeed(dMovement.x),
				dMovement.y,
				getNormalizedSpeed(dMovement.z));

		super.applyNaturalSlowdown();
	}

	@Override
	public float getMaxCartSpeedOnRail() {
		return 6f;
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putDouble("PushX", xPush);
		compoundTag.putDouble("PushZ", zPush);
		compoundTag.putInt("Power", power);
		compoundTag.putInt("ActiveButton", activeButton.getId());
		compoundTag.putInt("Speed", speed);
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		xPush = compoundTag.getDouble("PushX");
		zPush = compoundTag.getDouble("PushZ");
		power = compoundTag.getInt("Power");
		activeButton = ButtonId.getButtonFromId(compoundTag.getInt("ActiveButton"));
		speed = compoundTag.getInt("Speed");
		updateData();
	}

	@Override
	public void executeButtonClick(ButtonId buttonId) {
		activeButton = buttonId;
		switch (buttonId) {
			case BACK -> {
				setPush(getDirection().getCounterClockWise());
				speed = 1;
			}
			case STOP -> {
				xPush = 0;
				zPush = 0;
				speed = 1;
			}
			case FORWARD -> {
				setPush(getDirection().getClockWise());
				speed = 1;
			}
			case F_FORWARD -> {
				setPush(getDirection().getClockWise());
				speed = 2;
			}
			case FF_FORWARD -> {
				setPush(getDirection().getClockWise());
				speed = 4;
			}
		}
		updateData();
		setChanged();
	}

	private void setPush(Direction direction) {
		if (power <= 0) {
			return;
		}
		switch (direction) {
			case NORTH -> {
				xPush = 0;
				zPush = -1;
			}
			case SOUTH -> {
				xPush = 0;
				zPush = 1;
			}
			case WEST -> {
				xPush = -1;
				zPush = 0;
			}
			case EAST -> {
				xPush = 1;
				zPush = 0;
			}
		}
	}

	private double getNormalizedSpeed(double speed) {
		return Math.max(Math.min(speed, getMaxSpeed()), -getMaxSpeed());
	}

	public Vec3 getTopFilter() {
		return MinecartColor.YELLOW.getFilter().scale(1/100f);
	}

	public Vec3 getBottomFilter() {
		return MinecartColor.BROWN.getFilter().scale(1/100f);
	}

	public float normalizeRotation(float yRotation) {
		float expectedAngle = 0;
		int border = 120;

		switch (getMotionDirection()) {
			case NORTH -> expectedAngle = 90;
			case SOUTH -> expectedAngle = -90;
			case WEST -> expectedAngle = 0;
			case EAST -> expectedAngle = 180;
		}

		if (yRotation < expectedAngle - border || expectedAngle + border < yRotation) {
			yRotation += 180;
		}
		return yRotation;
	}
}
