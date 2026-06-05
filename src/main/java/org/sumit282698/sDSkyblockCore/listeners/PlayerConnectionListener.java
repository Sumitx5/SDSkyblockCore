package org.sumit282698.sDSkyblockCore.listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

import java.util.UUID;

public class PlayerConnectionListener implements Listener {

    private final SDSkyblockCore plugin;

    public PlayerConnectionListener(SDSkyblockCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getProfileManager().createProfile(uuid);
        PlayerSkills sPlayer = plugin.getProfileManager().getProfile(uuid);

        if (sPlayer == null) {
            plugin.getLogger().warning("Could not create stats profile for " + player.getName());
            return;
        }
        plugin.getDatabase().loadSPlayer(sPlayer);

        var maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr != null) {
            double targetVanillaMax = sPlayer.getMaxHealth() / 5.0;
            maxHealthAttr.setBaseValue(targetVanillaMax);
            player.setHealth(targetVanillaMax);
        }
        sPlayer.setCurrentHealth(sPlayer.getMaxHealth());
        sPlayer.setCurrentMana(sPlayer.getMaxMana());

        player.sendMessage("§a§lSKYBLOCK §7Your custom profile and stats have loaded!");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PlayerSkills sPlayer = plugin.getProfileManager().getProfile(uuid);

        if (sPlayer != null) {
            plugin.getDatabase().saveSPlayer(sPlayer);
            plugin.getProfileManager().removeProfile(uuid);
        }
    }
}