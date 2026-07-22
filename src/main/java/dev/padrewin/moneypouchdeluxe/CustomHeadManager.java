package dev.padrewin.moneypouchdeluxe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomHeadManager {

    private static final boolean IS_LEGACY = isLegacyVersion();

    public static ItemStack getCustomSkull(String textureURL) {
        //Bukkit.getLogger().info("[DEBUG] getCustomSkull() called with texture: " + textureURL);

        if (IS_LEGACY) {
            //Bukkit.getLogger().info("[DEBUG] Using legacy version (1.13 - 1.18)");
            return CustomHeadLegacy.getCustomSkull(textureURL);
        } else {
            //Bukkit.getLogger().info("[DEBUG] Using Bukkit PlayerProfile API (1.19+)");
            return CustomHeadPaper.getCustomSkull(textureURL);
        }
    }

    private static boolean isLegacyVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] parts = version.split("\\.");

        try {
            // Versions through 1.21 are written as 1.<minor>.<patch>, while
            // 26.1 starts with the release year.  The former code read the
            // second part of 26.1 as "1", incorrectly selecting the legacy
            // reflective implementation.
            if (parts.length >= 2 && "1".equals(parts[0])) {
                return Integer.parseInt(parts[1]) < 19;
            }
            return false;
        } catch (NumberFormatException ignored) {
            // If a fork supplies an unexpected version string, prefer the
            // supported Bukkit API over reflection into server internals.
            return false;
        }
    }
}
