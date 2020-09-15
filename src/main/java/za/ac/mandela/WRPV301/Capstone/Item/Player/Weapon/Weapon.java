package za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon;

import com.google.common.collect.ImmutableList;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableRunnable;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Equippable;

import java.util.HashSet;
import java.util.Set;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;
import static za.ac.mandela.WRPV301.Capstone.Util.Utils.p;

/**
 * Class to represent weapon items
 */
public abstract class Weapon extends Equippable {
    /**
     * Base attack damage for all Weapon instances
     */
    protected final static int BASE_ATTACK_DAMAGE = 10;
    /**
     * Modifier for attack success checks
     */
    private final int attackSuccessModifier;
    /**
     * The {@link WeaponType} of this Weapon
     */
    protected final WeaponType type;
    /**
     * The {@link WeaponMaterial} of this Weapon
     */
    protected final WeaponMaterial material;
    /**
     * The {@link WeaponAdjective} of this Weapon
     */
    protected final WeaponAdjective adjective;

    /**
     * @param attackSuccessModifier modifier for attack success checks
     * @param type the weapon weaponType; see {@link WeaponType}
     * @param material material this weapon is made of
     * @param adjective distinctive quality of this weapon
     */
    protected Weapon(int attackSuccessModifier, WeaponType type, WeaponMaterial material, WeaponAdjective adjective) {
        this.attackSuccessModifier = attackSuccessModifier;
        this.type = type;
        this.material = material;
        this.adjective = adjective;
    }

    /**
     * Enum containing values for possible weapon types
     */
    @SuppressWarnings("JavaDoc")
    protected enum WeaponType {
        FLAIL,
        WARHAMMER,
        SHORTSWORD,
        LONGSWORD,
        MAUL,
        MORNINGSTAR,
        SPEAR,
        MACE;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<WeaponType> values = ImmutableList.copyOf(values());
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
        public static WeaponType random() {
            return values.get(d(size));
        }
    }

    /**
     * @return the damage dealt for an attack made with this Weapon, as determined by its dice rolls and modifiers
     */
    public abstract int getDamageRoll();

    /**
     * @return this Weapon's percentile dice roll, used to determine if an attack is successful or not
     * @param defenseModifier the percentage by which to modify the likelihood of success
     */
    public boolean getAttackRoll(int defenseModifier) {
        return p(attackSuccessModifier - defenseModifier);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Equippable getCurrentlyEquipped() {
        return Game.getPlayer().getCurrentWeapon();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void equip() {
        Game.getPlayer().setCurrentWeapon(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unequip() {
        Game.getPlayer().setCurrentWeapon(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("A %s %s, seemingly made of %s", adjective, type, material);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return String.format("a %s %s %s", adjective, material, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAdjectives() {
        HashSet<String> adjectives = new HashSet<>();
        adjectives.add(adjective.toString());
        adjectives.add(material.toString());
        adjectives.add("weapon");
        return adjectives;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return this.type.toString();
    }
}
