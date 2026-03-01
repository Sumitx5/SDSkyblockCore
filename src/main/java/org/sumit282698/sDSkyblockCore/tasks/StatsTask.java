package org.sumit282698.sDSkyblockCore.tasks;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

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

            // Reset "Bonus" stats to 0 before each scan
            double bonusDefense = 0;
            double bonusHealth = 0;
            double bonusIntel = 0;

            // 1. SCAN ARMOR SLOTS
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor == null || !armor.hasItemMeta()) continue;

                var data = armor.getItemMeta().getPersistentDataContainer();
                String type = data.getOrDefault(new NamespacedKey(plugin, "item_type"), PersistentDataType.STRING, "ITEM");

                if (type.equals("ARMOR")) {
                    bonusDefense += data.getOrDefault(new NamespacedKey(plugin, "defense"), PersistentDataType.DOUBLE, 0.0);
                    bonusHealth += data.getOrDefault(new NamespacedKey(plugin, "max_health"), PersistentDataType.DOUBLE, 0.0);
                    bonusIntel += data.getOrDefault(new NamespacedKey(plugin, "intelligence"), PersistentDataType.DOUBLE, 0.0);
                }
            }

            // 2. SCAN HELD ITEM (Weapon/Utility)
            ItemStack held = player.getInventory().getItemInMainHand();
            if (held != null && held.hasItemMeta()) {
                var data = held.getItemMeta().getPersistentDataContainer();
                // We only care about Intelligence/Mana from the held item for now
                bonusIntel += data.getOrDefault(new NamespacedKey(plugin, "intelligence"), PersistentDataType.DOUBLE, 0.0);
            }

            // 3. APPLY INTELLIGENCE TO MANA (1 Intel = 1 Max Mana)
            double baseMana = 100; // Your starting mana
            sPlayer.setMaxMana(baseMana + bonusIntel);

            // 4. APPLY HEALTH SCALING
            double baseHealth = 100;
            double newMaxHealth = baseHealth + bonusHealth;
            if (sPlayer.getMaxHealth() != newMaxHealth) {
                sPlayer.setMaxHealth(newMaxHealth);
                player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(newMaxHealth);
            }

//            if (player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() != 20.0) {
//                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
//            }

//            double visualHealth = (sPlayer.getCurrentHealth() / sPlayer.getMaxHealth()) * 20.0;

//            player.setHealth(Math.max(0.1, Math.min(20.0, visualHealth)));
            //player.setHealthScale(20.0);
            //player.setHealthScaled(true);
            double currentSbHealth = sPlayer.getCurrentHealth();
            double maxSbHealth = sPlayer.getMaxHealth();

// 2. Calculate the percentage (0.0 to 1.0)
            double healthPercent = currentSbHealth / maxSbHealth;

// 3. Map it to Vanilla's 20.0 scale
            double vanillaDisplayHealth = healthPercent * 20.0;

// 4. Force the Sync
// We use Math.max(0.1, ...) because if you set vanilla health to 0,
// the player dies instantly before your Skyblock logic can process it.
            player.setHealth(Math.max(0.1, Math.min(20.0, vanillaDisplayHealth)));

            // 5. UPDATE CURRENT DEFENSE
            sPlayer.setDefense(bonusDefense);

            // 6. MANA REGEN (2% of Max Mana per second)
            double regenAmount = sPlayer.getMaxMana() * 0.02;
            sPlayer.setCurrentMana(Math.min(sPlayer.getMaxMana(), sPlayer.getCurrentMana() + regenAmount));

            sendActionBar(player, sPlayer);
        }
    }

    private void sendActionBar(Player player,PlayerSkills sPlayer){
        String healthStr = "§c" + (int) sPlayer.getCurrentHealth() + "/" + (int) sPlayer.getMaxHealth() + "❤";
        String defenseStr = "§a" + (int) sPlayer.getDefense() + "❈ Defense";
        String manaStr = "§b" + (int) sPlayer.getCurrentMana() + "/" + (int) sPlayer.getMaxMana() + "✎ Mana";

        String actionBar = healthStr + "     " + defenseStr + "     " + manaStr;

        // 3. Send to Player
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
    }
}
