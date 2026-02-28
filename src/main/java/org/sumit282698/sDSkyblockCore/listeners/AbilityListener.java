package org.sumit282698.sDSkyblockCore.listeners;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

import java.util.Set;

public class AbilityListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        // 1. Only detect Right Clicks with an Item
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null || !event.getItem().hasItemMeta()) return;

        Player player = event.getPlayer();
        String displayName = event.getItem().getItemMeta().getDisplayName();

        // 2. Identify the AOTE (We could use NamespacedKeys here too for better safety!)
        if (displayName.contains("Aspect of the End")) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) return;

            // 3. Mana Check
            if (sPlayer.getCurrentMana() < 50) {
                player.sendMessage("§cNot enough mana!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                return;
            }

            // 4. The Teleport Logic
            sPlayer.setCurrentMana(sPlayer.getCurrentMana() - 50);
            teleportForward(player, 8);

            //player.sendMessage("§3Used §6Instant Transmission §3(§b50 Mana§3)");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        }
    }

    private void teleportForward(Player player, int distance) {
        Location loc = player.getLocation();
        Vector dir = loc.getDirection();

        // Find the furthest safe block up to 'distance' blocks away
        Block targetBlock = player.getTargetBlock(null, distance);
        Location targetLoc = targetBlock.getLocation().add(0, 1, 0);
        targetLoc.setYaw(loc.getYaw());
        targetLoc.setPitch(loc.getPitch());

        player.teleport(targetLoc);
    }
}
