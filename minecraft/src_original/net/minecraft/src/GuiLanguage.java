package net.minecraft.src;

public class GuiLanguage extends GuiScreen {
	protected GuiScreen field_44009_a;
	private int field_44007_b = -1;
	private GuiSlotLanguage field_44008_c;
	private final GameSettings field_44006_d;
	private GuiSmallButton field_46029_e;

	public GuiLanguage(GuiScreen var1, GameSettings var2) {
		this.field_44009_a = var1;
		this.field_44006_d = var2;
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.getInstance();
		this.controlList.add(this.field_46029_e = new GuiSmallButton(6, this.width / 2 - 75, this.height - 38, var1.translateKey("gui.done")));
		this.field_44008_c = new GuiSlotLanguage(this);
		this.field_44008_c.registerScrollButtons(this.controlList, 7, 8);
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			if(var1.id != 5) {
				if(var1.id == 6) {
					this.field_44006_d.saveOptions();
					this.mc.displayGuiScreen(this.field_44009_a);
				} else {
					this.field_44008_c.actionPerformed(var1);
				}
			}

		}
	}

	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
	}

	protected void mouseMovedOrUp(int var1, int var2, int var3) {
		super.mouseMovedOrUp(var1, var2, var3);
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.field_44008_c.drawScreen(var1, var2, var3);
		if(this.field_44007_b <= 0) {
			this.mc.texturePackList.updateAvaliableTexturePacks();
			this.field_44007_b += 20;
		}

		StringTranslate var4 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, var4.translateKey("options.language"), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.fontRenderer, "(" + var4.translateKey("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.drawScreen(var1, var2, var3);
	}

	public void updateScreen() {
		super.updateScreen();
		--this.field_44007_b;
	}

	static GameSettings func_44005_a(GuiLanguage var0) {
		return var0.field_44006_d;
	}

	static GuiSmallButton func_46028_b(GuiLanguage var0) {
		return var0.field_46029_e;
	}
}
