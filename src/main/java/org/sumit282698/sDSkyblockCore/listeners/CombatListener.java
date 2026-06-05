package org.sumit282698.sDSkyblockCore.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.managers.MobManager;

import java.util.concurrent.ThreadLocalRandom;

public class CombatListener implements Listener {

    private final SDSkyblockCore plugin;
    private final MobManager mobManager;

    private final NamespacedKey damageKey;
    private final NamespacedKey hpKey;
    private final NamespacedKey maxHpKey;
    private final NamespacedKey levelKey;

    public CombatListener(SDSkyblockCore plugin, MobManager mobManager) {
        this.plugin = plugin;
        this.mobManager = mobManager;
        this.damageKey = new NamespacedKey(plugin, "damage");
        this.hpKey = new NamespacedKey(plugin, "current_health");
        this.maxHpKey = new NamespacedKey(plugin, "max_health");
        this.levelKey = new NamespacedKey(plugin, "level");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (target instanceof Player) return; // Prevent PvP confusion in this math loop

        PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
        if (sPlayer == null) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();

        double baseDamage = 0;
        if (weapon.hasItemMeta()) {
            baseDamage = weapon.getItemMeta().getPersistentDataContainer()
                    .getOrDefault(damageKey, PersistentDataType.DOUBLE, 0.0);
        }

        double strength = sPlayer.getStrength();
        double damage = (5.0 + baseDamage) * (1.0 + (strength / 100.0));

        boolean isCrit = (ThreadLocalRandom.current().nextDouble() * 100.0) < sPlayer.getCritChance();
        if (isCrit) {
            damage *= (1.0 + (sPlayer.getCritDamage() / 100.0));
        }

        var targetData = target.getPersistentDataContainer();

        if (targetData.has(hpKey, PersistentDataType.DOUBLE)) {
            double currentHp = targetData.get(hpKey, PersistentDataType.DOUBLE);
            double maxHp = targetData.get(maxHpKey, PersistentDataType.DOUBLE);
            int level = targetData.getOrDefault(levelKey, PersistentDataType.INTEGER, 1);

            double newHp = Math.max(0, currentHp - damage);
            targetData.set(hpKey, PersistentDataType.DOUBLE, newHp);

            mobManager.updateMobName(target, target.getType().toString(), level, newHp, maxHp);

            event.setDamage(0);

            if (newHp <= 0) {
                target.setHealth(0);
            }
        } else {
            event.setDamage(damage / 5.0);
        }

        plugin.getUtility().spawnDamageIndicator(target, damage, isCrit);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTakeDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
        if (sPlayer == null) return;

        double rawDamage = event.getDamage() * 5.0;
        double reduction = 100.0 / (100.0 + sPlayer.getDefense());
        double finalDamage = rawDamage * reduction;

        double updatedHp = Math.max(0, sPlayer.getCurrentHealth() - finalDamage);
        sPlayer.setCurrentHealth(updatedHp);

        event.setDamage(0);

        if (updatedHp <= 0) {
            player.setHealth(0);
            sPlayer.setCurrentHealth(sPlayer.getMaxHealth());
        }
    }
}