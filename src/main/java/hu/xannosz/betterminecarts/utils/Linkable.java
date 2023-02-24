/*############################################################
This file copied from Cammies-Minecart-Tweaks (https://github.com/CammiePone/Cammies-Minecart-Tweaks) 2022
Modified by Xannosz 2022-2023
############################################################*/
package hu.xannosz.betterminecarts.utils;

import net.minecraft.world.entity.vehicle.AbstractMinecart;

public interface Linkable {
	AbstractMinecart getLinkedParent();

	void setLinkedParent(AbstractMinecart parent);

	AbstractMinecart getLinkedChild();

	void setLinkedChild(AbstractMinecart child);

	void updateChains();

	AbstractMinecart getLinkedParentForRender();
}
