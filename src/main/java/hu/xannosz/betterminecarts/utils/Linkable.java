package hu.xannosz.betterminecarts.utils;

import net.minecraft.world.entity.vehicle.AbstractMinecart;

public interface Linkable {
	AbstractMinecart getLinkedParent();
	void setLinkedParent(AbstractMinecart parent);

	AbstractMinecart getLinkedChild();
	void setLinkedChild(AbstractMinecart child);
	 void updateChains();
	 boolean isUpdated();
	 void setUpdated();
}
