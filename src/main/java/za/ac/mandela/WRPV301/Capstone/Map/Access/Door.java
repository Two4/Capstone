package za.ac.mandela.WRPV301.Capstone.Map.Access;

import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

/**
 * A concrete implementation of {@link Lockable} representing a door
 */
public class Door extends Lockable {

    /**
     * Constructor
     * @param size enum value describing the size of this Door
     * @param material the {@link Material} this Door is made from
     * @param lock the {@link Lock} for this Door instance
     * @param isOpen whether this Door is open or not, i.e. can be traversed by the player to another {@link MapLocation}
     */
    protected Door(Size size, Material material, Lock lock, boolean isOpen) {
        super(size, material, lock, isOpen);
    }

    /**
     * @return a random locked door
     */
    public static Door randomLocked() {
        return new Door(Size.random(), Material.random(), Lock.randomLocked(), false);
    }

    /**
     * @return a random unlocked door, which may or may not be open
     */
    public static Door randomUnlocked() {
        return new Door(Size.random(), Material.random(), Lock.unlocked(), coin());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "door";
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    protected boolean canPeek() {
        return isOpen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void open() {
        super.open();
        peek();
    }
}
