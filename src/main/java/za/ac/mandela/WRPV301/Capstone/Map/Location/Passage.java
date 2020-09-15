package za.ac.mandela.WRPV301.Capstone.Map.Location;

/**
 * Concrete extension of the MapLocation class
 */
public class Passage extends MapLocation {

    /**
     * Constructor
     * @param row the grid row coordinate of this Passage
     * @param column the grid column coordinate of this Passage
     * @param floorMaterial the {@link Material} of the floor of the current location
     * @param wallMaterial the {@link Material} of the wall of the current location
     * @param lighting the {@link Lighting} of this Passage
     */
    protected Passage(int row, int column, Material floorMaterial, Material wallMaterial, Lighting lighting) {
        super(row, column, floorMaterial, wallMaterial, lighting);
    }

    /**
     * Creates a randomly generated Passage with the given grid coordinates
     * @param row the grid row coordinate of the resultant Passage
     * @param column the grid column coordinate of the resultant Passage
     * @return a randomly generated Passage with the given grid coordinates
     */
    public static Passage random(int row, int column) {
        return new Passage(row, column, Material.random(), Material.random(), Lighting.random());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "passage";
    }
}
