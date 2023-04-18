package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import hu.xannosz.betterminecarts.entity.SteamLocomotive;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
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

public class AbstractLocomotiveItem extends Item {

	public static final String TOP_COLOR_TAG = "topColor";
	public static final String BOTTOM_COLOR_TAG = "bottomColor";

	private final boolean isSteam;

	public AbstractLocomotiveItem(boolean isSteam) {
		super(new Properties().stacksTo(1));
		this.isSteam = isSteam;
		DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
	}

	private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		public @NotNull ItemStack execute(BlockSource blockSource, @NotNull ItemStack itemStack) {
			Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
			Level level = blockSource.getLevel();
			double d0 = blockSource.x() + (double) direction.getStepX() * 1.125D;
			double d1 = Math.floor(blockSource.y()) + (double) direction.getStepY();
			double d2 = blockSource.z() + (double) direction.getStepZ() * 1.125D;

			MinecartColor topColor = MinecartColor.getFromLabel(itemStack.getOrCreateTag().getString(TOP_COLOR_TAG));
			MinecartColor bottomColor = MinecartColor.getFromLabel(itemStack.getOrCreateTag().getString(BOTTOM_COLOR_TAG));

			if (topColor == null || bottomColor == null) {
				if (((AbstractLocomotiveItem) itemStack.getItem()).isSteam) {
					topColor = MinecartColor.LIGHT_GRAY;
					bottomColor = MinecartColor.GRAY;
				} else {
					topColor = MinecartColor.YELLOW;
					bottomColor = MinecartColor.BROWN;
				}
			}

			BlockPos blockpos = blockSource.getPos().relative(direction);
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

			AbstractLocomotive abstractLocomotive = ((AbstractLocomotiveItem) itemStack.getItem()).isSteam ?
					new SteamLocomotive(level, d0, d1 + d3, d2, topColor, bottomColor) :
					new ElectricLocomotive(level, d0, d1 + d3, d2, topColor, bottomColor);
			if (itemStack.hasCustomHoverName()) {
				abstractLocomotive.setCustomName(itemStack.getHoverName());
				abstractLocomotive.setCustomNameVisible(true);
			}

			abstractLocomotive.setStartDirection(direction);

			level.addFreshEntity(abstractLocomotive);
			itemStack.shrink(1);
			return itemStack;
		}

		protected void playSound(BlockSource p_42947_) {
			p_42947_.getLevel().levelEvent(1000, p_42947_.getPos(), 0);
		}
	};

	public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
		Level level = useOnContext.getLevel();
		BlockPos blockpos = useOnContext.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);

		MinecartColor topColor = MinecartColor.getFromLabel(useOnContext.getItemInHand().getOrCreateTag().getString(TOP_COLOR_TAG));
		MinecartColor bottomColor = MinecartColor.getFromLabel(useOnContext.getItemInHand().getOrCreateTag().getString(BOTTOM_COLOR_TAG));

		if (topColor == null || bottomColor == null) {
			if (isSteam) {
				topColor = MinecartColor.LIGHT_GRAY;
				bottomColor = MinecartColor.GRAY;
			} else {
				topColor = MinecartColor.YELLOW;
				bottomColor = MinecartColor.BROWN;
			}
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

				AbstractLocomotive abstractLocomotive = isSteam ?
						createSteamLocomotive(bottomColor, topColor, level, blockpos, d0) :
						createElectricLocomotive(bottomColor, topColor, level, blockpos, d0);
				if (itemstack.hasCustomHoverName()) {
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
	public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		MinecartColor topColor = MinecartColor.getFromLabel(itemStack.getOrCreateTag().getString(TOP_COLOR_TAG));
		MinecartColor bottomColor = MinecartColor.getFromLabel(itemStack.getOrCreateTag().getString(BOTTOM_COLOR_TAG));

		if (topColor == null || bottomColor == null) {
			if (isSteam) {
				topColor = MinecartColor.LIGHT_GRAY;
				bottomColor = MinecartColor.GRAY;
			} else {
				topColor = MinecartColor.YELLOW;
				bottomColor = MinecartColor.BROWN;
			}
		}

		components.add(Component.translatable("text.betterminecarts.locomotive.color." + topColor.getLabel()).withStyle(topColor.getLabelColor()));
		components.add(Component.translatable("text.betterminecarts.locomotive.color." + bottomColor.getLabel()).withStyle(bottomColor.getLabelColor()));

		super.appendHoverText(itemStack, level, components, tooltipFlag);
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

	@Override
	public @NotNull String getDescriptionId() {
		return "item." + BetterMinecarts.MOD_ID + "." + getLocomotiveName();
	}

	public String getLocomotiveName() {
		return (isSteam ? "steam" : "electric") + "_locomotive";
	}
}
