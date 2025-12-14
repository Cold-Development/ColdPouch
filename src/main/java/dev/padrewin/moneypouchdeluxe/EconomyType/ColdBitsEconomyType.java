package dev.padrewin.moneypouchdeluxe.EconomyType;

import dev.padrewin.coldbits.ColdBitsAPI;
import dev.padrewin.moneypouchdeluxe.MoneyPouchDeluxe;
import org.bukkit.entity.Player;

public class ColdBitsEconomyType extends EconomyType {

    private final MoneyPouchDeluxe plugin;

    public ColdBitsEconomyType(MoneyPouchDeluxe plugin, String prefix, String suffix) {
        super(prefix, suffix);
        this.plugin = plugin;
    }

    @Override
    public void processPayment(Player player, long amount) {
        ColdBitsAPI coldbitsAPI = plugin.getColdBitsAPI();
        if (coldbitsAPI != null) {
            coldbitsAPI.give(player.getUniqueId(), (int) amount);
        } else {
            plugin.getLogger().warning("ColdBits API is not available. Could not process payment.");
        }
    }

    @Override
    public boolean doTransaction(Player player, long amount) {
        ColdBitsAPI coldBitsAPI = plugin.getColdBitsAPI();
        if (coldBitsAPI != null) {
            if (coldBitsAPI.look(player.getUniqueId()) >= amount) {
                coldBitsAPI.take(player.getUniqueId(), (int) amount);
                return true;
            }
        } else {
            plugin.getLogger().warning("ColdBits API is not available. Could not process transaction.");
        }
        return false;
    }

    @Override
    public String toString() {
        return "ColdBitsEconomyType";
    }
}
