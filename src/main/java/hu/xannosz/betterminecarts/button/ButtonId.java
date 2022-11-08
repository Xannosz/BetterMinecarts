package hu.xannosz.betterminecarts.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonId {
	BACK(0), PAUSE(1), STOP(2), FORWARD(3), F_FORWARD(4), FF_FORWARD(5),
	LAMP(6), WHISTLE(7), REDSTONE(8);

	private final int id;

	public static ButtonId getButtonFromId(int id) {
		for (ButtonId buttonId : values()) {
			if (buttonId.id == id) {
				return buttonId;
			}
		}
		return null;
	}
}
