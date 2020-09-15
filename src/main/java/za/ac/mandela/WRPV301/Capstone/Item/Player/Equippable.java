package za.ac.mandela.WRPV301.Capstone.Item.Player;


import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Player;

import java.util.Objects;

/**
 * Class to represent a {@link PlayerItem} that occupies a designated slot to change
 * {@link Player} stats
 */
public abstract class Equippable extends PlayerItem {

    /**
     * Constructor
     */
    public Equippable() {
        addAction(ActionBuilder.newInstance()
                .setAliases("Equip", "Ready", "Gear", "Prepare", "Prep", "Take Out", "Use", "Utilise", "Utilize", "Employ")
                .setActivator(this::canEquip)
                .setDescriptor(this::describeEquipAction)
                .setActionRunnable(this::equip)
                .setFeedbackProvider(() -> String.format("You equip %s", getShortDescription()))
                .build());
        addAction(ActionBuilder.newInstance()
                .setAliases("Unequip", "Stow", "Store", "Stop Using", "Supplant", "Pack", "Stash")
                .setActivator(this::isEquipped)
                .setDescriptor(() -> String.format("Unequip this %s", getSingleWordDescription()))
                .setActionRunnable(this::unequip)
                .setFeedbackProvider(() -> String.format("You unequip %s", getShortDescription()))
                .build());
    }

    /**
     * @return true if this Equippable is currently in a slot
     */
    private boolean isEquipped() {
        return Objects.nonNull(getCurrentlyEquipped()) && getCurrentlyEquipped().equals(this);
    }

    /**
     * @return true if this Equippable can currently be equipped
     */
    protected boolean canEquip() {
        return !isEquipped();
    }

    /**
     * @return the Equippable currently occupying the player slot this Equippable would fill
     */
    protected abstract Equippable getCurrentlyEquipped();

    /**
     * Occupy the slot designated for this Equippable type, displacing the current Equippable if one is present
     */
    protected abstract void equip();

    /**
     * Void the slot designated for this Equippable type of this Equippable instance
     */
    protected abstract void unequip();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drop() {
        if (this.isEquipped()) {
            unequip();
        }
        super.drop();
    }

    /**
     * Feedback provider for equipping this item
     * @return a string describing the 'equip' action to the player
     */
    protected String describeEquipAction() {
        return String.format("Use this %s in place of your currently equipped %s", getSingleWordDescription(), getCurrentlyEquipped().getSingleWordDescription());
    }
}
