/*############################################################
This file copied from Cammies-Minecart-Tweaks (https://github.com/CammiePone/Cammies-Minecart-Tweaks)

Copyright (C) 2022 Cammie

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to use, copy, modify, and/or merge copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject to the following
restrictions:

 1) The above copyright notice and this permission notice shall be included in all copies or substantial
    portions of the Software.
 2) You include attribution to the copyright holder(s) in public display of any project that uses any
    portion of the Software.
 3) You may not publish or distribute substantial portions of the Software in its compiled or uncompiled
    forms without prior permission from the copyright holder.
 4) The Software does not make up a substantial portion of your own projects.

If more than 2 years have passed without any source code change on the origin repository for the Software
(https://github.com/CammiePone/Cammies-Minecart-Tweaks), then all but the first 2 restrictions are void, and
may be ignored when using, copying, modifying, and/or merging copies of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.

Modified by Xannosz 2022-2023
############################################################*/
package hu.xannosz.betterminecarts.mixin;

import hu.xannosz.betterminecarts.entity.CraftingMinecart;
import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.server.level.ServerLevel;
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
					minecart = AbstractMinecart.createMinecart((ServerLevel) level(), getX(), getY(), getZ(), type, player.getItemInHand(hand), player);
				} else {
					minecart = new CraftingMinecart(getX(), getY(), getZ(), level());
				}
				level().addFreshEntity(minecart);

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

				info.setReturnValue(InteractionResult.sidedSuccess(level().isClientSide()));
			}
		}

		super.interact(player, hand);
	}
}
