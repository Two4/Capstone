package za.ac.mandela.WRPV301.Capstone.Event;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Signals to the {@link za.ac.mandela.WRPV301.Capstone.UI.CanvasManager} that the map must be redrawn completely
 */
public class MapChangeEvent implements Serializable {
    /**
     * Creates a new MapChangeEvent instance and posts it to the shared {@link com.google.common.eventbus.EventBus} instance
     */
    public static void post() {
        //noinspection InstantiationOfUtilityClass
        eventBus.post(new MapChangeEvent());
    }
}
