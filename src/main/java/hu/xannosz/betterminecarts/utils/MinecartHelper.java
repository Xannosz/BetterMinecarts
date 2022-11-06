package hu.xannosz.betterminecarts.utils;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import hu.xannosz.betterminecarts.blocks.CrossedRailBlock;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MinecartHelper {

	private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS = Util.make(Maps.newEnumMap(RailShape.class), (p_38135_) -> {
		Vec3i vec3i = Direction.WEST.getNormal();
		Vec3i vec3i1 = Direction.EAST.getNormal();
		Vec3i vec3i2 = Direction.NORTH.getNormal();
		Vec3i vec3i3 = Direction.SOUTH.getNormal();
		Vec3i vec3i4 = vec3i.below();
		Vec3i vec3i5 = vec3i1.below();
		Vec3i vec3i6 = vec3i2.below();
		Vec3i vec3i7 = vec3i3.below();
		p_38135_.put(RailShape.NORTH_SOUTH, Pair.of(vec3i2, vec3i3));
		p_38135_.put(RailShape.EAST_WEST, Pair.of(vec3i, vec3i1));
		p_38135_.put(RailShape.ASCENDING_EAST, Pair.of(vec3i4, vec3i1));
		p_38135_.put(RailShape.ASCENDING_WEST, Pair.of(vec3i, vec3i5));
		p_38135_.put(RailShape.ASCENDING_NORTH, Pair.of(vec3i2, vec3i7));
		p_38135_.put(RailShape.ASCENDING_SOUTH, Pair.of(vec3i6, vec3i3));
		p_38135_.put(RailShape.SOUTH_EAST, Pair.of(vec3i3, vec3i1));
		p_38135_.put(RailShape.SOUTH_WEST, Pair.of(vec3i3, vec3i));
		p_38135_.put(RailShape.NORTH_WEST, Pair.of(vec3i2, vec3i));
		p_38135_.put(RailShape.NORTH_EAST, Pair.of(vec3i2, vec3i1));
	});

	public static boolean shouldSlowDown(AbstractMinecart minecart, Level world) {
		boolean slowEm = false;

		if (minecart != null) {
			int velocity = Mth.ceil(minecart.getDeltaMovement().horizontalDistance());
			Direction direction = Direction.getNearest(minecart.getDeltaMovement().x(), 0, minecart.getDeltaMovement().z());
			BlockPos minecartPos = minecart.blockPosition();
			Vec3i pain = new Vec3i(minecartPos.getX(), 0, minecartPos.getZ());
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			List<Vec3i> poses = new ArrayList<>();

			poses.add(minecartPos);

			for (int i = 0; i < poses.size(); i++) {
				pos.set(poses.get(i));
				int distance = pain.distManhattan(new Vec3i(pos.getX(), 0, pos.getZ()));

				if (distance > velocity)
					break;

				if (world.getBlockState(pos.below()).is(BlockTags.RAILS))
					pos.move(0, -1, 0);

				BlockState state = world.getBlockState(pos);

				if (state.is(BlockTags.RAILS) && state.getBlock() instanceof BaseRailBlock rails) {
					RailShape shape = state.getValue(rails.getShapeProperty());

					if (rails instanceof CrossedRailBlock && minecart.getDeltaMovement().horizontalDistance() > 0) {
						if (shape == RailShape.NORTH_SOUTH && (direction == Direction.EAST || direction == Direction.WEST)) {
							world.setBlock(pos, state.setValue(rails.getShapeProperty(), RailShape.EAST_WEST), 3);
							break;
						}

						if (shape == RailShape.EAST_WEST && (direction == Direction.NORTH || direction == Direction.SOUTH)) {
							world.setBlock(pos, state.setValue(rails.getShapeProperty(), RailShape.NORTH_SOUTH), 3);
							break;
						}
					}

					if ((shape != RailShape.NORTH_SOUTH && shape != RailShape.EAST_WEST)) {
						slowEm = true;
						break;
					}

					Pair<Vec3i, Vec3i> pair = EXITS.get(shape);
					Vec3i first = pair.getFirst().offset(pos);
					Vec3i second = pair.getSecond().offset(pos);

					if (distance < 2) {
						if (!poses.contains(first))
							poses.add(first);
						if (!poses.contains(second))
							poses.add(second);

						continue;
					}

					if ((shape == RailShape.NORTH_SOUTH && direction == Direction.NORTH) || (shape == RailShape.EAST_WEST && direction == Direction.WEST)) {
						if (!poses.contains(first))
							poses.add(first);
					} else {
						if (!poses.contains(second))
							poses.add(second);
					}
				}
			}
		}

		return slowEm;
	}
}
