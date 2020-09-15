package za.ac.mandela.WRPV301.Capstone.Event;

import za.ac.mandela.WRPV301.Capstone.Map.Access.Accessway;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Class used to create and pass{@link Accessway} change events
 */
public class AccesswayChangeEvent implements Serializable {
    /**
     * The{@link Accessway} that has changed
     */
    private final Accessway accessway;

    /**
     * Private constructor
     * @param accessway the{@link Accessway} that has changed
     */
    private AccesswayChangeEvent(Accessway accessway) {
        this.accessway = accessway;
    }

    /**
     * Creates and publishes a change event for a given{@link Accessway}
     * @param accessway the changed{@link Accessway}
     */
    public static void on(Accessway accessway) {
        eventBus.post(new AccesswayChangeEvent(accessway));
    }


    /**
     * Gets The{@link Accessway} that has changed.
     *
     * @return Value of The{@link Accessway} that has changed.
     */
    public Accessway getAccessway() {
        return accessway;
    }
}
