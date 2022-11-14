package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.button.ButtonId;
import hu.xannosz.betterminecarts.network.BurnTimePacket;
import hu.xannosz.betterminecarts.screen.SteamLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.item.crafting.RecipeType.SMELTING;
import static net.minecraftforge.common.ForgeHooks.getBurnTime;

@Slf4j
public class SteamLocomotive extends AbstractLocomotive {

	public static final int STEAM_KEY = 3;
	public static final int WATER_KEY = 4;
	public static final int HEAT_KEY = 5;
	public static final int BURN_KEY = 6;
	public static final int MAX_BURN_KEY = 7;
	public static final int DATA_SIZE = 8;

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
				case 0, 1, 2 -> stack.getItem() == Items.WATER_BUCKET;
				case 3, 4, 5, 6 -> getBurnTime(stack, SMELTING) > 0;
				case 7 -> false;
				default -> super.isItemValid(slot, stack);
			};
		}
	};
	@Getter
	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	public SteamLocomotive(EntityType<?> entityType, Level level) {
		super(entityType, level, MinecartColor.GRAY, MinecartColor.DARK_GRAY, DATA_SIZE);
	}

	public SteamLocomotive(Level level, double x, double y, double z) {
		super(BetterMinecarts.STEAM_LOCOMOTIVE.get(), x, y, z, level,
				MinecartColor.GRAY, MinecartColor.DARK_GRAY, DATA_SIZE);
	}

	@Override
	protected @NotNull Item getDropItem() {
		return BetterMinecarts.STEAM_LOCOMOTIVE_ITEM.get();
	}

	@Override
	protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
		updateData();
		return new SteamLocomotiveMenu(id, inventory, this, data);
	}

	@Override
	protected boolean canPush() {
		return steam > MINIMUM_STEAM;
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
		level.players().forEach(player ->
				BetterMinecarts.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
						new BurnTimePacket(burn > 0, getId()))
		);
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
			moveStack(1, 0);
			moveStack(2, 1);
			moveStack(4, 3);
			moveStack(5, 4);
			moveStack(6, 5);
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
	}

	private void consumeFuel() {
		if (itemHandler.getStackInSlot(3).getItem().equals(Items.AIR)) {
			return;
		}
		if (itemHandler.getStackInSlot(3).getItem().equals(Items.LAVA_BUCKET)) {
			if (itemHandler.getStackInSlot(7).isEmpty()) {
				itemHandler.setStackInSlot(7, new ItemStack(Items.BUCKET, 1));
			} else if (itemHandler.getStackInSlot(7).getCount() < 16) {
				itemHandler.getStackInSlot(7).grow(1);
			} else {
				return;
			}
		}
		maxBurn = getBurnTime(itemHandler.getStackInSlot(3), SMELTING);
		burn = maxBurn;
		itemHandler.getStackInSlot(3).shrink(1);
	}

	private void loadWater() {
		if (itemHandler.getStackInSlot(0).getItem().equals(Items.WATER_BUCKET)) {
			if (itemHandler.getStackInSlot(7).isEmpty()) {
				itemHandler.setStackInSlot(7, new ItemStack(Items.BUCKET, 1));
			} else if (itemHandler.getStackInSlot(7).getCount() < 16) {
				itemHandler.getStackInSlot(7).grow(1);
			} else {
				return;
			}
			itemHandler.getStackInSlot(0).shrink(1);
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

	// CLIENT SIDE
	public boolean isBurn() {
		return burn > 0;
	}

	public void setBurn(boolean burn) {
		this.burn = burn ? 20 : 0;
	}
}
