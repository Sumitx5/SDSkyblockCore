package org.sumit282698.sDSkyblockCore.database;

import org.sumit282698.sDSkyblockCore.SDSkyblockCore;
import org.sumit282698.sDSkyblockCore.api.PlayerSkills;

import java.io.File;
import java.sql.*;

public class Database {

    private Connection connection;

    /**
     * Connects to the SQLite database and creates the table if it doesn't exist
     */
    public void connect() throws SQLException {
        File folder = SDSkyblockCore.getInstance().getDataFolder();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File databaseFile = new File(folder, "data.db");
        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

        connection = DriverManager.getConnection(url);

        // Create table if it doesn't exist
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid TEXT PRIMARY KEY,
                    strength REAL DEFAULT 0,
                    defense REAL DEFAULT 0,
                    max_health REAL DEFAULT 100,
                    max_mana REAL DEFAULT 100,
                    crit_chance REAL DEFAULT 0,
                    crit_damage REAL DEFAULT 0
                );
            """);
        }
    }

    /**
     * Saves a player's stats to the database
     */
    public void saveSPlayer(PlayerSkills playerSkills) {
        String sql = """
            INSERT INTO player_stats (uuid, strength, defense, max_health, max_mana, crit_chance, crit_damage)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                strength = excluded.strength,
                defense = excluded.defense,
                max_health = excluded.max_health,
                max_mana = excluded.max_mana,
                crit_chance = excluded.crit_chance,
                crit_damage = excluded.crit_damage;
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerSkills.getUuid().toString());
            pstmt.setDouble(2, playerSkills.getStrength());
            pstmt.setDouble(3, playerSkills.getDefense());
            pstmt.setDouble(4, playerSkills.getMaxHealth());
            pstmt.setDouble(5, playerSkills.getMaxMana());
            pstmt.setDouble(6, playerSkills.getCritChance());
            pstmt.setDouble(7, playerSkills.getCritDamage());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a player's stats from the database into their PlayerSkills object
     */
    public void loadSPlayer(PlayerSkills sPlayer) {
        String sql = "SELECT * FROM player_stats WHERE uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sPlayer.getUuid().toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                sPlayer.setStrength(rs.getDouble("strength"));
                sPlayer.setDefense(rs.getDouble("defense"));
                sPlayer.setMaxHealth(rs.getDouble("max_health"));
                sPlayer.setMaxMana(rs.getDouble("max_mana"));
                sPlayer.setCritChance(rs.getDouble("crit_chance"));
                sPlayer.setCritDamage(rs.getDouble("crit_damage"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the database connection safely
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        }
    }
}