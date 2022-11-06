package hu.xannosz.betterminecarts.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.network.ButtonClickedPacket;
import lombok.Setter;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static net.minecraftforge.client.gui.ScreenUtils.drawTexturedModalRect;

@OnlyIn(Dist.CLIENT)
public class GraphicalButton extends AbstractButton {

	private final ButtonConfig config;
	@Setter
	private boolean selected = false;
	@Setter
	private int entityId = 0;

	public GraphicalButton(ButtonConfig config) {
		super(config.getHitBoxX(), config.getHitBoxY(),
				config.getHitBoxW(), config.getHitBoxH(), Component.empty());
		this.config = config;
	}

	@Override
	public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			if (isHovered || selected) {
				drawTexturedModalRect(poseStack, config.getHitBoxX(), config.getHitBoxY(),
						config.getHoveredX(), config.getHoveredY(),
						config.getHitBoxW(), config.getHitBoxH(), partialTicks);
			}
		}
	}

	@Override
	public void onPress() {
		BetterMinecarts.INSTANCE.sendToServer(new ButtonClickedPacket(config.getButtonId(), entityId));
	}

	@Override
	public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {

	}

	public void setVisibility(boolean visible) {
		this.visible = visible;
		active = visible;
	}
}
