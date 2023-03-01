package hu.xannosz.betterminecarts.datagen;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.item.AbstractLocomotiveItem;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static hu.xannosz.betterminecarts.item.ModItems.LOCOMOTIVE_ITEMS;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
	public ModRecipeProvider(DataGenerator dataGenerator) {
		super(dataGenerator.getPackOutput());
	}

	@Override
	protected void m_245200_(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer) {
		createRecipes(finishedRecipeConsumer, MinecartColor.LIGHT_GRAY, MinecartColor.GRAY, true);
		createRecipes(finishedRecipeConsumer, MinecartColor.YELLOW, MinecartColor.BROWN, false);
	}

	private void createRecipes(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer, MinecartColor top,
							   MinecartColor bottom, boolean isSteam) {
		for (MinecartColor additional1 : MinecartColor.values()) {
			topColorize(finishedRecipeConsumer, bottom, top, isSteam, additional1);
			bottomColorize(finishedRecipeConsumer, bottom, top, isSteam, additional1);
			for (MinecartColor additional2 : MinecartColor.values()) {
				bothColorize(finishedRecipeConsumer, bottom, top, isSteam, additional1, additional2);
				deColorize(finishedRecipeConsumer, bottom, top, isSteam, additional1, additional2);
			}
		}
	}

	private void bothColorize(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer,
							  MinecartColor sourceBottom, MinecartColor sourceTop, boolean isSteam,
							  MinecartColor targetBottom, MinecartColor targetTop) {
		if (sourceTop.equals(targetTop) || sourceBottom.equals(targetBottom)) {
			return;
		}
		final String sourceId = BetterMinecarts.generateNameFromData(sourceTop, sourceBottom, isSteam);
		final String targetId = BetterMinecarts.generateNameFromData(targetTop, targetBottom, isSteam);
		final AbstractLocomotiveItem source = LOCOMOTIVE_ITEMS.get(sourceId).get();
		final AbstractLocomotiveItem target = LOCOMOTIVE_ITEMS.get(targetId).get();
		ShapedRecipeBuilder.m_245327_(RecipeCategory.TRANSPORTATION,target)
				.define('T', targetTop.getDye())
				.define('L', source)
				.define('B', targetBottom.getDye())
				.pattern("T")
				.pattern("L")
				.pattern("B")
				.unlockedBy("never", inventoryTrigger(getItemPredicate()))
				.save(finishedRecipeConsumer, new ResourceLocation(BetterMinecarts.MOD_ID,
						sourceId + "_" + targetId + "_both"));
	}

	private void topColorize(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer,
							 MinecartColor sourceBottom, MinecartColor sourceTop, boolean isSteam,
							 MinecartColor targetTop) {
		if (sourceTop.equals(targetTop)) {
			return;
		}
		final String sourceId = BetterMinecarts.generateNameFromData(sourceTop, sourceBottom, isSteam);
		final String targetId = BetterMinecarts.generateNameFromData(targetTop, sourceBottom, isSteam);
		final AbstractLocomotiveItem source = LOCOMOTIVE_ITEMS.get(sourceId).get();
		final AbstractLocomotiveItem target = LOCOMOTIVE_ITEMS.get(targetId).get();
		ShapedRecipeBuilder.m_245327_(RecipeCategory.TRANSPORTATION,target)
				.define('T', targetTop.getDye())
				.define('L', source)
				.pattern("T")
				.pattern("L")
				.unlockedBy("never", inventoryTrigger(getItemPredicate()))
				.save(finishedRecipeConsumer, new ResourceLocation(BetterMinecarts.MOD_ID,
						sourceId + "_" + targetId + "_top"));
	}

	private void bottomColorize(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer,
								MinecartColor sourceBottom, MinecartColor sourceTop, boolean isSteam,
								MinecartColor targetBottom) {
		if (sourceBottom.equals(targetBottom)) {
			return;
		}
		final String sourceId = BetterMinecarts.generateNameFromData(sourceTop, sourceBottom, isSteam);
		final String targetId = BetterMinecarts.generateNameFromData(sourceTop, targetBottom, isSteam);
		final AbstractLocomotiveItem source = LOCOMOTIVE_ITEMS.get(sourceId).get();
		final AbstractLocomotiveItem target = LOCOMOTIVE_ITEMS.get(targetId).get();
		ShapedRecipeBuilder.m_245327_(RecipeCategory.TRANSPORTATION,target)
				.define('L', source)
				.define('B', targetBottom.getDye())
				.pattern("L")
				.pattern("B")
				.unlockedBy("never", inventoryTrigger(getItemPredicate()))
				.save(finishedRecipeConsumer, new ResourceLocation(BetterMinecarts.MOD_ID,
						sourceId + "_" + targetId + "_bottom"));
	}

	private void deColorize(@NotNull Consumer<FinishedRecipe> finishedRecipeConsumer,
							MinecartColor sourceBottom, MinecartColor sourceTop, boolean isSteam,
							MinecartColor targetBottom, MinecartColor targetTop) {
		if (sourceTop.equals(targetTop) && sourceBottom.equals(targetBottom)) {
			return;
		}
		final String sourceId = BetterMinecarts.generateNameFromData(sourceTop, sourceBottom, isSteam);
		final String targetId = BetterMinecarts.generateNameFromData(targetTop, targetBottom, isSteam);
		final AbstractLocomotiveItem source = LOCOMOTIVE_ITEMS.get(sourceId).get();
		final AbstractLocomotiveItem target = LOCOMOTIVE_ITEMS.get(targetId).get();
		ShapelessRecipeBuilder.m_245498_(RecipeCategory.TRANSPORTATION,source).requires(target)
				.unlockedBy("never", inventoryTrigger(getItemPredicate()))
				.save(finishedRecipeConsumer, new ResourceLocation(BetterMinecarts.MOD_ID,
						sourceId + "_" + targetId + "_revert"));
	}

	private ItemPredicate getItemPredicate() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("never", 0);
		return ItemPredicate.Builder.item().of(Items.BARRIER).hasNbt(tag).build();
	}
}
