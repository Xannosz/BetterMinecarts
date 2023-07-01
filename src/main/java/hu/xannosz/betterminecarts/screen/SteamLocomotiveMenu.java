package hu.xannosz.betterminecarts.screen;

import hu.xannosz.betterminecarts.entity.SteamLocomotive;
import hu.xannosz.betterminecarts.utils.ButtonId;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static hu.xannosz.betterminecarts.entity.AbstractLocomotive.*;
import static hu.xannosz.betterminecarts.entity.SteamLocomotive.*;
import static hu.xannosz.betterminecarts.screen.ModMenus.STEAM_LOCOMOTIVE_MENU;

public class SteamLocomotiveMenu extends AbstractContainerMenu {

	private static final int PLAYER_INVENTORY_HEIGHT = 96;

	private final SteamLocomotive entity;
	private final Level level;

	private SlotItemHandler out;

	private final ContainerData data;

	private List<ItemStack> itemStacks; //byPass menu creation

	@SuppressWarnings("unused")
	public SteamLocomotiveMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
		this(containerId, inv, null, new SimpleContainerData(DATA_SIZE));
	}

	public SteamLocomotiveMenu(int containerId, Inventory inv, Entity entity, ContainerData data) {
		super(STEAM_LOCOMOTIVE_MENU.get(), containerId);

		checkContainerSize(inv, 8);
		this.entity = ((SteamLocomotive) entity);
		level = inv.player.level();
		this.data = data;

		addPlayerInventory(inv);
		addPlayerHotBar(inv);

		addDataSlots(data);

		if (this.entity != null) {
			createSlotsInternal(this.entity);
		}
	}

	public void createSlots() {
		// CLIENT SIDE
		if (out == null && getEntity() != null) {
			createSlotsInternal(getEntity());
		}
		if (itemStacks != null && out != null) {
			initializeContents(1, itemStacks, new ItemStack(Blocks.AIR, 1));
			itemStacks = null;
		}
	}

	@Override
	public void initializeContents(int stateId, List<ItemStack> itemStacks, @NotNull ItemStack carried) {
		if (itemStacks.size() > slots.size()) {
			this.itemStacks = itemStacks;
			return;
		}
		super.initializeContents(stateId, itemStacks, carried);
	}

	private void createSlotsInternal(SteamLocomotive steamLocomotive) {
		ItemStackHandler handler = steamLocomotive.getItemHandler();
		SlotItemHandler waterIn1 = new SlotItemHandler(handler, 1, 71, 29);
		SlotItemHandler waterIn2 = new SlotItemHandler(handler, 2, 81, 9);
		SlotItemHandler waterIn3 = new SlotItemHandler(handler, 3, 61, 9);
		SlotItemHandler fuelIn1 = new SlotItemHandler(handler, 4, 130, 29);
		SlotItemHandler fuelIn2 = new SlotItemHandler(handler, 5, 130, 9);
		SlotItemHandler fuelIn3 = new SlotItemHandler(handler, 6, 150, 9);
		SlotItemHandler fuelIn4 = new SlotItemHandler(handler, 7, 110, 9);
		out = new SlotItemHandler(handler, 0, 71, 58);
		this.addSlot(waterIn1);
		this.addSlot(waterIn2);
		this.addSlot(waterIn3);
		this.addSlot(fuelIn1);
		this.addSlot(fuelIn2);
		this.addSlot(fuelIn3);
		this.addSlot(fuelIn4);
		this.addSlot(out);
	}

	// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
	// must assign a slot number to each of the slots used by the GUI.
	// For this container, we can see both the tile inventory's slots and the player inventory slots and the hotBar.
	// Each time we add a Slot to the container, it automatically increases the slotIndex, which means
	//  0 - 8 = hotBar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
	//  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
	//  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
	private static final int HOT_BAR_SLOT_COUNT = 9;
	private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
	private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	private static final int VANILLA_SLOT_COUNT = HOT_BAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
	private static final int VANILLA_FIRST_SLOT_INDEX = 0;
	private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

	// THIS YOU HAVE TO DEFINE!
	private static final int TE_INVENTORY_SLOT_COUNT = 8;  // must be the number of slots you have!

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
		Slot sourceSlot = slots.get(index);
		if (!sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack copyOfSourceStack = sourceStack.copy();

		// Check if the slot clicked is one of the vanilla container slots
		if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
			// This is a vanilla container slot so merge the stack into the tile inventory
			if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
					+ TE_INVENTORY_SLOT_COUNT, false)) {
				return ItemStack.EMPTY;  // EMPTY_ITEM
			}
		} else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
			// This is a TE slot so merge the stack into the players inventory
			if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
				return ItemStack.EMPTY;
			}
		} else {
			return ItemStack.EMPTY;
		}
		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.getCount() == 0) {
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}
		sourceSlot.onTake(playerIn, sourceStack);
		return copyOfSourceStack;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		ContainerLevelAccess containerLevelAccess = ContainerLevelAccess.create(level, entity.getOnPos());
		return containerLevelAccess.evaluate((level, blockPos) ->
				player.distanceToSqr((double) blockPos.getX() + 0.5D,
						(double) blockPos.getY() + 0.5D,
						(double) blockPos.getZ() + 0.5D) <= 64.0D, true);
	}

	private void addPlayerInventory(Inventory playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, PLAYER_INVENTORY_HEIGHT + i * 18));
			}
		}
	}

	private void addPlayerHotBar(Inventory playerInventory) {
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, PLAYER_INVENTORY_HEIGHT + 58));
		}
	}

	public int getSteamLocomotiveId() {
		return MinecartHelper.shortsToInt(new short[]{(short) data.get(ID_KEY_1), (short) data.get(ID_KEY_2)});
	}

	public SteamLocomotive getEntity() {
		return (SteamLocomotive) level.getEntity(getSteamLocomotiveId());
	}

	public ButtonId getActiveButton() {
		return ButtonId.getButtonFromId(data.get(ACTIVE_BUTTON_KEY));
	}

	public boolean isSignalActive() {
		return MinecartHelper.convertIntToBitArray(data.get(ACTIVE_FUNCTION_KEY), 2)[0];
	}

	public boolean isLampOn() {
		return MinecartHelper.convertIntToBitArray(data.get(ACTIVE_FUNCTION_KEY), 2)[1];
	}

	public int getSteam() {
		return data.get(STEAM_KEY);
	}

	public int getWater() {
		return data.get(WATER_KEY);
	}

	public int getHeat() {
		return data.get(HEAT_KEY);
	}

	public int getBurn() {
		return data.get(BURN_KEY);
	}

	public int getMaxBurn() {
		return data.get(MAX_BURN_KEY);
	}
}
