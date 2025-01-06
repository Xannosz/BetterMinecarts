package hu.xannosz.betterminecarts.screen;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@AllArgsConstructor
public class Gauge {
	private final int x;
	private final int y;
	private final int u;
	private final int v;
	private final int w;
	private final int h;

	public void render(ResourceLocation resourceLocation, GuiGraphics guiGraphics, int value, int max) {
		if (max == 0) {
			return;
		}
		if (value > max) {
			value = max;
		}
		int t = (value * h) / max;
		guiGraphics.blit(resourceLocation, x, y + h - t, u, v + h - t, w, t);
	}
}
