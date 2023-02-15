package hu.xannosz.betterminecarts.screen;

import hu.xannosz.betterminecarts.entity.CraftingMinecart;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CraftingMinecartMenu extends CraftingMenu {

	private final CraftingMinecart entity;
	private final Level level;

	public CraftingMinecartMenu(int id, Inventory inventory, CraftingMinecart entity) {
		super(id, inventory, ContainerLevelAccess.create(inventory.player.level, entity.getOnPos()));
		this.entity = entity;
		level = inventory.player.level;
	}

	public boolean stillValid(@NotNull Player player) {
		ContainerLevelAccess containerLevelAccess = ContainerLevelAccess.create(level, entity.getOnPos());
		return containerLevelAccess.evaluate((level, blockPos) ->
				player.distanceToSqr((double) blockPos.getX() + 0.5D,
						(double) blockPos.getY() + 0.5D,
						(double) blockPos.getZ() + 0.5D) <= 64.0D, true);
	}
}
