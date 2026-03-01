package org.sumit282698.sDSkyblockCore.managers;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

import java.io.File;
import java.util.*;

public class ItemManager {
    private final Map<String, ItemStack> customItems = new HashMap<>();
    NamespacedKey typeKey = new NamespacedKey(SDSkyblockCore.getInstance(), "item_type");

    public void loadItems() {
        customItems.clear();
        File folder = new File(SDSkyblockCore.getInstance().getDataFolder(), "items");
        if (!folder.exists()) folder.mkdirs();

        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String id = file.getName().replace(".yml", "");

                customItems.put(id, createItemStack(config));
            }
        }
    }

    private ItemStack createItemStack(FileConfiguration config) {
        Material mat = Material.matchMaterial(config.getString("material", "STONE"));
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // 1. Name And Lore
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("name", "&fItem")));
            List<String> lore = new ArrayList<>();
            for (String line : config.getStringList("lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);

            // 2. Vars or stats
            NamespacedKey typeKey = new NamespacedKey(SDSkyblockCore.getInstance(), "item_type");
            NamespacedKey damageKey = new NamespacedKey(SDSkyblockCore.getInstance(), "damage");
            NamespacedKey strengthKey = new NamespacedKey(SDSkyblockCore.getInstance(), "strength");
            NamespacedKey defenseKey = new NamespacedKey(SDSkyblockCore.getInstance(), "defense");
            NamespacedKey healthKey = new NamespacedKey(SDSkyblockCore.getInstance(), "max_health");
            NamespacedKey intelKey = new NamespacedKey(SDSkyblockCore.getInstance(), "intelligence");
            NamespacedKey critChanceKey = new NamespacedKey(SDSkyblockCore.getInstance(), "crit_chance");
            NamespacedKey critDamageKey = new NamespacedKey(SDSkyblockCore.getInstance(), "crit_damage");

            // 3.
            String type = config.getString("type", "ITEM").toUpperCase();
            meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, type);
            // make the stats work
            meta.getPersistentDataContainer().set(intelKey, PersistentDataType.DOUBLE, config.getDouble("stats.intelligence", 0.0));
            meta.getPersistentDataContainer().set(critChanceKey, PersistentDataType.DOUBLE, config.getDouble("stats.crit_chance", 0.0));
            meta.getPersistentDataContainer().set(critDamageKey, PersistentDataType.DOUBLE, config.getDouble("stats.crit_damage", 0.0));

            // 4. The IF/ELSE Logic
            if (type.equals("ARMOR")) {
                // Save Armor stats to the hidden data
                meta.getPersistentDataContainer().set(defenseKey, PersistentDataType.DOUBLE, config.getDouble("stats.defense", 0.0));
                meta.getPersistentDataContainer().set(healthKey, PersistentDataType.DOUBLE, config.getDouble("stats.max_health", 0.0));
                meta.getPersistentDataContainer().set(strengthKey, PersistentDataType.DOUBLE, config.getDouble("stats.strength", 0.0));
            } else {
                // Save Weapon stats to the hidden data
                meta.getPersistentDataContainer().set(damageKey, PersistentDataType.DOUBLE, config.getDouble("stats.damage", 0.0));
                meta.getPersistentDataContainer().set(strengthKey, PersistentDataType.DOUBLE, config.getDouble("stats.strength", 0.0));
            }

            // 5. Item Meta/Data Saver
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getItem(String id) {
        return customItems.get(id).clone();
    }
}
