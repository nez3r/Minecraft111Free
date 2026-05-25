package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreadedChunkLoader implements IThreadedFileIO, IChunkLoader {
	private List pendingChunkList = new ArrayList();
	private Set pendingChunkCoords = new HashSet();
	private Object chunkSaveLock = new Object();
	private final File chunkSaveLocation;

	public ThreadedChunkLoader(File var1) {
		this.chunkSaveLocation = var1;
	}

	public Chunk loadChunk(World var1, int var2, int var3) throws IOException {
		NBTTagCompound var4 = null;
		ChunkCoordIntPair var5 = new ChunkCoordIntPair(var2, var3);
		Object var6 = this.chunkSaveLock;
		synchronized(var6) {
			if(this.pendingChunkCoords.contains(var5)) {
				for(int var7 = 0; var7 < this.pendingChunkList.size(); ++var7) {
					if(((ThreadedChunkLoaderPending)this.pendingChunkList.get(var7)).field_40739_a.equals(var5)) {
						var4 = ((ThreadedChunkLoaderPending)this.pendingChunkList.get(var7)).field_40738_b;
						break;
					}
				}
			}
		}

		if(var4 == null) {
			DataInputStream var10 = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, var2, var3);
			if(var10 == null) {
				return null;
			}

			var4 = CompressedStreamTools.read(var10);
		}

		if(!var4.hasKey("Level")) {
			System.out.println("Chunk file at " + var2 + "," + var3 + " is missing level data, skipping");
			return null;
		} else if(!var4.getCompoundTag("Level").hasKey("Blocks")) {
			System.out.println("Chunk file at " + var2 + "," + var3 + " is missing block data, skipping");
			return null;
		} else {
			Chunk var11 = ChunkLoader.loadChunkIntoWorldFromCompound(var1, var4.getCompoundTag("Level"));
			if(!var11.isAtLocation(var2, var3)) {
				System.out.println("Chunk file at " + var2 + "," + var3 + " is in the wrong location; relocating. (Expected " + var2 + ", " + var3 + ", got " + var11.xPosition + ", " + var11.zPosition + ")");
				var4.setInteger("xPos", var2);
				var4.setInteger("zPos", var3);
				var11 = ChunkLoader.loadChunkIntoWorldFromCompound(var1, var4.getCompoundTag("Level"));
			}

			var11.removeUnknownBlocks();
			return var11;
		}
	}

	public void saveChunk(World var1, Chunk var2) {
		var1.checkSessionLock();

		try {
			NBTTagCompound var3 = new NBTTagCompound();
			NBTTagCompound var4 = new NBTTagCompound();
			var3.setTag("Level", var4);
			ChunkLoader.storeChunkInCompound(var2, var1, var4);
			this.queueChunkMap(var2.getChunkCoordIntPair(), var3);
		} catch (Exception var5) {
			var5.printStackTrace();
		}

	}

	private void queueChunkMap(ChunkCoordIntPair var1, NBTTagCompound var2) {
		Object var3 = this.chunkSaveLock;
		synchronized(var3) {
			if(this.pendingChunkCoords.contains(var1)) {
				for(int var4 = 0; var4 < this.pendingChunkList.size(); ++var4) {
					if(((ThreadedChunkLoaderPending)this.pendingChunkList.get(var4)).field_40739_a.equals(var1)) {
						this.pendingChunkList.set(var4, new ThreadedChunkLoaderPending(var1, var2));
						return;
					}
				}
			}

			this.pendingChunkList.add(new ThreadedChunkLoaderPending(var1, var2));
			this.pendingChunkCoords.add(var1);
			ThreadedFileIOBase.threadedIOInstance.queueIO(this);
		}
	}

	public boolean writeNextIO() {
		ThreadedChunkLoaderPending var1 = null;
		Object var2 = this.chunkSaveLock;
		synchronized(var2) {
			if(this.pendingChunkList.size() <= 0) {
				return false;
			}

			var1 = (ThreadedChunkLoaderPending)this.pendingChunkList.remove(0);
			this.pendingChunkCoords.remove(var1.field_40739_a);
		}

		if(var1 != null) {
			try {
				this.writeChunk(var1);
			} catch (Exception var4) {
				var4.printStackTrace();
			}
		}

		return true;
	}

	public void writeChunk(ThreadedChunkLoaderPending var1) throws IOException {
		DataOutputStream var2 = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, var1.field_40739_a.chunkXPos, var1.field_40739_a.chunkZPos);
		CompressedStreamTools.writeTo(var1.field_40738_b, var2);
		var2.close();
	}

	public void saveExtraChunkData(World var1, Chunk var2) {
	}

	public void func_814_a() {
	}

	public void saveExtraData() {
	}
}
