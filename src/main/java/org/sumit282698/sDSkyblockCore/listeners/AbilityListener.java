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

public class AbilityListener implements Listener {

    private final SDSkyblockCore plugin;

    private final NamespacedKey hpKey;
    private final NamespacedKey maxHpKey;
    private final NamespacedKey levelKey;

    public AbilityListener(SDSkyblockCore plugin) {
        this.plugin = plugin;
        this.hpKey = new NamespacedKey(plugin, "current_health");
        this.maxHpKey = new NamespacedKey(plugin, "max_health");
        this.levelKey = new NamespacedKey(plugin, "level");
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null || !event.getItem().hasItemMeta()) return;

        Player player = event.getPlayer();
        var meta = event.getItem().getItemMeta();

        if (!meta.hasDisplayName()) return;
        String displayName = meta.getDisplayName();

        if (displayName.contains("Aspect of the End")) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) return;

            if (sPlayer.getCurrentMana() < 50) {
                player.sendMessage("§cNot enough mana!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                return;
            }

            sPlayer.useMana(50);
            teleportForward(player, 8);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        }

        if (displayName.contains("Hyperion")) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) return;

            if (sPlayer.getCurrentMana() < 100) {
                player.sendMessage("§cNot enough mana!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                return;
            }

            sPlayer.useMana(100);
            useWitherImpact(player, sPlayer);
        }

        if (displayName.contains("Burning Soul")) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) return;

            if (sPlayer.getCurrentMana() < 60) {
                player.sendMessage("§cNot enough mana!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                return;
            }

            sPlayer.useMana(60);
            useBurningSoul(player, sPlayer);
        }

        if (displayName.contains("Giant's Sword")) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) return;

            if (sPlayer.getCurrentMana() < 100) {
                player.sendMessage("§cNot enough mana!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
                return;
            }

            sPlayer.useMana(100);
            useGiantsSlam(player, sPlayer);
        }
    }

    private void teleportForward(Player player, int distance) {
        Location loc = player.getLocation();
        Block targetBlock = player.getTargetBlock(null, distance);

        Location targetLoc = targetBlock.getLocation().add(0.5, 1.0, 0.5);
        targetLoc.setYaw(loc.getYaw());
        targetLoc.setPitch(loc.getPitch());

        player.teleport(targetLoc);
    }

    public void useWitherImpact(Player player, PlayerSkills sPlayer) {
        Location loc = player.getLocation();
        Block target = player.getTargetBlock(null, 6);
        Location teleLoc = target.getLocation().add(0.5, 1.0, 0.5);
        teleLoc.setYaw(loc.getYaw());
        teleLoc.setPitch(loc.getPitch());
        player.teleport(teleLoc);

        teleLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, teleLoc, 1);
        player.playSound(teleLoc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

        double magicDamage = 10000 + (sPlayer.getMaxMana() * 0.1);
        for (Entity entity : player.getNearbyEntities(6, 6, 6)) {
            if (entity instanceof LivingEntity victim && !(entity instanceof Player)) {
                victim.damage(1.0);
                victim.setNoDamageTicks(0);
                applyCustomDamage(victim, magicDamage);
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 2));
    }

    public void useBurningSoul(Player player, PlayerSkills sPlayer) {
        sPlayer.setBonusStats(0, 300,0,0,0,0);
        player.sendMessage("§6§lBURNING SOUL! §e+300 Defense");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 100 || !player.isOnline()) {
                    // Reset bonus stats back to 0 when the skill expires
                    sPlayer.setBonusStats(0, 0,0,0,0,0);
                    this.cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.05);

                player.getNearbyEntities(3, 3, 3).forEach(e -> {
                    if (e instanceof LivingEntity m && !(e instanceof Player)) m.setFireTicks(20);
                });
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    public void useGiantsSlam(Player player, PlayerSkills sPlayer) {
        Location loc = player.getLocation();

        player.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2f, 0.5f);
        player.getWorld().spawnParticle(Particle.CLOUD, loc, 50, 2, 0.1, 2, 0.2);

        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof LivingEntity target && !(entity instanceof Player)) {
                double slamDamage = 100000 * (1 + (sPlayer.getStrength() / 100.0));
                applyCustomDamage(target, slamDamage);
                target.setVelocity(new Vector(0, 0.5, 0));
            }
        }
    }

    private void applyCustomDamage(LivingEntity target, double damage) {
        var data = target.getPersistentDataContainer();

        if (data.has(hpKey, PersistentDataType.DOUBLE)) {
            double currentHp = data.get(hpKey, PersistentDataType.DOUBLE);
            double maxHp = data.get(maxHpKey, PersistentDataType.DOUBLE);
            int level = data.get(levelKey, PersistentDataType.INTEGER);

            double newHp = Math.max(0, currentHp - damage);
            data.set(hpKey, PersistentDataType.DOUBLE, newHp);

            plugin.getMobManager().updateMobName(target, target.getType().toString(), level, newHp, maxHp);

            if (newHp <= 0) {
                target.setHealth(0);
            }
        }

        plugin.getUtility().spawnDamageIndicator(target, damage, true);
    }
}