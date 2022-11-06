package hu.xannosz.betterminecarts.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class ElectricLocomotiveModel extends EntityModel<ElectricLocomotive> {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(new ResourceLocation(BetterMinecarts.MOD_ID, "electric_locomotive"),
					"main");
	private final ModelPart body;
	private Vec3 topFilter;
	private Vec3 bottomFilter;

	public ElectricLocomotiveModel(ModelPart root) {
		this.body = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition body = meshdefinition.getRoot();

		PartDefinition bottom = body.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, 3.0F, -8.0F, 22.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-10.5F, -5.0F, -2.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition top = body.addOrReplaceChild("top", CubeListBuilder.create().texOffs(32, 30).addBox(-10.0F, -7.0F, -7.0F, 7.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 18).addBox(-3.0F, -7.5F, -7.5F, 8.0F, 11.0F, 15.0F, new CubeDeformation(0.0F))
				.texOffs(0, 44).addBox(4.5F, -7.0F, -7.0F, 6.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition main = body.addOrReplaceChild("main", CubeListBuilder.create().texOffs(7, 9).addBox(-11.0F, -4.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(10, 2).addBox(10.0F, -0.5F, -4.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(6, 0).addBox(10.0F, -0.5F, 2.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(@NotNull ElectricLocomotive entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		topFilter = entity.getTopFilter();
		bottomFilter = entity.getBottomFilter();
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.getChild("bottom").render(poseStack, vertexConsumer, packedLight, packedOverlay, (float) bottomFilter.x, (float) bottomFilter.y, (float) bottomFilter.z, alpha);
		body.getChild("top").render(poseStack, vertexConsumer, packedLight, packedOverlay, (float) topFilter.x, (float) topFilter.y, (float) topFilter.z, alpha);
		body.getChild("main").render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
