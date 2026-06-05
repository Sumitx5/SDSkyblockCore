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

    private final NamespacedKey typeKey;
    private final NamespacedKey dmgKey;
    private final NamespacedKey strKey;
    private final NamespacedKey defKey;
    private final NamespacedKey healthKey;
    private final NamespacedKey intelKey;
    private final NamespacedKey ccKey;
    private final NamespacedKey cdKey;

    public StatsTask(SDSkyblockCore plugin) {
        this.plugin = plugin;
        this.typeKey = new NamespacedKey(plugin, "item_type");
        this.dmgKey = new NamespacedKey(plugin, "damage");
        this.strKey = new NamespacedKey(plugin, "strength");
        this.defKey = new NamespacedKey(plugin, "defense");
        this.healthKey = new NamespacedKey(plugin, "max_health");
        this.intelKey = new NamespacedKey(plugin, "intelligence");
        this.ccKey = new NamespacedKey(plugin, "crit_chance");
        this.cdKey = new NamespacedKey(plugin, "crit_damage");
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerSkills sPlayer = SDSkyblockCore.getSPlayer(player.getUniqueId());
            if (sPlayer == null) continue;

            double bonusStrength = 0;
            double bonusDefense = 0;
            double bonusHealth = 0;
            double bonusIntelligence = 0;
            double bonusCritChance = 0;
            double bonusCritDamage = 0;

            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor == null || !armor.hasItemMeta()) continue;

                var data = armor.getItemMeta().getPersistentDataContainer();

                bonusStrength += data.getOrDefault(strKey, PersistentDataType.DOUBLE, 0.0);
                bonusDefense += data.getOrDefault(defKey, PersistentDataType.DOUBLE, 0.0);
                bonusHealth += data.getOrDefault(healthKey, PersistentDataType.DOUBLE, 0.0);
                bonusIntelligence += data.getOrDefault(intelKey, PersistentDataType.DOUBLE, 0.0);
                bonusCritChance += data.getOrDefault(ccKey, PersistentDataType.DOUBLE, 0.0);
                bonusCritDamage += data.getOrDefault(cdKey, PersistentDataType.DOUBLE, 0.0);
            }

            ItemStack held = player.getInventory().getItemInMainHand();
            if (held != null && held.hasItemMeta()) {
                var data = held.getItemMeta().getPersistentDataContainer();

                bonusStrength += data.getOrDefault(strKey, PersistentDataType.DOUBLE, 0.0);
                bonusDefense += data.getOrDefault(defKey, PersistentDataType.DOUBLE, 0.0);
                bonusHealth += data.getOrDefault(healthKey, PersistentDataType.DOUBLE, 0.0);
                bonusIntelligence += data.getOrDefault(intelKey, PersistentDataType.DOUBLE, 0.0);
                bonusCritChance += data.getOrDefault(ccKey, PersistentDataType.DOUBLE, 0.0);
                bonusCritDamage += data.getOrDefault(cdKey, PersistentDataType.DOUBLE, 0.0);
            }

            sPlayer.setBonusStats(bonusStrength, bonusDefense, bonusHealth, bonusIntelligence, bonusCritChance, bonusCritDamage);

            var maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double targetVanillaMax = sPlayer.getMaxHealth() / 5.0;
                maxHealthAttr.setBaseValue(targetVanillaMax);

                double healthPercent = sPlayer.getCurrentHealth() / sPlayer.getMaxHealth();
                double targetVanillaHealth = Math.max(1.0, Math.min(targetVanillaMax, healthPercent * targetVanillaMax));

                if (Math.abs(player.getHealth() - targetVanillaHealth) > 0.5) {
                    player.setHealth(targetVanillaHealth);
                }
            }

            double regenAmount = sPlayer.getMaxMana() * 0.001;
            sPlayer.regenMana(regenAmount);

            double hpRegenAmount = ((sPlayer.getMaxHealth() * 0.02) + 1.0) / 20.0;
            sPlayer.heal(hpRegenAmount);

            sendActionBar(player, sPlayer);
        }
    }

    private void sendActionBar(Player player, PlayerSkills sPlayer) {
        String healthStr = "§c" + (int) Math.round(sPlayer.getCurrentHealth()) + "/" + (int) Math.round(sPlayer.getMaxHealth()) + "❤ Health";
        String defenseStr = "§a" + (int) Math.round(sPlayer.getDefense()) + "❈ Defense";
        String manaStr = "§b" + (int) Math.round(sPlayer.getCurrentMana()) + "/" + (int) Math.round(sPlayer.getMaxMana()) + "✎ Mana";

        String actionBar = healthStr + "     " + defenseStr + "     " + manaStr;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
    }
}