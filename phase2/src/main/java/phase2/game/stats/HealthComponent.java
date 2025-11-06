package phase2.game.stats;

import phase2.game.combat.CombatManager;
import phase2.game.combat.DamageSource;
import phase2.game.combat.Damageable;

/**
 * Component for managing health and damage for entities
 */
public class HealthComponent {
    private int maxHealth;
    private int currentHealth;
    private int defense;
    private boolean isDead;
    private Damageable ent;

    /**
     * Create a health component with specified max HP and defense
     * @param maxHealth Maximum health points
     * @param defense Defense value (reduces incoming damage)
     */
    public HealthComponent(int maxHealth, int defense, Damageable ent) {
        this.maxHealth = Math.max(1, maxHealth);
        this.currentHealth = this.maxHealth;
        this.defense = Math.max(0, defense);
        this.isDead = false;
        this.ent = ent;
    }

    /**
     * Create a health component with just max HP (0 defense)
     * @param maxHealth Maximum health points
     */
    public HealthComponent(int maxHealth, int defence) {
        this(maxHealth, defence, null );
    }

    public void setEnt (Damageable ent) {
        this.ent = ent;
    }
    /**
     * Apply damage to this entity
     * @param amount The amount of damage to take
     * @param source The source of the damage
     */
    public void takeDamage(int amount, DamageSource source) {
        if (isDead || amount <= 0) {
            return;
        }

        currentHealth = Math.max(0, currentHealth - amount);
        
        // Notify listeners
        CombatManager.notifyDamage(ent, amount, source); // Note: target needs to be passed from entity

        if (currentHealth == 0 && !isDead) {
            isDead = true;
            CombatManager.notifyDeath(ent, source); // Note: target needs to be passed from entity
        }
    }

    /**
     * Heal this entity
     * @param amount The amount to heal
     */
    public void heal(int amount) {
        if (isDead || amount <= 0) {
            return;
        }

        int oldHealth = currentHealth;
        currentHealth = Math.min(maxHealth, currentHealth + amount);
        int actualHealed = currentHealth - oldHealth;

        if (actualHealed > 0) {
            CombatManager.notifyHeal(ent, actualHealed); // Note: target needs to be passed from entity
        }
    }

    /**
     * Check if entity is alive
     * @return true if current health > 0
     */
    public boolean isAlive() {
        return currentHealth > 0 && !isDead;
    }

    /**
     * Get current health
     * @return Current HP
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Get maximum health
     * @return Max HP
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Get defense value
     * @return Defense stat
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Set maximum health and fully heal
     * @param maxHealth New max health
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        this.currentHealth = Math.min(this.currentHealth, this.maxHealth);
    }

    /**
     * Set defense value
     * @param defense New defense value
     */
    public void setDefense(int defense) {
        this.defense = Math.max(0, defense);
    }

    /**
     * Fully restore health
     */
    public void fullHeal() {
        currentHealth = maxHealth;
        isDead = false;
    }

    /**
     * Get health as a percentage
     * @return Health percentage (0.0 to 1.0)
     */
    public double getHealthPercentage() {
        return (double) currentHealth / maxHealth;
    }

    /**
     * Revive with specified health percentage
     * @param healthPercentage Percentage of max health to revive with (0.0 to 1.0)
     */
    public void revive(double healthPercentage) {
        isDead = false;
        currentHealth = (int) (maxHealth * Math.max(0.0, Math.min(1.0, healthPercentage)));
    }

    @Override
    public String toString() {
        return String.format("Health: %d/%d (%.0f%%) Defense: %d", 
                           currentHealth, maxHealth, getHealthPercentage() * 100, defense);
    }
}
