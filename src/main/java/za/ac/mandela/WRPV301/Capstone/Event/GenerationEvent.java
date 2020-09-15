package za.ac.mandela.WRPV301.Capstone.Event;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Class used to pass messages to the user during map generation via an {@link com.google.common.eventbus.EventBus}
 */
public class GenerationEvent implements Serializable {
    /**
     * The message to pass to the user during generation
     */
    private final String message;

    /**
     * Private constructor
     * @param message the message to pass to the user during generation
     */
    private GenerationEvent(String message) {
        this.message = message;
    }

    /**
     * @return the message to pass to the user during generation.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Creates and publishes a new Generation event to a shared {@link com.google.common.eventbus.EventBus instance}
     * @param message the message to pass to the user during generation.
     */
    public static void of(String message) {
        eventBus.post(new GenerationEvent(message));
    }
}
