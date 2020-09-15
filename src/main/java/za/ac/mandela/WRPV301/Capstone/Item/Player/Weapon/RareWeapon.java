package za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon;

import com.google.common.collect.ImmutableList;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * {@inheritDoc}
 */
public class RareWeapon extends Weapon {
    /**
     * Success modifier for this class of weapon
     */
    protected final static int BASE_SUCCESS_MODIFIER = 85;

    /**
     * {@inheritDoc}
     */
    private RareWeapon(WeaponType weaponType, RareAdjective adjective, RareMaterial material) {
        super(BASE_SUCCESS_MODIFIER, weaponType, material, adjective);
    }

    /**
     * @return a randomly generated RareWeapon instance
     */
    public static RareWeapon random() {
        return new RareWeapon(WeaponType.random(), RareAdjective.random(), RareMaterial.random());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamageRoll() {
        return BASE_ATTACK_DAMAGE + d(10) + d(6,2);
    }

    /**
     * Enum containing distinctive qualities for a weapon
     */
    @SuppressWarnings("JavaDoc")
    private enum RareAdjective implements WeaponAdjective {
        ELEGANT,
        STURDY,
        FINE,
        POLISHED;

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

    /**
     * Enum containing values for possible weapon materials
     */
    @SuppressWarnings("JavaDoc")
    private enum RareMaterial implements WeaponMaterial {
        STEEL,
        SILVER,
        TUNGSTEN,
        ELECTRUM;

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
}