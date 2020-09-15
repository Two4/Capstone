package za.ac.mandela.WRPV301.Capstone.Map.Location;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * Enum with values representing cardinal directions, for use with {@link MapLocation}s and
 * {@link za.ac.mandela.WRPV301.Capstone.Map.Access.Accessway}s
 */
@SuppressWarnings("JavaDoc")
public enum Direction implements Serializable {
    NORTH(-1, 0),
    EAST(0, 1),
    SOUTH(1, 0),
    WEST(0, -1);

    /**
     * Translation values for movement on a grid using a particular Direction
     */
    private final int rowModifier, columnModifier;

    /**
     * Static list of enum values, used to generate random values statically
     */
    private static final ImmutableList<Direction> values = ImmutableList.copyOf(values());
    /**
     * Statically stored size of this enum
     */
    private static final int size = values.size();

    /**
     * Constructor
     * @param rowModifier translation values for row movement on a grid using a particular Direction
     * @param columnModifier translation values for row movement on a grid using a particular Direction
     */
    Direction(int rowModifier, int columnModifier) {
        this.rowModifier = rowModifier;
        this.columnModifier = columnModifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }

    /**
     * @return a random value instance of this enum
     */
    public static Direction random() {
        return values.get(d(size));
    }

    /**
     * Translates a column coordinate toward this Direction
     * @param column the column coordinate to translate
     * @return the translated column coordinate
     */
    public int translateColumn(int column) {
        return column + columnModifier;
    }

    /**
     * Translates a row coordinate toward this Direction
     * @param row the row coordinate to translate
     * @return the translated row coordinate
     */
    public int translateRow(int row) {
        return row + rowModifier;
    }

    /**
     * Translates the positions of two adjacent nodes into a Direction, relative to the second node ('from')
     * @param location the target {@link MapLocation}; i.e., the location the direction of which must be found
     * @param from the {@link MapLocation} the resultant Direction must be relative to
     * @return the Direction of 'location' relative to 'from'
     */
    public static Direction of(MapLocation location, MapLocation from) {
        int rowTranslation = location.getRow() - from.getRow();
        int columnTranslation = location.getColumn() - from.getColumn();
        for (Direction direction : Direction.values()) {
            if (rowTranslation == direction.rowModifier && columnTranslation == direction.columnModifier) {
                return direction;
            }
        }
        return null;
    }

    /**
     * Translates a screenspace X coordinate by a specified amount toward this Direction
     * @param x the screenspace X coordinate
     * @param amt the translation magnitude
     * @return a translated screenspace X coordinate
     */
    public double translateX(double x, double amt) {
        return x + (amt * columnModifier);
    }

    /**
     * Translates a screenspace Y coordinate by a specified amount toward this Direction
     * @param y the screenspace Y coordinate
     * @param amt the translation magnitude
     * @return a translated screenspace Y coordinate
     */
    public double translateY(double y, double amt) {
        return y + (amt * rowModifier);
    }
}
