package org.sumit282698.sDSkyblockCore.tasks;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;

public class StatsTask extends BukkitRunnable {

    private final SDSkyblockCore plugin;

    public StatsTask(SDSkyblockCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) continue;

            // ------------------------
            // Initialize bonus stats
            // ------------------------
            double bonusDefense = 0;
            double bonusHealth = 0;
            double bonusIntelligence = 0;

            // Pre-create NamespacedKeys to avoid repeated instantiation
            NamespacedKey typeKey = new NamespacedKey(plugin, "item_type");
            NamespacedKey defKey = new NamespacedKey(plugin, "defense");
            NamespacedKey healthKey = new NamespacedKey(plugin, "max_health");
            NamespacedKey intelKey = new NamespacedKey(plugin, "intelligence");

            // ------------------------
            // Scan armor slots
            // ------------------------
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor == null || !armor.hasItemMeta()) continue;

                var data = armor.getItemMeta().getPersistentDataContainer();
                String type = data.getOrDefault(typeKey, PersistentDataType.STRING, "ITEM");

                if ("ARMOR".equals(type)) {
                    bonusDefense += data.getOrDefault(defKey, PersistentDataType.DOUBLE, 0.0);
                    bonusHealth += data.getOrDefault(healthKey, PersistentDataType.DOUBLE, 0.0);
                    bonusIntelligence += data.getOrDefault(intelKey, PersistentDataType.DOUBLE, 0.0);
                }
            }

            // ------------------------
            // Scan main hand (weapon/utility)
            // ------------------------
            ItemStack held = player.getInventory().getItemInMainHand();
            if (held != null && held.hasItemMeta()) {
                var data = held.getItemMeta().getPersistentDataContainer();
                bonusIntelligence += data.getOrDefault(intelKey, PersistentDataType.DOUBLE, 0.0);
            }

            // ------------------------
            // Apply Skyblock stats
            // ------------------------
            sPlayer.setDefense(bonusDefense);

            // Mana calculation: 1 Intelligence = 1 Max Mana
            double baseMana = 100;
            sPlayer.setMaxMana(baseMana + bonusIntelligence);
            sPlayer.setCurrentMana(Math.min(sPlayer.getMaxMana(), sPlayer.getCurrentMana()));

            // Health calculation
            double baseHealth = 100; // Skyblock base HP
            double newMaxHealth = baseHealth + bonusHealth;
            sPlayer.setMaxHealth(newMaxHealth);

            // Map Skyblock health (0–100+) to Minecraft's 20 HP scale
            double healthPercent = sPlayer.getCurrentHealth() / sPlayer.getMaxHealth();
            double vanillaHealth = Math.max(0.1, Math.min(20.0, healthPercent * 20.0));

            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(newMaxHealth / 5.0);
            player.setHealth(vanillaHealth);

            // ------------------------
            // Mana regeneration (2% per tick)
            // ------------------------
            double regenAmount = sPlayer.getMaxMana() * 0.02;
            sPlayer.setCurrentMana(Math.min(sPlayer.getMaxMana(), sPlayer.getCurrentMana() + regenAmount));

            // ------------------------
            // Update Action Bar
            // ------------------------
            sendActionBar(player, sPlayer);
        }
    }

    private void sendActionBar(Player player, PlayerSkills sPlayer) {
        String healthStr = "§c" + (int) sPlayer.getCurrentHealth() + "/" + (int) sPlayer.getMaxHealth() + "❤";
        String defenseStr = "§a" + (int) sPlayer.getDefense() + "❈ Defense";
        String manaStr = "§b" + (int) sPlayer.getCurrentMana() + "/" + (int) sPlayer.getMaxMana() + "✎ Mana";

        String actionBar = healthStr + "     " + defenseStr + "     " + manaStr;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
    }
}