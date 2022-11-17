package hu.xannosz.betterminecarts.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrowbarMode {
	CONNECT("connect"), DISCONNECT("disconnect"), REVERT("revert");

	private final String label;

	public CrowbarMode next() {
		switch (this) {
			case CONNECT -> {
				return DISCONNECT;
			}
			case DISCONNECT -> {
				return REVERT;
			}
			case REVERT -> {
				return CONNECT;
			}
		}
		return null;
	}

	public static CrowbarMode getFromLabel(String label) {
		for (CrowbarMode mode : values()) {
			if (mode.label.equals(label)) {
				return mode;
			}
		}
		return CONNECT;
	}
}
