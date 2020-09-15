package za.ac.mandela.WRPV301.Capstone.Item.Room;

import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

import java.util.Set;

/**
 * Class used to place the crown icon in the maze endpoint
 */
public class Crown extends RoomItem {

    /**
     * Constructor
     *
     * @param location the {@link Room} this RoomItem is in
     */
    public Crown(Room location) {
        super(location);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getShortDescription() {
        return null;
    }

    @Override
    public String getSingleWordDescription() {
        return null;
    }

    @Override
    public Set<String> getAdjectives() {
        return null;
    }
}
