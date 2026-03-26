package org.sumit282698.sDSkyblockCore.api;

import java.util.UUID;
import org.sumit282698.sDSkyblockCore.SDSkyblockCore;

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

    // Core

    public double getDamageReduction() {
        if (defense <= 0) return 0;
        return defense / (defense + 100.0);
    }

    public void heal(double amount) {
        if (amount <= 0) return;
        this.currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public void damage(double amount) {
        if (amount <= 0) return;
        this.currentHealth = Math.max(0, currentHealth - amount);
    }

    public void useMana(double amount) {
        if (amount <= 0) return;
        this.currentMana = Math.max(0, currentMana - amount);
    }

    public void regenMana(double amount) {
        if (amount <= 0) return;
        this.currentMana = Math.min(maxMana, currentMana + amount);
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    // =========================
    // 📊 GETTERS
    // =========================

    public UUID getUuid() { return uuid; }

    public double getStrength() { return strength; }
    public double getDefense() { return defense; }

    public double getMaxHealth() { return maxHealth; }
    public double getCurrentHealth() { return currentHealth; }

    public double getMaxMana() { return maxMana; }
    public double getCurrentMana() { return currentMana; }

    public double getCritChance() { return critChance; }
    public double getCritDamage() { return critDamage; }

    // =========================
    // ⚙️ SETTERS (SAFE)
    // =========================

    public void setStrength(double strength) {
        this.strength = Math.max(0, strength);
    }

    public void setDefense(double defense) {
        this.defense = Math.max(0, defense);
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);

        // Clamp current health
        if (this.currentHealth > this.maxHealth) {
            this.currentHealth = this.maxHealth;
        }
    }

    public void setCurrentHealth(double currentHealth) {
        this.currentHealth = Math.max(0, Math.min(maxHealth, currentHealth));
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = Math.max(0, maxMana);

        if (this.currentMana > this.maxMana) {
            this.currentMana = this.maxMana;
        }
    }

    public void setCurrentMana(double currentMana) {
        this.currentMana = Math.max(0, Math.min(maxMana, currentMana));
    }

    public void setCritChance(double critChance) {
        this.critChance = Math.max(0, critChance);
    }

    public void setCritDamage(double critDamage) {
        this.critDamage = Math.max(0, critDamage);
    }
}