package hu.xannosz.betterminecarts.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.utils.ButtonId;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static net.minecraftforge.client.gui.ScreenUtils.drawTexturedModalRect;

@OnlyIn(Dist.CLIENT)
public class ElectricLocomotiveScreen extends AbstractContainerScreen<ElectricLocomotiveMenu> {

	private static final ResourceLocation TEXTURE =
			new ResourceLocation(BetterMinecarts.MOD_ID, "textures/gui/electric_locomotive.png");

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

	public ElectricLocomotiveScreen(ElectricLocomotiveMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		imageHeight = 42;
	}

	@Override
	protected void init() {
		super.init();

		x = (width - imageWidth) / 2;
		y = (height - imageHeight) / 2;

		back = new GraphicalButton(generateConfig(26, 0, 7, ButtonId.BACK));
		stop = new GraphicalButton(generateConfig(34, 7, 7, ButtonId.STOP));
		forward = new GraphicalButton(generateConfig(42, 14, 7, ButtonId.FORWARD));
		fForward = new GraphicalButton(generateConfig(50, 21, 10, ButtonId.F_FORWARD));
		ffForward = new GraphicalButton(generateConfig(61, 31, 13, ButtonId.FF_FORWARD));
		lamp = new GraphicalButton(ButtonConfig.builder()
				.buttonId(ButtonId.LAMP)
				.hitBoxX(x + 155)
				.hitBoxY(y + 7)
				.hitBoxW(10)
				.hitBoxH(11)
				.hoveredX(177)
				.hoveredY(1)
				.build());
		whistle = new GraphicalButton(ButtonConfig.builder()
				.buttonId(ButtonId.WHISTLE)
				.hitBoxX(x + 155)
				.hitBoxY(y + 23)
				.hitBoxW(10)
				.hitBoxH(11)
				.hoveredX(177)
				.hoveredY(13)
				.build());
		redstone = new GraphicalButton(ButtonConfig.builder()
				.buttonId(ButtonId.REDSTONE)
				.hitBoxX(x + 139)
				.hitBoxY(y + 23)
				.hitBoxW(10)
				.hitBoxH(11)
				.hoveredX(177)
				.hoveredY(25)
				.build());

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
	protected void renderBg(@NotNull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.enableBlend();
		this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

		back.setEntityId(menu.getElectricLocomotiveId());
		stop.setEntityId(menu.getElectricLocomotiveId());
		forward.setEntityId(menu.getElectricLocomotiveId());
		fForward.setEntityId(menu.getElectricLocomotiveId());
		ffForward.setEntityId(menu.getElectricLocomotiveId());
		lamp.setEntityId(menu.getElectricLocomotiveId());
		whistle.setEntityId(menu.getElectricLocomotiveId());
		redstone.setEntityId(menu.getElectricLocomotiveId());

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

		int power = menu.getPower();
		if (power > ElectricLocomotive.MAX_POWER) {
			power = ElectricLocomotive.MAX_POWER;
		}
		int t = (power * 140) / ElectricLocomotive.MAX_POWER;
		drawTexturedModalRect(poseStack, x + 6, y + 6, 0, 43, t, 12, partialTick);
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
				.hitBoxY(y + 24)
				.hitBoxW(w)
				.hitBoxH(9)
				.hoveredX(hoverX)
				.hoveredY(55)
				.build();
	}
}
