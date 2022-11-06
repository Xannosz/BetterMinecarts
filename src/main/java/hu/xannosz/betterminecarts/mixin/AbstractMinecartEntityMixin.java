package hu.xannosz.betterminecarts.mixin;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.network.SyncChainedMinecartPacket;
import hu.xannosz.betterminecarts.utils.Linkable;
import hu.xannosz.betterminecarts.utils.MinecartHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

	@Shadow
	public abstract double getMaxSpeed();

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
			info.setReturnValue(BetterMinecarts.getConfig().getOtherMinecartSpeed());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void betterminecarts$tick(CallbackInfo info) {
		if (!level.isClientSide()) {
			for (Player player : level.players()) {
				BetterMinecarts.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SyncChainedMinecartPacket(linkedParent, (AbstractMinecart) (Object) this));
			}

			if (getLinkedParent() != null) {
				double distance = getLinkedParent().distanceTo(this) - 1;

				if (distance <= 4) {
					Vec3 direction = getLinkedParent().position().subtract(position()).normalize();

					if (distance > 1) {
						Vec3 parentVelocity = getLinkedParent().getDeltaMovement();

						if (parentVelocity.length() == 0) {
							setDeltaMovement(direction.scale(0.05));
						} else {
							setDeltaMovement(direction.scale(parentVelocity.length()));
							setDeltaMovement(getDeltaMovement().scale(distance));
						}
					} else if (distance < 0.8)
						setDeltaMovement(direction.scale(-0.05));
					else
						setDeltaMovement(Vec3.ZERO);
				} else {
					((Linkable) getLinkedParent()).setLinkedChild(null);
					setLinkedParent(null);
					spawnAtLocation(new ItemStack(Items.CHAIN));
					return;
				}

				if (getLinkedParent().isRemoved())
					setLinkedParent(null);
			} else {
				MinecartHelper.shouldSlowDown((AbstractMinecart) (Object) this, level);
			}

			if (getLinkedChild() != null && getLinkedChild().isRemoved())
				setLinkedChild(null);
		} else {
			if (BetterMinecarts.getConfig().clientTweaks.playerViewIsLocked) {
				Vec3 directionVec = getDeltaMovement().normalize();

				if (getDeltaMovement().length() > BetterMinecarts.getConfig().getOtherMinecartSpeed() * 0.5) {
					float yaw = (float) Mth.wrapDegrees(Math.toDegrees(Math.atan2(directionVec.z(), directionVec.x())) - 90);

					for (Entity passenger : getPassengers()) {
						float wantedYaw = Mth.wrapDegrees(Mth.rotateIfNecessary(passenger.getYRot(), yaw, BetterMinecarts.getConfig().clientTweaks.maxViewAngle) - passenger.getYRot());
						float steps = Math.abs(wantedYaw) / 5F;

						if (wantedYaw >= steps)
							passenger.setYRot(passenger.getYRot() + steps);
						if (wantedYaw <= -steps)
							passenger.setYRot(passenger.getYRot() - steps);
					}
				}
			}
		}
	}

	@Inject(method = "destroy", at = @At("HEAD"))
	public void betterminecarts$dropChain(DamageSource damageSource, CallbackInfo info) {
		if (getLinkedParent() != null || getLinkedChild() != null)
			spawnAtLocation(new ItemStack(Items.CHAIN));
	}

	@Inject(method = "canCollideWith", at = @At("HEAD"))
	public void betterminecarts$damageEntities(Entity other, CallbackInfoReturnable<Boolean> info) {
		if (other instanceof AbstractMinecart minecart && getLinkedParent() != null && !getLinkedParent().equals(minecart))
			minecart.setDeltaMovement(getDeltaMovement());

		float damage = BetterMinecarts.getConfig().serverTweaks.minecartDamage;

		if (damage > 0 && !level.isClientSide() && other instanceof LivingEntity living && living.isAlive() && !living.isPassenger() && getDeltaMovement().length() > 1.5) {
			living.hurt(BetterMinecarts.minecart(this), damage);

			Vec3 knockback = living.getDeltaMovement().add(getDeltaMovement().x() * 0.9, getDeltaMovement().length() * 0.2, getDeltaMovement().z() * 0.9);
			living.setDeltaMovement(knockback);
			living.hasImpulse = true;
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	public void betterminecarts$readNbt(CompoundTag nbt, CallbackInfo info) {
		if (nbt.contains("ParentUuid"))
			parentUuid = nbt.getUUID("ParentUuid");
		if (nbt.contains("ChildUuid"))
			childUuid = nbt.getUUID("ChildUuid");
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	public void betterminecarts$writeNbt(CompoundTag nbt, CallbackInfo info) {
		if (getLinkedParent() != null)
			nbt.putUUID("ParentUuid", getLinkedParent().getUUID());
		if (getLinkedChild() != null)
			nbt.putUUID("ChildUuid", getLinkedChild().getUUID());
	}

	//@Redirect(method = "setCurrentCartSpeedCapOnRail", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(DD)D"))
	private double betterminecarts$uncapSpeed(double garbo, double uncappedSpeed) {
		return uncappedSpeed; //TODO unused
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isShiftKeyDown() && stack.is(Items.CHAIN)) {
			if (level instanceof ServerLevel server) {
				CompoundTag nbt = stack.getOrCreateTag();

				if (nbt.contains("ParentEntity") && !getUUID().equals(nbt.getUUID("ParentEntity"))) {
					if (server.getEntity(nbt.getUUID("ParentEntity")) instanceof AbstractMinecart parent) {
						Linkable linkable = (Linkable) parent;
						Set<Linkable> train = new HashSet<>();
						train.add(linkable);

						while ((linkable = (Linkable) linkable.getLinkedParent()) instanceof Linkable && !train.contains(linkable)) {
							train.add(linkable);
						}

						if (train.contains(this) || ((Linkable) parent).getLinkedChild() != null) {
							player.displayClientMessage(Component.translatable(BetterMinecarts.MOD_ID + ".cant_link_to_engine").withStyle(ChatFormatting.RED), true);
						} else {
							if (getLinkedParent() != null)
								((Linkable) getLinkedParent()).setLinkedChild(null);


							setLinkedParent(parent);
							((Linkable) parent).setLinkedChild((AbstractMinecart) (Object) this);
						}
					} else {
						nbt.remove("ParentEntity");

						if (nbt.isEmpty())
							stack.setTag(null);
					}

					level.playSound(null, getX(), getY(), getZ(), SoundEvents.CHAIN_PLACE, SoundSource.NEUTRAL, 1F, 1F);

					if (!player.isCreative())
						stack.shrink(1);

					nbt.remove("ParentEntity");

					if (nbt.isEmpty())
						stack.setTag(null);
				} else {
					nbt.putUUID("ParentEntity", getUUID());
					level.playSound(null, getX(), getY(), getZ(), SoundEvents.CHAIN_HIT, SoundSource.NEUTRAL, 1F, 1F);
				}
			}

			return InteractionResult.sidedSuccess(true);
		}

		return super.interact(player, hand);
	}

	@Override
	public AbstractMinecart getLinkedParent() {
		if (level instanceof ServerLevel server && linkedParent == null && parentUuid != null && server.getEntity(parentUuid) instanceof AbstractMinecart parent)
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
		if (level instanceof ServerLevel server && linkedChild == null && childUuid != null && server.getEntity(childUuid) instanceof AbstractMinecart child)
			setLinkedChild(child);

		return linkedChild;
	}

	@Override
	public void setLinkedChild(AbstractMinecart child) {
		linkedChild = child;

		if (child == null)
			childUuid = null;
	}
}
