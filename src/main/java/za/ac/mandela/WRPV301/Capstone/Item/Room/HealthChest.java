package za.ac.mandela.WRPV301.Capstone.Item.Room;

import com.google.common.collect.ImmutableSet;
import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableCallable;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableRunnable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

/**
 * Class to represent an immovable health pickup that fully heals the player
 */
public class HealthChest extends RoomItem {
    /**
     * Immutable set of adjectives for all HealthChest instances
     */
    private final static ImmutableSet<String> adjectives = ImmutableSet.of("health, medicine, medical, healing, medicinal");

    /**
     * Constructor
     * @param location the location of this HealthChest
     */
    public HealthChest(Room location) {
        super(location);
        addAction(ActionBuilder.newInstance()
                .setAliases("Use", "Utilise", "Utilize", "Heal", "Take", "Apply",
                        "Treat", "Consume", "Employ", "Expend", "Tend")
                .setActivator(this::canUse)
                .setDescriptor(() -> "Make use of this health chest to heal your injuries")
                .setFeedbackProvider(HealthChest::getUseFeedback)
                .setActionRunnable(this::use)
                .build()
        );
    }

    /**
     * @return true if this HealthChest can currently be used by the player
     */
    private boolean canUse() {
        return Game.getPlayer().getCurrentLocation().equals(Game.getMapData().get(location.getRow(), location.getColumn()));
    }

    /**
     * @return a short feedback string for when the player uses this HealthChest
     */
    private static String getUseFeedback() {
        if (Game.getPlayer().getCurrentHealth() < 100) {
            return "You tend your wounds using the supplies in the chest, and soon feel refreshed and full of vigour";
        }
        return "You are not currently in need of healing.";
    }


    /**
     * Consumes this health chest and restores the player to full health
     */
    private void use() {
        if (Game.getPlayer().getCurrentHealth() < 100) {
            Game.getPlayer().heal(100);
        }
        ((Room) Game.getPlayer().getCurrentLocation()).removeRoomItem(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "A chest containing bandages, tinctures, ointments and medicines; everything one needs to return to full health, if need be";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return "a medicine chest";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "chest";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableSet<String> getAdjectives() {
        return adjectives;
    }
}
