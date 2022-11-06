package hu.xannosz.betterminecarts.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.client.models.ElectricLocomotiveModel;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ElectricLocomotiveRenderer extends EntityRenderer<ElectricLocomotive> {
	private static final ResourceLocation MINECART_LOCATION =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/electric_locomotive.png");
	protected final EntityModel<ElectricLocomotive> model;

	public ElectricLocomotiveRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.7F;
		this.model = new ElectricLocomotiveModel(context.bakeLayer(ElectricLocomotiveModel.LAYER_LOCATION));
	}

	public void render(@NotNull ElectricLocomotive electricLocomotive, float yRotation, float p_115420_,
					   @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight) {
		super.render(electricLocomotive, yRotation, p_115420_, poseStack, multiBufferSource, packedLight);
		poseStack.pushPose();
		long i = (long) electricLocomotive.getId() * 493286711L;
		i = i * i * 4392167121L + i * 98761L;
		float f = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f1 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f2 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		poseStack.translate(f, f1, f2);
		double d0 = Mth.lerp(p_115420_, electricLocomotive.xOld, electricLocomotive.getX());
		double d1 = Mth.lerp(p_115420_, electricLocomotive.yOld, electricLocomotive.getY());
		double d2 = Mth.lerp(p_115420_, electricLocomotive.zOld, electricLocomotive.getZ());
		Vec3 vec3 = electricLocomotive.getPos(d0, d1, d2);
		float f3 = Mth.lerp(p_115420_, electricLocomotive.xRotO, electricLocomotive.getXRot());
		if (vec3 != null) {
			Vec3 vec31 = electricLocomotive.getPosOffs(d0, d1, d2, 0.3F);
			Vec3 vec32 = electricLocomotive.getPosOffs(d0, d1, d2, -0.3F);
			if (vec31 == null) {
				vec31 = vec3;
			}

			if (vec32 == null) {
				vec32 = vec3;
			}

			poseStack.translate(vec3.x - d0, (vec31.y + vec32.y) / 2.0D - d1, vec3.z - d2);
			Vec3 vec33 = vec32.add(-vec31.x, -vec31.y, -vec31.z);
			if (vec33.length() != 0.0D) {
				vec33 = vec33.normalize();
				yRotation = (float) (Math.atan2(vec33.z, vec33.x) * 180.0D / Math.PI);
				yRotation = electricLocomotive.normalizeRotation(yRotation); //flip
				f3 = (float) (Math.atan(vec33.y) * 73.0D);
			}
		}

		poseStack.translate(0.0D, 0.375D, 0.0D);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yRotation));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(-f3));
		float f5 = (float) electricLocomotive.getHurtTime() - p_115420_;
		float f6 = electricLocomotive.getDamage() - p_115420_;
		if (f6 < 0.0F) {
			f6 = 0.0F;
		}

		if (f5 > 0.0F) {
			poseStack.mulPose(Vector3f.XP.rotationDegrees(
					Mth.sin(f5) * f5 * f6 / 10.0F * (float) electricLocomotive.getHurtDir()));
		}

		int j = electricLocomotive.getDisplayOffset();
		BlockState blockstate = electricLocomotive.getDisplayBlockState();
		if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
			poseStack.pushPose();
			poseStack.scale(0.75F, 0.75F, 0.75F);
			poseStack.translate(-0.5D, ((float) (j - 8) / 16.0F), 0.5D);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
			poseStack.popPose();
		}

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		this.model.setupAnim(electricLocomotive, 0.0F, 0.0F,
				0.0F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = multiBufferSource.getBuffer(
				this.model.renderType(this.getTextureLocation(electricLocomotive)));
		this.model.renderToBuffer(poseStack, vertexconsumer, packedLight,
				OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	public @NotNull ResourceLocation getTextureLocation(@NotNull ElectricLocomotive electricLocomotive) {
		return MINECART_LOCATION;
	}
}
