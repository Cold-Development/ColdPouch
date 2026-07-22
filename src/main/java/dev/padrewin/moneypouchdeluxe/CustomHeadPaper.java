package dev.padrewin.moneypouchdeluxe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomHeadPaper {

    private static final Pattern TEXTURE_URL = Pattern.compile("\\\"url\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    public static ItemStack getCustomSkull(String textureValue) {
        // Uses the public Bukkit API.  Paper's old com.destroystokyo profile
        // classes are not available on recent Paper/Spigot versions.

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        if (!(skull.getItemMeta() instanceof SkullMeta skullMeta)) {
            return skull;
        }

        URL textureUrl = extractTextureUrl(textureValue);
        if (textureUrl == null) {
            Bukkit.getLogger().warning("[MoneyPouchDeluxe] Invalid player-head texture. "
                    + "Use a Base64 texture value or a textures.minecraft.net URL.");
            return skull;
        }

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        textures.setSkin(textureUrl);
        profile.setTextures(textures);

        skullMeta.setOwnerProfile(profile);
        skull.setItemMeta(skullMeta);

        return skull;
    }

    private static URL extractTextureUrl(String textureValue) {
        if (textureValue == null || textureValue.isBlank()) {
            return null;
        }

        String value = textureValue.trim();
        if (!value.startsWith("http://") && !value.startsWith("https://")) {
            try {
                value = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        Matcher matcher = TEXTURE_URL.matcher(value);
        if (matcher.find()) {
            value = matcher.group(1).replace("\\/", "/");
        }

        try {
            return URI.create(value).toURL();
        } catch (IllegalArgumentException | java.net.MalformedURLException ignored) {
            return null;
        }
    }
}
