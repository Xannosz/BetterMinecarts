package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.blockentity.GlowingRailBlockEntity;
import hu.xannosz.betterminecarts.config.BetterMinecartsConfig;
import hu.xannosz.betterminecarts.item.AbstractLocomotiveItem;
import hu.xannosz.betterminecarts.network.ModMessages;
import hu.xannosz.betterminecarts.network.PlaySoundPacket;
import hu.xannosz.betterminecarts.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static hu.xannosz.betterminecarts.blocks.ModBlocks.GLOWING_RAIL;
import static hu.xannosz.betterminecarts.item.ModItems.*;
import static hu.xannosz.betterminecarts.utils.MinecartHelper.*;
import static net.minecraft.world.level.block.BaseRailBlock.WATERLOGGED;
import static net.minecraft.world.level.block.RailBlock.SHAPE;

public abstract class AbstractLocomotive extends AbstractMinecart implements ButtonUser, MenuProvider, KeyUser {

	public static final int ID_KEY_1 = 0;
	public static final int ID_KEY_2 = 1;
	public static final int ACTIVE_BUTTON_KEY = 2;
	public static final int ACTIVE_FUNCTION_KEY = 3;

	protected ButtonId activeButton = ButtonId.STOP;
	protected double xPush = 0;
	protected double zPush = 0;
	protected final ContainerData data;
	protected int speed = 0;

	private boolean sendSignal = false;
	private boolean lampOn = false;
	private MinecartColor topFilter;
	private MinecartColor bottomFilter;
	private ChunkPos prevChunkPos;
	private final LocomotiveType locomotiveType;

	protected AbstractLocomotive(EntityType<?> entityType, Level level, LocomotiveType locomotiveType,
								 MinecartColor topFilter, MinecartColor bottomFilter, int dataSize) {
		super(entityType, level);
		this.locomotiveType = locomotiveType;
		this.topFilter = topFilter;
		this.bottomFilter = bottomFilter;
		data = new SimpleContainerData(dataSize);
		prevChunkPos = chunkPosition();
		updateData();
	}

	protected AbstractLocomotive(EntityType<?> entityType, double x, double y, double z, Level level, LocomotiveType locomotiveType,
								 MinecartColor topFilter, MinecartColor bottomFilter, int dataSize) {
		super(entityType, level, x, y, z);
		this.locomotiveType = locomotiveType;
		this.topFilter = topFilter;
		this.bottomFilter = bottomFilter;
		data = new SimpleContainerData(dataSize);
		prevChunkPos = chunkPosition();
		updateData();
	}

	// common functions

	protected abstract boolean canPush();

	public void setStartDirection(Direction direction) {
		switch (direction) {
			case NORTH -> setYRot(90f);
			case EAST -> setYRot(180f);
			case SOUTH -> setYRot(-90f);
		}
		updateData();
	}

	public float normalizeRotation(float yRotation) {
		float expectedAngle = 0;

		switch (getMotionDirection()) {
			case NORTH -> expectedAngle = 90;
			case SOUTH -> expectedAngle = 270;
			case WEST -> expectedAngle = 0;
			case EAST -> expectedAngle = 180;
		}

		if (!isInRange(yRotation, expectedAngle)) {
			yRotation += 180;
		}
		return yRotation;
	}

	private boolean isInRange(float yRotation, float expectedAngle) {
		final int border = 80;
		float plusYRotation = yRotation < 0 ? yRotation + 360 : yRotation;
		if (expectedAngle == 0) {
			return expectedAngle - border < plusYRotation && (plusYRotation < expectedAngle + border ||
					360 - border < plusYRotation);
		} else {
			return expectedAngle - border < plusYRotation && plusYRotation < expectedAngle + border;
		}
	}

	public float normalizePitch(float pitch) {
		switch (getMotionDirection()) {
			case NORTH, WEST -> {
				return (-1) * pitch;
			}
		}
		return pitch;
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

	@Override
	public void executeKeyPress(KeyId keyId, Player player) {
		switch (keyId) {
			case LAMP -> {
				executeButtonClick(ButtonId.LAMP);
				player.displayClientMessage(Component.translatable("text.betterminecarts.keyPress.lamp." + lampOn).withStyle(ChatFormatting.AQUA), true);
			}
			case WHISTLE -> executeButtonClick(ButtonId.WHISTLE);
			case REDSTONE -> {
				executeButtonClick(ButtonId.REDSTONE);
				player.displayClientMessage(Component.translatable("text.betterminecarts.keyPress.redstone." + sendSignal).withStyle(ChatFormatting.AQUA), true);
			}
			case DATA -> {
				getEngineData().forEach(player::sendSystemMessage);
				player.sendSystemMessage(Component.translatable("text.betterminecarts.data.lamp").append(
						Component.translatable("text.betterminecarts.keyPress.lamp." + lampOn)
								.withStyle(lampOn ? ChatFormatting.YELLOW : ChatFormatting.GOLD)
				));
				player.sendSystemMessage(Component.translatable("text.betterminecarts.data.redstone").append(
						Component.translatable("text.betterminecarts.keyPress.redstone." + sendSignal)
								.withStyle(sendSignal ? ChatFormatting.RED : ChatFormatting.DARK_RED)
				));
				player.sendSystemMessage(Component.translatable("text.betterminecarts.data.speed").append(
						Component.translatable("text.betterminecarts.keyPress.speed." + activeButton)
								.withStyle(ChatFormatting.DARK_GRAY)
				));
			}
			case INCREASE -> {
				switch (activeButton) {
					case BACK -> executeButtonClick(ButtonId.STOP);
					case PAUSE -> executeButtonClick(ButtonId.FORWARD);
					case STOP -> {
						if (this instanceof SteamLocomotive) {
							executeButtonClick(ButtonId.PAUSE);
						} else {
							executeButtonClick(ButtonId.FORWARD);
						}
					}
					case FORWARD -> executeButtonClick(ButtonId.F_FORWARD);
					case F_FORWARD -> executeButtonClick(ButtonId.FF_FORWARD);
				}
				player.displayClientMessage(Component.translatable("text.betterminecarts.keyPress.speed." + activeButton).withStyle(ChatFormatting.AQUA), true);
			}
			case DECREASE -> {
				switch (activeButton) {
					case PAUSE -> executeButtonClick(ButtonId.STOP);
					case STOP -> executeButtonClick(ButtonId.BACK);
					case FORWARD -> {
						if (this instanceof SteamLocomotive) {
							executeButtonClick(ButtonId.PAUSE);
						} else {
							executeButtonClick(ButtonId.STOP);
						}
					}
					case F_FORWARD -> executeButtonClick(ButtonId.FORWARD);
					case FF_FORWARD -> executeButtonClick(ButtonId.F_FORWARD);
				}
				player.displayClientMessage(Component.translatable("text.betterminecarts.keyPress.speed." + activeButton).withStyle(ChatFormatting.AQUA), true);
			}
		}
	}

	protected abstract List<Component> getEngineData();

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
		short[] ids = MinecartHelper.intToShorts(this.getId());
		data.set(ID_KEY_1, ids[0]);
		data.set(ID_KEY_2, ids[1]);
		data.set(ACTIVE_BUTTON_KEY, activeButton.getId());
		data.set(ACTIVE_FUNCTION_KEY, MinecartHelper.convertBitArrayToInt(new boolean[]{sendSignal, lampOn}));
		if (level().isClientSide()) {
			return;
		}
		entityData.set(TOP_FILTER, topFilter.getLabel());
		entityData.set(BOTTOM_FILTER, bottomFilter.getLabel());
		entityData.set(IS_LAMP_ON, lampOn);
	}

	private void whistle() {
		try {
			if (BetterMinecartsConfig.WHISTLE_USE_STEAM_ON_STEAM_LOCOMOTIVE.get() &&
					this instanceof SteamLocomotive steamLocomotive) {
				if (!steamLocomotive.canWhistle()) {
					return;
				}
			}

			level().players().forEach(player -> {
						if (player.distanceToSqr(this) < 7000) {
							ModMessages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
									new PlaySoundPacket(blockPosition(), locomotiveType));
						}
					}
			);

			if (BetterMinecartsConfig.MOBS_PANIC_AFTER_WHISTLE.get()) {
				level().getEntities(this,
						new AABB(this.getOnPos().offset(-BetterMinecartsConfig.MOBS_PANIC_AFTER_WHISTLE_RANGE.get(), -10, -BetterMinecartsConfig.MOBS_PANIC_AFTER_WHISTLE_RANGE.get()),
								this.getOnPos().offset(BetterMinecartsConfig.MOBS_PANIC_AFTER_WHISTLE_RANGE.get(), 25, BetterMinecartsConfig.MOBS_PANIC_AFTER_WHISTLE_RANGE.get()))).forEach(
						entity -> {
							if (entity instanceof Mob cow) {
								cow.goalSelector.getAvailableGoals().forEach(
										wrappedGoal -> {
											if (wrappedGoal.getGoal() instanceof PanicGoal) {
												wrappedGoal.stop();
												wrappedGoal.start();
											}
										}
								);
							}
						}
				);
			}
		} catch (Exception ex) {
			//
		}
	}

	@Override
	public void tick() {
		super.tick();

		loadChunk();
		checkRedstoneUnderLocomotive();
		setLamp();
	}

	private void setLamp() {
		if (lampOn && !level().isClientSide()) {
			Set<BlockPos> positions = new HashSet<>();
			positions.add(getOnPos());
			positions.add(getOnPos().offset(1, 0, 1));
			positions.add(getOnPos().offset(1, 0, 0));
			positions.add(getOnPos().offset(1, 0, -1));
			positions.add(getOnPos().offset(0, 0, -1));
			positions.add(getOnPos().offset(-1, 0, -1));
			positions.add(getOnPos().offset(-1, 0, 0));
			positions.add(getOnPos().offset(-1, 0, 1));
			positions.add(getOnPos().offset(0, 0, 1));

			for (BlockPos position : positions) {
				setLampOnPosition(position);
			}
		}
	}

	private void setLampOnPosition(BlockPos position) {
		BlockState state = level().getBlockState(position);
		if (state.getBlock().equals(GLOWING_RAIL.get())) {
			if (level().getBlockEntity(position) instanceof GlowingRailBlockEntity glowingRailBlockEntity) {
				glowingRailBlockEntity.setCount(0);
			}
		}
		if (state.getBlock().equals(Blocks.RAIL)) {
			level().setBlock(position, GLOWING_RAIL.get().defaultBlockState()
							.setValue(SHAPE, state.getValue(SHAPE))
							.setValue(WATERLOGGED, state.getValue(WATERLOGGED)),
					2, 0);
		}
	}

	private void checkRedstoneUnderLocomotive() {
		if (activeButton != ButtonId.STOP &&
				(level().getBlockState(getOnPos()).getBlock().equals(Blocks.RAIL) || level().getBlockState(getOnPos()).getBlock().equals(GLOWING_RAIL.get())) &&
				level().getBlockState(getOnPos().below()).getBlock().equals(Blocks.REDSTONE_BLOCK)) {
			whistle();
		}
	}

	private void loadChunk() {
		if (BetterMinecartsConfig.FURNACE_MINECARTS_LOAD_CHUNKS.get() && level() instanceof ServerLevel server) {
			ChunkPos currentChunkPos = SectionPos.of(this).chunk();

			if (!activeButton.equals(ButtonId.STOP) && !activeButton.equals(ButtonId.PAUSE))
				server.getChunkSource().addRegionTicket(TicketType.PLAYER, currentChunkPos, 3, chunkPosition());
			if (!currentChunkPos.equals(prevChunkPos) || activeButton.equals(ButtonId.STOP) || activeButton.equals(ButtonId.PAUSE))
				server.getChunkSource().removeRegionTicket(TicketType.PLAYER, prevChunkPos, 3, chunkPosition());

			prevChunkPos = currentChunkPos;
		}
	}

	// minecart functions
	@Override
	public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.is(CROWBAR.get())) {
			return super.interact(player, hand);
		}
		if (MinecartColor.getFromItem(stack.getItem()) != null) {
			if (player.isShiftKeyDown()) {
				bottomFilter = MinecartColor.getFromItem(stack.getItem());
			} else {
				topFilter = MinecartColor.getFromItem(stack.getItem());
			}
			stack.shrink(1);
			updateData();
			return InteractionResult.SUCCESS;
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
	public ItemStack getPickResult() {
		ItemStack result = null;
		switch (locomotiveType) {
			case ELECTRIC -> result = new ItemStack(ELECTRIC_LOCOMOTIVE.get(), 1);
			case STEAM -> result = new ItemStack(STEAM_LOCOMOTIVE.get(), 1);
			case DIESEL -> result = new ItemStack(DIESEL_LOCOMOTIVE.get(), 1);
		}

		result.getOrCreateTag().putString(AbstractLocomotiveItem.TOP_COLOR_TAG, getTopFilter().getLabel());
		result.getOrCreateTag().putString(AbstractLocomotiveItem.BOTTOM_COLOR_TAG, getBottomFilter().getLabel());

		if (hasCustomName()) {
			result.setHoverName(getCustomName());
		}

		return result;
	}

	@Override
	public boolean causeFallDamage(float height, float p_146829_, @NotNull DamageSource damageSource) {
		if (height > 2 && height < 6) {
			explode(2);
			return true;
		} else if (height >= 6) {
			explode(8);
			return true;
		}
		return false;
	}

	protected void explode(float power) { //power: 3 normal creeper, 6 powered creeper
		if (!this.level().isClientSide && BetterMinecartsConfig.LOCOMOTIVE_EXPLODE_AFTER_FALL_DAMAGE.get()) {
			this.level().explode(this, this.getX(), this.getY(), this.getZ(), power, Level.ExplosionInteraction.BLOCK);
			this.discard();
		}
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
		if (other instanceof AbstractMinecart minecart &&
				((self.getLinkedParent() != null && !self.getLinkedParent().equals(minecart)) ||
						self.getLinkedParent() == null)) {
			minecart.setDeltaMovement(getDeltaMovement());
		}

		float damage = BetterMinecartsConfig.MINECART_DAMAGE.get().floatValue();

		if (damage > 0 && !level().isClientSide() && other instanceof LivingEntity living &&
				living.isAlive() && !living.isPassenger() && speed > 1) {
			living.hurt(new DamageSource(level().registryAccess()
					.registryOrThrow(Registries.DAMAGE_TYPE)
					.getHolderOrThrow(BetterMinecarts.MINECART_DAMAGE), this), damage);

			Vec3 knockBack = living.getDeltaMovement().add(getDeltaMovement().x() * speed, getDeltaMovement().length() * 0.5 * speed, getDeltaMovement().z() * speed);
			living.setDeltaMovement(knockBack);
			living.hasImpulse = true;
			return false;
		}
		return Boat.canVehicleCollide(this, other);
	}

	// entity functions
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(TOP_FILTER, MinecartColor.MAGENTA.getLabel());
		entityData.define(BOTTOM_FILTER, MinecartColor.BLACK.getLabel());
		entityData.define(IS_LAMP_ON, false);
	}

	public MinecartColor getTopFilter() {
		return MinecartColor.getFromLabel(entityData.get(TOP_FILTER));
	}

	public MinecartColor getBottomFilter() {
		return MinecartColor.getFromLabel(entityData.get(BOTTOM_FILTER));
	}

	public boolean isLampOn() {
		return entityData.get(IS_LAMP_ON);
	}

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
		compoundTag.putLong("PrevChunkPos", prevChunkPos.toLong());
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
		prevChunkPos = new ChunkPos(compoundTag.getLong("PrevChunkPos"));
		updateData();
	}
}
