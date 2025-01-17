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

import hu.xannosz.betterminecarts.config.BetterMinecartsConfig;
import hu.xannosz.betterminecarts.utils.Linkable;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import hu.xannosz.betterminecarts.utils.TrainUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;

import static hu.xannosz.betterminecarts.item.ModItems.CROWBAR;
import static hu.xannosz.betterminecarts.utils.MinecartHelper.LINKED_PARENT;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements Linkable {
	@Shadow
	public abstract Direction getMotionDirection();

	@Unique
	private AbstractMinecart linkedParent;
	@Unique
	private AbstractMinecart linkedChild;
	@Unique
	private UUID parentUuid;
	@Unique
	private UUID childUuid;
	@Unique
	private boolean isUpdated = false;

	@Shadow
	public abstract float getMaxSpeedAirVertical();

	public AbstractMinecartEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Inject(method = "getMaxSpeed", at = @At("RETURN"), cancellable = true)
	public void betterminecarts$increaseSpeed(CallbackInfoReturnable<Double> info) {
		if (getLinkedParent() != null) {
			try {
				Class<? extends AbstractMinecart> clazz = getLinkedParent().getClass();
				Method retrieveItems = clazz.getDeclaredMethod("getMaxSpeed");
				retrieveItems.setAccessible(true);
				info.setReturnValue((double) retrieveItems.invoke(getLinkedParent()));
			} catch (Exception ex) {
				info.setReturnValue(0D);
			}
		} else
			info.setReturnValue(0.5);
	}

	@Inject(method = "comeOffTrack", at = @At("HEAD"), cancellable = true)
	public void comeOffTrackHack(CallbackInfo info) {
		if (this.onGround()) {
			if (getLinkedParent() != null || getLinkedChild() != null) {
				this.setDeltaMovement(this.getDeltaMovement().scale(0.96D));
			} else {
				this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
			}
			if (new Random().nextInt(100) < 9 && this.getDeltaMovement().length() > 0.04f) {
				hurt(new DamageSource(level().registryAccess()
						.registryOrThrow(Registries.DAMAGE_TYPE)
						.getHolderOrThrow(DamageTypes.GENERIC), this), 1.5f);
			}
		}

		if (getMaxSpeedAirVertical() > 0 && getDeltaMovement().y > getMaxSpeedAirVertical()) {
			if (Math.abs(getDeltaMovement().x) < 0.3f && Math.abs(getDeltaMovement().z) < 0.3f)
				setDeltaMovement(new Vec3(getDeltaMovement().x, 0.15f, getDeltaMovement().z));
			else
				setDeltaMovement(new Vec3(getDeltaMovement().x, getMaxSpeedAirVertical(), getDeltaMovement().z));
		}

		this.move(MoverType.SELF, this.getDeltaMovement());

		info.cancel();
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "tick", at = @At("HEAD"))
	public void betterminecarts$tick(CallbackInfo info) {
		if (!level().isClientSide()) {
			if (getLinkedParent() != null) {
				double distance = getLinkedParent().distanceTo(this) - 1;

				if (distance <= 4) {
					Vec3 direction = getLinkedParent().position().subtract(position()).normalize();
					Vec3 parentVelocity = getLinkedParent().getDeltaMovement();

					if (distance > 1) {
						if (parentVelocity.length() == 0) {
							setDeltaMovement(direction.scale(0.05));
						} else {
							setDeltaMovement(direction.scale(parentVelocity.length()));
							setDeltaMovement(getDeltaMovement().scale(distance));
						}
					} else if (distance < 0.8) {
						if (parentVelocity.length() == 0) {
							setDeltaMovement(direction.scale(-0.05));
						} else {
							setDeltaMovement(direction.scale(parentVelocity.length()));
							setDeltaMovement(getDeltaMovement().scale(-1.5));
						}
					} else {
						setDeltaMovement(getDeltaMovement().scale(0.6));
					}
				} else {
					((Linkable) getLinkedParent()).setLinkedChild(null);
					setLinkedParent(null);
					return;
				}

				if (getLinkedParent().isRemoved()) {
					setLinkedParent(null);
				}
			} else {
				MinecartHelper.shouldSlowDown((AbstractMinecart) (Object) this, level());
			}

			if (getLinkedChild() != null && getLinkedChild().isRemoved()) {
				setLinkedChild(null);
			}
			updateChains();
		}
	}

	@Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
	public void betterminecarts$damageEntities(Entity other, CallbackInfoReturnable<Boolean> info) {
		if (other instanceof AbstractMinecart minecart && getLinkedParent() != null && !getLinkedParent().equals(minecart))
			minecart.setDeltaMovement(getDeltaMovement());

		float damage = BetterMinecartsConfig.MINECART_DAMAGE.get().floatValue();

		if (damage > 0 && !level().isClientSide() && other instanceof LivingEntity living && living.isAlive() && !living.isPassenger() && getDeltaMovement().length() > 0.25) {
			Vec3 knockBack = living.getDeltaMovement().add(getDeltaMovement().x() * 0.9, getDeltaMovement().length() * 0.2, getDeltaMovement().z() * 0.9);
			living.setDeltaMovement(knockBack);
			living.hasImpulse = true;
			info.setReturnValue(false);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	public void betterminecarts$readNbt(CompoundTag nbt, CallbackInfo info) {
		if (nbt.contains("ParentUuid"))
			parentUuid = nbt.getUUID("ParentUuid");
		if (nbt.contains("ChildUuid"))
			childUuid = nbt.getUUID("ChildUuid");
		updateChains();
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	public void betterminecarts$writeNbt(CompoundTag nbt, CallbackInfo info) {
		if (getLinkedParent() != null)
			nbt.putUUID("ParentUuid", getLinkedParent().getUUID());
		if (getLinkedChild() != null)
			nbt.putUUID("ChildUuid", getLinkedChild().getUUID());
	}

	@Inject(method = "defineSynchedData", at = @At("HEAD"))
	protected void defineSynchedDataAdditional(CallbackInfo info) {
		entityData.set(LINKED_PARENT, -1);
	}

	@Override
	public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.is(CROWBAR.get())) {
			if (level() instanceof ServerLevel server) {
				TrainUtil.clickedByCrowbar(stack, this, server);
				updateChains();
			}
			return InteractionResult.SUCCESS;
		}

		return super.interact(player, hand);
	}

	@Override
	public AbstractMinecart getLinkedParent() {
		if (level() instanceof ServerLevel server && linkedParent == null && parentUuid != null && server.getEntity(parentUuid) instanceof AbstractMinecart parent)
			setLinkedParent(parent);

		return linkedParent;
	}

	@Override
	public void setLinkedParent(AbstractMinecart parent) {
		linkedParent = parent;

		if (parent == null)
			parentUuid = null;
	}

	@Override
	public AbstractMinecart getLinkedChild() {
		if (level() instanceof ServerLevel server && linkedChild == null && childUuid != null && server.getEntity(childUuid) instanceof AbstractMinecart child)
			setLinkedChild(child);

		return linkedChild;
	}

	@Override
	public void setLinkedChild(AbstractMinecart child) {
		linkedChild = child;

		if (child == null)
			childUuid = null;
	}

	@Override
	public void updateChains() {
		if (!level().isClientSide()) {
			AbstractMinecart parent = getLinkedParent();
			if (parent == null) {
				entityData.set(LINKED_PARENT, -1);
			} else {
				entityData.set(LINKED_PARENT, linkedParent.getId());
			}
		}
	}

	@Override
	public AbstractMinecart getLinkedParentForRender() {
		int id = entityData.get(LINKED_PARENT);
		if (id == -1) {
			return null;
		} else {
			return (AbstractMinecart) level().getEntity(id);
		}
	}
}
