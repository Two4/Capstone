package za.ac.mandela.WRPV301.Capstone.Map.Access;

import com.google.common.collect.ImmutableList;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * A concrete implementation of {@link Accessway} representing an unblocked
 * inter-{@link za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation} connection
 */
public class Opening extends Accessway {
    /**
     * The {@link Type} of Opening
     */
    private final Type type;

    /**
     * Constructor
     *
     * @param size enum value describing the size of this Accessway
     * @param type the {@link Type} of Opening
     */
    public Opening(Size size, Type type) {
        super(size, true);
        this.type = type;
    }

    /**
     * @return an Opening of random size and weaponType
     */
    public static Opening random() {
        return new Opening(Size.random(), Type.random());
    }

    /**
     * Enum with values describing types of {@link Opening}s
     */
    @SuppressWarnings("JavaDoc")
    private enum Type {
        DOORWAY,
        GATEWAY,
        ARCHWAY,
        ENTRYWAY,
        HOLE,
        CLEFT,
        CREVICE,
        APERTURE,
        INLET;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<Type> values = ImmutableList.copyOf(values());
        /**
         * Statically stored size of this enum
         */
        private static final int size = values.size();

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        /**
         * @return a random value instance of this enum
         */
        public static Type random() {
            return values.get(d(size));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        getOpposingMapLocation().setVisible(true);
        return String.format("A %s %s, facing %s, through which you can see %s", size, type, getFacingDirection(), getOpposingMapLocation().getShortDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return String.format("a %s %s", size, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return String.format("%s", type);
    }
}
