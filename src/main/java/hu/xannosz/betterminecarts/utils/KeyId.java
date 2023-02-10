package hu.xannosz.betterminecarts.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeyId {
	INCREASE(0), DECREASE(1), LAMP(2), WHISTLE(3), REDSTONE(4), DATA(5);

	private final int id;

	public static KeyId getKeyFromId(int id) {
		for (KeyId keyId : values()) {
			if (keyId.id == id) {
				return keyId;
			}
		}
		return null;
	}
}
