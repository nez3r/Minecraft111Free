package net.minecraft.src;

import org.lwjgl.opengl.GL11;

class GuiSlotServer extends GuiSlot {
	final GuiMultiplayer field_35410_a;

	public GuiSlotServer(GuiMultiplayer var1) {
		super(var1.mc, var1.width, var1.height, 32, var1.height - 64, 36);
		this.field_35410_a = var1;
	}

	protected int getSize() {
		return GuiMultiplayer.getServerList(this.field_35410_a).size();
	}

	protected void elementClicked(int var1, boolean var2) {
		GuiMultiplayer.setSelectedServer(this.field_35410_a, var1);
		boolean var3 = GuiMultiplayer.getSelectedServer(this.field_35410_a) >= 0 && GuiMultiplayer.getSelectedServer(this.field_35410_a) < this.getSize();
		GuiMultiplayer.getButtonSelect(this.field_35410_a).enabled = var3;
		GuiMultiplayer.getButtonEdit(this.field_35410_a).enabled = var3;
		GuiMultiplayer.getButtonDelete(this.field_35410_a).enabled = var3;
		if(var2 && var3) {
			GuiMultiplayer.joinServer(this.field_35410_a, var1);
		}

	}

	protected boolean isSelected(int var1) {
		return var1 == GuiMultiplayer.getSelectedServer(this.field_35410_a);
	}

	protected int getContentHeight() {
		return GuiMultiplayer.getServerList(this.field_35410_a).size() * 36;
	}

	protected void drawBackground() {
		this.field_35410_a.drawDefaultBackground();
	}

	protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
		ServerNBTStorage var6 = (ServerNBTStorage)GuiMultiplayer.getServerList(this.field_35410_a).get(var1);
		Object var7 = GuiMultiplayer.getLock();
		synchronized(var7) {
			if(GuiMultiplayer.getThreadsPending() < 5 && !var6.polled) {
				var6.polled = true;
				var6.lag = -2L;
				var6.motd = "";
				var6.playerCount = "";
				GuiMultiplayer.incrementThreadsPending();
				(new ThreadPollServers(this, var6)).start();
			}
		}

		this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.name, var2 + 2, var3 + 1, 16777215);
		this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.motd, var2 + 2, var3 + 12, 8421504);
		this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.playerCount, var2 + 215 - this.field_35410_a.fontRenderer.getStringWidth(var6.playerCount), var3 + 12, 8421504);
		this.field_35410_a.drawString(this.field_35410_a.fontRenderer, var6.host, var2 + 2, var3 + 12 + 11, 3158064);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_35410_a.mc.renderEngine.bindTexture(this.field_35410_a.mc.renderEngine.getTexture("/gui/icons.png"));
		boolean var12 = false;
		boolean var8 = false;
		String var9 = "";
		byte var13;
		int var14;
		if(var6.polled && var6.lag != -2L) {
			var13 = 0;
			var8 = false;
			if(var6.lag < 0L) {
				var14 = 5;
			} else if(var6.lag < 150L) {
				var14 = 0;
			} else if(var6.lag < 300L) {
				var14 = 1;
			} else if(var6.lag < 600L) {
				var14 = 2;
			} else if(var6.lag < 1000L) {
				var14 = 3;
			} else {
				var14 = 4;
			}

			if(var6.lag < 0L) {
				var9 = "(no connection)";
			} else {
				var9 = var6.lag + "ms";
			}
		} else {
			var13 = 1;
			var14 = (int)(System.currentTimeMillis() / 100L + (long)(var1 * 2) & 7L);
			if(var14 > 4) {
				var14 = 8 - var14;
			}

			var9 = "Polling..";
		}

		this.field_35410_a.drawTexturedModalRect(var2 + 205, var3, 0 + var13 * 10, 176 + var14 * 8, 10, 8);
		byte var10 = 4;
		if(this.field_35409_k >= var2 + 205 - var10 && this.field_35408_l >= var3 - var10 && this.field_35409_k <= var2 + 205 + 10 + var10 && this.field_35408_l <= var3 + 8 + var10) {
			GuiMultiplayer.func_35327_a(this.field_35410_a, var9);
		}

	}
}
