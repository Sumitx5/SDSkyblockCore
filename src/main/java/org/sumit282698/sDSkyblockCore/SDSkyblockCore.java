package org.sumit282698.sDSkyblockCore;

import org.bukkit.plugin.java.JavaPlugin;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.commands.GetItemCommand;
import org.sumit282698.sDSkyblockCore.commands.profilecommand;
import org.sumit282698.sDSkyblockCore.commands.statsAdder;
import org.sumit282698.sDSkyblockCore.database.Database;
import org.sumit282698.sDSkyblockCore.listeners.AbilityListener;
import org.sumit282698.sDSkyblockCore.listeners.PlayerConnectionListener;
import org.sumit282698.sDSkyblockCore.listeners.CombatListener;
import org.sumit282698.sDSkyblockCore.managers.ItemManager;
import org.sumit282698.sDSkyblockCore.managers.ProfileManager;
import org.sumit282698.sDSkyblockCore.menus.profilemenu;
import org.sumit282698.sDSkyblockCore.tasks.StatsTask;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public final class SDSkyblockCore extends JavaPlugin {

    // ===== STATIC INSTANCE =====
    private static SDSkyblockCore instance;

    // ===== MANAGERS =====
    private ProfileManager profileManager;
    private ItemManager itemManager;
    private Database database;

    // ===== PLUGIN ENABLE =====
    @Override
    public void onEnable() {
        instance = this;

        // Create plugin folder if missing
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        saveDefaultConfig();

        // ===== DATABASE =====
        this.database = new Database();
        try {
            this.database.connect();
        } catch (SQLException e) {
            getLogger().severe("Could not connect to database! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Database connected successfully!");

        // ===== MANAGERS =====
        this.profileManager = new ProfileManager();
        this.itemManager = new ItemManager();
        this.itemManager.loadItems();

        // ===== COMMANDS =====
        statsAdder setStat = new statsAdder(this); // pass plugin instance

        if (getCommand("sdget") != null) {
            getCommand("sdget").setExecutor(new GetItemCommand(this));
        }

        if (getCommand("sdskills") != null) {
            getCommand("sdskills").setExecutor(setStat);
            getCommand("sdskills").setTabCompleter((sender, cmd, alias, args) -> {
                if (args.length == 2)
                    return Arrays.asList("health", "defense", "strength", "intelligence");
                return null;
            });
        }

        if (getCommand("sdprofile") != null) {
            getCommand("sdprofile").setExecutor(new profilecommand());
        }

        // ===== EVENTS =====
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);

        // ===== TASKS =====
        new StatsTask(this).runTaskTimer(this, 20L, 20L);

        getLogger().info("SDSkyblockCore enabled successfully!");
    }

    // ===== STATIC HELPERS =====
    public static SDSkyblockCore getInstance() {
        return instance;
    }

    public static PlayerSkills getSPlayer(UUID uuid) {
        return getInstance().getProfileManager().getProfile(uuid);
    }

    // ===== GETTERS =====
    public ProfileManager getProfileManager() { return profileManager; }
    public ItemManager getItemManager() { return itemManager; }
    public Database getDatabase() { return database; }
}