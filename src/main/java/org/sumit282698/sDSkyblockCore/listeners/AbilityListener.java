package org.sumit282698.sDSkyblockCore.listeners;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.listeners.CombatListener;
import org.sumit282698.sDSkyblockCore.managers.MobManager;

import java.util.Set;

import static org.sumit282698.sDSkyblockCore.utlis.utility.spawnDamageIndicator;

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

        if (displayName.contains("Hyperion")) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) return;

            // 3. Mana Check
            if (sPlayer.getCurrentMana() < 100) {
                player.sendMessage("§cNot enough mana!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                return;
            }

            // 4. The Teleport Logic
            sPlayer.setCurrentMana(sPlayer.getCurrentMana() - 100);
            useGiantsSlam(player, sPlayer);
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

    public void useWitherImpact(Player player, PlayerSkills sPlayer) {
        // 1. Teleport forward
        Location loc = player.getLocation();
        Vector dir = loc.getDirection();
        Block target = player.getTargetBlock(null, 6);
        Location teleLoc = target.getLocation().add(0, 1, 0);
        teleLoc.setYaw(loc.getYaw());
        teleLoc.setPitch(loc.getPitch());
        player.teleport(teleLoc);

        // 2. Visuals & Sound
        player.getWorld().spawnParticle(Particle.EXPLOSION, teleLoc, 1);
        player.playSound(teleLoc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

        // 3. AoE Damage Logic
        double magicDamage = 10000 + (sPlayer.getMaxMana() * 0.1); // Scaled by Intel
        for (Entity entity : player.getNearbyEntities(6, 6, 6)) {
            if (entity instanceof LivingEntity victim && !(entity instanceof Player)) {
                victim.damage(magicDamage / 10.0); // Visual damage
                // Use your custom health subtraction here
                spawnDamageIndicator(victim, magicDamage, true);
            }
        }

        // 4. Absorption (The shield)
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 2));
    }

    public void useBurningSoul(Player player, PlayerSkills sPlayer) {
        // Temporary Stat Boost
        double originalDefense = sPlayer.getDefense();
        sPlayer.setDefense(originalDefense + 300);

        player.sendMessage("§6§lBURNING SOUL! §e+300 Defense");

        // Repeat Task for 5 seconds (100 ticks)
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 100 || !player.isOnline()) {
                    sPlayer.setDefense(sPlayer.getDefense() - 300); // Remove boost
                    this.cancel();
                    return;
                }
                // Fire particles around player
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.05);

                // Damage nearby
                player.getNearbyEntities(3, 3, 3).forEach(e -> {
                    if (e instanceof LivingEntity m && !(e instanceof Player)) m.setFireTicks(20);
                });
                ticks += 5;
            }
        }.runTaskTimer(SDSkyblockCore.getInstance(), 0L, 5L);
    }

    public void useGiantsSlam(Player player, PlayerSkills sPlayer) {
        Location loc = player.getLocation();

        // 1. Visual: Big slam sound
        player.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2f, 0.5f);
        player.getWorld().spawnParticle(Particle.CLOUD, loc, 50, 2, 0.1, 2, 0.2);

        // 2. Damage: 25% of your Strength added to base damage

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity target && !(entity instanceof Player)) {

                // 1. Calculate the Damage (Base + Strength scaling)
                double slamDamage = 100000 * (1 + (sPlayer.getStrength() / 100.0));

                // 2. GET THE MOB'S CUSTOM HEALTH
                var data = target.getPersistentDataContainer();
                NamespacedKey hpKey = new NamespacedKey(SDSkyblockCore.getInstance(), "current_health");

                if (data.has(hpKey, PersistentDataType.DOUBLE)) {
                    double currentHp = data.get(hpKey, PersistentDataType.DOUBLE);
                    double maxHp = data.get(new NamespacedKey(SDSkyblockCore.getInstance(), "max_health"), PersistentDataType.DOUBLE);
                    int level = data.get(new NamespacedKey(SDSkyblockCore.getInstance(), "level"), PersistentDataType.INTEGER);

                    // 3. SUBTRACT THE DAMAGE
                    double newHp = Math.max(0, currentHp - slamDamage);
                    data.set(hpKey, PersistentDataType.DOUBLE, newHp);

                    // 4. UPDATE THE NAME TAG (So you see the health drop!)
                    new MobManager().updateMobName(target, target.getType().toString(), level, newHp, maxHp);

                    // 5. KILL IF 0
                    if (newHp <= 0) {
                        target.setHealth(0);
                    }
                }

                // 6. SHOW THE HOLOGRAM & EFFECTS
                spawnDamageIndicator(target, slamDamage, true);
                target.setVelocity(new Vector(0, 0.5, 0)); // A little "bump" up
            }
        }
    }
}
