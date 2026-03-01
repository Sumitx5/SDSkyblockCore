package org.sumit282698.sDSkyblockCore.utlis;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

public class utility {

    public static void spawnDamageIndicator(LivingEntity entity, double damage, boolean isCrit) {
        String text = isCrit ? "§f✧ §e" + (int)damage + " §f✧" : "§7" + (int)damage;

        Location spawnLoc = entity.getLocation().add(Math.random() * 1.4 - 0.7, 1.5, Math.random() * 1.4 - 0.7);

        ArmorStand hologram = entity.getWorld().spawn(spawnLoc, ArmorStand.class, as -> {
            as.setVisible(false);
            as.setMarker(true);
            as.setGravity(false);
            as.setCustomName(text);
            as.setCustomNameVisible(true);
            as.setSmall(true);
        });

        Bukkit.getScheduler().runTaskLater(SDSkyblockCore.getInstance(), hologram::remove, 20L);
    }
}