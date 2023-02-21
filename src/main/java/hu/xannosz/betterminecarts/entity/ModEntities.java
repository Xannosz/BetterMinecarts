package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BetterMinecarts.MOD_ID);

	public static final RegistryObject<EntityType<ElectricLocomotive>> ELECTRIC_LOCOMOTIVE = ENTITIES.register("electric_locomotive",
			() -> EntityType.Builder.<ElectricLocomotive>of(ElectricLocomotive::new, MobCategory.MISC).sized(1.0f, 1.0f).build(BetterMinecarts.MOD_ID + ":electric_locomotive"));

	public static final RegistryObject<EntityType<SteamLocomotive>> STEAM_LOCOMOTIVE = ENTITIES.register("steam_locomotive",
			() -> EntityType.Builder.<SteamLocomotive>of(SteamLocomotive::new, MobCategory.MISC).sized(1.0f, 1.0f).build(BetterMinecarts.MOD_ID + ":steam_locomotive"));

	public static final RegistryObject<EntityType<CraftingMinecart>> CRAFTING_MINECART = ENTITIES.register("crafting_minecart_item.json",
			() -> EntityType.Builder.<CraftingMinecart>of(CraftingMinecart::new, MobCategory.MISC).sized(1.0f, 1.0f).build(BetterMinecarts.MOD_ID + ":crafting_minecart_item.json"));

}
