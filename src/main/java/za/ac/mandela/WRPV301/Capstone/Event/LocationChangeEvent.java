package za.ac.mandela.WRPV301.Capstone.Event;

import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Class used to create and pass {@link MapLocation} change events
 */
public class LocationChangeEvent implements Serializable {
    /**
     * The node that has changed
     */
    private final MapLocation location;

    /**
     * Private constructor
     * @param location the node that has changed
     */
    private LocationChangeEvent(MapLocation location) {
        this.location = location;
    }

    /**
     * Creates and publishes a change event for a given {@link MapLocation}
     * @param location the changed {@link MapLocation}
     */
    public static void on(MapLocation location) {
        eventBus.post(new LocationChangeEvent(location));
    }


    /**
     * Gets The node that has changed.
     *
     * @return Value of The node that has changed.
     */
    public MapLocation getLocation() {
        return location;
    }
}
