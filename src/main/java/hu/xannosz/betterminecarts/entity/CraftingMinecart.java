package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.screen.CraftingMinecartMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CraftingMinecart extends AbstractMinecart implements MenuProvider {
	public CraftingMinecart(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public CraftingMinecart(double x, double y, double z, Level level) {
		super(BetterMinecarts.CRAFTING_MINECART.get(), level, x, y, z);
	}

	@Override
	protected @NotNull Item getDropItem() {
		return BetterMinecarts.CRAFTING_MINECART_ITEM.get();
	}

	@Override
	public @NotNull Type getMinecartType() {
		return Type.FURNACE;
	}

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
	@SuppressWarnings("ConstantConditions")
	public @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
		return new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) ->
				new CraftingMinecartMenu(p_52229_, p_52230_, this),
				Component.translatable("container.crafting"))
				.createMenu(id, inventory, player);
	}

	@Override
	public @NotNull BlockState getDisplayBlockState() {
		return Blocks.CRAFTING_TABLE.defaultBlockState();
	}
}