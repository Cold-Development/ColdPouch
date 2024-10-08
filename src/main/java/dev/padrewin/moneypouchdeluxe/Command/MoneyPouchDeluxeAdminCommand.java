package dev.padrewin.moneypouchdeluxe.Command;

import dev.padrewin.moneypouchdeluxe.Exception.HologramHandler;
import dev.padrewin.moneypouchdeluxe.MoneyPouchDeluxe;
import dev.padrewin.moneypouchdeluxe.Pouch;
import dev.padrewin.moneypouchdeluxe.EconomyType.EconomyType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import java.util.*;

public class MoneyPouchDeluxeAdminCommand implements CommandExecutor, TabCompleter {

    private final MoneyPouchDeluxe plugin;
    private final HologramHandler hologramHandler;

    public MoneyPouchDeluxeAdminCommand(MoneyPouchDeluxe plugin, HologramHandler hologramHandler) {
        this.plugin = plugin;
        this.hologramHandler = hologramHandler;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equals("list")) {
                for (Pouch pouch : plugin.getPouches()) {
                    sender.sendMessage(ChatColor.GOLD + pouch.getId() + " " + ChatColor.YELLOW + "(min: " +
                            pouch.getMinRange() + ", max: " + pouch.getMaxRange() + ", economy: " +
                            pouch.getEconomyType().toString() + " [" + pouch.getEconomyType().getPrefix() +
                            ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + pouch.getEconomyType().getSuffix() + "])");
                }
                return true;
            } else if (args[0].equals("economy") || args[0].equals("economies")) {
                for (Map.Entry<String, EconomyType> economyTypeEntry : plugin.getEconomyTypes().entrySet()) {
                    sender.sendMessage(ChatColor.GOLD + economyTypeEntry.getKey() + " " + ChatColor.YELLOW
                            + economyTypeEntry.getValue().toString() + " [" + economyTypeEntry.getValue().getPrefix() +
                            ChatColor.DARK_GRAY + "/" + ChatColor.YELLOW + economyTypeEntry.getValue().getSuffix() + "])");
                }
                return true;
            } else if (args[0].equals("reload")) {
                plugin.reload();
                String reloadMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.reloaded")));
                sender.sendMessage(reloadMessage);
                return true;
            } else if (args[0].equals("killholo")) {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            if (livingEntity.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_hologram"), PersistentDataType.BYTE)) {
                                livingEntity.remove();
                            }
                        }
                    }
                }
                String hologramsRemovedMessage = plugin.getMessage(MoneyPouchDeluxe.Message.KILL_HOLO);
                sender.sendMessage(hologramsRemovedMessage);
                return true;
            } else if (args[0].equals("toggleholo")) {
                boolean enabled = plugin.areHologramsEnabled();
                plugin.setHologramsEnabled(!enabled);

                if (!enabled) {
                    hologramHandler.killAllPouchHolograms();
                }

                String messageKey = enabled ? "messages.holograms_disabled" : "messages.holograms_enabled";
                String message = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString(messageKey)));

                sender.sendMessage(message);
                return true;
            }
        }

        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "MoneyPouchDeluxe (ver " + plugin.getDescription().getVersion() + ")");
        sender.sendMessage(ChatColor.GRAY + "<> = required, [] = optional");
        sender.sendMessage(ChatColor.YELLOW + "/mpa | /cpa :" + ChatColor.GRAY + " view this menu");
        sender.sendMessage(ChatColor.YELLOW + "/mp (/cp) <tier> [player] [amount] :" + ChatColor.GRAY + " give <item> to [player] (or self if blank)");
        sender.sendMessage(ChatColor.YELLOW + "/mpshop | /cpshop :" + ChatColor.GRAY + " open the shop");
        sender.sendMessage(ChatColor.YELLOW + "/mpa list | /cpa list :" + ChatColor.GRAY + " list all pouches");
        sender.sendMessage(ChatColor.YELLOW + "/mpa economies | /cpa economies :" + ChatColor.GRAY + " list all economies");
        sender.sendMessage(ChatColor.YELLOW + "/mpa reload | /cpa rload :" + ChatColor.GRAY + " reload the config");
        sender.sendMessage(ChatColor.YELLOW + "/mpa killholo | /cpa killholo :" + ChatColor.GRAY + " kill holo made by plugin");
        sender.sendMessage(ChatColor.YELLOW + "/mpa toggleholo | /cpa toggleholo:" + ChatColor.GRAY + " enable or disable holograms for pouches");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                List<String> options = Arrays.asList("list", "economies", "reload", "killholo", "toggleholo");
                List<String> completions = new ArrayList<>();
                StringUtil.copyPartialMatches(args[0], options, completions);
                Collections.sort(completions);
                return completions;
            }
        }
        return null;
    }
}