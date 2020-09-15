package za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon;

import com.google.common.collect.ImmutableList;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * {@inheritDoc}
 */
public class CommonWeapon extends Weapon {
    /**
     * Success modifier for this class of weapon
     */
    private static final int BASE_SUCCESS_MODIFIER = 75;

    /**
     * {@inheritDoc}
     */
    private CommonWeapon(WeaponType weaponType, WeaponMaterial material, WeaponAdjective adjective) {
        super(BASE_SUCCESS_MODIFIER, weaponType, material, adjective);
    }

    /**
     * @return a randomly generated CommonWeapon instance
     */
    public static CommonWeapon random() {
        return new CommonWeapon(WeaponType.random(), CommonMaterial.random(), CommonAdjective.random());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamageRoll() {
        return BASE_ATTACK_DAMAGE + d(8, 2);
    }

    /**
     * Enum containing distinctive qualities for a weapon
     */
    @SuppressWarnings("JavaDoc")
    private enum CommonAdjective implements WeaponAdjective {
        BLUNT,
        SCRATCHED,
        ROUGH,
        OLD;

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

    /**
     * Enum containing values for possible weapon materials
     */
    @SuppressWarnings("JavaDoc")
    private enum CommonMaterial implements WeaponMaterial {
        IRON,
        BRONZE;

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
}