package za.ac.mandela.WRPV301.Capstone.Action;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Class to represent an action a player can take
 */
public class Action implements Serializable {
    /**
     * The name of this action
     */
    private final String name;
    /**
     * Alternate names for this action
     */
    private final String[] aliases;
    /**
     * Function that provides a description string for this action
     */
    private final SerializableCallable<String> descriptor;
    /**
     * Function that provides a short feedback string to the player when is action is executed
     */
    private final SerializableCallable<String> feedbackProvider;
    /**
     * Function for which the return boolean determines if this action is currently possible
     */
    private final SerializableCallable<Boolean> activator;
    /**
     * {@link Runnable} that is called when this action is executed
     */
    private final SerializableRunnable actionRunnable;

    /**
     * Constructor
     * @param name the name of this action
     * @param aliases Alternate names for this action
     * @param descriptor Function that provides a description string for this action
     * @param feedbackProvider Function that provides a short feedback string to the player when is action is executed
     * @param activator Function for which the return boolean determines if this action is currently possible
     * @param actionRunnable {@link Runnable} that is called when this action is executed
     */
    Action(String name, String[] aliases, SerializableCallable<String> descriptor, SerializableCallable<String> feedbackProvider, SerializableCallable<Boolean> activator, SerializableRunnable actionRunnable) {
        this.name = name;
        this.aliases = aliases;
        this.descriptor = descriptor;
        this.feedbackProvider = feedbackProvider;
        this.activator = activator;
        this.actionRunnable = actionRunnable;
    }

    /**
     * @return the aliases associated with this action
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * @return the description String for this action
     */
    public String getDescription() {
        try {
            return descriptor.call();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return the feedback String for this action
     */
    public String getFeedBack() {
        try {
            return feedbackProvider.call();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Run this action
     */
    public void execute() {
        this.actionRunnable.run();
    }

    /**
     * @return true if this action is currently active
     */
    public boolean isActive() {
        try {
            return activator.call();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @return the name of this action
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        try {
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.toString();
    }
}
