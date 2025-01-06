package hu.xannosz.betterminecarts.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.DieselLocomotive;
import hu.xannosz.betterminecarts.utils.ButtonId;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DieselLocomotiveScreen extends AbstractContainerScreen<DieselLocomotiveMenu> {

	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath(BetterMinecarts.MOD_ID, "textures/gui/diesel_locomotive.png");

	private int x;
	private int y;

	private GraphicalButton back;
	private GraphicalButton stop;
	private GraphicalButton forward;
	private GraphicalButton fForward;
	private GraphicalButton ffForward;
	private GraphicalButton lamp;
	private GraphicalButton whistle;
	private GraphicalButton redstone;
	private Gauge fuel;

	public DieselLocomotiveScreen(DieselLocomotiveMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		imageHeight = 178;
	}

	@Override
	protected void init() {
		super.init();

		x = (width - imageWidth) / 2;
		y = (height - imageHeight) / 2;

		back = new GraphicalButton(generateConfig(65, 0, 7, ButtonId.BACK));
		stop = new GraphicalButton(generateConfig(73, 7, 7, ButtonId.STOP));
		forward = new GraphicalButton(generateConfig(81, 14, 7, ButtonId.FORWARD));
		fForward = new GraphicalButton(generateConfig(89, 21, 10, ButtonId.F_FORWARD));
		ffForward = new GraphicalButton(generateConfig(100, 31, 13, ButtonId.FF_FORWARD));
		lamp = new GraphicalButton(ButtonConfig.builder()
				.buttonId(ButtonId.LAMP)
				.hitBoxX(x + 155)
				.hitBoxY(y + 46)
				.hitBoxW(10)
				.hitBoxH(11)
				.hoveredX(177)
				.hoveredY(84)
				.build());
		whistle = new GraphicalButton(ButtonConfig.builder()
				.buttonId(ButtonId.WHISTLE)
				.hitBoxX(x + 155)
				.hitBoxY(y + 62)
				.hitBoxW(10)
				.hitBoxH(11)
				.hoveredX(177)
				.hoveredY(96)
				.build());
		redstone = new GraphicalButton(ButtonConfig.builder()
				.buttonId(ButtonId.REDSTONE)
				.hitBoxX(x + 155)
				.hitBoxY(y + 78)
				.hitBoxW(10)
				.hitBoxH(11)
				.hoveredX(177)
				.hoveredY(108)
				.build());

		fuel = new Gauge(x + 11, y + 9, 197, 2, 15, 79);

		addRenderableWidget(back);
		addRenderableWidget(stop);
		addRenderableWidget(forward);
		addRenderableWidget(fForward);
		addRenderableWidget(ffForward);
		addRenderableWidget(lamp);
		addRenderableWidget(whistle);
		addRenderableWidget(redstone);
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.enableBlend();
		guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

		menu.createSlots();

		back.setEntityId(menu.getDieselLocomotiveId());
		stop.setEntityId(menu.getDieselLocomotiveId());
		forward.setEntityId(menu.getDieselLocomotiveId());
		fForward.setEntityId(menu.getDieselLocomotiveId());
		ffForward.setEntityId(menu.getDieselLocomotiveId());
		lamp.setEntityId(menu.getDieselLocomotiveId());
		whistle.setEntityId(menu.getDieselLocomotiveId());
		redstone.setEntityId(menu.getDieselLocomotiveId());

		back.setSelected(false);
		stop.setSelected(false);
		forward.setSelected(false);
		fForward.setSelected(false);
		ffForward.setSelected(false);

		switch (menu.getActiveButton()) {
			case BACK -> back.setSelected(true);
			case STOP -> stop.setSelected(true);
			case FORWARD -> forward.setSelected(true);
			case F_FORWARD -> fForward.setSelected(true);
			case FF_FORWARD -> ffForward.setSelected(true);
		}

		lamp.setSelected(menu.isLampOn());
		redstone.setSelected(menu.isSignalActive());

		final int color = menu.getFuelColor();
		float a = (color >> 24 & 0xff) / 255f;
		float r = ((color & 0xff0000) >> 16) / 255f;
		float g = ((color & 0xff00) >> 8) / 255f;
		float b = (color & 0xff) / 255f;
		RenderSystem.setShaderColor(a, r, g, b);
		fuel.render(TEXTURE, guiGraphics, menu.getFuelAmount(), DieselLocomotive.MAX_FUEL);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		guiGraphics.blit(TEXTURE, x + 11, y + 9, 178, 2, 15, 79);

		int power = menu.getPower();
		if (power > DieselLocomotive.MAX_POWER) {
			power = DieselLocomotive.MAX_POWER;
		}
		int t = (power * 59) / DieselLocomotive.MAX_POWER;
		guiGraphics.blit(TEXTURE, x + 45, y + 37, 112, 178, t, 2);

		int v = 178 + menu.getClock() / 6 * 8;
		guiGraphics.blit(TEXTURE, x + 42, y + 28, 45, v, 65, 8);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		//call built-in functions
		renderTransparentBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, delta);

		//call built-in function
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {

	}

	private ButtonConfig generateConfig(int buttonX, int hoverX, int w, ButtonId buttonId) {
		return ButtonConfig.builder()
				.buttonId(buttonId)
				.hitBoxX(x + buttonX)
				.hitBoxY(y + 80)
				.hitBoxW(w)
				.hitBoxH(9)
				.hoveredX(hoverX)
				.hoveredY(178)
				.build();
	}
}
