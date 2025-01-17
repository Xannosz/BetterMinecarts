/*############################################################
This file copied from Cammies-Minecart-Tweaks (https://github.com/CammiePone/Cammies-Minecart-Tweaks)

Copyright (C) 2022 Cammie

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to use, copy, modify, and/or merge copies of the
Software, and to permit persons to whom the Software is furnished to do so, subject to the following
restrictions:

 1) The above copyright notice and this permission notice shall be included in all copies or substantial
    portions of the Software.
 2) You include attribution to the copyright holder(s) in public display of any project that uses any
    portion of the Software.
 3) You may not publish or distribute substantial portions of the Software in its compiled or uncompiled
    forms without prior permission from the copyright holder.
 4) The Software does not make up a substantial portion of your own projects.

If more than 2 years have passed without any source code change on the origin repository for the Software
(https://github.com/CammiePone/Cammies-Minecart-Tweaks), then all but the first 2 restrictions are void, and
may be ignored when using, copying, modifying, and/or merging copies of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.

Modified by Xannosz 2022-2023
############################################################*/
package hu.xannosz.betterminecarts.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.utils.Colorable;
import hu.xannosz.betterminecarts.utils.Linkable;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
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

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
		if (((Colorable) entity).getColor().equals(MinecartColor.LIGHT_GRAY)) { // if light gray use the original
			return new ResourceLocation("textures/entity/minecart.png");
		} else {
			return new ResourceLocation(BetterMinecarts.MOD_ID, "textures/entity/minecart.png");
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	public void betterminecarts$render(T child, float yaw, float tickDelta, PoseStack stack, MultiBufferSource provider, int light, CallbackInfo info) {
		if (child instanceof Linkable linkable) {
			AbstractMinecart parent = linkable.getLinkedParentForRender();
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

				renderChain(distanceX, distanceY, distanceZ, (float) hAngle, (float) vAngle, stack, provider, 15728880);
			}
		}
	}

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
