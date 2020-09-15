package za.ac.mandela.WRPV301.Capstone.Item.Room;

import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.Describable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A class to represent a dead body of an {@link za.ac.mandela.WRPV301.Capstone.Combat.Enemy}
 */
public abstract class Corpse extends RoomItem {

    /**
     * Constructor
     * @param originalSingleWordDescriptor the {@link Describable#getSingleWordDescription()} of the {@link za.ac.mandela.WRPV301.Capstone.Combat.Enemy}
     *                                     instance that spawned this Corpse
     * @param location the {@link Room} this Corpse is in
     */
    public Corpse(String originalSingleWordDescriptor, Room location) {
        super(location);
        addAction(ActionBuilder.newInstance()
                .setAliases("Kick", "Hit", "Attack", "Punch", "Stab", "Dismember", "Gore", "Destroy")
                .setFeedbackProvider(() -> String.format("You attack the %s corpse", originalSingleWordDescriptor))
                .setActivator(this::canGore)
                .setActionRunnable(this::gore)
                .build());
    }

    /**
     * @return true if the player can take the 'gore' action
     */
    private boolean canGore() {
        return Game.getPlayer().getCurrentLocation().equals(Game.getMapData().get(location.getRow(), location.getColumn()));
    }

    /**
     * Fluff action; gives a short feedback console output for attacking this Corpse
     */
    protected abstract void gore();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "corpse";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAdjectives() {
        return new HashSet<>(Arrays.asList("Dead", "Rotting", "Decomposing", "Deceased", "Expired"));
    }
}
