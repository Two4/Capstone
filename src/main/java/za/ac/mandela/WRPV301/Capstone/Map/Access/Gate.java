package za.ac.mandela.WRPV301.Capstone.Map.Access;

import za.ac.mandela.WRPV301.Capstone.Action.SerializableCallable;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

/**
 * A concrete implementation of {@link Lockable} representing a gate
 */
public class Gate extends Lockable {

    /**
     * Constructor
     * @param size enum value describing the size of this Gate
     * @param material the {@link Material} this Gate is made from
     * @param lock the {@link Lock} for this Gate instance
     * @param isOpen whether this Gate is open or not, i.e. can be traversed by the player to another {@link MapLocation}
     */
    protected Gate(Size size, Material material, Lock lock, boolean isOpen) {
        super(size, material, lock, isOpen);
    }

    /**
     * @return a random locked Gate
     */
    public static Gate randomLocked() {
        return new Gate(Size.random(), Material.random(), Lock.randomLocked(), false);
    }

    /**
     * @return a random unlocked Gate, which may or may not be open
     */
    public static Gate randomUnlocked() {
        return new Gate(Size.random(), Material.random(), Lock.unlocked(), coin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "gate";
    }

}
