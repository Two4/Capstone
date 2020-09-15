package za.ac.mandela.WRPV301.Capstone.Combat;

/**
 * A common interface for entities involved in a {@link Fight}
 */
public interface Combatant {
    /**
     * Gets the success of this Combatant's attack roll
     * @param defenseModifier the target's defense modifier
     * @return true if the attack is successful
     */
    boolean getAttackSuccessRoll(int defenseModifier);

    /**
     * @return the amount of damage inflicted by this Combatant's attack
     */
    int getDamageRoll();

    /**
     * @return the percentage by which this target reduces the likelihood of successful attacks
     */
    int getDefenseModifier();

    /**
     * Applies incoming attacks to this Combatant
     * @param from the source of the attack
     */
    void receiveAttack(Combatant from);

    /**
     * Applies an attack from this Combatant to another
     * @param against the target Combatant
     */
    void initiateAttack(Combatant against);

    /**
     * @return the fight this combatant is currently a member of, which may be {@code null}
     */
    Fight getCurrentFight();

    /**
     * Sets the current fight for this Combatant
     * @param currentFight the fight instance to set as the current fight for this Combatant
     */
    void setCurrentFight(Fight currentFight);

    /**
     * @return a more specific short descriptor than {@link Describable#getShortDescription()}
     */
    String getCombatantDescriptor();
}
