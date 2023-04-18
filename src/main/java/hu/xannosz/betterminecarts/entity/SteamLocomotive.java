package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.item.AbstractLocomotiveItem;
import hu.xannosz.betterminecarts.item.ModItems;
import hu.xannosz.betterminecarts.screen.SteamLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.ButtonId;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static hu.xannosz.betterminecarts.entity.ModEntities.STEAM_LOCOMOTIVE;
import static hu.xannosz.betterminecarts.utils.MinecartHelper.IS_BURN;
import static net.minecraft.world.item.crafting.RecipeType.SMELTING;
import static net.minecraftforge.common.ForgeHooks.getBurnTime;

public class SteamLocomotive extends AbstractLocomotive implements Container {

	public static final int STEAM_KEY = 4;
	public static final int WATER_KEY = 5;
	public static final int HEAT_KEY = 6;
	public static final int BURN_KEY = 7;
	public static final int MAX_BURN_KEY = 8;
	public static final int DATA_SIZE = 9;

	public static final int MAX_STEAM = 2000;
	public static final int MAX_WATER = 2000;
	public static final int MAX_HEAT = 520;
	public static final int MINIMUM_HEAT = 32;
	public static final int MINIMUM_STEAM = 10;

	private int steam = 0;
	private int water = 0;
	private int heat = MINIMUM_HEAT;
	private int burn = 0;
	private int maxBurn = 0;

	private int clock = 0;

	@Getter
	private final ItemStackHandler itemHandler = new ItemStackHandler(8) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return switch (slot) {
				case 1, 2, 3 -> stack.getItem() == Items.WATER_BUCKET;
				case 4, 5, 6, 7 -> getBurnTime(stack, SMELTING) > 0;
				case 0 -> false;
				default -> super.isItemValid(slot, stack);
			};
		}
	};
	@Getter
	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	public SteamLocomotive(EntityType<?> entityType, Level level) {
		super(entityType, level, MinecartColor.LIGHT_GRAY, MinecartColor.GRAY, DATA_SIZE);
	}

	public SteamLocomotive(Level level, double x, double y, double z, MinecartColor top, MinecartColor bottom) {
		super(STEAM_LOCOMOTIVE.get(), x, y, z, level, top, bottom, DATA_SIZE);
	}

	@Override
	protected @NotNull Item getDropItem() {
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots() + 1);
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		ItemStack locomotive = new ItemStack(ModItems.STEAM_LOCOMOTIVE.get());
		locomotive.getOrCreateTag().putString(AbstractLocomotiveItem.TOP_COLOR_TAG, getTopFilter().getLabel());
		locomotive.getOrCreateTag().putString(AbstractLocomotiveItem.BOTTOM_COLOR_TAG, getBottomFilter().getLabel());
		inventory.setItem(itemHandler.getSlots(), locomotive);
		Containers.dropContents(level, blockPosition(), inventory);

		return Items.AIR;
	}

	@Override
	public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
		updateData();
		return new SteamLocomotiveMenu(id, inventory, this, data);
	}

	@Override
	protected boolean canPush() {
		return steam > MINIMUM_STEAM;
	}

	public boolean canWhistle() {
		if (steam > MINIMUM_STEAM) {
			steam -= 1;
			return true;
		}
		return false;
	}

	@Override
	protected List<Component> getEngineData() {
		return Arrays.asList(
				Component.translatable("text.betterminecarts.data.water").append(
						Component.literal(water + "/" + MAX_WATER)
								.withStyle(ChatFormatting.BLUE)),
				Component.translatable("text.betterminecarts.data.steam").append(
						Component.literal(steam + "/" + MAX_STEAM)
								.withStyle(ChatFormatting.GRAY)),
				Component.translatable("text.betterminecarts.data.bucket").append(
						Component.literal("" + (itemHandler.getStackInSlot(1).getCount() +
										itemHandler.getStackInSlot(2).getCount() +
										itemHandler.getStackInSlot(3).getCount()))
								.withStyle(ChatFormatting.BOLD)),
				Component.translatable("text.betterminecarts.data.coal").append(
						Component.literal("" + itemHandler.getStackInSlot(4).getCount() +
										"/" + itemHandler.getStackInSlot(5).getCount() +
										"/" + itemHandler.getStackInSlot(6).getCount() +
										"/" + itemHandler.getStackInSlot(7).getCount())
								.withStyle(ChatFormatting.BLACK))
		);
	}

	@Override
	public void updateData() {
		data.set(STEAM_KEY, steam);
		data.set(WATER_KEY, water);
		data.set(HEAT_KEY, heat);
		data.set(BURN_KEY, burn);
		data.set(MAX_BURN_KEY, maxBurn);
		super.updateData();
		if (level.isClientSide()) {
			return;
		}
		entityData.set(IS_BURN, burn > 0);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Steam", steam);
		compoundTag.putInt("Water", water);
		compoundTag.putInt("Heat", heat);
		compoundTag.putInt("Burn", burn);
		compoundTag.putInt("MaxBurn", maxBurn);
		compoundTag.put("Inventory", itemHandler.serializeNBT());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		steam = compoundTag.getInt("Steam");
		water = compoundTag.getInt("Water");
		heat = compoundTag.getInt("Heat");
		burn = compoundTag.getInt("Burn");
		maxBurn = compoundTag.getInt("MaxBurn");
		itemHandler.deserializeNBT(compoundTag.getCompound("Inventory"));
		updateData();
	}

	@Override
	public void tick() {
		super.tick();

		if (isBurn() && this.random.nextInt(2) == 0) {
			final Vec3 smokeCoordinates = getSmokeCoordinates();
			this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
					smokeCoordinates.x() + (random.nextFloat() - 0.5) * 0.1,
					smokeCoordinates.y(),
					smokeCoordinates.z() + (random.nextFloat() - 0.5) * 0.1,
					0.0D, 0.2D, 0.0D);
		}

		if (level.isClientSide()) {
			return;
		}

		burn--;
		burn = Math.max(burn, 0);

		if (burn > 0) {
			heat++;
		} else {
			heat--;
		}
		heat = Math.max(Math.min(heat, MAX_HEAT), MINIMUM_HEAT);

		if (clock > 4) {
			clock = 0;
			if (!activeButton.equals(ButtonId.PAUSE)) {
				steam -= speed;
			}
			moveStack(2, 1);
			moveStack(3, 2);
			moveStack(5, 4);
			moveStack(6, 5);
			moveStack(7, 6);
		} else {
			clock++;
		}

		if (heat > 110 && water > 0 && steam < MAX_STEAM - 1) { // create 2 steam
			steam += 2;
			water--;
			heat--;
		}
		if (heat < 100) {
			if (steam > 2) {
				water++;
			}
			steam -= 3;
		}
		steam = Math.max(Math.min(steam, MAX_STEAM), 0);
		water = Math.max(Math.min(water, MAX_WATER), 0);

		if (burn == 0 && !activeButton.equals(ButtonId.STOP) && heat < 210) {
			consumeFuel();
		}

		if (water <= MAX_WATER - 1000) {
			loadWater();
		}

		if (steam < MINIMUM_STEAM && activeButton != ButtonId.PAUSE) {
			xPush = 0;
			zPush = 0;
			activeButton = ButtonId.STOP;
		}

		updateData();
		setChanged();
	}

	private Vec3 getSmokeCoordinates() {
		switch (getMotionDirection()) {
			case NORTH -> {
				return new Vec3(this.getX(), this.getY() + 1.23, this.getZ() - 0.45);
			}
			case SOUTH -> {
				return new Vec3(this.getX(), this.getY() + 1.23, this.getZ() + 0.45);
			}
			case WEST -> {
				return new Vec3(this.getX() - 0.45, this.getY() + 1.23, this.getZ());
			}
			case EAST -> {
				return new Vec3(this.getX() + 0.45, this.getY() + 1.23, this.getZ());
			}
		}
		return new Vec3(this.getX(), this.getY() + 1.23, this.getZ());
	}

	private void consumeFuel() {
		if (itemHandler.getStackInSlot(4).getItem().equals(Items.AIR)) {
			return;
		}
		if (itemHandler.getStackInSlot(4).getItem().equals(Items.LAVA_BUCKET)) {
			if (itemHandler.getStackInSlot(0).isEmpty()) {
				itemHandler.setStackInSlot(0, new ItemStack(Items.BUCKET, 1));
			} else if (itemHandler.getStackInSlot(0).getCount() < 16) {
				itemHandler.getStackInSlot(0).grow(1);
			} else {
				return;
			}
		}
		maxBurn = getBurnTime(itemHandler.getStackInSlot(4), SMELTING);
		burn = maxBurn;
		itemHandler.getStackInSlot(4).shrink(1);
	}

	private void loadWater() {
		if (itemHandler.getStackInSlot(1).getItem().equals(Items.WATER_BUCKET)) {
			if (itemHandler.getStackInSlot(0).isEmpty()) {
				itemHandler.setStackInSlot(0, new ItemStack(Items.BUCKET, 1));
			} else if (itemHandler.getStackInSlot(0).getCount() < 16) {
				itemHandler.getStackInSlot(0).grow(1);
			} else {
				return;
			}
			itemHandler.getStackInSlot(1).shrink(1);
			water += 1000;
		}
	}

	private void moveStack(int s, int t) {
		if (itemHandler.getStackInSlot(s).isEmpty()) {
			return;
		}
		if (itemHandler.getStackInSlot(t).isEmpty()) {
			itemHandler.setStackInSlot(t, new ItemStack(itemHandler.getStackInSlot(s).getItem(), 1));
		} else if (itemHandler.getStackInSlot(t).getCount() < itemHandler.getStackInSlot(t).getMaxStackSize() &&
				itemHandler.getStackInSlot(s).getItem().equals(itemHandler.getStackInSlot(t).getItem())) {
			itemHandler.getStackInSlot(t).grow(1);
		} else {
			return;
		}
		itemHandler.getStackInSlot(s).shrink(1);
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		lazyItemHandler = LazyOptional.of(() -> itemHandler);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		lazyItemHandler.invalidate();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(IS_BURN, false);
	}

	public boolean isBurn() {
		return entityData.get(IS_BURN);
	}

	@Override
	public int getContainerSize() {
		return 8;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			ItemStack itemstack = itemHandler.getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public @NotNull ItemStack getItem(int slot) {
		return itemHandler.getStackInSlot(slot);
	}

	@Override
	public @NotNull ItemStack removeItem(int slot, int count) {
		if (slot == 0) {
			return count > 0 ? itemHandler.getStackInSlot(slot).split(count) : ItemStack.EMPTY;
		}
		return new ItemStack(Blocks.AIR, 0);
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int slot) {
		return getItem(slot);
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack itemStack) {
		itemHandler.setStackInSlot(slot, itemStack);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return false;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			itemHandler.setStackInSlot(i, new ItemStack(Blocks.AIR, 0));
		}
	}

	@Override
	public boolean canPlaceItem(int slot, @NotNull ItemStack itemStack) {
		return itemHandler.isItemValid(slot, itemStack);
	}
}
