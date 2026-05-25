package net.minecraft.src;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedStreamTools {
	public static NBTTagCompound loadGzippedCompoundFromOutputStream(InputStream var0) throws IOException {
		DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var0)));

		NBTTagCompound var2;
		try {
			var2 = read(var1);
		} finally {
			var1.close();
		}

		return var2;
	}

	public static void writeGzippedCompoundToOutputStream(NBTTagCompound var0, OutputStream var1) throws IOException {
		DataOutputStream var2 = new DataOutputStream(new GZIPOutputStream(var1));

		try {
			writeTo(var0, var2);
		} finally {
			var2.close();
		}

	}

	public static NBTTagCompound loadMapFromByteArray(byte[] var0) throws IOException {
		DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(var0))));

		NBTTagCompound var2;
		try {
			var2 = read(var1);
		} finally {
			var1.close();
		}

		return var2;
	}

	public static byte[] writeMapToByteArray(NBTTagCompound var0) throws IOException {
		ByteArrayOutputStream var1 = new ByteArrayOutputStream();
		DataOutputStream var2 = new DataOutputStream(new GZIPOutputStream(var1));

		try {
			writeTo(var0, var2);
		} finally {
			var2.close();
		}

		return var1.toByteArray();
	}

	public static void saveMapToFileWithBackup(NBTTagCompound var0, File var1) throws IOException {
		File var2 = new File(var1.getAbsolutePath() + "_tmp");
		if(var2.exists()) {
			var2.delete();
		}

		saveMapToFile(var0, var2);
		if(var1.exists()) {
			var1.delete();
		}

		if(var1.exists()) {
			throw new IOException("Failed to delete " + var1);
		} else {
			var2.renameTo(var1);
		}
	}

	public static void saveMapToFile(NBTTagCompound var0, File var1) throws IOException {
		DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

		try {
			writeTo(var0, var2);
		} finally {
			var2.close();
		}

	}

	public static NBTTagCompound writeMapToFileUncompressed(File var0) throws IOException {
		if(!var0.exists()) {
			return null;
		} else {
			DataInputStream var1 = new DataInputStream(new FileInputStream(var0));

			NBTTagCompound var2;
			try {
				var2 = read(var1);
			} finally {
				var1.close();
			}

			return var2;
		}
	}

	public static NBTTagCompound read(DataInput var0) throws IOException {
		NBTBase var1 = NBTBase.readTag(var0);
		if(var1 instanceof NBTTagCompound) {
			return (NBTTagCompound)var1;
		} else {
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void writeTo(NBTTagCompound var0, DataOutput var1) throws IOException {
		NBTBase.writeTag(var0, var1);
	}
}
