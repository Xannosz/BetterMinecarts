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
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.world.item.crafting.RecipeType.SMELTING;
import static net.minecraftforge.common.ForgeHooks.getBurnTime;

@Mixin(MinecartFurnace.class)
public abstract class FurnaceMinecartEntityMixin extends AbstractMinecart implements Linkable {
	@Shadow
	protected abstract boolean hasFuel();

	@Shadow
	private int fuel;
	@Shadow
	public double xPush;
	@Shadow
	public double zPush;
	@Shadow
	@Final
	@Mutable
	private static Ingredient INGREDIENT;

	@Shadow
	public abstract InteractionResult interact(Player player, InteractionHand hand);

	@Unique
	private int altFuel;
	@Unique
	private double altPushX;
	@Unique
	private double altPushZ;
	@Unique
	private static final Ingredient OLD_ACCEPTABLE_FUEL = INGREDIENT;
	@Unique
	private final Set<AbstractMinecart> train = new HashSet<>();
	@Unique
	private ChunkPos prevChunkPos;

	protected FurnaceMinecartEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDD)V", at = @At("TAIL"))
	public void betterminecarts$initPrevChunPos(Level world, double x, double y, double z, CallbackInfo info) {
		prevChunkPos = chunkPosition();
	}

	@Inject(method = "getMaxSpeed", at = @At("RETURN"), cancellable = true)
	public void betterminecarts$increaseSpeed(CallbackInfoReturnable<Double> info) {
		if (hasFuel())
			info.setReturnValue(2D);
		else
			info.setReturnValue(super.getMaxSpeed());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void betterminecarts$loadChunks(CallbackInfo info) {
		if (BetterMinecartsConfig.FURNACE_MINECARTS_LOAD_CHUNKS.get() && level() instanceof ServerLevel server) {
			ChunkPos currentChunkPos = SectionPos.of(this).chunk();

			if (fuel > 0)
				server.getChunkSource().addRegionTicket(TicketType.PLAYER, currentChunkPos, 3, chunkPosition());
			if (!currentChunkPos.equals(prevChunkPos) || fuel <= 0)
				server.getChunkSource().removeRegionTicket(TicketType.PLAYER, prevChunkPos, 3, chunkPosition());

			prevChunkPos = currentChunkPos;
		}
	}

	@Inject(method = "moveAlongTrack", at = @At("TAIL"))
	public void betterminecarts$slowDown(BlockPos pos, BlockState state, CallbackInfo info) {
		if (altFuel <= 0 && fuel > 0) {
			if (state.is(Blocks.POWERED_RAIL) && !state.getValue(PoweredRailBlock.POWERED)) {
				altPushX = xPush;
				altPushZ = zPush;
				altFuel += fuel;
				fuel = 0;
			}
		} else if (!state.is(Blocks.POWERED_RAIL) || (state.is(Blocks.POWERED_RAIL) && state.getValue(PoweredRailBlock.POWERED))) {
			fuel += altFuel;
			altFuel = 0;
			xPush = altPushX;
			zPush = altPushZ;
		}

		AtomicBoolean shouldSlowDown = new AtomicBoolean(MinecartHelper.shouldSlowDown(this, level()));
		train.add(this);

		if (getLinkedChild() != null) {
			Linkable linkable = (Linkable) getLinkedChild();
			train.add(getLinkedChild());

			while ((linkable = (Linkable) linkable.getLinkedChild()) instanceof Linkable && !train.contains(linkable)) {
				train.add(linkable.getLinkedChild());
			}

			train.forEach(child -> shouldSlowDown.set(shouldSlowDown.get() || MinecartHelper.shouldSlowDown(child, level())));
		}


		if (shouldSlowDown.get() && getDeltaMovement().length() > 0.4)
			setDeltaMovement(getDeltaMovement().normalize().scale(0.4));
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	public void betterminecarts$addOtherFuels(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		ItemStack stack = player.getItemInHand(hand);

		if (!stack.isEmpty() && getBurnTime(stack, SMELTING) > 0) {
			int fuelTime = getBurnTime(stack, SMELTING);

			if (!player.isCreative() && fuelTime > 0) {
				if (stack.getItem() instanceof BucketItem)
					player.getInventory().setItem(player.getInventory().selected, BucketItem.getEmptySuccessItem(stack, player));
				else
					stack.shrink(1);
			}

			if (stack.getItem() instanceof BucketItem) {
				SoundEvent soundEvent = SoundEvents.BUCKET_EMPTY_LAVA;
				level().playSound(player, player.blockPosition(), soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
			}

			fuel = (int) Math.min(72000, fuel + (fuelTime * 2.25));
		}

		if (fuel > 0) {
			xPush = getX() - player.getX();
			zPush = getZ() - player.getZ();
		}

		INGREDIENT = Ingredient.of();
		info.setReturnValue(InteractionResult.sidedSuccess(level().isClientSide()));
	}

	@ModifyConstant(method = "interact", constant = @Constant(intValue = 32000))
	public int betterminecarts$maxBurnTime(int maxBurnTime) {
		return 72000;
	}

	@ModifyArg(method = "tick", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"
	))
	public int betterminecarts$removeRandom(int i) {
		return 1;
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	public void betterminecarts$readNbt(CompoundTag nbt, CallbackInfo info) {
		fuel = nbt.getInt("RealFuel");
		altFuel = nbt.getInt("AltFuel");
		altPushX = nbt.getDouble("AltPushX");
		altPushZ = nbt.getDouble("AltPushZ");
		prevChunkPos = new ChunkPos(nbt.getLong("PrevChunkPos"));
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	public void betterminecarts$writeNbt(CompoundTag nbt, CallbackInfo info) {
		if (fuel > Short.MAX_VALUE)
			nbt.putShort("Fuel", Short.MAX_VALUE);

		nbt.putInt("RealFuel", fuel);
		nbt.putInt("AltFuel", altFuel);
		nbt.putDouble("AltPushX", altPushX);
		nbt.putDouble("AltPushZ", altPushZ);
		nbt.putLong("PrevChunkPos", prevChunkPos.toLong());
	}
}
