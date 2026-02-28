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
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("name", "&fUnnamed Item")));

            List<String> lore = new ArrayList<>();
            for (String line : config.getStringList("lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);

            NamespacedKey damageKey = new NamespacedKey(SDSkyblockCore.getInstance(), "damage");
            NamespacedKey strengthKey = new NamespacedKey(SDSkyblockCore.getInstance(), "strength");
            NamespacedKey critChanceKey = new NamespacedKey(SDSkyblockCore.getInstance(), "crit_chance");
            NamespacedKey critDamageKey = new NamespacedKey(SDSkyblockCore.getInstance(), "crit_damage");
            NamespacedKey manaKey = new NamespacedKey(SDSkyblockCore.getInstance(), "intelligence");

            meta.getPersistentDataContainer().set(damageKey, PersistentDataType.DOUBLE, config.getDouble("stats.damage", 0.0));
            meta.getPersistentDataContainer().set(strengthKey, PersistentDataType.DOUBLE, config.getDouble("stats.strength", 0.0));
            meta.getPersistentDataContainer().set(critChanceKey, PersistentDataType.DOUBLE, config.getDouble("stats.crit_chance", 0.0));
            meta.getPersistentDataContainer().set(critDamageKey, PersistentDataType.DOUBLE, config.getDouble("stats.crit_damage", 0.0));
            meta.getPersistentDataContainer().set(manaKey, PersistentDataType.DOUBLE, config.getDouble("stats.intelligence", 0.0));

            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getItem(String id) {
        return customItems.get(id).clone();
    }
}
