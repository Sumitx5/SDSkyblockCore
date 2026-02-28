package org.sumit282698.sDSkyblockCore;

import org.bukkit.plugin.java.JavaPlugin;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.commands.GetItemCommand;
import org.sumit282698.sDSkyblockCore.database.Database;
import org.sumit282698.sDSkyblockCore.listeners.AbilityListener;
import org.sumit282698.sDSkyblockCore.listeners.PlayerConnectionListener;
import org.sumit282698.sDSkyblockCore.managers.ItemManager;
import org.sumit282698.sDSkyblockCore.managers.ProfileManager;
import org.sumit282698.sDSkyblockCore.tasks.StatsTask;
import org.sumit282698.sDSkyblockCore.listeners.CombatListener;

import java.util.UUID;

public class SDSkyblockCore extends JavaPlugin {
    private static SDSkyblockCore instance;
    private ProfileManager profileManager;
    private ItemManager itemManager;
    private Database database;

    @Override
    public void onEnable() {
        instance = this;

        // Checking if folder Exists XD.
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        //Managing if no profile
        this.profileManager = new ProfileManager();
        this.database = new Database();
        // items manager
        this.itemManager = new ItemManager();
        this.itemManager.loadItems();
        this.profileManager = new ProfileManager();

        // commands
        getCommand("sbget").setExecutor(new GetItemCommand());

        // loading Profile
        saveDefaultConfig();
        getLogger().info("Data Layer Loaded!");

        // loading For Data Basse

        try {
            this.database.connect();
        } catch (java.sql.SQLException e) {
            getLogger().severe("Could not connect to database! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.profileManager = new ProfileManager();
        getLogger().info("Database connected successfully!");
        // Player Join And Leave Evnts
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getLogger().info("Player Connection");
// Action Bar
        new StatsTask(this).runTaskTimer(this, 20L, 20L);
        getLogger().info("Step 4: Stats Task started!");
        // COmbat
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);


    }

    // main Functions
// player load
    public static PlayerSkills getSPlayer(UUID uuid) {
        return getInstance().getProfileManager().getProfile(uuid);
    }
    // item loader
    public ItemManager getItemManager() {
        return itemManager;
    }

    // db load
    public Database getDatabase() { return database; }
    // other loaders XD
    public static SDSkyblockCore getInstance() { return instance; }
    public ProfileManager getProfileManager() { return profileManager; }
}
