package hu.xannosz.betterminecarts.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hu.xannosz.betterminecarts.utils.Colorable;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartModel.class)
public abstract class MinecartModelMixin<T extends Entity> extends HierarchicalModel<T> {
	@Unique
	private Vec3 filter = MinecartColor.LIGHT_GRAY.getFormattedFilter();

	@Inject(method = "setupAnim", at = @At("TAIL"))
	public void betterminecarts$setupAnim(T entity, float p_103101_, float p_103102_, float p_103103_, float p_103104_, float p_103105_, CallbackInfo info) {
		filter = ((Colorable) entity).getColor().getFormattedFilter();
	}

	@Override
	public void renderToBuffer(PoseStack p_170625_, VertexConsumer p_170626_, int p_170627_, int p_170628_, float p_170629_, float p_170630_, float p_170631_, float p_170632_) {
		this.root().render(p_170625_, p_170626_, p_170627_, p_170628_, (float) filter.x, (float) filter.y, (float) filter.z, p_170632_);
	}
}