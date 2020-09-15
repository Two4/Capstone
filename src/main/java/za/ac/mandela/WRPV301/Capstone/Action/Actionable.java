package za.ac.mandela.WRPV301.Capstone.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Extending classes are marked as have actions associated with them
 */
public abstract class Actionable implements Serializable {
    /**
     * The actions associated with this object
     */
    private final ArrayList<Action> actions = new ArrayList<>();

    /**
     * Add an action to this object
     * @param action the {@link Action} to add
     */
    protected void addAction(Action action) {
        if (!actions.contains(action)) {
            actions.add(action);
        }
    }

    /**
     * Returns an {@link ArrayList} of {@link Action}s for this object for which the {@link Action#isActive()} method returns true
     * @return an {@link ArrayList} of available {@link Action}s for this object
     */
    public ArrayList<Action> getAvailableActions() {
        return actions.stream().filter(Action::isActive).collect(Collectors.toCollection(ArrayList::new));
    }
}
