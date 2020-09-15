package za.ac.mandela.WRPV301.Capstone.Action;

import com.google.common.base.Strings;

import java.util.concurrent.Callable;

/**
 * {@link Action} Builder class
 */
@SuppressWarnings("JavaDoc")
public class ActionBuilder {
    /**
     * @see Action for field descriptions
     */
    private String name;
    private String[] aliases;
    private SerializableCallable<String> descriptor;
    private SerializableCallable<String> feedbackProvider;
    private SerializableCallable<Boolean> activator;
    private SerializableRunnable actionRunnable;

    /**
     * Private constructor
     */
    private ActionBuilder() {
        this.aliases = new String[]{"Unnamed action"};
        this.descriptor = () -> "No description available";
        this.feedbackProvider = () -> "No action feedback provided";
        this.activator = () -> false;
        this.actionRunnable = () -> {};
    }

    /**
     * Static constructor method
     * @return a new ActionBuilder instance
     */
    public static ActionBuilder newInstance() {
        return new ActionBuilder();
    }

    /**
     * @see Action#aliases
     */
    public ActionBuilder setAliases(String ... aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * @see Action#descriptor
     */
    public ActionBuilder setDescriptor(SerializableCallable<String> descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    /**
     * @see Action#feedbackProvider
     */
    public ActionBuilder setFeedbackProvider(SerializableCallable<String> feedbackProvider) {
        this.feedbackProvider = feedbackProvider;
        return this;
    }

    /**
     * @see Action#activator
     */
    public ActionBuilder setActivator(SerializableCallable<Boolean> activator) {
        this.activator = activator;
        return this;
    }

    /**
     * @see Action#actionRunnable
     */
    public ActionBuilder setActionRunnable(SerializableRunnable actionRunnable) {
        this.actionRunnable = actionRunnable;
        return this;
    }

    /**
     * @return a new {@link Action} instance populated with the builder values
     */
    public Action build() {
        if (Strings.isNullOrEmpty(name)) {
            if (aliases.length > 0) {
                name = aliases[0];
            } else {
                name = "Unnamed Action";
            }
        }
        return new Action(name, aliases, descriptor, feedbackProvider, activator, actionRunnable);
    }
}
