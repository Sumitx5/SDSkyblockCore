package org.sumit282698.sDSkyblockCore.api;

import java.util.UUID;

public class PlayerSkills {

    private final UUID uuid;
    //Base Stats
    private double baseStrength;
    private double baseDefense;
    private double baseMaxHealth;
    private double baseMaxMana;
    private double baseCritChance;
    private double baseCritDamage;
// Bonus Stats
    private double bonusStrength;
    private double bonusDefense;
    private double bonusMaxHealth;
    private double bonusMaxMana;
    private double bonusCritChance;
    private double bonusCritDamage;
// Current Health and Mana
    private double currentHealth;
    private double currentMana;

    public PlayerSkills(UUID uuid) {
        this.uuid = uuid;

        this.baseMaxHealth = 100.0;
        this.currentHealth = 100.0;

        this.baseMaxMana = 100.0;
        this.currentMana = 100.0;

        this.baseStrength = 0.0;
        this.baseDefense = 0.0;

        this.baseCritChance = 30.0;
        this.baseCritDamage = 50.0;

        this.bonusStrength = 0;
        this.bonusDefense = 0;
        this.bonusMaxHealth = 0;
        this.bonusMaxMana = 0;
        this.bonusCritChance = 0;
        this.bonusCritDamage = 0;
    }

    public double getDamageReduction() {
        double totalDefense = getDefense();
        if (totalDefense <= 0) return 0;
        return totalDefense / (totalDefense + 100.0);
    }

    public void heal(double amount) {
        if (amount <= 0) return;
        this.currentHealth = Math.min(getMaxHealth(), currentHealth + amount);
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
        this.currentMana = Math.min(getMaxMana(), currentMana + amount);
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public UUID getUuid() { return uuid; }

    public double getStrength() { return baseStrength + bonusStrength; }
    public double getDefense() { return baseDefense + bonusDefense; }
    public double getMaxHealth() { return baseMaxHealth + bonusMaxHealth; }
    public double getMaxMana() { return baseMaxMana + bonusMaxMana; }
    public double getCritChance() { return baseCritChance + bonusCritChance; }
    public double getCritDamage() { return baseCritDamage + bonusCritDamage; }

    public double getCurrentHealth() { return currentHealth; }
    public double getCurrentMana() { return currentMana; }


    public double getBaseStrength() { return baseStrength; }
    public void setBaseStrength(double baseStrength) { this.baseStrength = baseStrength; }

    public double getBaseDefense() { return baseDefense; }
    public void setBaseDefense(double baseDefense) { this.baseDefense = baseDefense; }

    public double getBaseMaxHealth() { return baseMaxHealth; }
    public void setBaseMaxHealth(double baseMaxHealth) {
        this.baseMaxHealth = Math.max(1, baseMaxHealth);
        clampCurrentStats();
    }

    public double getBaseMaxMana() { return baseMaxMana; }
    public void setBaseMaxMana(double baseMaxMana) {
        this.baseMaxMana = Math.max(0, baseMaxMana);
        clampCurrentStats();
    }

    public double getBaseCritChance() { return baseCritChance; }
    public void setBaseCritChance(double baseCritChance) { this.baseCritChance = baseCritChance; }

    public double getBaseCritDamage() { return baseCritDamage; }
    public void setBaseCritDamage(double baseCritDamage) { this.baseCritDamage = baseCritDamage; }

    public void setBonusStats(double strength, double defense, double health, double mana, double cc, double cd) {
        this.bonusStrength = strength;
        this.bonusDefense = defense;
        this.bonusMaxHealth = health;
        this.bonusMaxMana = mana;
        this.bonusCritChance = cc;
        this.bonusCritDamage = cd;
        clampCurrentStats();
    }

    public void setCurrentHealth(double currentHealth) {
        this.currentHealth = Math.max(0, Math.min(getMaxHealth(), currentHealth));
    }

    public void setCurrentMana(double currentMana) {
        this.currentMana = Math.max(0, Math.min(getMaxMana(), currentMana));
    }

    private void clampCurrentStats() {
        if (this.currentHealth > getMaxHealth()) this.currentHealth = getMaxHealth();
        if (this.currentMana > getMaxMana()) this.currentMana = getMaxMana();
    }
}