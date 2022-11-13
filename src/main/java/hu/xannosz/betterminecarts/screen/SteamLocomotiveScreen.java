package hu.xannosz.betterminecarts.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.button.ButtonId;
import hu.xannosz.betterminecarts.entity.SteamLocomotive;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SteamLocomotiveScreen extends AbstractContainerScreen<SteamLocomotiveMenu> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/gui/steam_locomotive.png");

	private int x;
	private int y;

	private GraphicalButton back;
	private GraphicalButton stop;
	private GraphicalButton pause;
	private GraphicalButton forward;
	private GraphicalButton fForward;
	private GraphicalButton ffForward;
	private GraphicalButton lamp;
	private GraphicalButton whistle;
	private GraphicalButton redstone;

	private Gauge steam;
	private Gauge water;
	private Gauge heat;
	private Gauge burn;

	public SteamLocomotiveScreen(SteamLocomotiveMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		imageHeight = 178;
	}

	@Override
	protected void init() {
		super.init();

		x = (width - imageWidth) / 2;
		y = (height - imageHeight) / 2;

		back = new GraphicalButton(generateConfig(61, 0, 7, ButtonId.BACK));
		stop = new GraphicalButton(generateConfig(69, 7, 7, ButtonId.STOP));
		pause = new GraphicalButton(generateConfig(77, 14, 7, ButtonId.PAUSE));
		forward = new GraphicalButton(generateConfig(85, 21, 7, ButtonId.FORWARD));
		fForward = new GraphicalButton(generateConfig(93, 28, 10, ButtonId.F_FORWARD));
		ffForward = new GraphicalButton(generateConfig(104, 38, 13, ButtonId.FF_FORWARD));
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

		steam = new Gauge(x + 11, y + 9, 178, 2, 15, 79);
		water = new Gauge(x + 30, y + 9, 197, 2, 15, 79);
		heat = new Gauge(x + 50, y + 9, 216, 2, 3, 79);
		burn = new Gauge(x + 102, y + 59, 189, 84, 13, 13);

		addRenderableWidget(back);
		addRenderableWidget(stop);
		addRenderableWidget(pause);
		addRenderableWidget(forward);
		addRenderableWidget(fForward);
		addRenderableWidget(ffForward);
		addRenderableWidget(lamp);
		addRenderableWidget(whistle);
		addRenderableWidget(redstone);
	}

	@Override
	protected void renderBg(@NotNull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.enableBlend();
		this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

		menu.createSlots();

		back.setEntityId(menu.getSteamLocomotiveId());
		stop.setEntityId(menu.getSteamLocomotiveId());
		pause.setEntityId(menu.getSteamLocomotiveId());
		forward.setEntityId(menu.getSteamLocomotiveId());
		fForward.setEntityId(menu.getSteamLocomotiveId());
		ffForward.setEntityId(menu.getSteamLocomotiveId());
		lamp.setEntityId(menu.getSteamLocomotiveId());
		whistle.setEntityId(menu.getSteamLocomotiveId());
		redstone.setEntityId(menu.getSteamLocomotiveId());

		back.setSelected(false);
		stop.setSelected(false);
		pause.setSelected(false);
		forward.setSelected(false);
		fForward.setSelected(false);
		ffForward.setSelected(false);

		switch (menu.getActiveButton()) {
			case BACK -> back.setSelected(true);
			case STOP -> stop.setSelected(true);
			case PAUSE -> pause.setSelected(true);
			case FORWARD -> forward.setSelected(true);
			case F_FORWARD -> fForward.setSelected(true);
			case FF_FORWARD -> ffForward.setSelected(true);
		}

		lamp.setSelected(menu.isLampOn());
		redstone.setSelected(menu.isSignalActive());

		steam.render(poseStack, menu.getSteam(), SteamLocomotive.MAX_STEAM, partialTick);
		water.render(poseStack, menu.getWater(), SteamLocomotive.MAX_WATER, partialTick);
		heat.render(poseStack, menu.getHeat(), 125, partialTick);
		burn.render(poseStack, menu.getBurn(), menu.getMaxBurn(), partialTick);
	}

	@Override
	public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float delta) {
		//call built-in functions
		renderBackground(poseStack);
		super.render(poseStack, mouseX, mouseY, delta);

		//call built-in function
		renderTooltip(poseStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(@NotNull PoseStack poseStack, int mouseX, int mouseY) {

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
