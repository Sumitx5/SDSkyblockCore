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
import java.util.HashMap;
import java.util.Map;

public class MobManager {
    private final Map<String, FileConfiguration> mobTemplates = new HashMap<>();

    public void loadMobs() {
        File folder = new File(SDSkyblockCore.getInstance().getDataFolder(), "mobs");
        if (!folder.exists()) folder.mkdirs();

        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                String id = file.getName().replace(".yml", "");
                mobTemplates.put(id, YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    public void spawnMob(String id, Location loc) {
        FileConfiguration config = mobTemplates.get(id);
        if (config == null) return;

        EntityType type = EntityType.valueOf(config.getString("type"));
        LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);

        // Save Custom Stats to the Mob
        var data = entity.getPersistentDataContainer();
        double maxHp = config.getDouble("stats.max_health");
        int level = config.getInt("level");

        data.set(new NamespacedKey(SDSkyblockCore.getInstance(), "max_health"), PersistentDataType.DOUBLE, maxHp);
        data.set(new NamespacedKey(SDSkyblockCore.getInstance(), "current_health"), PersistentDataType.DOUBLE, maxHp);
        data.set(new NamespacedKey(SDSkyblockCore.getInstance(), "level"), PersistentDataType.INTEGER, level);

        // Set the Name Tag
        updateMobName(entity, config.getString("name"), level, maxHp, maxHp);
    }

    public void updateMobName(LivingEntity entity, String name, int lvl, double cur, double max) {
        entity.setCustomName("§8[§7Lv" + lvl + "§8] §c" + name + " §a" + (int)cur + "§7/§a" + (int)max + "§c❤");
        entity.setCustomNameVisible(true);
    }
}