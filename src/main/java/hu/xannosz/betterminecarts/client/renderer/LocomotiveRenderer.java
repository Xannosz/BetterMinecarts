package hu.xannosz.betterminecarts.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.entity.DieselLocomotive;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import hu.xannosz.betterminecarts.entity.SteamLocomotive;
import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Unique;

public class LocomotiveRenderer extends EntityRenderer<AbstractLocomotive> {
	private static final ResourceLocation ELECTRIC_LOCOMOTIVE =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/electric_locomotive.png");
	private static final ResourceLocation ELECTRIC_LOCOMOTIVE_ON =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/electric_locomotive_on.png");
	private static final ResourceLocation STEAM_LOCOMOTIVE =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/steam_locomotive.png");
	private static final ResourceLocation STEAM_LOCOMOTIVE_ON =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/steam_locomotive_on.png");
	private static final ResourceLocation STEAM_LOCOMOTIVE_BURN =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/steam_locomotive_burn.png");
	private static final ResourceLocation STEAM_LOCOMOTIVE_ON_BURN =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/steam_locomotive_on_burn.png");
	private static final ResourceLocation DIESEL_LOCOMOTIVE =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/diesel_locomotive.png");
	private static final ResourceLocation DIESEL_LOCOMOTIVE_ON =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/diesel_locomotive_on.png");

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
				f3 = locomotive.normalizePitch(f3); //flip
			}
		}

		poseStack.translate(0.0D, 0.375D, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yRotation));
		poseStack.mulPose(Axis.ZP.rotationDegrees(-f3));
		float f5 = (float) locomotive.getHurtTime() - p_115420_;
		float f6 = locomotive.getDamage() - p_115420_;
		if (f6 < 0.0F) {
			f6 = 0.0F;
		}

		if (f5 > 0.0F) {
			poseStack.mulPose(Axis.XP.rotationDegrees(
					Mth.sin(f5) * f5 * f6 / 10.0F * (float) locomotive.getHurtDir()));
		}

		int j = locomotive.getDisplayOffset();
		BlockState blockstate = locomotive.getDisplayBlockState();
		if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
			poseStack.pushPose();
			poseStack.scale(0.75F, 0.75F, 0.75F);
			poseStack.translate(-0.5D, ((float) (j - 8) / 16.0F), 0.5D);
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
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

		//copy of mixin
		if (locomotive instanceof Linkable linkable) {
			AbstractMinecart parent = linkable.getLinkedParentForRender();
			if (parent != null) {
				double startX = parent.getX();
				double startY = parent.getY();
				double startZ = parent.getZ();
				double endX = locomotive.getX();
				double endY = locomotive.getY();
				double endZ = locomotive.getZ();

				float distanceX = (float) (startX - endX);
				float distanceY = (float) (startY - endY);
				float distanceZ = (float) (startZ - endZ);
				float distance = locomotive.distanceTo(parent);

				double hAngle = Math.toDegrees(Math.atan2(endZ - startZ, endX - startX));
				hAngle += Math.ceil(-hAngle / 360) * 360;

				double vAngle = Math.asin(distanceY / distance);

				renderChain(distanceX, distanceY, distanceZ, (float) hAngle, (float) vAngle, poseStack, multiBufferSource, 15728880);
			}
		}
	}

	public @NotNull ResourceLocation getTextureLocation(@NotNull AbstractLocomotive locomotive) {
		if (locomotive instanceof ElectricLocomotive) {
			if (locomotive.isLampOn()) {
				return ELECTRIC_LOCOMOTIVE_ON;
			} else {
				return ELECTRIC_LOCOMOTIVE;
			}
		}
		if (locomotive instanceof SteamLocomotive steamLocomotive) {
			if (steamLocomotive.isBurn()) {
				if (locomotive.isLampOn()) {
					return STEAM_LOCOMOTIVE_ON_BURN;
				} else {
					return STEAM_LOCOMOTIVE_BURN;
				}
			} else {
				if (locomotive.isLampOn()) {
					return STEAM_LOCOMOTIVE_ON;
				} else {
					return STEAM_LOCOMOTIVE;
				}
			}
		}
		if (locomotive instanceof DieselLocomotive) {
			if (locomotive.isLampOn()) {
				return DIESEL_LOCOMOTIVE_ON;
			} else {
				return DIESEL_LOCOMOTIVE;
			}
		}
		return ELECTRIC_LOCOMOTIVE;
	}

	//copy mixin
	private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/chain.png");
	private static final RenderType CHAIN_LAYER = RenderType.entityCutoutNoCull(CHAIN_TEXTURE);

	@Unique
	public void renderChain(float x, float y, float z, float hAngle, float vAngle, PoseStack stack, MultiBufferSource provider, int light) {
		float squaredLength = x * x + y * y + z * z;
		float length = Mth.sqrt(squaredLength) - 1F;

		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(-hAngle - 90));
		stack.mulPose(Axis.XP.rotation(-vAngle));
		stack.translate(0, 0, 0.5);
		stack.pushPose();

		VertexConsumer vertexConsumer = provider.getBuffer(CHAIN_LAYER);
		float vertX1 = 0F;
		float vertY1 = 0.25F;
		float vertX2 = Mth.sin(6.2831855F) * 0.125F;
		float vertY2 = Mth.cos(6.2831855F) * 0.125F;
		float minU = 0F;
		float maxU = 0.1875F;
		float minV = 0F;
		float maxV = length / 10;
		PoseStack.Pose entry = stack.last();
		Matrix4f matrix4f = entry.pose();
		Matrix3f matrix3f = entry.normal();

		vertexConsumer.vertex(matrix4f, vertX1, vertY1, 0F).color(0, 0, 0, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4f, vertX1, vertY1, length).color(255, 255, 255, 255).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4f, vertX2, vertY2, length).color(255, 255, 255, 255).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4f, vertX2, vertY2, 0F).color(0, 0, 0, 255).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

		stack.popPose();
		stack.translate(0.19, 0.19, 0);
		stack.mulPose(Axis.ZP.rotationDegrees(90));

		entry = stack.last();
		matrix4f = entry.pose();
		matrix3f = entry.normal();

		vertexConsumer.vertex(matrix4f, vertX1, vertY1, 0F).color(0, 0, 0, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4f, vertX1, vertY1, length).color(255, 255, 255, 255).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4f, vertX2, vertY2, length).color(255, 255, 255, 255).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4f, vertX2, vertY2, 0F).color(0, 0, 0, 255).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

		stack.popPose();
	}
}
