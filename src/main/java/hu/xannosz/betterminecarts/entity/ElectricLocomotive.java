package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.button.ButtonId;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
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
public class ElectricLocomotive extends AbstractLocomotive {

	public static final int ID_KEY = 0;
	public static final int POWER_KEY = 1;
	public static final int ACTIVE_BUTTON_KEY = 2;
	public static final int ACTIVE_FUNCTION_KEY = 3;
	public static final int DATA_SIZE = 4;

	public static final int MAX_POWER = 20;

	private ButtonId activeButton = ButtonId.STOP;
	private int power = 0;
	private double xPush = 0;
	private double zPush = 0;
	private int speed = 0;
	private boolean sendSignal = false;
	private boolean lampOn = false;
	private BlockPos lastBlockPos;

	private final ContainerData data = new SimpleContainerData(DATA_SIZE);

	public ElectricLocomotive(EntityType<?> entityType, Level level) {
		super(entityType, level);
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
		data.set(ACTIVE_FUNCTION_KEY, MinecartHelper.convertBitArrayToInt(new boolean[]{sendSignal, lampOn}));
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
		compoundTag.putBoolean("SendSignal", sendSignal);
		compoundTag.putBoolean("LampOn", lampOn);
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		xPush = compoundTag.getDouble("PushX");
		zPush = compoundTag.getDouble("PushZ");
		power = compoundTag.getInt("Power");
		activeButton = ButtonId.getButtonFromId(compoundTag.getInt("ActiveButton"));
		speed = compoundTag.getInt("Speed");
		sendSignal = compoundTag.getBoolean("SendSignal");
		lampOn = compoundTag.getBoolean("LampOn");
		updateData();
	}

	@Override
	public void executeButtonClick(ButtonId buttonId) {
		switch (buttonId) {
			case BACK -> {
				setPush(getDirection().getCounterClockWise());
				speed = 1;
				activeButton = buttonId;
			}
			case STOP, PAUSE -> {
				xPush = 0;
				zPush = 0;
				speed = 1;
				activeButton = buttonId;
			}
			case FORWARD -> {
				setPush(getDirection().getClockWise());
				speed = 1;
				activeButton = buttonId;
			}
			case F_FORWARD -> {
				setPush(getDirection().getClockWise());
				speed = 2;
				activeButton = buttonId;
			}
			case FF_FORWARD -> {
				setPush(getDirection().getClockWise());
				speed = 4;
				activeButton = buttonId;
			}
			case LAMP -> lampOn = !lampOn;
			case WHISTLE -> log.info("WHISTLE");
			case REDSTONE -> sendSignal = !sendSignal;
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
		return MinecartColor.YELLOW.getFilter().scale(1 / 100f);
	}

	public Vec3 getBottomFilter() {
		return MinecartColor.BROWN.getFilter().scale(1 / 100f);
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

	public boolean popSignal() {
		if (sendSignal) {
			sendSignal = false;
			return true;
		} else {
			return false;
		}
	}
}
