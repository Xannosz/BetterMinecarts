package hu.xannosz.betterminecarts.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {MinecartChest.class}, priority = 0)
public abstract class ChestMinecartMixin extends net.minecraft.world.entity.vehicle.AbstractMinecart {
	protected ChestMinecartMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void betterminecarts$heckUMojang(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		if (super.interact(player, hand).equals(InteractionResult.SUCCESS)) {
			info.setReturnValue(InteractionResult.SUCCESS);
		}
	}
}
