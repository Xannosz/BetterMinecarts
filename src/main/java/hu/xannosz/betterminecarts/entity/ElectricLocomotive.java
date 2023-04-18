package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.item.AbstractLocomotiveItem;
import hu.xannosz.betterminecarts.item.ModItems;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveMenu;
import hu.xannosz.betterminecarts.utils.ButtonId;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static hu.xannosz.betterminecarts.entity.ModEntities.ELECTRIC_LOCOMOTIVE;

public class ElectricLocomotive extends AbstractLocomotive {

	public static final int POWER_KEY = 4;
	public static final int MAX_POWER = 20;
	public static final int DATA_SIZE = 5;

	private int power = 0;
	private BlockPos lastBlockPos;

	public ElectricLocomotive(EntityType<?> entityType, Level level) {
		super(entityType, level, MinecartColor.YELLOW, MinecartColor.BROWN, DATA_SIZE);
	}

	public ElectricLocomotive(Level level, double x, double y, double z, MinecartColor top, MinecartColor bottom) {
		super(ELECTRIC_LOCOMOTIVE.get(), x, y, z, level, top, bottom, DATA_SIZE);
	}

	@Override
	protected @NotNull Item getDropItem() {
		SimpleContainer inventory = new SimpleContainer(1);
		ItemStack locomotive = new ItemStack(ModItems.ELECTRIC_LOCOMOTIVE.get());
		locomotive.getOrCreateTag().putString(AbstractLocomotiveItem.TOP_COLOR_TAG, getTopFilter().getLabel());
		locomotive.getOrCreateTag().putString(AbstractLocomotiveItem.BOTTOM_COLOR_TAG, getBottomFilter().getLabel());
		inventory.setItem(0, locomotive);
		Containers.dropContents(level, blockPosition(), inventory);

		return Items.AIR;
	}

	@Override
	public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inv, @NotNull Player player) {
		updateData();
		return new ElectricLocomotiveMenu(containerId, inv, this, data);
	}

	@Override
	protected boolean canPush() {
		return power > 0;
	}

	@Override
	protected List<Component> getEngineData() {
		return List.of(
				Component.translatable("text.betterminecarts.data.energy").append(
						Component.literal(power + "/" + MAX_POWER)
								.withStyle(ChatFormatting.DARK_GREEN)
				));
	}

	@Override
	public void updateData() {
		data.set(POWER_KEY, power);
		super.updateData();
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Power", power);
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		power = compoundTag.getInt("Power");
		updateData();
	}

	@Override
	protected void moveAlongTrack(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		super.moveAlongTrack(blockPos, blockState);
		if (!blockPos.equals(lastBlockPos)) {
			lastBlockPos = blockPos;
			power--;
		}
		if (power < 0) {
			power = 0;
			xPush = 0;
			zPush = 0;
			activeButton = ButtonId.STOP;
		}
		if (blockState.getBlock().equals(Blocks.POWERED_RAIL) && blockState.getValue(PoweredRailBlock.POWERED)) {
			power = MAX_POWER;
		}
	}
}
