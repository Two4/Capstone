package za.ac.mandela.WRPV301.Capstone.Event;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Event used to pass a game loss event to the UI
 */
public class GameLossEvent implements Serializable {
    /**
     * Creates and posts a new instance of this class to a shared {@link com.google.common.eventbus.EventBus} instance
     */
    public static void create() {
        //noinspection InstantiationOfUtilityClass
        eventBus.post(new GameLossEvent());
    }
}
