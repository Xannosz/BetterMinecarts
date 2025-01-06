package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.*;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static hu.xannosz.betterminecarts.component.ModComponentTypes.BOTTOM_COLOR_TAG;
import static hu.xannosz.betterminecarts.component.ModComponentTypes.TOP_COLOR_TAG;

public class AbstractLocomotiveItem extends Item {
	private final LocomotiveType locomotiveType;

	public AbstractLocomotiveItem(LocomotiveType locomotiveType) {
		super(new Properties().stacksTo(1));
		this.locomotiveType = locomotiveType;
		DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
	}

	private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		public @NotNull ItemStack execute(BlockSource blockSource, @NotNull ItemStack itemStack) {
			Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
			Level level = blockSource.level();
			double d0 = blockSource.pos().getX() + (double) direction.getStepX() * 1.125D;
			double d1 = Math.floor(blockSource.pos().getY()) + (double) direction.getStepY();
			double d2 = blockSource.pos().getZ() + (double) direction.getStepZ() * 1.125D;

			MinecartColor topColor = MinecartColor.getFromLabel(itemStack.get(TOP_COLOR_TAG.get()));
			MinecartColor bottomColor = MinecartColor.getFromLabel(itemStack.get(BOTTOM_COLOR_TAG.get()));

			if (topColor == null || bottomColor == null) {
				topColor = ((AbstractLocomotiveItem) itemStack.getItem()).locomotiveType.getTopColor();
				bottomColor = ((AbstractLocomotiveItem) itemStack.getItem()).locomotiveType.getBottomColor();
			}

			BlockPos blockpos = blockSource.pos().relative(direction);
			BlockState blockstate = level.getBlockState(blockpos);
			RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
			double d3;
			if (blockstate.is(BlockTags.RAILS)) {
				if (railshape.isAscending()) {
					d3 = 0.6D;
				} else {
					d3 = 0.1D;
				}
			} else {
				if (!blockstate.isAir() || !level.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
					return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
				}

				BlockState blockState = level.getBlockState(blockpos.below());
				RailShape railShape = blockState.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
				if (direction != Direction.DOWN && railShape.isAscending()) {
					d3 = -0.4D;
				} else {
					d3 = -0.9D;
				}
			}

			AbstractLocomotive abstractLocomotive;
			switch (((AbstractLocomotiveItem) itemStack.getItem()).locomotiveType) {
				case ELECTRIC ->
						abstractLocomotive =new ElectricLocomotive(level, d0, d1 + d3, d2, topColor, bottomColor);
				case STEAM ->
						abstractLocomotive = new SteamLocomotive(level, d0, d1 + d3, d2, topColor, bottomColor) ;
				case DIESEL ->
						abstractLocomotive = new DieselLocomotive(level, d0, d1 + d3, d2, topColor, bottomColor);
				default -> abstractLocomotive = null;
			}

			if (itemStack.has(DataComponents.CUSTOM_NAME)) {
				abstractLocomotive.setCustomName(itemStack.getHoverName());
				abstractLocomotive.setCustomNameVisible(true);
			}

			abstractLocomotive.setStartDirection(direction);

			level.addFreshEntity(abstractLocomotive);
			itemStack.shrink(1);
			return itemStack;
		}

		protected void playSound(BlockSource p_42947_) {
			p_42947_.level().levelEvent(1000, p_42947_.pos(), 0);
		}
	};

	public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
		Level level = useOnContext.getLevel();
		BlockPos blockpos = useOnContext.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);

		MinecartColor topColor = MinecartColor.getFromLabel(useOnContext.getItemInHand().get(TOP_COLOR_TAG.get()));
		MinecartColor bottomColor = MinecartColor.getFromLabel(useOnContext.getItemInHand().get(BOTTOM_COLOR_TAG.get()));

		if (topColor == null || bottomColor == null) {
			topColor = locomotiveType.getTopColor();
			bottomColor = locomotiveType.getBottomColor();
		}

		if (!blockstate.is(BlockTags.RAILS)) {
			return InteractionResult.FAIL;
		} else {
			ItemStack itemstack = useOnContext.getItemInHand();
			if (!level.isClientSide) {
				RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
				double d0 = 0.0D;
				if (railshape.isAscending()) {
					d0 = 0.5D;
				}

				AbstractLocomotive abstractLocomotive;
				switch (locomotiveType) {
					case ELECTRIC ->
							abstractLocomotive = createElectricLocomotive(bottomColor, topColor, level, blockpos, d0);
					case STEAM ->
							abstractLocomotive = createSteamLocomotive(bottomColor, topColor, level, blockpos, d0);
					case DIESEL ->
							abstractLocomotive = createDieselLocomotive(bottomColor, topColor, level, blockpos, d0);
					default -> abstractLocomotive = null;
				}

				if (itemstack.has(DataComponents.CUSTOM_NAME)) {
					abstractLocomotive.setCustomName(itemstack.getHoverName());
					abstractLocomotive.setCustomNameVisible(true);
				}

				abstractLocomotive.setStartDirection(useOnContext.getHorizontalDirection());

				level.addFreshEntity(abstractLocomotive);
				level.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(useOnContext.getPlayer(), level.getBlockState(blockpos.below())));
			}

			itemstack.shrink(1);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack itemStack, @Nullable TooltipContext tooltipContext, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		MinecartColor topColor = MinecartColor.getFromLabel(itemStack.get(TOP_COLOR_TAG.get()));
		MinecartColor bottomColor = MinecartColor.getFromLabel(itemStack.get(BOTTOM_COLOR_TAG.get()));

		if (topColor == null || bottomColor == null) {
			topColor = locomotiveType.getTopColor();
			bottomColor = locomotiveType.getBottomColor();
		}

		components.add(Component.translatable("text.betterminecarts.locomotive.color." + topColor.getLabel()).withStyle(topColor.getLabelColor()));
		components.add(Component.translatable("text.betterminecarts.locomotive.color." + bottomColor.getLabel()).withStyle(bottomColor.getLabelColor()));

		super.appendHoverText(itemStack, tooltipContext, components, tooltipFlag);
	}

	private AbstractLocomotive createElectricLocomotive(MinecartColor bottom, MinecartColor top, Level level, BlockPos blockpos, double d0) {
		return new ElectricLocomotive(level,
				(double) blockpos.getX() + 0.5D,
				(double) blockpos.getY() + 0.0625D + d0,
				(double) blockpos.getZ() + 0.5D, top, bottom);
	}

	private AbstractLocomotive createSteamLocomotive(MinecartColor bottom, MinecartColor top, Level level, BlockPos blockpos, double d0) {
		return new SteamLocomotive(level,
				(double) blockpos.getX() + 0.5D,
				(double) blockpos.getY() + 0.0625D + d0,
				(double) blockpos.getZ() + 0.5D, top, bottom);
	}

	private AbstractLocomotive createDieselLocomotive(MinecartColor bottom, MinecartColor top, Level level, BlockPos blockpos, double d0) {
		return new DieselLocomotive(level,
				(double) blockpos.getX() + 0.5D,
				(double) blockpos.getY() + 0.0625D + d0,
				(double) blockpos.getZ() + 0.5D, top, bottom);
	}

	@Override
	public @NotNull String getDescriptionId() {
		return "item." + BetterMinecarts.MOD_ID + "." + getLocomotiveName();
	}

	public String getLocomotiveName() {
		return locomotiveType.getName();
	}
}
