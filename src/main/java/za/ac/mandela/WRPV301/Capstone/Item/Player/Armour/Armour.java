package za.ac.mandela.WRPV301.Capstone.Item.Player.Armour;

import za.ac.mandela.WRPV301.Capstone.Action.SerializableRunnable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Equippable;

import java.util.HashSet;

/**
 * Class to represent armour items
 */
public abstract class Armour extends Equippable {
    /**
     * The distinctive quality of this armour
     */
    protected final ArmourAdjective adjective;
    /**
     * The material this armour is made from
     */
    protected final ArmourMaterial material;
    /**
     * The amount this piece of armour modifies the player's armour class
     */
    private final int defenseModifier;

    /**
     * Protected constructor
     * @param adjective the distinctive quality of this armour
     * @param material the material this armour is made from
     * @param defenseModifier the amount this piece of armour modifies the player's armour class
     */
    protected Armour(ArmourAdjective adjective, ArmourMaterial material, int defenseModifier) {
        this.adjective = adjective;
        this.material = material;
        this.defenseModifier = defenseModifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Equippable getCurrentlyEquipped() {
        return Game.getPlayer().getCurrentArmour();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void equip() {
        Game.getPlayer().setCurrentArmour(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void unequip() {
        Game.getPlayer().setCurrentArmour(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("A %s suit of armour made of %s.", adjective, material);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return String.format("%s %s armour", adjective, material);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "armour";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<String> getAdjectives() {
        HashSet<String> adjectives = new HashSet<>();
        adjectives.add(adjective.toString());
        adjectives.add(material.toString());
        return adjectives;
    }

    /**
     * @return the amount this piece of armour modifies the player's armour class.
     */
    public int getDefenseModifier() {
        return defenseModifier;
    }
}
