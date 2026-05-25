package net.minecraft.src;

/**
 * World provider for the bedrock tunnel dimension
 */
public class WorldProviderTunnel extends WorldProvider {
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F, 0.0F);
        this.isAlternateDimension = true;
        this.hasNoSky = true;
        this.worldType = 66; // Custom dimension ID
    }

    public Vec3D getFogColor(float var1, float var2) {
        return Vec3D.createVector(0.0D, 0.0D, 0.0D); // Pure black fog
    }

    protected void generateLightBrightnessTable() {
        // Very dark dimension
        for(int i = 0; i <= 15; ++i) {
            float brightness = (float)i / 15.0F;
            this.lightBrightnessTable[i] = brightness * 0.3F; // 30% of normal brightness
        }
    }

    public IChunkProvider getChunkProvider() {
        return new ChunkProviderTunnel(this.worldObj, this.worldObj.getWorldSeed());
    }

    public boolean canCoordinateBeSpawn(int x, int z) {
        return true;
    }

    public float calculateCelestialAngle(long time, float partialTicks) {
        return 0.0F; // Always midnight
    }

    public boolean canRespawnHere() {
        return false;
    }

    public int getAverageGroundLevel() {
        return 64;
    }

    public boolean canSnowAt(int x, int y, int z) {
        return false;
    }
}
