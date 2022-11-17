package hu.xannosz.betterminecarts.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static hu.xannosz.betterminecarts.item.Crowbar.FIRST_CART_ID_TAG;
import static hu.xannosz.betterminecarts.item.Crowbar.MODE_TAG;

@Slf4j
@UtilityClass
public class TrainUtil {
	public static void clickedByCrowbar(ItemStack itemStack, Entity minecart, ServerLevel server) {
		final CrowbarMode mode = CrowbarMode.getFromLabel(itemStack.getOrCreateTag().getString(MODE_TAG));
		final int firstCartIdTag = itemStack.getOrCreateTag().getInt(FIRST_CART_ID_TAG);

		if (firstCartIdTag == minecart.getId()) {
			return;
		}

		if (firstCartIdTag == 0) {
			itemStack.getOrCreateTag().putInt(FIRST_CART_ID_TAG, minecart.getId());
			return;
		}

		final Linkable parent = (Linkable) server.getEntity(firstCartIdTag);
		final Linkable child = (Linkable) minecart;
		final BlockPos position = minecart.getOnPos();
		if (parent == null) {
			return;
		}

		switch (mode) {
			case CONNECT -> {
				if (parent.getLinkedChild() == null && child.getLinkedParent() == null) {
					parent.setLinkedChild((AbstractMinecart) child);
					child.setLinkedParent((AbstractMinecart) parent);
					server.playSound(null, position.getX(), position.getY(), position.getZ(),
							SoundEvents.CHAIN_PLACE, SoundSource.NEUTRAL, 1F, 1F);
				}
			}
			case DISCONNECT -> {
				if (child.equals(parent.getLinkedChild()) && parent.equals(child.getLinkedParent())) {
					parent.setLinkedChild(null);
					child.setLinkedParent(null);
					server.playSound(null, position.getX(), position.getY(), position.getZ(),
							SoundEvents.CHAIN_HIT, SoundSource.NEUTRAL, 1F, 1F);
				}
			}
			case REVERT -> {
				final Set<Linkable> train = new HashSet<>();
				final Deque<Linkable> carts = new LinkedList<>();
				carts.add(child);

				while (!carts.isEmpty()) {
					Linkable cart = carts.pop();
					train.add(cart);
					if (cart.getLinkedChild() != null && !train.contains((Linkable) cart.getLinkedChild())) {
						carts.add((Linkable) cart.getLinkedChild());
					}
					if (cart.getLinkedParent() != null && !train.contains((Linkable) cart.getLinkedParent())) {
						carts.add((Linkable) cart.getLinkedParent());
					}
				}

				for (Linkable cart : train) {
					final AbstractMinecart p = cart.getLinkedParent();
					final AbstractMinecart c = cart.getLinkedChild();
					cart.setLinkedParent(c);
					cart.setLinkedChild(p);
				}

				server.playSound(null, position.getX(), position.getY(), position.getZ(),
						SoundEvents.CHAIN_PLACE, SoundSource.NEUTRAL, 1F, 1F);
			}
		}

		itemStack.getOrCreateTag().putInt(FIRST_CART_ID_TAG, minecart.getId());
	}
}
