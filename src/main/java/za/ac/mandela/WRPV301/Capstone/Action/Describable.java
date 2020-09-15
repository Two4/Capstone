package za.ac.mandela.WRPV301.Capstone.Action;

import com.google.common.collect.ListMultimap;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;

import java.util.Set;

/**
 * An abstract extension of {@link Actionable} that specifies an object as having a description, adjectives and a "look"
 * action associated with it
 */
public abstract class Describable extends Actionable {
    /**
     * @return a full description of this object
     */
    public abstract String getDescription();

    /**
     * @return a condensed description of this object
     */
    public abstract String getShortDescription();

    /**
     * @return a single word describing this object, e.g. "cup", "window", etc.
     */
    public abstract String getSingleWordDescription();

    /**
     * @return whether this object can currently be inspected by the player
     */
    protected boolean canLook() {
        return true;
    }

    /**
     * Prints a description of this object
     */
    protected void look() {
        ConsoleEvent.output(getDescription());
    }

    /**
     * Constructor
     */
    public Describable() {
        addAction(ActionBuilder.newInstance()
                .setAliases("Look", "Inspect", "Describe", "Investigate", "Analyse", "Assess", "Evaluate",
                        "Scrutinise", "Examine", "Assess", "Check", "Scan", "Review", "Survey", "Appraise", "View",
                        "Study", "Consider", "Interrogate")
                .setDescriptor(() -> String.format("Inspect this %s for relevant details.", getSingleWordDescription()))
                .setFeedbackProvider(() -> String.format("Your inspect the %s", getSingleWordDescription()))
                .setActivator(this::canLook)
                .setActionRunnable(this::look)
                .build()
        );
    }

    /**
     * @return a set of adjectives associated with this object, used for user command disambiguation
     * @see za.ac.mandela.WRPV301.Capstone.Util.InputParser#disambiguate(ListMultimap, String)
     */
    public abstract Set<String> getAdjectives();
}
