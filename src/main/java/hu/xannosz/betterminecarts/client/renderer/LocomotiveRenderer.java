package hu.xannosz.betterminecarts.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import hu.xannosz.betterminecarts.entity.SteamLocomotive;
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

public class LocomotiveRenderer extends EntityRenderer<AbstractLocomotive> {
	private static final ResourceLocation ELECTRIC_LOCOMOTIVE =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/electric_locomotive.png");
	private static final ResourceLocation STEAM_LOCOMOTIVE =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/steam_locomotive.png");
	protected final EntityModel<AbstractLocomotive> model;

	public LocomotiveRenderer(EntityRendererProvider.Context context, EntityModel<AbstractLocomotive> model) {
		super(context);
		this.shadowRadius = 0.7F;
		this.model = model;
	}

	public void render(@NotNull AbstractLocomotive locomotive, float yRotation, float p_115420_,
					   @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight) {
		super.render(locomotive, yRotation, p_115420_, poseStack, multiBufferSource, packedLight);
		poseStack.pushPose();
		long i = (long) locomotive.getId() * 493286711L;
		i = i * i * 4392167121L + i * 98761L;
		float f = (((float) (i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f1 = (((float) (i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float f2 = (((float) (i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		poseStack.translate(f, f1, f2);
		double d0 = Mth.lerp(p_115420_, locomotive.xOld, locomotive.getX());
		double d1 = Mth.lerp(p_115420_, locomotive.yOld, locomotive.getY());
		double d2 = Mth.lerp(p_115420_, locomotive.zOld, locomotive.getZ());
		Vec3 vec3 = locomotive.getPos(d0, d1, d2);
		float f3 = Mth.lerp(p_115420_, locomotive.xRotO, locomotive.getXRot());
		if (vec3 != null) {
			Vec3 vec31 = locomotive.getPosOffs(d0, d1, d2, 0.3F);
			Vec3 vec32 = locomotive.getPosOffs(d0, d1, d2, -0.3F);
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
				yRotation = locomotive.normalizeRotation(yRotation); //flip
				f3 = (float) (Math.atan(vec33.y) * 73.0D);
			}
		}

		poseStack.translate(0.0D, 0.375D, 0.0D);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yRotation));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(-f3));
		float f5 = (float) locomotive.getHurtTime() - p_115420_;
		float f6 = locomotive.getDamage() - p_115420_;
		if (f6 < 0.0F) {
			f6 = 0.0F;
		}

		if (f5 > 0.0F) {
			poseStack.mulPose(Vector3f.XP.rotationDegrees(
					Mth.sin(f5) * f5 * f6 / 10.0F * (float) locomotive.getHurtDir()));
		}

		int j = locomotive.getDisplayOffset();
		BlockState blockstate = locomotive.getDisplayBlockState();
		if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
			poseStack.pushPose();
			poseStack.scale(0.75F, 0.75F, 0.75F);
			poseStack.translate(-0.5D, ((float) (j - 8) / 16.0F), 0.5D);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
			poseStack.popPose();
		}

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		this.model.setupAnim(locomotive, 0.0F, 0.0F,
				0.0F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = multiBufferSource.getBuffer(
				this.model.renderType(this.getTextureLocation(locomotive)));
		this.model.renderToBuffer(poseStack, vertexconsumer, packedLight,
				OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	public @NotNull ResourceLocation getTextureLocation(@NotNull AbstractLocomotive locomotive) {
		if(locomotive instanceof ElectricLocomotive){
			return ELECTRIC_LOCOMOTIVE;
		}
		if(locomotive instanceof SteamLocomotive steamLocomotive){
			return STEAM_LOCOMOTIVE;
		}
		return ELECTRIC_LOCOMOTIVE;
	}
}
