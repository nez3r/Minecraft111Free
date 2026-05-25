package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

class GuiSlotLanguage extends GuiSlot {
	private ArrayList field_44013_b;
	private TreeMap field_44014_c;
	final GuiLanguage field_44015_a;

	public GuiSlotLanguage(GuiLanguage var1) {
		super(var1.mc, var1.width, var1.height, 32, var1.height - 65 + 4, 18);
		this.field_44015_a = var1;
		this.field_44014_c = StringTranslate.getInstance().func_44022_b();
		this.field_44013_b = new ArrayList();
		Iterator var2 = this.field_44014_c.keySet().iterator();

		while(var2.hasNext()) {
			String var3 = (String)var2.next();
			this.field_44013_b.add(var3);
		}

	}

	protected int getSize() {
		return this.field_44013_b.size();
	}

	protected void elementClicked(int var1, boolean var2) {
		StringTranslate.getInstance().func_44023_a((String)this.field_44013_b.get(var1));
		this.field_44015_a.mc.fontRenderer.func_44032_a(StringTranslate.getInstance().func_46110_d());
		GuiLanguage.func_44005_a(this.field_44015_a).field_44018_Q = (String)this.field_44013_b.get(var1);
		this.field_44015_a.fontRenderer.func_46123_b(StringTranslate.func_46109_d(GuiLanguage.func_44005_a(this.field_44015_a).field_44018_Q));
		GuiLanguage.func_46028_b(this.field_44015_a).displayString = StringTranslate.getInstance().translateKey("gui.done");
	}

	protected boolean isSelected(int var1) {
		return ((String)this.field_44013_b.get(var1)).equals(StringTranslate.getInstance().func_44024_c());
	}

	protected int getContentHeight() {
		return this.getSize() * 18;
	}

	protected void drawBackground() {
		this.field_44015_a.drawDefaultBackground();
	}

	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
		this.field_44015_a.fontRenderer.func_46123_b(true);
		this.field_44015_a.drawCenteredString(this.field_44015_a.fontRenderer, (String)this.field_44014_c.get(this.field_44013_b.get(var1)), this.field_44015_a.width / 2, var3 + 1, 16777215);
		this.field_44015_a.fontRenderer.func_46123_b(StringTranslate.func_46109_d(GuiLanguage.func_44005_a(this.field_44015_a).field_44018_Q));
	}
}
