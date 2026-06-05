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

@SuppressWarnings("ALL")
public class ItemManager {
    private final Map<String, ItemStack> customItems = new HashMap<>();
    private final SDSkyblockCore plugin;

    //Cache the keys globally
    private final NamespacedKey typeKey;
    private final NamespacedKey damageKey;
    private final NamespacedKey strengthKey;
    private final NamespacedKey defenseKey;
    private final NamespacedKey healthKey;
    private final NamespacedKey intelKey;
    private final NamespacedKey critChanceKey;
    private final NamespacedKey critDamageKey;

    public ItemManager(SDSkyblockCore plugin) {
        this.plugin = plugin;
        this.typeKey = new NamespacedKey(plugin, "item_type");
        this.damageKey = new NamespacedKey(plugin, "damage");
        this.strengthKey = new NamespacedKey(plugin, "strength");
        this.defenseKey = new NamespacedKey(plugin, "defense");
        this.healthKey = new NamespacedKey(plugin, "max_health");
        this.intelKey = new NamespacedKey(plugin, "intelligence");
        this.critChanceKey = new NamespacedKey(plugin, "crit_chance");
        this.critDamageKey = new NamespacedKey(plugin, "crit_damage");
    }

    public void loadItems() {
        customItems.clear();
        File folder = new File(plugin.getDataFolder(), "items");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().endsWith(".yml")) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String id = file.getName().replace(".yml", "").toLowerCase();

                customItems.put(id, createItemStack(config));
            }
        }
        plugin.getLogger().info("Loaded " + customItems.size() + " custom Skyblock items successfully!");
    }

    private ItemStack createItemStack(FileConfiguration config) {
        Material mat = Material.matchMaterial(config.getString("material", "STONE"));
        if (mat == null) mat = Material.STONE;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("name", "&fItem")));
            List<String> lore = new ArrayList<>();
            for (String line : config.getStringList("lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);

            String type = config.getString("type", "ITEM").toUpperCase();
            var container = meta.getPersistentDataContainer();

            container.set(typeKey, PersistentDataType.STRING, type);

            container.set(damageKey, PersistentDataType.DOUBLE, config.getDouble("stats.damage", 0.0));
            container.set(strengthKey, PersistentDataType.DOUBLE, config.getDouble("stats.strength", 0.0));
            container.set(defenseKey, PersistentDataType.DOUBLE, config.getDouble("stats.defense", 0.0));
            container.set(healthKey, PersistentDataType.DOUBLE, config.getDouble("stats.max_health", 0.0));
            container.set(intelKey, PersistentDataType.DOUBLE, config.getDouble("stats.intelligence", 0.0));
            container.set(critChanceKey, PersistentDataType.DOUBLE, config.getDouble("stats.crit_chance", 0.0));
            container.set(critDamageKey, PersistentDataType.DOUBLE, config.getDouble("stats.crit_damage", 0.0));

            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getItem(String id) {
        if (id == null) return null;
        ItemStack target = customItems.get(id.toLowerCase());
        return (target != null) ? target.clone() : null;
    }
}