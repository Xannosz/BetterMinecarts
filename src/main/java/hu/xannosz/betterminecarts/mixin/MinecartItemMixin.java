package hu.xannosz.betterminecarts.mixin;

import hu.xannosz.betterminecarts.utils.Colorable;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.core.BlockSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static hu.xannosz.betterminecarts.item.AbstractLocomotiveItem.BOTTOM_COLOR_TAG;

@Mixin(MinecartItem.class)
public abstract class MinecartItemMixin extends Item {
	public MinecartItemMixin(Item.Properties p_42939_) {
		super(p_42939_);
	}

	@Unique
	private String colorName;

	@ModifyVariable(
			method = "useOn",
			at = @At(value = "STORE"),
			ordinal = 0
	)
	public ItemStack catchItemStack1(ItemStack itemstack) {
		if (itemstack.getOrCreateTag().contains(BOTTOM_COLOR_TAG)) {
			colorName = itemstack.getOrCreateTag().getString(BOTTOM_COLOR_TAG);
		} else {
			colorName = MinecartColor.LIGHT_GRAY.getLabel();
		}
		return itemstack;
	}

	@ModifyVariable(
			method = "useOn",
			at = @At(value = "STORE"),
			ordinal = 0
	)
	public AbstractMinecart modifyMinecart1(AbstractMinecart abstractMinecart) {
		((Colorable) abstractMinecart).setColor(colorName);
		return abstractMinecart;
	}

	@Inject(method = "execute", at = @At("HEAD"))
	public void catchItemStack2(BlockSource p_42949_, ItemStack itemstack, CallbackInfo info) {
		if (itemstack.getOrCreateTag().contains(BOTTOM_COLOR_TAG)) {
			colorName = itemstack.getOrCreateTag().getString(BOTTOM_COLOR_TAG);
		} else {
			colorName = MinecartColor.LIGHT_GRAY.getLabel();
		}
	}

	@ModifyVariable(
			method = "execute",
			at = @At(value = "STORE"),
			ordinal = 0
	)
	public AbstractMinecart modifyMinecart2(AbstractMinecart abstractMinecart) {
		((Colorable) abstractMinecart).setColor(colorName);
		return abstractMinecart;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
		MinecartColor bottomColor = MinecartColor.getFromLabel(itemStack.getOrCreateTag().getString(BOTTOM_COLOR_TAG));

		if (bottomColor == null) {
			bottomColor = MinecartColor.LIGHT_GRAY;
		}

		components.add(Component.translatable("text.betterminecarts.locomotive.color." + bottomColor.getLabel()).withStyle(bottomColor.getLabelColor()));

		super.appendHoverText(itemStack, level, components, tooltipFlag);
	}
}
