package net.minecraft.src;

import org.lwjgl.opengl.Display;

/**
 * Dynamic window title - changes title via Display.setTitle() based on events.
 */
public class DynamicWindowTitle {
    private static String originalTitle = "Minecraft 1.1.1 Free";
    private static boolean titleUpdated = false;
    private static EntityPlayer player;
    private static String forcedTitle;
    private static long forcedTitleEnd;

    public static void updateTitle(EntityPlayer player) {
        if (player == null) return;

        if (forcedTitle != null) {
            if (System.currentTimeMillis() < forcedTitleEnd) {
                Display.setTitle(forcedTitle);
                return;
            }
            forcedTitle = null;
        }

        // Update title based on health or events
        float healthPercent = (float)player.health / (float)player.getMaxHealth();

        String newTitle = originalTitle;

        if (healthPercent < 0.25F) {
            // Low health - show scary messages
            String[] messages = {
                "Don't look back...",
                "It's closer now",
                "Only 25% left...",
                "They're watching",
                "No escape"
            };
            newTitle = messages[(int)(Math.random() * messages.length)];
        } else if (Math.random() < 0.01) {
            // Sometimes show coordinates
            newTitle = "X: " + (int)player.posX + " Y: " + (int)player.posY + " Z: " + (int)player.posZ;
        }

        // Only update if different or randomly
        if (!newTitle.equals(originalTitle) || Math.random() < 0.05) {
            try {
                Display.setTitle(newTitle);
                titleUpdated = true;
            } catch (Exception e) {
                // Ignore errors from Display not being initialized
            }
        }
    }

    public static void resetTitle() {
        try {
            Display.setTitle(originalTitle);
            titleUpdated = false;
        } catch (Exception e) {
            // Ignore
        }
    }

    public static void setPlayer(EntityPlayer p) {
        player = p;
    }

    public static void triggerGlitchedTitle(EntityPlayer p) {
        if (p != null) {
            triggerTitle(p, "???", 5000L);
        }
    }

    public static void triggerTitle(EntityPlayer p, String title, long duration) {
        if (p == null || title == null) return;
        forcedTitle = title;
        forcedTitleEnd = System.currentTimeMillis() + duration;
        Display.setTitle(title);
    }
}
