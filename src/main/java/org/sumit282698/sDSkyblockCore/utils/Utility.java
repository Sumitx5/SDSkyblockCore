package org.sumit282698.sDSkyblockCore.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

import java.util.concurrent.ThreadLocalRandom;

public class Utility {

    private final SDSkyblockCore plugin;

    public Utility(SDSkyblockCore plugin) {
        this.plugin = plugin;
    }

    public void spawnDamageIndicator(LivingEntity entity, double damage, boolean isCrit) {
        if (entity == null || entity.getWorld() == null) return;

        String text;
        int displayDamage = (int) Math.ceil(damage);

        if (isCrit) {
            text = "§f✧ §e" + displayDamage + " §f✧";
        } else {
            text = "§7" + displayDamage;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        double offsetX = random.nextDouble(-0.6, 0.6);
        double offsetZ = random.nextDouble(-0.6, 0.6);

        double targetHeight = entity.getHeight() * 0.75;
        Location spawnLoc = entity.getLocation().add(offsetX, targetHeight, offsetZ);

        entity.getWorld().spawn(spawnLoc, ArmorStand.class, hologram -> {
            hologram.setVisible(false);
            hologram.setMarker(true);
            hologram.setGravity(false);
            hologram.setPersistent(false);
            hologram.setCustomName(text);
            hologram.setCustomNameVisible(true);
            hologram.setSmall(true);
            hologram.setCanPickupItems(false);
            hologram.setArms(false);
            hologram.setBasePlate(false);

            plugin.getServer().getScheduler().runTaskLater(plugin, hologram::remove, 20L);
        });
    }
}