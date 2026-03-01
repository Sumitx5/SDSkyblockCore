package org.sumit282698.sDSkyblockCore.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.managers.MobManager;


public class CombatListener implements Listener {
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        if (!(event.getEntity() instanceof LivingEntity target)) return;

        PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
        ItemStack weapon = player.getInventory().getItemInMainHand();

        // 1. Get Weapon Base Damage
        double baseDamage = 0;
        if (weapon.hasItemMeta()) {
            baseDamage = weapon.getItemMeta().getPersistentDataContainer()
                    .getOrDefault(new NamespacedKey(SDSkyblockCore.getInstance(), "damage"), PersistentDataType.DOUBLE, 0.0);
        }

        // 2. The Formula
        double strength = sPlayer.getStrength();
        double critDamage = sPlayer.getCritDamage();

        double damage = (5 + baseDamage) * (1 + (strength / 100.0));

        // 3. Roll for Crit
        boolean isCrit = Math.random() * 100 < sPlayer.getCritChance();
        if (isCrit) {
            damage *= (1 + (sPlayer.getCritDamage() / 100.0));
        }
        spawnDamageIndicator(target, damage, isCrit);
        event.setDamage(damage / 5.0);

        var targetData = target.getPersistentDataContainer();
        NamespacedKey hpKey = new NamespacedKey(SDSkyblockCore.getInstance(), "current_health");

        if (targetData.has(hpKey, PersistentDataType.DOUBLE)) {
            double currentHp = targetData.get(hpKey, PersistentDataType.DOUBLE);
            double maxHp = targetData.get(new NamespacedKey(SDSkyblockCore.getInstance(), "max_health"), PersistentDataType.DOUBLE);
            int level = targetData.get(new NamespacedKey(SDSkyblockCore.getInstance(), "level"), PersistentDataType.INTEGER);

            // Calculate new health
            double newHp = Math.max(0, currentHp - damage);
            targetData.set(hpKey, PersistentDataType.DOUBLE, newHp);

            // Update Name Tag
            String name = target.getType().toString(); // Or get from a 'name' key
            new MobManager().updateMobName(target, name, level, newHp, maxHp);

            // If health is 0, kill the mob
            if (newHp <= 0) {
                target.setHealth(0);
            } else {
                // Prevent vanilla death, we handle the health!
                event.setDamage(0);
            }
        }
    }
    private void spawnDamageIndicator(LivingEntity entity, double damage, boolean isCrit) {
        // 1. Format the text (White for normal, colorful for Crits)
        String text;
        if (isCrit) {
            // Hypixel style: §f✧ §e1§62§c3 §f✧
            text = "§f✧ §e" + (int)damage + " §f✧";
        } else {
            text = "§7" + (int)damage;
        }

        // 2. Spawn the ArmorStand at the mob's location + a little height
        Location spawnLoc = entity.getLocation().add(
                Math.random() * 1.5 - 0.75, // Random X offset so numbers don't stack
                1.5,
                Math.random() * 1.5 - 0.75  // Random Z offset
        );

        ArmorStand hologram = entity.getWorld().spawn(spawnLoc, ArmorStand.class, as -> {
            as.setVisible(false);       // Invisible
            as.setMarker(true);        // No hitbox (can't hit it)
            as.setGravity(false);      // Doesn't fall
            as.setCustomName(text);    // The Damage Number
            as.setCustomNameVisible(true);
            as.setSmall(true);         // Smaller text look
        });

        // 3. Remove it after 1 second (20 ticks)
        Bukkit.getScheduler().runTaskLater(SDSkyblockCore.getInstance(), hologram::remove, 20L);
    }
    @EventHandler
    public void onPlayerTakeDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
        if (sPlayer == null) return;

        // 1. Calculate Damage reduction from Defense
        double rawDamage = event.getDamage() * 5; // Scale up mob damage
        double reduction = 100.0 / (100.0 + sPlayer.getDefense());
        double finalDamage = rawDamage * reduction;

        // 2. Subtract from SPlayer health
        sPlayer.setCurrentHealth(sPlayer.getCurrentHealth() - finalDamage);

        // 3. Cancel vanilla damage so the "red hearts" don't glitch
        event.setDamage(0);

        if (sPlayer.getCurrentHealth() <= 0) {
            player.setHealth(0); // Trigger actual death
            sPlayer.setCurrentHealth(sPlayer.getMaxHealth()); // Reset for respawn
        }
    }
}
