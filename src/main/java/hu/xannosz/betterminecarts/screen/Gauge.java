package hu.xannosz.betterminecarts.screen;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;

import static net.minecraftforge.client.gui.ScreenUtils.drawTexturedModalRect;

@AllArgsConstructor
public class Gauge {
	private final int x;
	private final int y;
	private final int u;
	private final int v;
	private final int w;
	private final int h;

	public void render(GuiGraphics guiGraphics, int value, int max, float partialTick) {
		if (max == 0) {
			return;
		}
		if (value > max) {
			value = max;
		}
		int t = (value * h) / max;
		drawTexturedModalRect(guiGraphics, x, y + h - t, u, v + h - t, w, t, partialTick);
	}
}
