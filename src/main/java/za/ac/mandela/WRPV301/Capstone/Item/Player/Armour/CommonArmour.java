package za.ac.mandela.WRPV301.Capstone.Item.Player.Armour;

import com.google.common.collect.ImmutableList;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * {@inheritDoc}
 */
public class CommonArmour extends Armour {
    /**
     * Static defense modifier for this class of {@link Armour}
     */
    private static final int DEFENSE_MODIFIER = 35;

    /**
     * {@inheritDoc}
     */
    private CommonArmour(ArmourAdjective adjective, ArmourMaterial material, int armourClassModifier) {
        super(adjective, material, armourClassModifier);
    }

    /**
     * @return a randomly generated CommonArmour instance
     */
    public static CommonArmour random() {
        return new CommonArmour(CommonAdjective.random(), CommonMaterial.random(), DEFENSE_MODIFIER);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("JavaDoc")
    private enum CommonMaterial implements ArmourMaterial {
        IRON,
        LEATHER,
        BRONZE,
        CERAMIC;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<CommonMaterial> values = ImmutableList.copyOf(values());
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
        public static CommonMaterial random() {
            return values.get(d(size));
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("JavaDoc")
    enum CommonAdjective implements ArmourAdjective {
        POLISHED,
        ENGRAVED,
        STURDY,
        TOUGH;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<CommonAdjective> values = ImmutableList.copyOf(values());
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
        public static CommonAdjective random() {
            return values.get(d(size));
        }
    }


}
