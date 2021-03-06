package com.elmakers.mine.bukkit.protection;

import me.NoChance.PvPManager.PvPManager;
import me.NoChance.PvPManager.PvPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * This a manager specifically for the PvPManager plugin.
 *
 * This is why it has kind of a funky name.
 */
public class PvPManagerManager implements PVPManager {

    private boolean enabled = false;
    private PvPManager manager = null;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled && manager != null;
    }

    public void initialize(Plugin plugin) {
        if (enabled) {
            try {
                Plugin pvpPlugin = plugin.getServer().getPluginManager().getPlugin("PvPManager");
                if (pvpPlugin instanceof PvPManager) {
                    manager = (PvPManager)pvpPlugin;
                }
            } catch (Throwable ex) {
            }

            if (manager != null) {
                plugin.getLogger().info("PvPManager found, will respect PVP settings");
            }
        } else {
            manager = null;
        }
    }

    @Override
    public boolean isPVPAllowed(Player player, Location location) {
        if (!enabled || manager == null || player == null) return true;

        PvPlayer pvpPlayer = manager.getPlayerHandler().get(player);
        return pvpPlayer != null && pvpPlayer.hasPvPEnabled();
    }
}
