package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.entity.CraftingMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class CraftingMinecartItem extends Item {
	public CraftingMinecartItem() {
		super(new Properties().stacksTo(1));
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

			CraftingMinecart minecart = new CraftingMinecart(d0, d1 + d3, d2, level);
			if (itemStack.has(DataComponents.CUSTOM_NAME)) {
				minecart.setCustomName(itemStack.getHoverName());
			}

			level.addFreshEntity(minecart);
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

				CraftingMinecart minecart = new CraftingMinecart(
						(double) blockpos.getX() + 0.5D,
						(double) blockpos.getY() + 0.0625D + d0,
						(double) blockpos.getZ() + 0.5D,
						level);
				if (itemstack.has(DataComponents.CUSTOM_NAME)) {
					minecart.setCustomName(itemstack.getHoverName());
				}

				level.addFreshEntity(minecart);
				level.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(useOnContext.getPlayer(), level.getBlockState(blockpos.below())));
			}

			itemstack.shrink(1);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}
}
