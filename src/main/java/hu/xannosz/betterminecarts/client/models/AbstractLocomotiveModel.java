package hu.xannosz.betterminecarts.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLocomotiveModel extends EntityModel<AbstractLocomotive> {
	private final ModelPart body;
	private Vec3 topFilter;
	private Vec3 bottomFilter;

	protected AbstractLocomotiveModel(ModelPart root) {
		this.body = root;
	}

	@Override
	public void setupAnim(@NotNull AbstractLocomotive entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		topFilter = entity.getTopFilter().getFormattedFilter();
		bottomFilter = entity.getBottomFilter().getFormattedFilter();
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.getChild("bottom").render(poseStack, vertexConsumer, packedLight, packedOverlay, (float) bottomFilter.x, (float) bottomFilter.y, (float) bottomFilter.z, alpha);
		body.getChild("top").render(poseStack, vertexConsumer, packedLight, packedOverlay, (float) topFilter.x, (float) topFilter.y, (float) topFilter.z, alpha);
		body.getChild("main").render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
