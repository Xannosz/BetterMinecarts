package hu.xannosz.betterminecarts.utils;

import lombok.experimental.UtilityClass;
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

import static hu.xannosz.betterminecarts.component.ModComponentTypes.FIRST_CART_ID_TAG;
import static hu.xannosz.betterminecarts.component.ModComponentTypes.MODE_TAG;

@UtilityClass
public class TrainUtil {

	public static Linkable getHeadOfTrain(Linkable cart) {
		if (cart.getLinkedParent() == null) {
			return cart;
		} else {
			return getHeadOfTrain((Linkable) cart.getLinkedParent());
		}
	}

	public static void clickedByCrowbar(ItemStack itemStack, Entity minecart, ServerLevel server) {
		final CrowbarMode mode = CrowbarMode.getFromLabel(itemStack.get(MODE_TAG.get()));
		final int firstCartIdTag = itemStack.get(FIRST_CART_ID_TAG.get());

		if (firstCartIdTag == minecart.getId()) {
			return;
		}

		if (mode.equals(CrowbarMode.LABEL)) {
			minecart.setCustomNameVisible(!minecart.isCustomNameVisible());
			itemStack.set(FIRST_CART_ID_TAG.get(), minecart.getId());
			return;
		}

		if (firstCartIdTag == 0) {
			itemStack.set(FIRST_CART_ID_TAG.get(), minecart.getId());
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
					cart.updateChains();
				}

				server.playSound(null, position.getX(), position.getY(), position.getZ(),
						SoundEvents.CHAIN_PLACE, SoundSource.NEUTRAL, 1F, 1F);
			}
		}

		itemStack.set(FIRST_CART_ID_TAG.get(), minecart.getId());
	}
}
