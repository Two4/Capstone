package za.ac.mandela.WRPV301.Capstone.Item.Player.Armour;

import com.google.common.collect.ImmutableList;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * {@inheritDoc}
 */
public class RareArmour extends Armour {
    /**
     * Static defense modifier for this class of {@link Armour}
     */
    private static final int DEFENSE_MODIFIER = 50;

    /**
     * {@inheritDoc}
     */
    private RareArmour(ArmourAdjective adjective, ArmourMaterial material, int armourClassModifier) {
        super(adjective, material, armourClassModifier);
    }

    /**
     * @return a randomly generated RareArmour instance
     */
    public static RareArmour random() {
        return new RareArmour(RareAdjective.random(), RareMaterial.random(), DEFENSE_MODIFIER);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("JavaDoc")
    private enum RareMaterial implements ArmourMaterial {
        STEEL,
        TUNGSTEN,
        DIAMOND, //shoutout to Minecraft, yo
        ADAMANTIUM; //...and Marvel comics

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<RareMaterial> values = ImmutableList.copyOf(values());
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
        public static RareMaterial random() {
            return values.get(d(size));
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("JavaDoc")
    enum RareAdjective implements ArmourAdjective {
        FILIGREED,
        GLEAMING,
        ELEGANT,
        MASTERCRAFTED;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<RareAdjective> values = ImmutableList.copyOf(values());
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
        public static RareAdjective random() {
            return values.get(d(size));
        }
    }

}
