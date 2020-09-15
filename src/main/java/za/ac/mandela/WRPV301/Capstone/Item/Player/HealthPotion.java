package za.ac.mandela.WRPV301.Capstone.Item.Player;

import com.google.common.collect.ImmutableSet;
import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableCallable;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableRunnable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

import java.util.Set;

/**
 * Class representing health pickups
 */
public class HealthPotion extends PlayerItem {
    /**
     * Immutable set of adjectives for all HealthPotion instances
     */
    private final static ImmutableSet<String> adjectives = ImmutableSet.of("health, medicine, medical, healing, medicinal, vial");
    /**
     * The number of charges this HealthPotion has - a method of 'duplicating' items for the player
     */
    private int charges;
    /**
     * Whether this has been picked up by the player
     */
    private boolean isInPlayerInventory = false;

    /**
     * Constructor
     * @param charges the number of charges this HealthPotion has - a method of 'duplicating' items for the player
     */
    public HealthPotion(int charges) {
        this.charges = charges;
        addAction(ActionBuilder.newInstance()
                .setDescriptor(() -> "Drink a health potion")
                .setFeedbackProvider(() -> "You drink a health potion, and instantly feel a bit better")
                .setActivator(this::canUse)
                .setActionRunnable(this::use)
                .setAliases("Use", "Utilise", "Utilize", "Heal", "Take", "Apply", "Treat", "Consume","Expend", "Drink",
                        "Swallow", "Quaff", "Imbibe", "Drain", "Slug", "Swig", "Chug", "Ingurgitate", "Sip", "Gulp")
                .build());
    }

    /**
     * @return a new 0-charge instance for use in instantiating a new {@link za.ac.mandela.WRPV301.Capstone.Player}
     */
    public static HealthPotion playerInstance() {
        HealthPotion healthPotion = new HealthPotion(0);
        healthPotion.isInPlayerInventory = true;
        return healthPotion;
    }

    /**
     * Consumes a charge on the {@link za.ac.mandela.WRPV301.Capstone.Player#healthPotion} instance
     */
    private void use() {
        Game.getPlayer().heal(30);
        Game.getPlayer().getHealthPotion().charges--;
    }

    /**
     * @return true if the player can consume charges on this instance
     */
    private boolean canUse() {
        return isInPlayerInventory && Game.getPlayer().getHealthPotion().charges > 0 && Game.getPlayer().getCurrentHealth() < 100;
    }

    /**
     * Adds a specified number of charges to this instance
     * @param charges the number of charges to add
     */
    private void addCharges(int charges) {
        this.charges += charges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String takeFeedback() {
        if (charges < 2) {
            return "You gingerly pick up the single vial of health potion.";
        }
        return "You gingerly pick up each vial of health potion.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canTake() {
        return charges > 0 && !isInPlayerInventory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void take() {
        ((Room) Game.getPlayer().getCurrentLocation()).removePlayerItem(this);
        Game.getPlayer().getHealthPotion().addCharges(this.charges);
        this.charges = 0;
        this.isInPlayerInventory = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canDrop() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    protected boolean canLook() {
        return charges > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        if (charges < 1) {
            return "A single empty vial, once full of delicious, life-giving potion";
        }
        if (charges == 1) {
            return "A single vial of a dark, swirling amber liquid; you get the feeling that drinking this would be good for your health.";
        }
        return String.format("%d vials of a dark, swirling amber liquid; you get the feeling that drinking one of these would be good for your health.", charges);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        if (charges < 1) {
            return "an empty potion vial";
        }
        if (charges == 1) {
            return "a vial of health potion";
        }
        return String.format("%d vials of health potion", charges);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "potion";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAdjectives() {
        return adjectives;
    }
}
