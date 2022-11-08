package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.button.ButtonUser;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractLocomotive extends AbstractMinecartContainer implements ButtonUser {
	protected AbstractLocomotive(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	protected AbstractLocomotive(EntityType<?> entityType, double x, double y, double z, Level level) {
		super(entityType, x, y, z, level);
	}

	public abstract Vec3 getTopFilter();

	public abstract Vec3 getBottomFilter();
}
