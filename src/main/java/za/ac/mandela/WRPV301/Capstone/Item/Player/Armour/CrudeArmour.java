package za.ac.mandela.WRPV301.Capstone.Item.Player.Armour;

import com.google.common.collect.ImmutableList;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * {@inheritDoc}
 */
public class CrudeArmour extends Armour {
    /**
     * Static defense modifier for this class of {@link Armour}
     */
    private static final int DEFENSE_MODIFIER = 25;

    /**
     * {@inheritDoc}
     */
    private CrudeArmour(ArmourAdjective adjective, ArmourMaterial material, int armourClassModifier) {
        super(adjective, material, armourClassModifier);
    }

    /**
     * @return a randomly generated CrudeArmour instance
     */
    public static CrudeArmour random() {
        return new CrudeArmour(CrudeAdjective.random(), CrudeMaterial.random(), DEFENSE_MODIFIER);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("JavaDoc")
    private enum CrudeMaterial implements ArmourMaterial {
        HIDE,
        BONE,
        CHITIN,
        SCRAP;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<CrudeMaterial> values = ImmutableList.copyOf(values());
        /**
         * Statically stored size of this enum
         */
        private static final int size = values.size();

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        /**
         * @return a random value instance of this enum
         */
        public static CrudeMaterial random() {
            return values.get(d(size));
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("JavaDoc")
    enum CrudeAdjective implements ArmourAdjective {
        DIRTY,
        OLD,
        CORRODED,
        ROUGH;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<CrudeAdjective> values = ImmutableList.copyOf(values());
        /**
         * Statically stored size of this enum
         */
        private static final int size = values.size();

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }

        /**
         * @return a random value instance of this enum
         */
        public static CrudeAdjective random() {
            return values.get(d(size));
        }
    }

}
