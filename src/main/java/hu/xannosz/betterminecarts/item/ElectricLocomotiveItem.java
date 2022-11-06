package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class ElectricLocomotiveItem extends Item {

	public ElectricLocomotiveItem(Item.Properties properties) {
		super(properties);
	}

	public @NotNull InteractionResult useOn(UseOnContext p_42943_) {
		Level level = p_42943_.getLevel();
		BlockPos blockpos = p_42943_.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);
		if (!blockstate.is(BlockTags.RAILS)) {
			return InteractionResult.FAIL;
		} else {
			ItemStack itemstack = p_42943_.getItemInHand();
			if (!level.isClientSide) {
				RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
				double d0 = 0.0D;
				if (railshape.isAscending()) {
					d0 = 0.5D;
				}

				AbstractMinecart abstractminecart = new ElectricLocomotive(level,
						(double) blockpos.getX() + 0.5D,
						(double) blockpos.getY() + 0.0625D + d0,
						(double) blockpos.getZ() + 0.5D);
				if (itemstack.hasCustomHoverName()) {
					abstractminecart.setCustomName(itemstack.getHoverName());
				}

				level.addFreshEntity(abstractminecart);
				level.gameEvent(GameEvent.ENTITY_PLACE, blockpos, GameEvent.Context.of(p_42943_.getPlayer(), level.getBlockState(blockpos.below())));
			}

			itemstack.shrink(1);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}
}
