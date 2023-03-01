package hu.xannosz.betterminecarts.datagen;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.item.AbstractLocomotiveItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static hu.xannosz.betterminecarts.item.ModItems.LOCOMOTIVE_ITEMS;

public class ModItemModelProvider extends ItemModelProvider {
	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator.getPackOutput(), BetterMinecarts.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		LOCOMOTIVE_ITEMS.forEach((name, register) -> locomotiveItem(name, register.get()));
	}

	private void locomotiveItem(String name, AbstractLocomotiveItem item) {
		withExistingParent(name, new ResourceLocation("item/generated")).texture("layer0",
				new ResourceLocation(BetterMinecarts.MOD_ID, "item/" + item.getLocomotiveName()));
	}
}
