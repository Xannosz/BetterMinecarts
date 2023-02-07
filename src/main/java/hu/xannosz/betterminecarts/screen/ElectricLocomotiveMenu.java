package hu.xannosz.betterminecarts.screen;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.button.ButtonId;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static hu.xannosz.betterminecarts.entity.ElectricLocomotive.*;

public class ElectricLocomotiveMenu extends AbstractContainerMenu {

	private final ElectricLocomotive entity;
	private final Level level;

	private final ContainerData data;

	@SuppressWarnings("unused")
	public ElectricLocomotiveMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
		this(containerId, inv, null, new SimpleContainerData(DATA_SIZE));
	}

	public ElectricLocomotiveMenu(int containerId, Inventory inv, Entity entity, ContainerData data) {
		super(BetterMinecarts.ELECTRIC_LOCOMOTIVE_MENU.get(), containerId);

		checkContainerSize(inv, 0);
		this.entity = ((ElectricLocomotive) entity);
		level = inv.player.level;
		this.data = data;

		addDataSlots(data);
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		ContainerLevelAccess containerLevelAccess = ContainerLevelAccess.create(level, entity.getOnPos());
		return containerLevelAccess.evaluate((level, blockPos) ->
				player.distanceToSqr((double) blockPos.getX() + 0.5D,
						(double) blockPos.getY() + 0.5D,
						(double) blockPos.getZ() + 0.5D) <= 64.0D, true);
	}

	public int getElectricLocomotiveId() {
		return MinecartHelper.shortsToInt(new short[]{(short) data.get(ID_KEY_1), (short) data.get(ID_KEY_2)});
	}

	public ElectricLocomotive getEntity() {
		return (ElectricLocomotive) level.getEntity(getElectricLocomotiveId());
	}

	public int getPower() {
		return data.get(POWER_KEY);
	}

	public ButtonId getActiveButton() {
		return ButtonId.getButtonFromId(data.get(ACTIVE_BUTTON_KEY));
	}

	public boolean isSignalActive(){
		return MinecartHelper.convertIntToBitArray(data.get(ACTIVE_FUNCTION_KEY),2)[0];
	}

	public boolean isLampOn(){
		return MinecartHelper.convertIntToBitArray(data.get(ACTIVE_FUNCTION_KEY),2)[1];
	}
}
