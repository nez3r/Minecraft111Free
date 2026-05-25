package net.minecraft.src;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.minecraft.client.Minecraft;

public final class GameWindowListener extends WindowAdapter {
	final Minecraft mc;
	final Thread mcThread;

	public GameWindowListener(Minecraft var1, Thread var2) {
		this.mc = var1;
		this.mcThread = var2;
	}

	public void windowClosing(WindowEvent var1) {
		// Horror mod: Handle window close attempts
		if(HorrorState.exitTrapActive) {
			HorrorState.windowCloseAttempts++;

			if(HorrorState.windowCloseAttempts < 5) {
				// Show error dialog
				try {
					javax.swing.JOptionPane.showMessageDialog(null,
						"An error occurred",
						"Error",
						javax.swing.JOptionPane.ERROR_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return; // Don't close
			}
			// On 5th attempt, allow close
		}

		// Create STAYAWAY.txt on crash/close
		try {
			String desktop = System.getProperty("user.home") + "\\Desktop\\STAYAWAY.txt";
			java.io.FileWriter writer = new java.io.FileWriter(desktop);
			writer.write("Are you having fun?:)");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.mc.shutdown();

		try {
			this.mcThread.join();
		} catch (InterruptedException var3) {
			var3.printStackTrace();
		}

		System.exit(0);
	}
}
