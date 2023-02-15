package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.entity.CraftingMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class CraftingMinecartItem extends Item {
	public CraftingMinecartItem() {
		super(new Properties().tab(CreativeModeTab.TAB_TRANSPORTATION).stacksTo(1));
	}

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
				if (itemstack.hasCustomHoverName()) {
					minecart.setCustomName(itemstack.getHoverName());
					minecart.setCustomNameVisible(true);
				}

				level.addFreshEntity(minecart);
				level.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(useOnContext.getPlayer(), level.getBlockState(blockpos.below())));
			}

			itemstack.shrink(1);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}
}
