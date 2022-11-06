package hu.xannosz.betterminecarts.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartRenderer.class)
public abstract class MinecartEntityRendererMixin<T extends AbstractMinecart> extends EntityRenderer<T> {
	@Unique
	private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/chain.png");
	@Unique
	private static final RenderType CHAIN_LAYER = RenderType.entityCutoutNoCull(CHAIN_TEXTURE);

	protected MinecartEntityRendererMixin(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	//@Inject(method = "render(Lnet/minecraft/world/entity/vehicle/AbstractMinecart;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/render/MultiBufferSource;I)V", at = @At("TAIL"))
	@Inject(method = "render", at = @At("TAIL"))
	public void betterminecarts$render(T child, float yaw, float tickDelta, PoseStack stack, MultiBufferSource provider, int light, CallbackInfo info) {
		if (child instanceof Linkable linkable) {
			AbstractMinecart parent = linkable.getLinkedParent();
			if (parent != null) {
				double startX = parent.getX();
				double startY = parent.getY();
				double startZ = parent.getZ();
				double endX = child.getX();
				double endY = child.getY();
				double endZ = child.getZ();

				float distanceX = (float) (startX - endX);
				float distanceY = (float) (startY - endY);
				float distanceZ = (float) (startZ - endZ);
				float distance = child.distanceTo(parent);

				double hAngle = Math.toDegrees(Math.atan2(endZ - startZ, endX - startX));
				hAngle += Math.ceil(-hAngle / 360) * 360;

				double vAngle = Math.asin(distanceY / distance);

				renderChain(distanceX, distanceY, distanceZ, (float) hAngle, (float) vAngle, stack, provider, light);
			}
		}
	}

	@Unique
	public void renderChain(float x, float y, float z, float hAngle, float vAngle, PoseStack stack, MultiBufferSource provider, int light) {
		float squaredLength = x * x + y * y + z * z;
		float length = Mth.sqrt(squaredLength) - 1F;

		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(-hAngle - 90));
		stack.mulPose(Vector3f.XP.rotation(-vAngle));
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
		stack.mulPose(Vector3f.ZP.rotationDegrees(90));

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
