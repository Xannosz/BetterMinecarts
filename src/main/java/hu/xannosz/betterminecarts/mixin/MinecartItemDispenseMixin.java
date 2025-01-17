package hu.xannosz.betterminecarts.mixin;

import hu.xannosz.betterminecarts.utils.Colorable;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.core.BlockSource;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static hu.xannosz.betterminecarts.item.AbstractLocomotiveItem.BOTTOM_COLOR_TAG;

@Mixin(targets = "net.minecraft.world.item.MinecartItem$1")
public class MinecartItemDispenseMixin {
	@Unique
	private String colorName;

	@Inject(method = "execute", at = @At("HEAD"))
	public void catchItemStack2(BlockSource p_42949_, ItemStack itemstack, CallbackInfoReturnable info) {
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
}
