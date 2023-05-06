package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import hu.xannosz.betterminecarts.entity.SteamLocomotive;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
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
		super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TRANSPORTATION));
		this.isSteam = isSteam;
	}

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
