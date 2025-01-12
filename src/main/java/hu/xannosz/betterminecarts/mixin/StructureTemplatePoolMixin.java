package hu.xannosz.betterminecarts.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = {StructureTemplatePool.class})
public interface StructureTemplatePoolMixin {
	@Accessor(value = "rawTemplates")
	List<Pair<StructurePoolElement, Integer>> getRawTemplates();

	@Accessor(value = "rawTemplates")
	@Mutable
	void setRawTemplates(List<Pair<StructurePoolElement, Integer>> input);

	@Accessor(value = "templates")
	ObjectArrayList<StructurePoolElement> getTemplates();
}
