package org.sumit282698.sDSkyblockCore.managers;

import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;

import java.io.File;
import java.sql.*;

public class DatabaseManager {

    private final SDSkyblockCore plugin;
    private Connection connection;

    public DatabaseManager(SDSkyblockCore plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File file = new File(plugin.getDataFolder(), "data.db");
        String url = "jdbc:sqlite:" + file.getAbsolutePath();
        connection = DriverManager.getConnection(url);

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid TEXT PRIMARY KEY,
                    base_strength REAL DEFAULT 0.0,
                    base_defense REAL DEFAULT 0.0,
                    base_max_health REAL DEFAULT 100.0,
                    base_max_mana REAL DEFAULT 100.0,
                    base_crit_chance REAL DEFAULT 30.0,
                    base_crit_damage REAL DEFAULT 50.0
                );
            """);

            safelyAddColumn(statement, "base_crit_chance", "REAL DEFAULT 30.0");
            safelyAddColumn(statement, "base_crit_damage", "REAL DEFAULT 50.0");
        }
    }

    private void safelyAddColumn(Statement statement, String columnName, String typeAndDefault) {
        try {
            statement.execute("ALTER TABLE player_stats ADD COLUMN " + columnName + " " + typeAndDefault + ";");
        } catch (SQLException ignored) {
            // Pass
        }
    }

    public void saveSPlayer(PlayerSkills playerSkills) {
        String sql = """
            INSERT INTO player_stats (uuid, base_strength, base_defense, base_max_health, base_max_mana, base_crit_chance, base_crit_damage)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                base_strength = excluded.base_strength,
                base_defense = excluded.base_defense,
                base_max_health = excluded.base_max_health,
                base_max_mana = excluded.base_max_mana,
                base_crit_chance = excluded.base_crit_chance,
                base_crit_damage = excluded.base_crit_damage;
        """;

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, playerSkills.getUuid().toString());
            pstmt.setDouble(2, playerSkills.getBaseStrength());
            pstmt.setDouble(3, playerSkills.getBaseDefense());
            pstmt.setDouble(4, playerSkills.getBaseMaxHealth());
            pstmt.setDouble(5, playerSkills.getBaseMaxMana());
            pstmt.setDouble(6, playerSkills.getBaseCritChance());
            pstmt.setDouble(7, playerSkills.getBaseCritDamage());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save Skyblock profile for UUID: " + playerSkills.getUuid() + " - " + e.getMessage());
        }
    }

    public void loadSPlayer(PlayerSkills sPlayer) {
        String sql = "SELECT * FROM player_stats WHERE uuid = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, sPlayer.getUuid().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    sPlayer.setBaseStrength(rs.getDouble("base_strength"));
                    sPlayer.setBaseDefense(rs.getDouble("base_defense"));
                    sPlayer.setBaseMaxHealth(rs.getDouble("base_max_health"));
                    sPlayer.setBaseMaxMana(rs.getDouble("base_max_mana"));
                    sPlayer.setBaseCritChance(rs.getDouble("base_crit_chance"));
                    sPlayer.setBaseCritDamage(rs.getDouble("base_crit_damage"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to load Skyblock profile for UUID: " + sPlayer.getUuid() + " - " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to safely close the database connection: " + e.getMessage());
        }
    }
}