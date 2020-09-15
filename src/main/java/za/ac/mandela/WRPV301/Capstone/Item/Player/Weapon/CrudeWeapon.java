package za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon;

import com.google.common.collect.ImmutableList;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * {@inheritDoc}
 */
public class CrudeWeapon extends Weapon {
    /**
     * Attack success modifier for this class of weapon
     */
    private static final int BASE_SUCCESS_MODIFIER = 60;

    /**
     * {@inheritDoc}
     */
    private CrudeWeapon(WeaponType weaponType, WeaponMaterial material, WeaponAdjective adjective) {
        super(BASE_SUCCESS_MODIFIER, weaponType, material, adjective);
    }

    /**
     * @return a randomly generated CrudeWeapon instance
     */
    public static CrudeWeapon random() {
        return new CrudeWeapon(WeaponType.random(), CrudeMaterial.random(), CrudeAdjective.random());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamageRoll() {
        return BASE_ATTACK_DAMAGE + d(4, 2);
    }

    /**
     * Enum containing distinctive qualities for a weapon
     */
    @SuppressWarnings("JavaDoc")
    private enum CrudeAdjective implements WeaponAdjective {
        CORRODED,
        CHIPPED,
        BENT,
        DAMAGED;

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

    /**
     * Enum containing values for possible weapon materials
     */
    @SuppressWarnings("JavaDoc")
    private enum CrudeMaterial implements WeaponMaterial {
        FLINT,
        BONE,
        STONE,
        LEAD,
        OBSIDIAN;

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
}