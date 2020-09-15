package za.ac.mandela.WRPV301.Capstone.Item.Room;

import za.ac.mandela.WRPV301.Capstone.Action.Describable;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

/**
 * Abstract class for items that are tied to a specific room
 */
public abstract class RoomItem extends Describable {
    /**
     * The {@link Room} this RoomItem is in
     */
    protected final Room location;

    /**
     * Constructor
     * @param location the {@link Room} this RoomItem is in
     */
    protected RoomItem(Room location) {
        this.location = location;
    }
}
