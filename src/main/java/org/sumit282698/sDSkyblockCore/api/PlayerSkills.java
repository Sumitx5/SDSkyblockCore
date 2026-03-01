package org.sumit282698.sDSkyblockCore.api;

import org.bukkit.entity.Player;
import java.util.UUID;

public class PlayerSkills {
    private final UUID uuid;

    // Core Stats
    private double strength;
    private double defense;
    private double maxHealth;
    private double currentHealth;
    private double maxMana;
    private double currentMana;
    private double critChance;
    private double critDamage;

    public PlayerSkills(UUID uuid) {
        this.uuid = uuid;
        // Default Skyblock Stats
        this.maxHealth = 100.0;
        this.currentHealth = 100.0;
        this.maxMana = 100.0;
        this.currentMana = 100.0;
        this.strength = 0;
        this.defense = 0;
        this.critChance = 0;
        this.critDamage = 0;
    }

    // --- Math Logic Inside the Object ---

    public double getDamageReduction() {
        // Hypixel Formula: Defense / (Defense + 100)
        return defense / (defense + 100.0);
    }

    public void heal(double amount) {
        this.currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    // Getters
    public UUID getUuid() { return uuid; }
    public double getStrength() { return strength; }
    public double getMaxHealth() { return maxHealth; }
    public double getMaxMana() { return maxMana; }
    public double getDefense() { return defense; }
    public double getCritDamage() { return critDamage; }
    public double getCritChance() { return critChance; }
    public double getCurrentHealth() { return currentHealth; }
    public double getCurrentMana() { return currentMana; }

    // setters
    public void setStrength(double strength) { this.strength = strength; }
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }
    public void setMaxMana(double maxMana) { this.maxMana = maxMana; }
    public void setDefense(double defense) { this.defense = defense; }
    public void setCritChance(double critChance) { this.critChance = critChance; }
    public void setCritDamage(double critDamage) { this.critDamage = critDamage; }
    public void setCurrentHealth(double currentHealth ) { this.currentHealth = currentHealth; }
    public void setCurrentMana(double currentMana) { this.currentMana = currentMana; }

}