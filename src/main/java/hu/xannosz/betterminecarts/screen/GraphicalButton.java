package hu.xannosz.betterminecarts.screen;

import hu.xannosz.betterminecarts.network.ButtonClickedPacket;
import hu.xannosz.betterminecarts.network.ModMessages;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GraphicalButton extends AbstractButton {

	private final ButtonConfig config;
	@Setter
	private boolean selected = false;
	@Setter
	private ResourceLocation resourceLocation;
	@Setter
	private int entityId = 0;

	public GraphicalButton(ButtonConfig config) {
		super(config.getHitBoxX(), config.getHitBoxY(),
				config.getHitBoxW(), config.getHitBoxH(), Component.empty());
		this.config = config;
	}

	@Override
	public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			if (isHovered || selected) {
				guiGraphics.blit(resourceLocation, config.getHitBoxX(), config.getHitBoxY(),
						config.getHoveredX(), config.getHoveredY(),
						config.getHitBoxW(), config.getHitBoxH());
			}
		}
	}

	@Override
	public void onPress() {
		ModMessages.INSTANCE.send(new ButtonClickedPacket(config.getButtonId(), entityId), PacketDistributor.SERVER.noArg());
	}

	@Override
	public void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

	}
}
