package org.sumit282698.sDSkyblockCore.managers;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MobManager {

    private final SDSkyblockCore plugin;
    private final Map<String, FileConfiguration> mobTemplates = new HashMap<>();

    private final NamespacedKey maxHpKey;
    private final NamespacedKey currentHpKey;
    private final NamespacedKey levelKey;
    private final NamespacedKey nameKey;

    public MobManager(SDSkyblockCore plugin) {
        this.plugin = plugin;
        this.maxHpKey = new NamespacedKey(plugin, "max_health");
        this.currentHpKey = new NamespacedKey(plugin, "current_health");
        this.levelKey = new NamespacedKey(plugin, "level");
        this.nameKey = new NamespacedKey(plugin, "custom_name");
    }

    public void loadMobs() {
        File folder = new File(plugin.getDataFolder(), "mobs");
        if (!folder.exists()) {
            folder.mkdirs();
            createDefaultTemplate(folder);
        }

        File[] files = folder.listFiles();
        if (files == null) return;

        mobTemplates.clear();
        for (File file : files) {
            if (file.getName().endsWith(".yml")) {
                String id = file.getName().replace(".yml", "");
                mobTemplates.put(id, YamlConfiguration.loadConfiguration(file));
            }
        }
        plugin.getLogger().info("Successfully loaded " + mobTemplates.size() + " custom Skyblock mob templates!");
    }

    public void spawnMob(String id, Location loc) {
        FileConfiguration config = mobTemplates.get(id);
        if (config == null) return;

        String typeStr = config.getString("type");
        if (typeStr == null) return;

        EntityType type = EntityType.valueOf(typeStr.toUpperCase());
        if (loc.getWorld() == null) return;

        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);

        double maxHp = config.getDouble("stats.max_health", 100.0);
        int level = config.getInt("level", 1);
        String customName = config.getString("name", type.toString());

        var data = entity.getPersistentDataContainer();
        data.set(maxHpKey, PersistentDataType.DOUBLE, maxHp);
        data.set(currentHpKey, PersistentDataType.DOUBLE, maxHp);
        data.set(levelKey, PersistentDataType.INTEGER, level);

        data.set(nameKey, PersistentDataType.STRING, customName);

        updateMobName(entity, customName, level, maxHp, maxHp);
    }

    public void updateMobName(LivingEntity entity, String fallbackName, int lvl, double cur, double max) {
        var data = entity.getPersistentDataContainer();

        String templateName = data.getOrDefault(nameKey, PersistentDataType.STRING, fallbackName);

        int displayCur = (int) Math.ceil(Math.max(0, cur));
        int displayMax = (int) Math.ceil(max);

        entity.setCustomName("§8[§7Lv" + lvl + "§8] §c" + templateName + " §a" + displayCur + "§7/§a" + displayMax + "§c❤");
        entity.setCustomNameVisible(true);
    }

    private void createDefaultTemplate(File folder) {
        File exampleFile = new File(folder, "crypt_ghoul.yml");
        if (!exampleFile.exists()) {
            try {
                FileConfiguration config = new YamlConfiguration();
                config.set("name", "Crypt Ghoul");
                config.set("type", "ZOMBIE");
                config.set("level", 30);
                config.set("stats.max_health", 2000.0);
                config.save(exampleFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to generate default sample mob file: " + e.getMessage());
            }
        }
    }
}