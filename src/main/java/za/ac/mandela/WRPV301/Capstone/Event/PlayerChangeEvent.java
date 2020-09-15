package za.ac.mandela.WRPV301.Capstone.Event;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Signals that the player position has changed to the subscribing method
 */
public class PlayerChangeEvent implements Serializable {
    /**
     * Creates and posts a new instance of this class to a shared {@link com.google.common.eventbus.EventBus} instance
     */
    public static void post() {
        eventBus.post(new PlayerChangeEvent());
    }
}
