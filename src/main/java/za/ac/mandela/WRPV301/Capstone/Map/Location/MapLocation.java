package za.ac.mandela.WRPV301.Capstone.Map.Location;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.*;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Access.Accessway;
import za.ac.mandela.WRPV301.Capstone.Event.AccesswayChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Event.LocationChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Player;

import java.io.Serializable;
import java.util.*;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

/**
 * A class representing map nodes
 */
public abstract class MapLocation extends Describable implements Serializable {
    //private static final long serialVersionUID = 3451801901980545505L;
    /**
     * The column coordinate of this MapLocation
     */
    private final int column;
    /**
     * The row coordinate of this MapLocation
     */
    private final int row;
    /**
     * The {@link Material} of the floor of the current location
     */
    private final Material floorMaterial;
    /**
     * The {@link Material} of the walls of the current location
     */
    private final Material wallMaterial;
    /**
     * The {@link Lighting} of this MapLocation
     */
    private final Lighting lighting;
    /**
     * Whether this {@link MapLocation} is visible to the {@link Player}
     */
    private boolean isVisible = false;

    /**
     * Enum with values describing wall and floor materials
     */
    @SuppressWarnings("JavaDoc")
    protected enum Material {
        STONE,
        BRICK,
        WOOD,
        CONCRETE,
        CLAY;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<Material> values = ImmutableList.copyOf(values());
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
        public static Material random() {
            return values.get(d(size));
        }
    }

    /**
     * Enum with values describing lighting levels
     */
    @SuppressWarnings("JavaDoc")
    protected enum Lighting {
        BARELY,
        DIMLY,
        PASSABLY,
        WELL,
        BRIGHTLY;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<Lighting> values = ImmutableList.copyOf(values());
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
        public static Lighting random() {
            return values.get(d(size));
        }
    }

    /**
     * Constructor
     * Adds a default 'enter' action
     * @param row the grid row coordinate of this MapLocation
     * @param column the grid column coordinate of this MapLocation
     * @param floorMaterial the {@link Material} of the floor of the current location
     * @param wallMaterial the {@link Material} of the wall of the current location
     * @param lighting the {@link Lighting} of this MapLocation
     */
    protected MapLocation(int row, int column, Material floorMaterial, Material wallMaterial, Lighting lighting) {
        this.column = column;
        this.row = row;
        this.floorMaterial = floorMaterial;
        this.wallMaterial = wallMaterial;
        this.lighting = lighting;
        addAction(ActionBuilder.newInstance()
                .setAliases("Enter", "Infiltrate", "Move Into", "Move To", "Move", "Step Into", "Access",
                        "Go", "Pass Into", "Walk", "Proceed Into", "Proceed To", "Proceed")
                .setDescriptor(() -> String.format("Enter the %s to the %s.", getShortDescription(), getRelativeDirection()))
                .setFeedbackProvider(() -> String.format("You move into the %s to the %s", getSingleWordDescription(), getRelativeDirection()))
                .setActivator(this::canEnter)
                .setActionRunnable(this::enter)
                .build()
        );
    }

    /**
     * @return the column coordinate of this MapLocation
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return the row coordinate of this MapLocation
     */
    public int getRow() {
        return row;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    protected boolean canLook() {
        return Game.getPlayer().getCurrentLocation().equals(this) || canEnter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void look() {
        setVisible(true);
        super.look();
    }

    /**
     * @return true if the {@link Player} can traverse to this MapLocation
     */
    private boolean canEnter() {
        MapLocation playerLocation = Game.getPlayer().getCurrentLocation();
        if (!playerLocation.equals(this)) {
            Accessway accessway = Game.getMapData().edgeConnectingOrNull(playerLocation, this);
            if (Objects.nonNull(accessway)) {
                return accessway.canEnter();
            }

        }
        return false;
    }

    /**
     * Moves the {@link Player} to this MapLocation
     */
    public void enter() {
        this.setVisible(true);
        MapLocation playerLocation = Game.getPlayer().getCurrentLocation();
        if (!playerLocation.equals(this) && Game.getMapData().hasEdgeConnecting(this, playerLocation)) {
            Accessway accessway = Game.getMapData().edgeConnecting(this, playerLocation).orElseThrow();
            Game.getPlayer().setCurrentLocation(this);
            AccesswayChangeEvent.on(accessway);
        }
        ConsoleEvent.output(getDescription());
    }

    /**
     * @return true if this MapLocation is visible to the {@link Player}
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Sets if this MapLocation is visible to the {@link Player}
     * @param visible true to make this MapLocation visible, false otherwise
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
        LocationChangeEvent.on(this);
    }

    /**
     * Sets if this MapLocation is visible to the {@link Player}, but without creating a {@link LocationChangeEvent}
     * @param visible true to make this MapLocation visible, false otherwise
     */
    public void setVisibleNoEvent(boolean visible) {
        isVisible = visible;
    }

    /**
     * Creates a randomly generated MapLocation with the given grid coordinates
     * @param row the grid row coordinate of the resultant MapLocation
     * @param column the grid column coordinate of the resultant MapLocation
     * @return a randomly generated MapLocation with the given grid coordinates
     */
    public static MapLocation random(int row, int column) {
        return coin() ? Room.random(row, column) : Passage.random(row, column);
    }

    /**
     * @return a {@link com.google.common.collect.Multimap} of all {@link Action}s available in the context of this
     * {@link MapLocation}
     */
    public ListMultimap<Actionable, Action> getLocationActions() {
        ListMultimap<Actionable, Action> actionMap = MultimapBuilder
                .linkedHashKeys()
                .arrayListValues()
                .build();
        actionMap.putAll(this, getAvailableActions());
        for (MapLocation connectedLocation : getConnectedLocations()) {
            actionMap.putAll(connectedLocation, connectedLocation.getAvailableActions());
        }
        for (Accessway accessway : getAccessways().values()) {
            actionMap.putAll(accessway, accessway.getAvailableActions());
        }
        return actionMap;
    }

    /**
     * @return a {@link HashMap} of all {@link Accessway}s connected to this MapLocation, keyed by their relative
     * {@link Direction}
     */
    public HashMap<Direction, Accessway> getAccessways() {
        HashMap<Direction, Accessway> accessways = new HashMap<>();
        for (MapLocation adjacentNode : Game.getMapData().adjacentNodes(this)) {
            accessways.put(Direction.of(adjacentNode, this), Game.getMapData().edgeConnectingOrNull(adjacentNode, this));
        }
        return accessways;
    }

    /**
     * @return the relative {@link Direction} of this MapLocation from the {@link Player}
     */
    private Direction getRelativeDirection() {
        MapLocation playerLocation = Game.getPlayer().getCurrentLocation();
        if (!playerLocation.equals(this) && this.isConnectedTo(playerLocation)) {
            return Direction.of(this, Game.getPlayer().getCurrentLocation());
        }
        return null;
    }

    /**
     * Checks if this MapLocation is connected to the given other MapLocation
     * @param location the other MapLocation, for which to check for a connection to this MapLocation
     * @return true if the other MapLocation is connected to this MapLocation
     */
    private boolean isConnectedTo(MapLocation location) {
        return Game.getMapData().hasEdgeConnecting(this, location);
    }

    /**
     * @return a {@link Set} of immediate neighbour MapLocations that are connected to this MapLocation
     */
    private Set<MapLocation> getConnectedLocations() {
        return Game.getMapData().adjacentNodes(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        if (wallMaterial.equals(floorMaterial)) {
            builder.append(String.format("A %s, %s lit. The walls and floor are made of %s", getSingleWordDescription(), lighting, wallMaterial));
        } else {
            builder.append(String.format("A %s, %s lit. The floor is made of %s, and the walls of %s.", getSingleWordDescription(), lighting, floorMaterial, wallMaterial));
        }
        if (this.equals(Game.getPlayer().getCurrentLocation())) {
            for (Map.Entry<Direction, Accessway> directionAccessway : getAccessways().entrySet()) {
                builder.append(String.format("\nTo the %s you see %s.", directionAccessway.getKey(), directionAccessway.getValue().getShortDescription()));
            }
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        if (wallMaterial.equals(floorMaterial)) {
            return String.format("a %s lit %s, made of %s", lighting, getSingleWordDescription(), wallMaterial);
        }
        return String.format("a %s lit %s, made of %s and %s", lighting, getSingleWordDescription(), wallMaterial, floorMaterial);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<String> getAdjectives() {
        HashSet<String> adjectives = new HashSet<>();
        if (isVisible()) {
            adjectives.add(floorMaterial.toString());
            adjectives.add(wallMaterial.toString());
            adjectives.add(String.format("%s lit", lighting));
        }
        if (Objects.nonNull(getRelativeDirection())) {
            adjectives.add(getRelativeDirection().toString());
        }
        return adjectives;
    }
}
