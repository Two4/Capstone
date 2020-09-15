package za.ac.mandela.WRPV301.Capstone.Event;

import javafx.scene.paint.Color;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.eventBus;

/**
 * Class used to create and pass console events
 */
public class ConsoleEvent implements Serializable {
    /**
     * The console message to print
     */
    private final String message;
    /**
     * The {@link Color} of the message when printed
     */
    private final Color textColor;
    /**
     * Color of input messages
     */
    private static final Color input = Color.GREEN;
    /**
     * Color of output messages
     */
    private static final Color output = Color.BLUE;

    /**
     * Private constructor
     * @param message the console message to print
     * @param textColor the {@link Color} of the message when printed
     */
    private ConsoleEvent(String message, Color textColor) {
        this.message = message;
        this.textColor = textColor;
    }

    /**
     * Creates and publishes a new player input console event
     * @param message the console message to print
     */
    public static void input(String message) {
        eventBus.post(new ConsoleEvent(message, input));
    }

    /**
     * Creates and publishes a new game output console event
     * @param message the console message to print
     */
    public static void output(String message) {
        eventBus.post(new ConsoleEvent(message, output));
    }

    /**
     * Gets textColor.
     *
     * @return Value of textColor.
     */
    public Color getTextColor() {
        return textColor;
    }

    /**
     * Gets message.
     *
     * @return Value of message.
     */
    public String getMessage() {
        return message;
    }
}
