package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.button.ButtonId;
import hu.xannosz.betterminecarts.button.ButtonUser;
import hu.xannosz.betterminecarts.network.LampSetPacket;
import hu.xannosz.betterminecarts.network.PlaySoundPacket;
import hu.xannosz.betterminecarts.utils.Linkable;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;

@Slf4j
public abstract class AbstractLocomotive extends AbstractMinecart implements ButtonUser, MenuProvider {

	public static final int ID_KEY = 0;
	public static final int ACTIVE_BUTTON_KEY = 1;
	public static final int ACTIVE_FUNCTION_KEY = 2;

	protected ButtonId activeButton = ButtonId.STOP;
	protected double xPush = 0;
	protected double zPush = 0;
	protected final ContainerData data;
	protected int speed = 0;

	private boolean sendSignal = false;
	@Setter
	@Getter
	private boolean lampOn = false;
	@Setter
	@Getter
	private MinecartColor topFilter;
	@Setter
	@Getter
	private MinecartColor bottomFilter;
	@Setter
	@Getter
	private boolean filterUpdateDone = false;

	protected AbstractLocomotive(EntityType<?> entityType, Level level,
								 MinecartColor topFilter, MinecartColor bottomFilter, int dataSize) {
		super(entityType, level);
		this.topFilter = topFilter;
		this.bottomFilter = bottomFilter;
		data = new SimpleContainerData(dataSize);
		updateData();
	}

	protected AbstractLocomotive(EntityType<?> entityType, double x, double y, double z, Level level,
								 MinecartColor topFilter, MinecartColor bottomFilter, int dataSize) {
		super(entityType, level, x, y, z);
		this.topFilter = topFilter;
		this.bottomFilter = bottomFilter;
		data = new SimpleContainerData(dataSize);
		updateData();
	}

	// common functions

	protected abstract boolean canPush();

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
			case WHISTLE -> whistle();
			case REDSTONE -> sendSignal = !sendSignal;
		}
		updateData();
	}

	private void setPush(Direction direction) {
		if (!canPush()) {
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

	public void updateData() {
		data.set(ID_KEY, this.getId());
		data.set(ACTIVE_BUTTON_KEY, activeButton.getId());
		data.set(ACTIVE_FUNCTION_KEY, MinecartHelper.convertBitArrayToInt(new boolean[]{sendSignal, lampOn}));
		if (level.isClientSide()) {
			return;
		}
		level.players().forEach(player ->
				BetterMinecarts.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
						new LampSetPacket(lampOn, getId()))
		);
	}

	private void whistle() {
		level.players().forEach(player ->
				BetterMinecarts.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
						new PlaySoundPacket(blockPosition()))
		);
	}

	// minecart functions
	@Override
	public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.is(BetterMinecarts.CROWBAR.get())) {
			return super.interact(player, hand);
		}
		if (player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		player.openMenu(this);
		return InteractionResult.SUCCESS;
	}

	@Override
	public @NotNull Type getMinecartType() {
		return Type.FURNACE;
	}

	@Override
	protected void moveAlongTrack(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		double d0 = 1.0E-4D;
		double d1 = 0.001D;
		try {
			super.moveAlongTrack(blockPos, blockState);
		} catch (ConcurrentModificationException ex) {
			//catch double collusion handling
		}
		Vec3 vec3 = this.getDeltaMovement();
		double d2 = vec3.horizontalDistanceSqr();
		double d3 = this.xPush * this.xPush + this.zPush * this.zPush;
		if (d3 > d0 && d2 > d1) {
			double d4 = Math.sqrt(d2);
			double d5 = Math.sqrt(d3);
			this.xPush = vec3.x / d4 * d5;
			this.zPush = vec3.z / d4 * d5;
		}
		updateData();
	}

	@Override
	protected double getMaxSpeed() {
		return (this.isInWater() ? 1.5D : 2.0D) * speed / 20.0D;
	}

	@Override
	public float getMaxCartSpeedOnRail() {
		return 6f;
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
	public void activateMinecart(int p_38659_, int p_38660_, int p_38661_, boolean p_38662_) {
		if (p_38662_) {
			whistle();
		}
	}

	@Override
	public boolean canCollideWith(@NotNull Entity other) {
		Linkable self = (Linkable) this;
		if (other instanceof AbstractMinecart minecart && self.getLinkedParent() != null && !self.getLinkedParent().equals(minecart))
			minecart.setDeltaMovement(getDeltaMovement());

		float damage = BetterMinecarts.getConfig().serverTweaks.minecartDamage;

		if (damage > 0 && !level.isClientSide() && other instanceof LivingEntity living && living.isAlive() && !living.isPassenger() && speed > 1) {
			living.hurt(BetterMinecarts.minecart(this), damage);

			Vec3 knockBack = living.getDeltaMovement().add(getDeltaMovement().x() * speed, getDeltaMovement().length() * 0.5 * speed, getDeltaMovement().z() * speed);
			living.setDeltaMovement(knockBack);
			living.hasImpulse = true;
			return false;
		}
		return Boat.canVehicleCollide(this, other);
	}

	// entity functions

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putDouble("PushX", xPush);
		compoundTag.putDouble("PushZ", zPush);
		compoundTag.putInt("ActiveButton", activeButton.getId());
		compoundTag.putInt("Speed", speed);
		compoundTag.putBoolean("SendSignal", sendSignal);
		compoundTag.putBoolean("LampOn", lampOn);
		compoundTag.putString("TopFilter", topFilter.getLabel());
		compoundTag.putString("BottomFilter", bottomFilter.getLabel());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		xPush = compoundTag.getDouble("PushX");
		zPush = compoundTag.getDouble("PushZ");
		activeButton = ButtonId.getButtonFromId(compoundTag.getInt("ActiveButton"));
		speed = compoundTag.getInt("Speed");
		sendSignal = compoundTag.getBoolean("SendSignal");
		lampOn = compoundTag.getBoolean("LampOn");
		topFilter = MinecartColor.getFromLabel(compoundTag.getString("TopFilter"));
		bottomFilter = MinecartColor.getFromLabel(compoundTag.getString("BottomFilter"));
		updateData();
	}
}
