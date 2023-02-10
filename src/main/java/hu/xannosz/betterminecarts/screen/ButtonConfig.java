package hu.xannosz.betterminecarts.screen;

import hu.xannosz.betterminecarts.utils.ButtonId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ButtonConfig {
	private ButtonId buttonId;
	private int hoveredX;
	private int hoveredY;
	private int hitBoxX;
	private int hitBoxY;
	private int hitBoxW;
	private int hitBoxH;
}
