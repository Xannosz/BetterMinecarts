package hu.xannosz.betterminecarts.integration;

public class MinecartTweaksConfig {
	public ClientTweaks clientTweaks = new ClientTweaks();
	public ServerTweaks serverTweaks = new ServerTweaks();

	public double getFurnaceMinecartSpeed() {
		return Math.max(0.1, serverTweaks.furnaceMinecartSpeed * 0.05);
	}

	public double getOtherMinecartSpeed() {
		return Math.max(0.1, serverTweaks.otherMinecartSpeed * 0.05);
	}

	public double getMaxSpeedAroundTurns() {
		return Math.min(1, serverTweaks.maxSpeedAroundTurns * 0.05);
	}

	public static class ClientTweaks {
		public boolean playerViewIsLocked = false;
		public int maxViewAngle = 90; //max 90
	}

	public static class ServerTweaks {
		public double furnaceMinecartSpeed = 40D;
		public double otherMinecartSpeed = 10D;
		public double maxSpeedAroundTurns = 8D;
		public float minecartDamage = 20F;
		public int furnaceMaxBurnTime = 72000;
		public boolean shouldPoweredRailsStopFurnace = true;
		public boolean furnacesCanUseAllFuels = true;
		public boolean furnaceMinecartsLoadChunks = false;
	}
}
