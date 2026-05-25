package net.minecraft.src;

import org.lwjgl.input.Keyboard;

public class GuiScreenAddServer extends GuiScreen {
	private GuiScreen field_35362_a;
	private GuiTextField serverAddress;
	private GuiTextField serverName;
	private ServerNBTStorage serverNBTStorage;

	public GuiScreenAddServer(GuiScreen var1, ServerNBTStorage var2) {
		this.field_35362_a = var1;
		this.serverNBTStorage = var2;
	}

	public void updateScreen() {
		this.serverName.updateCursorCounter();
		this.serverAddress.updateCursorCounter();
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, var1.translateKey("addServer.add")));
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.translateKey("gui.cancel")));
		this.serverName = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 76, 200, 20, this.serverNBTStorage.name);
		this.serverName.isFocused = true;
		this.serverName.setMaxStringLength(32);
		this.serverAddress = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 116, 200, 20, this.serverNBTStorage.host);
		this.serverAddress.setMaxStringLength(128);
		((GuiButton)this.controlList.get(0)).enabled = this.serverAddress.getText().length() > 0 && this.serverName.getText().length() > 0;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			if(var1.id == 1) {
				this.field_35362_a.deleteWorld(false, 0);
			} else if(var1.id == 0) {
				this.serverNBTStorage.name = this.serverName.getText();
				this.serverNBTStorage.host = this.serverAddress.getText();
				this.field_35362_a.deleteWorld(true, 0);
			}

		}
	}

	protected void keyTyped(char var1, int var2) {
		this.serverName.textboxKeyTyped(var1, var2);
		this.serverAddress.textboxKeyTyped(var1, var2);
		if(var1 == 9) {
			if(this.serverName.isFocused) {
				this.serverName.isFocused = false;
				this.serverAddress.isFocused = true;
			} else {
				this.serverName.isFocused = true;
				this.serverAddress.isFocused = false;
			}
		}

		if(var1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}

		((GuiButton)this.controlList.get(0)).enabled = this.serverAddress.getText().length() > 0 && this.serverName.getText().length() > 0;
		if(((GuiButton)this.controlList.get(0)).enabled) {
			String var3 = this.serverAddress.getText().trim();
			String[] var4 = var3.split(":");
			if(var4.length > 2) {
				((GuiButton)this.controlList.get(0)).enabled = false;
			}
		}

	}

	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
		this.serverAddress.mouseClicked(var1, var2, var3);
		this.serverName.mouseClicked(var1, var2, var3);
	}

	public void drawScreen(int var1, int var2, float var3) {
		StringTranslate var4 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, var4.translateKey("addServer.title"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
		this.drawString(this.fontRenderer, var4.translateKey("addServer.enterName"), this.width / 2 - 100, 63, 10526880);
		this.drawString(this.fontRenderer, var4.translateKey("addServer.enterIp"), this.width / 2 - 100, 104, 10526880);
		this.serverName.drawTextBox();
		this.serverAddress.drawTextBox();
		super.drawScreen(var1, var2, var3);
	}
}
