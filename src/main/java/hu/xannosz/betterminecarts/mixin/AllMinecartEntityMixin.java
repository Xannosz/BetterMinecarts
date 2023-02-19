package hu.xannosz.betterminecarts.mixin;

import hu.xannosz.betterminecarts.entity.CraftingMinecart;
import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {MinecartFurnace.class, Minecart.class, AbstractMinecartContainer.class, MinecartCommandBlock.class}, priority = 0)
public abstract class AllMinecartEntityMixin extends net.minecraft.world.entity.vehicle.AbstractMinecart implements Linkable {
	protected AllMinecartEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void betterminecarts$heckUMojang(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		if (getMinecartType() == net.minecraft.world.entity.vehicle.AbstractMinecart.Type.RIDEABLE) {
			net.minecraft.world.entity.vehicle.AbstractMinecart parent = getLinkedParent();
			net.minecraft.world.entity.vehicle.AbstractMinecart child = getLinkedChild();
			ItemStack stack = player.getItemInHand(hand);
			Item item = stack.getItem();
			net.minecraft.world.entity.vehicle.AbstractMinecart.Type type = net.minecraft.world.entity.vehicle.AbstractMinecart.Type.RIDEABLE;

			if (item == Items.FURNACE)
				type = net.minecraft.world.entity.vehicle.AbstractMinecart.Type.FURNACE;
			if (item == Items.CHEST)
				type = net.minecraft.world.entity.vehicle.AbstractMinecart.Type.CHEST;
			if (item == Items.TNT)
				type = net.minecraft.world.entity.vehicle.AbstractMinecart.Type.TNT;
			if (item == Items.HOPPER)
				type = net.minecraft.world.entity.vehicle.AbstractMinecart.Type.HOPPER;
			if (item == Items.CRAFTING_TABLE)
				type = null;

			if (type != net.minecraft.world.entity.vehicle.AbstractMinecart.Type.RIDEABLE) {
				net.minecraft.world.entity.vehicle.AbstractMinecart minecart;
				if (type != null) {
					minecart = AbstractMinecart.createMinecart(level, getX(), getY(), getZ(), type);
				} else {
					minecart = new CraftingMinecart(getX(), getY(), getZ(), level);
				}
				level.addFreshEntity(minecart);

				if (parent != null) {
					setLinkedParent(null);
					((Linkable) minecart).setLinkedParent(parent);
					((Linkable) parent).setLinkedChild(minecart);
				}
				if (child != null) {
					setLinkedChild(null);
					((Linkable) minecart).setLinkedChild(child);
					((Linkable) child).setLinkedParent(minecart);
				}

				remove(RemovalReason.DISCARDED);

				if (!player.isCreative())
					stack.shrink(1);

				info.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
			}
		}

		super.interact(player, hand);
	}
}
