package org.sumit282698.sDSkyblockCore;

import org.bukkit.plugin.java.JavaPlugin;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;
import org.sumit282698.sDSkyblockCore.commands.GetItemCommand;
import org.sumit282698.sDSkyblockCore.commands.ProfileCommand;
import org.sumit282698.sDSkyblockCore.commands.StatAdder;
import org.sumit282698.sDSkyblockCore.managers.DatabaseManager;
import org.sumit282698.sDSkyblockCore.managers.MobManager;
import org.sumit282698.sDSkyblockCore.listeners.AbilityListener;
import org.sumit282698.sDSkyblockCore.listeners.PlayerConnectionListener;
import org.sumit282698.sDSkyblockCore.listeners.CombatListener;
import org.sumit282698.sDSkyblockCore.managers.ItemManager;
import org.sumit282698.sDSkyblockCore.managers.ProfileManager;
import org.sumit282698.sDSkyblockCore.tasks.StatsTask;
import org.sumit282698.sDSkyblockCore.utils.Utility; // FIX: Added missing utility package import

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public final class SDSkyblockCore extends JavaPlugin {

    //STATIC INSTANCE
    private static SDSkyblockCore instance;

    //MANAGERS & UTILITIES
    private ProfileManager profileManager;
    private ItemManager itemManager;
    private MobManager mobManager;
    private DatabaseManager databaseManager;
    private Utility utility;

    //Plugin Messages
    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        saveDefaultConfig();

        this.databaseManager = new DatabaseManager(this);
        try {
            this.databaseManager.connect();
        } catch (SQLException e) {
            getLogger().severe("Could not connect to SQLite database! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Database connected successfully!");

        //MANAGERS & UTILITIES INITIALIZATION
        this.utility = new Utility(this);
        this.profileManager = new ProfileManager();

        this.mobManager = new MobManager(this);
        this.mobManager.loadMobs();

        this.itemManager = new ItemManager(this);
        this.itemManager.loadItems();

        //Commands
        StatAdder setStat = new StatAdder(this);

        if (getCommand("sdget") != null) {
            getCommand("sdget").setExecutor(new GetItemCommand(this));
        }

        if (getCommand("sdskills") != null) {
            getCommand("sdskills").setExecutor(setStat);
            getCommand("sdskills").setTabCompleter((sender, cmd, alias, args) -> {
                if (args.length == 2) {
                    return Arrays.asList("health", "defense", "strength", "intelligence", "cc", "cd", "crit_chance", "crit_damage");
                }
                return null;
            });
        }

        if (getCommand("sdprofile") != null) {
            getCommand("sdprofile").setExecutor(new ProfileCommand());
        }

        //Events
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this, this.mobManager), this);

        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);

        //Tasks
        new StatsTask(this).runTaskTimer(this, 0L, 1L);

        getLogger().info("SDSkyblockCore enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (this.databaseManager != null) {
            this.databaseManager.disconnect();
        }
    }

    //Static Helpers
    public static SDSkyblockCore getInstance() {
        return instance;
    }

    public static PlayerSkills getSPlayer(UUID uuid) {
        return getInstance().getProfileManager().getProfile(uuid);
    }

    //Getters
    public ProfileManager getProfileManager() { return profileManager; }
    public ItemManager getItemManager() { return itemManager; }
    public MobManager getMobManager() { return mobManager; }
    public DatabaseManager getDatabase() { return databaseManager; }

    public Utility getUtility() { return utility; }
}