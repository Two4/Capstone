package za.ac.mandela.WRPV301.Capstone.Map.Access;

import com.google.common.collect.ImmutableList;
import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.Describable;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableRunnable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Direction;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;
import za.ac.mandela.WRPV301.Capstone.Player;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

/**
 * Abstract class to represent the connections between map nodes
 */
public abstract class Accessway extends Describable implements Serializable {
    /**
     * Enum value describing the size of this Accessway
     * @see Size
     */
    protected final Size size;
    /**
     * Whether this Accessway is open or not, i.e. can be traversed by the player to another {@link MapLocation}
     */
    protected boolean isOpen;

    /**
     * Constructor
     * @param size enum value describing the size of this Accessway
     * @param isOpen whether this Accessway is open or not, i.e. can be traversed by the player to another {@link MapLocation}
     */
    protected Accessway(Size size, boolean isOpen) {
        this.size = size;
        this.isOpen = isOpen;
        addAction(ActionBuilder.newInstance()
                .setAliases("Peek", "Peer", "Look Through", "Peep", "Spy")
                .setDescriptor(() -> String.format("Peek through this %s to see what lies beyond", getSingleWordDescription()))
                .setFeedbackProvider(() -> String.format("You look through the %s.", getSingleWordDescription()))
                .setActivator(this::canPeek)
                .setActionRunnable(this::peek)
                .build()
        );
        addAction(ActionBuilder.newInstance()
                .setAliases("Enter", "Traverse", "Move Through", "Move Into", "Step Through", "Step Into", "Access",
                        "Infiltrate", "Go Into", "Go Through", "Pass Through", "Walk Into", "Walk Through",
                        "Proceed Into", "Proceed Through")
                .setDescriptor(() -> String.format("Go through this %s.", getSingleWordDescription()))
                .setFeedbackProvider(() -> String.format("You move through the %s", getSingleWordDescription()))
                .setActivator(this::canEnter)
                .setActionRunnable(this::enter)
                .build()
        );
    }

    /**
     * Generates a random non-locked Accessway
     * @return a random non-locked Accessway
     */
    public static Accessway randomUnlockedOrOpen() {
        switch (d(3)) {
            case 0:
                return Door.randomUnlocked();
            case 1:
                return Gate.randomUnlocked();
            default:
                return Opening.random();
        }
    }

    /**
     * @return whether this Accessway can be traversed by the player to another {@link MapLocation}
     */
    public boolean canEnter() {
        return true;
    }

    /**
     * Moves the player to the other side of this Accessway
     */
    public void enter() {
        getOpposingMapLocation().enter();
    }

    /**
     * @return whether the player can look through this Accessway to view the {@link MapLocation} on the other side
     */
    protected boolean canPeek() {
        return true;
    }

    /**
     * Reveals and describes the {@link MapLocation} on the other side of this Accessway to the player
     */
    protected void peek() {
        getOpposingMapLocation().setVisible(true);
        ConsoleEvent.output(String.format("Through this %s, you see: %s", getSingleWordDescription(), getOpposingMapLocation().getDescription()));
    }

    /**
     * @return the {@link MapLocation} on the other side of this Accessway, relative to the current position of the {@link Player}
     * @see Player#getCurrentLocation()
     */
    public MapLocation getOpposingMapLocation() {
        return getOpposingMapLocation(Game.getPlayer().getCurrentLocation());
    }

    /**
     * @param from the {@link MapLocation} that "opposing" is relative to for this Accessway
     * @return the {@link MapLocation} on the other side of this Accessway, relative to the specified MapLocation
     */
    protected MapLocation getOpposingMapLocation(MapLocation from) {
        Optional<MapLocation> optional = Optional.empty();
        try {
            optional = Optional.ofNullable(Game.getMapData().incidentNodes(this).adjacentNode(from));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return optional.orElseThrow();
    }

    /**
     * @return the {@link Direction} that this Accessway is facing, relative to the current {@link MapLocation} of the {@link Player}
     * @see Player#getCurrentLocation()
     */
    protected Direction getFacingDirection() {
        return Direction.of(getOpposingMapLocation(), Game.getPlayer().getCurrentLocation());
    }

    /**
     * @return the {@link Direction} that this Accessway is facing, relative to the given {@link MapLocation}
     * @param relativeTo the {@link MapLocation} the resultant {@link Direction} must be relative to
     */
    public Direction getFacingDirection(MapLocation relativeTo) {
        return Direction.of(getOpposingMapLocation(relativeTo), relativeTo);
    }


    /**
     * @return whether this Accessway is open or not, i.e. can be traversed by the player to another {@link MapLocation}
     */
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public HashSet<String> getAdjectives() {
        HashSet<String> adjectives = new HashSet<>();
        adjectives.add(getFacingDirection().toString());
        adjectives.add(size.toString());
        return adjectives;
    }

    /**
     * Enum to describe the size of an {@link Accessway}; for fluff only, does not affect the game in any way
     */
    @SuppressWarnings("JavaDoc")
    protected enum Size {
        CRAMPED,
        STANDARD,
        WIDE,
        LARGE,
        HUGE;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<Size> values = ImmutableList.copyOf(values());
        /**
         * Statically stored size of this enum
         */
        private static final int size = values.size();

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        /**
         * @return a random value instance of this enum
         */
        public static Size random() {
            return values.get(d(size));
        }
    }
}
