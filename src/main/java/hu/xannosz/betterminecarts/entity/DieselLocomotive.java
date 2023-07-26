package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.item.AbstractLocomotiveItem;
import hu.xannosz.betterminecarts.item.ModItems;
import hu.xannosz.betterminecarts.screen.DieselLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.ButtonId;
import hu.xannosz.betterminecarts.utils.FuelHolder;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static hu.xannosz.betterminecarts.entity.ModEntities.DIESEL_LOCOMOTIVE;

public class DieselLocomotive extends AbstractLocomotive implements Container {

	public static final int FUEL_AMOUNT_KEY = 4;
	public static final int FUEL_COLOR_KEY_1 = 5;
	public static final int FUEL_COLOR_KEY_2 = 6;
	public static final int POWER_KEY = 7;
	public static final int CLOCK_KEY = 8;
	public static final int MAX_FUEL = 2000;
	public static final int MAX_POWER = 50;
	public static final int DATA_SIZE = 9;

	private int power = 0;
	private Map<CompoundTag, Integer> fuels = new HashMap<>();
	@Getter
	private int clock = 0;

	@Getter
	private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return switch (slot) {
				case 1, 2, 3 -> FuelHolder.getINSTANCE().isFuel(stack);
				case 0 -> false;
				default -> super.isItemValid(slot, stack);
			};
		}
	};

	@Getter
	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	public DieselLocomotive(EntityType<?> entityType, Level level) {
		super(entityType, level, LocomotiveType.DIESEL.getTopColor(), LocomotiveType.DIESEL.getBottomColor(), DATA_SIZE);
	}

	public DieselLocomotive(Level level, double x, double y, double z, MinecartColor topFilter, MinecartColor bottomFilter) {
		super(DIESEL_LOCOMOTIVE.get(), x, y, z, level, topFilter, bottomFilter, DATA_SIZE);
	}

	@Override
	protected @NotNull Item getDropItem() {
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots() + 1);
		for (int i = 0; i < itemHandler.getSlots(); i++) {
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		ItemStack locomotive = new ItemStack(ModItems.DIESEL_LOCOMOTIVE.get());
		locomotive.getOrCreateTag().putString(AbstractLocomotiveItem.TOP_COLOR_TAG, getTopFilter().getLabel());
		locomotive.getOrCreateTag().putString(AbstractLocomotiveItem.BOTTOM_COLOR_TAG, getBottomFilter().getLabel());
		if (hasCustomName()) {
			locomotive.setHoverName(getCustomName());
		}
		inventory.setItem(itemHandler.getSlots(), locomotive);
		Containers.dropContents(level(), blockPosition(), inventory);

		return Items.AIR;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
		updateData();
		return new DieselLocomotiveMenu(id, inventory, this, data);
	}

	@Override
	protected boolean canPush() {
		return power > 0;
	}

	@Override
	protected List<Component> getEngineData() {
		return Arrays.asList(
				Component.translatable("text.betterminecarts.data.fuel").append(
						Component.literal(getFuelAmount() + "/" + MAX_FUEL)
								.withStyle(ChatFormatting.GOLD)),
				Component.translatable("text.betterminecarts.data.power").append(
						Component.literal(power + "/" + MAX_POWER)
								.withStyle(ChatFormatting.GRAY)),
				Component.translatable("text.betterminecarts.data.fuel").append(
						Component.literal("" + (itemHandler.getStackInSlot(1).getCount() +
										itemHandler.getStackInSlot(2).getCount() +
										itemHandler.getStackInSlot(3).getCount()))
								.withStyle(ChatFormatting.BOLD))
		);
	}

	@Override
	public void updateData() {
		data.set(FUEL_AMOUNT_KEY, getFuelAmount());
		short[] colors = MinecartHelper.intToShorts(getFuelColor());
		data.set(FUEL_COLOR_KEY_1, colors[0]);
		data.set(FUEL_COLOR_KEY_2, colors[1]);
		data.set(POWER_KEY, power);
		data.set(CLOCK_KEY, clock);
		super.updateData();
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Power", power);
		ListTag nbtTagList = new ListTag();
		for (var entry : fuels.entrySet()) {
			entry.getKey().putInt("Amount", entry.getValue());
			nbtTagList.add(entry.getKey());
		}
		compoundTag.put("FuelItems", nbtTagList);
		compoundTag.put("Inventory", itemHandler.serializeNBT());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		power = compoundTag.getInt("Power");
		fuels = new HashMap<>();
		ListTag tagList = compoundTag.getList("FuelItems", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int amount = itemTags.getInt("Amount");
			fuels.put(itemTags, amount);
		}
		itemHandler.deserializeNBT(compoundTag.getCompound("Inventory"));
		updateData();
	}

	@Override
	public void tick() {
		super.tick();

		//smoke

		if (level().isClientSide()) {
			return;
		}

		if (activeButton != ButtonId.STOP) {
			clock++;
			if (activeButton == ButtonId.F_FORWARD) {
				clock++;
			}
			if (activeButton == ButtonId.FF_FORWARD) {
				clock += 2;
			}
			if (clock >= 24) {
				clock = 0;
			}
		}

		if (activeButton != ButtonId.STOP) {
			power--;
			if (activeButton == ButtonId.F_FORWARD) {
				power--;
			}
			if (activeButton == ButtonId.FF_FORWARD) {
				power -= 2;
			}
			if (power < 0) {
				power = 0;
			}
		}

		if (power == 0) {
			xPush = 0;
			zPush = 0;
			activeButton = ButtonId.STOP;
		}

		List<CompoundTag> keys = new ArrayList<>(fuels.keySet());
		if (keys.size() > 0) {
			CompoundTag key = keys.get(new Random().nextInt(keys.size()));
			for (; ; ) {
				int energy = FuelHolder.getINSTANCE().getFuelPower(ItemStack.of(key));
				if (power + energy <= MAX_POWER) {
					power += energy;
					fuels.put(key, getFromFuels(key) - 1);
				} else {
					break;
				}
				if (getFromFuels(key) <= 0) {
					fuels.remove(key);
					break;
				}
			}
		}

		int fuelAmount = FuelHolder.getINSTANCE().getFuelAmount(itemHandler.getStackInSlot(1));
		Item leftover = FuelHolder.getINSTANCE().getLeftover(itemHandler.getStackInSlot(1));
		if (getFuelAmount() + fuelAmount <= MAX_FUEL && fuels.size() < 11 &&
				(itemHandler.getStackInSlot(0).isEmpty() || (itemHandler.getStackInSlot(0).getItem().equals(leftover) &&
						itemHandler.getStackInSlot(0).getCount() < itemHandler.getStackInSlot(0).getMaxStackSize()))) {
			if (!fuels.containsKey(getFuelKey(itemHandler.getStackInSlot(1)))) {
				fuels.put(getFuelKey(itemHandler.getStackInSlot(1)), 0);
			}
			fuels.put(getFuelKey(itemHandler.getStackInSlot(1)), getFromFuels(getFuelKey(itemHandler.getStackInSlot(1))) + fuelAmount);
			itemHandler.getStackInSlot(1).shrink(1);
			if (itemHandler.getStackInSlot(0).isEmpty()) {
				itemHandler.setStackInSlot(0, new ItemStack(leftover, 1));
			} else {
				itemHandler.getStackInSlot(0).grow(1);
			}
		}

		moveStack(2, 1);
		moveStack(3, 2);

		updateData();
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

	private int getFuelAmount() {
		int result = 0;
		if (fuels != null) {
			for (int f : fuels.values()) {
				result += f;
			}
		}
		return result;
	}

	private int getFuelColor() {
		if (fuels == null) {
			return 0xFFFFFFFF;
		}

		double maxFuel = getFuelAmount();
		double a = 0;
		double r = 0;
		double g = 0;
		double b = 0;

		for (CompoundTag stack : fuels.keySet()) {
			int color = FuelHolder.getINSTANCE().getFuelColor(ItemStack.of(stack));
			a += (color >> 24 & 0xff) * getFromFuels(stack) / maxFuel;
			r += ((color & 0xff0000) >> 16) * getFromFuels(stack) / maxFuel;
			g += ((color & 0xff00) >> 8) * getFromFuels(stack) / maxFuel;
			b += (color & 0xff) * getFromFuels(stack) / maxFuel;
		}

		return ((int) a) << 24 | ((int) r) << 16 | ((int) g) << 8 | ((int) b);
	}

	@Override
	public int getContainerSize() {
		return 4;
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

	private CompoundTag getFuelKey(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		CompoundTag itemTag = new CompoundTag();
		itemStack.save(itemTag);
		return itemTag;
	}

	private int getFromFuels(CompoundTag tag) {
		Integer num = fuels.get(tag);
		if (num == null) {
			return 0;
		}
		return num;
	}
}
