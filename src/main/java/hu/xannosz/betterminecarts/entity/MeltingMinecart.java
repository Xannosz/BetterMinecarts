package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.screen.SteamLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static hu.xannosz.betterminecarts.entity.ModEntities.CRAFTING_MINECART;
import static hu.xannosz.betterminecarts.item.ModItems.CROWBAR;
import static hu.xannosz.betterminecarts.utils.MinecartHelper.IS_BURN;
import static net.minecraft.world.item.crafting.RecipeType.SMELTING;
import static net.minecraftforge.common.ForgeHooks.getBurnTime;

public class MeltingMinecart extends AbstractMinecart implements MenuProvider, Container {

	public static final int ID_KEY_1 = 0;
	public static final int ID_KEY_2 = 1;
	public static final int BURN_KEY = 2;
	public static final int MAX_BURN_KEY = 3;
	public static final int COOKING_KEY = 4;
	public static final int MAX_COOKING_KEY = 5;
	public static final int DATA_SIZE = 6;

	private int burn = 0;
	private int maxBurn = 0;
	private int cooking = 0;
	private int maxCooking = 0;

	private final ContainerData data = new SimpleContainerData(DATA_SIZE);

	@Getter
	private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return switch (slot) {
				case 1 -> true;
				case 2 -> getBurnTime(stack, SMELTING) > 0;
				case 0 -> false;
				default -> super.isItemValid(slot, stack);
			};
		}
	};

	@Getter
	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	public MeltingMinecart(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public MeltingMinecart(double x, double y, double z, Level level) {
		super(CRAFTING_MINECART.get(), level, x, y, z);//TODO
	}

	@Override
	protected @NotNull Item getDropItem() {
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		Containers.dropContents(level(), blockPosition(), inventory);

		return Items.FURNACE_MINECART;
	}

	@Override
	public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
		updateData();
		return new SteamLocomotiveMenu(id, inventory, this, data);
	}

	@Override
	public @NotNull Type getMinecartType() {
		return Type.FURNACE;
	}

	@Override
	public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.is(CROWBAR.get())) {
			return super.interact(player, hand);
		}
		if (player.isShiftKeyDown()) {
			return InteractionResult.PASS;
		}
		player.openMenu(this);
		return InteractionResult.SUCCESS;
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide()) {
			return;
		}

		burn--;
		burn = Math.max(burn, 0);


		updateData();
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

	public void updateData() {
		short[] ids = MinecartHelper.intToShorts(this.getId());
		data.set(ID_KEY_1, ids[0]);
		data.set(ID_KEY_2, ids[1]);
		data.set(COOKING_KEY, cooking);
		data.set(MAX_COOKING_KEY, maxCooking);
		data.set(BURN_KEY, burn);
		data.set(MAX_BURN_KEY, maxBurn);
		if (level().isClientSide()) {
			return;
		}
		entityData.set(IS_BURN, burn > 0);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Cooking", cooking);
		compoundTag.putInt("MaxCooking", maxCooking);
		compoundTag.putInt("Burn", burn);
		compoundTag.putInt("MaxBurn", maxBurn);
		compoundTag.put("Inventory", itemHandler.serializeNBT());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		cooking = compoundTag.getInt("Cooking");
		maxCooking = compoundTag.getInt("MaxCooking");
		burn = compoundTag.getInt("Burn");
		maxBurn = compoundTag.getInt("MaxBurn");
		itemHandler.deserializeNBT(compoundTag.getCompound("Inventory"));
		updateData();
	}

	@Override
	public @NotNull BlockState getDisplayBlockState() {
		return Blocks.FURNACE.defaultBlockState();
	}

	@Override
	public int getContainerSize() {
		return 3;
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
