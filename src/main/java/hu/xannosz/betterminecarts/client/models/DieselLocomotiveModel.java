package hu.xannosz.betterminecarts.client.models;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class DieselLocomotiveModel extends AbstractLocomotiveModel {
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(BetterMinecarts.MOD_ID, "diesel_locomotive"),
					"main");

	public DieselLocomotiveModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition body = meshdefinition.getRoot();

		PartDefinition bottom = body.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-11.0F, 3.0F, -8.0F, 22.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-10.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(60, 0).addBox(-0.5F, -9.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition top = body.addOrReplaceChild("top", CubeListBuilder.create().texOffs(32, 30).addBox(-10.0F, -7.0F, -7.0F, 7.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(0, 18).addBox(-3.0F, -7.5F, -7.5F, 8.0F, 11.0F, 15.0F, new CubeDeformation(0.0F))
				.texOffs(0, 44).addBox(4.5F, -7.0F, -7.0F, 6.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition main = body.addOrReplaceChild("main", CubeListBuilder.create().texOffs(7, 9).addBox(-11.0F, -4.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(10, 2).addBox(10.0F, -0.5F, -4.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(6, 0).addBox(10.0F, -0.5F, 2.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
}
