package hu.xannosz.betterminecarts.item;

import hu.xannosz.betterminecarts.utils.CrowbarMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class Crowbar extends Item {

	public static final String MODE_TAG = "mode";
	public static final String FIRST_CART_ID_TAG = "firstCartId";

	public Crowbar(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
		if (useOnContext.getPlayer() == null) {
			return InteractionResult.FAIL;
		}

		if (useOnContext.getPlayer().level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		final ItemStack itemStack = useOnContext.getItemInHand();
		final CrowbarMode mode = CrowbarMode.getFromLabel(itemStack.getOrCreateTag().getString(MODE_TAG));
		final int firstCartIdTag = itemStack.getOrCreateTag().getInt(FIRST_CART_ID_TAG);

		if (firstCartIdTag == 0) {
			itemStack.getOrCreateTag().putString(MODE_TAG, Objects.requireNonNull(mode.next()).getLabel());
			useOnContext.getPlayer().displayClientMessage(Component.translatable("text.betterminecarts.crowbar.mode." + mode.next().getLabel()).withStyle(ChatFormatting.AQUA), true);
		} else {
			itemStack.getOrCreateTag().putInt(FIRST_CART_ID_TAG, 0);
			useOnContext.getPlayer().displayClientMessage(Component.translatable("text.betterminecarts.crowbar.deleteFirstCartId").withStyle(ChatFormatting.BLUE), true);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		final CrowbarMode mode = CrowbarMode.getFromLabel(itemStack.getOrCreateTag().getString(MODE_TAG));
		final int firstCartIdTag = itemStack.getOrCreateTag().getInt(FIRST_CART_ID_TAG);
		if (mode != null) {
			components.add(Component.translatable("text.betterminecarts.crowbar.mode." + mode.getLabel()).withStyle(ChatFormatting.AQUA));
		}
		if (firstCartIdTag != 0) {
			components.add(Component.translatable("text.betterminecarts.crowbar.firstCartIdTag", firstCartIdTag).withStyle(ChatFormatting.BLUE));
		}
		super.appendHoverText(itemStack, level, components, tooltipFlag);
	}
}
