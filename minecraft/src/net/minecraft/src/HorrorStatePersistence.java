package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Persists the horror campaign separately from level.dat.
 */
public final class HorrorStatePersistence {
	private HorrorStatePersistence() {
	}

	public static void load(World world) {
		try {
			File file = world.getSaveHandler().getMapFile("horror_state");
			if(!file.exists()) {
				HorrorState.reset();
				return;
			}
			NBTTagCompound root = CompressedStreamTools.loadGzippedCompoundFromOutputStream(
					new FileInputStream(file));
			read(root);
		} catch(Exception exception) {
			exception.printStackTrace();
			HorrorState.reset();
		}
	}

	public static void save(World world) {
		try {
			File file = world.getSaveHandler().getMapFile("horror_state");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(write(),
					new FileOutputStream(file));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private static NBTTagCompound write() {
		NBTTagCompound root = new NBTTagCompound();
		root.setLong("WorldStartTime", HorrorState.worldStartTime);
		root.setLong("TotalPlayTime", HorrorState.totalPlayTime);
		root.setInteger("EffectStage", HorrorState.currentEffectStage);
		root.setLong("LastEffectTime", HorrorState.lastEffectTriggerTime);
		root.setInteger("EffectsTriggered", HorrorState.effectsTriggeredCount);
		root.setFloat("SpeedMultiplier", HorrorState.horrorSpeedMultiplier);
		root.setBoolean("SafeMode", HorrorState.safeMode);
		root.setBoolean("TunnelSpawned", HorrorState.tunnelSpawned);
		root.setBoolean("TunnelGenerated", HorrorState.tunnelGenerated);
		root.setBoolean("PortalGenerated", HorrorState.portalGenerated);
		root.setBoolean("Error404Triggered", HorrorState.error404Triggered);
		root.setInteger("PortalX", HorrorState.portalX);
		root.setInteger("PortalY", HorrorState.portalY);
		root.setInteger("PortalZ", HorrorState.portalZ);
		root.setInteger("TunnelX", HorrorState.tunnelX);
		root.setInteger("TunnelY", HorrorState.tunnelY);
		root.setInteger("TunnelZ", HorrorState.tunnelZ);
		root.setBoolean("TunnelEntranceClosed", HorrorState.tunnelEntranceClosed);
		root.setBoolean("TunnelChestOpened", HorrorState.tunnelChestOpened);
		root.setInteger("Error404HouseX", HorrorState.error404HouseX);
		root.setInteger("Error404HouseY", HorrorState.error404HouseY);
		root.setInteger("Error404HouseZ", HorrorState.error404HouseZ);
		return root;
	}

	private static void read(NBTTagCompound root) {
		HorrorState.worldStartTime = root.getLong("WorldStartTime");
		HorrorState.totalPlayTime = root.getLong("TotalPlayTime");
		HorrorState.currentEffectStage = root.getInteger("EffectStage");
		HorrorState.lastEffectTriggerTime = root.getLong("LastEffectTime");
		HorrorState.effectsTriggeredCount = root.getInteger("EffectsTriggered");
		HorrorState.horrorSpeedMultiplier = Math.max(0.1F, root.getFloat("SpeedMultiplier"));
		HorrorState.safeMode = root.getBoolean("SafeMode");
		HorrorState.tunnelSpawned = root.getBoolean("TunnelSpawned");
		HorrorState.tunnelGenerated = root.getBoolean("TunnelGenerated");
		HorrorState.portalGenerated = root.getBoolean("PortalGenerated");
		HorrorState.error404Triggered = root.getBoolean("Error404Triggered");
		HorrorState.portalX = root.getInteger("PortalX");
		HorrorState.portalY = root.getInteger("PortalY");
		HorrorState.portalZ = root.getInteger("PortalZ");
		HorrorState.tunnelX = root.getInteger("TunnelX");
		HorrorState.tunnelY = root.getInteger("TunnelY");
		HorrorState.tunnelZ = root.getInteger("TunnelZ");
		HorrorState.tunnelEntranceClosed = root.getBoolean("TunnelEntranceClosed");
		HorrorState.tunnelChestOpened = root.getBoolean("TunnelChestOpened");
		HorrorState.error404HouseX = root.getInteger("Error404HouseX");
		HorrorState.error404HouseY = root.getInteger("Error404HouseY");
		HorrorState.error404HouseZ = root.getInteger("Error404HouseZ");
	}
}
