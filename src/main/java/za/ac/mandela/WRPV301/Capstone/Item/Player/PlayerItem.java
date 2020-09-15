package za.ac.mandela.WRPV301.Capstone.Item.Player;

import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.Describable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;
import za.ac.mandela.WRPV301.Capstone.Player;

/**
 * Abstract class to represent items the player can place and remove from their inventory
 */
public abstract class PlayerItem extends Describable {
    /**
     * Constructor
     * Adds two default actions: take and drop
     */
    public PlayerItem() {
        addAction(ActionBuilder.newInstance()
                .setAliases("Take", "Pick Up", "Acquire", "Seize", "Get", "Collect", "Procure", "Grab", "Carry")
                .setDescriptor(() -> String.format("Put this %s into your inventory", getSingleWordDescription()))
                .setFeedbackProvider(this::takeFeedback)
                .setActivator(this::canTake)
                .setActionRunnable(this::take)
                .build()
        );
        addAction(ActionBuilder.newInstance()
                .setAliases("Drop", "Remove", "Discard", "Discharge", "Evict", "Eject", "Expel", "Purge")
                .setDescriptor(() -> String.format("Remove this %s from your inventory", getSingleWordDescription()))
                .setFeedbackProvider(this::dropFeedback)
                .setActivator(this::canDrop)
                .setActionRunnable(this::drop)
                .build()
        );
    }

    /**
     * @return a short feedback string printed when the player executes the 'take' action
     */
    protected String takeFeedback() {
        return String.format("You take the %s.", getSingleWordDescription());
    }

    /**
     * @return a short feedback string printed when the player executes the 'drop' action
     */
    protected String dropFeedback() {
        return String.format("You drop the %s.", getShortDescription());
    }

    /**
     * @return true if the {@link Player} is allowed to take this item
     */
    protected boolean canTake() {
        return !Game.getPlayer().hasItem(this);
    }

    /**
     * Removes the item from its current {@link za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation} and places it
     * in the {@link Player#inventory}
     */
    protected void take() {
        Game.getPlayer().getInventory().add(this);
        ((Room) Game.getPlayer().getCurrentLocation()).removePlayerItem(this);
    }

    /**
     * @return true if the {@link Player} can drop this item instance
     */
    protected boolean canDrop() {
        return Game.getPlayer().getCurrentLocation() instanceof Room && !canTake();
    }

    /**
     * Removes the item from the {@link Player#inventory} and places at the
     * {@link Player#currentLocation}
     */
    protected void drop() {
        Game.getPlayer().getInventory().remove(this);
        ((Room) Game.getPlayer().getCurrentLocation()).addPlayerItem(this);
    }
}
