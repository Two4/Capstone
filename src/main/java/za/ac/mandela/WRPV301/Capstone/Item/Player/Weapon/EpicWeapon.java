package za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * {@inheritDoc}
 */
public class EpicWeapon extends Weapon {
    /**
     * Attack success modifier for this class of weapon
     */
    private static final int BASE_SUCCESS_MODIFIER = 100;
    /**
     * The name of this epic weapon
     */
    private final Name name;
    /**
     * Used to determine if the player has seen the name for this weapon yet
     */
    private boolean hasName = false;

    /**
     * Constructor
     * @param weaponType the weapon weaponType; see {@link WeaponType}
     * @param adjective distinctive quality of this weapon
     * @param material material this weapon is made of
     * @param name name of this epic weapon
     */
    private EpicWeapon(WeaponType weaponType, EpicAdjective adjective, EpicMaterial material, Name name) {
        super(BASE_SUCCESS_MODIFIER, weaponType, material, adjective);
        this.name = name;
    }

    /**
     * @return a randomly generated EpicWeapon instance
     */
    public static EpicWeapon random() {
        return new EpicWeapon(WeaponType.random(), EpicAdjective.random(), EpicMaterial.random(), Name.random());
    }

    /**
     * Enum of randomly generated weapon names (except for Steve)
     *
     * Generated using  <a href="https://www.fantasynamegenerators.com/sword-names.php">https://www.fantasynamegenerators.com/sword-names.php</a>
     * Copyright notice:
     * "You're free to use names on this site to name anything in any of your own works, assuming they aren't already trademarked by others of course.
     * All background images part of the generators are part of the public domain and thus free to be used by anybody,
     * with the exception of user submitted backgrounds, images part of existing, copyrighted works, and the pet name generator images.
     * All other original content is part of FantasyNameGenerators.com and cannot be copied, sold or redistributed without permission.
     *
     * Copyright© 2012-2020 FantasyNameGenerators.com"
     */
    @SuppressWarnings("JavaDoc")
    private enum Name {
        REFLECTION,
        BLAZEFURY,
        SNOWFALL,
        OATHKEEPER,
        SOULKEEPER,
        NIGHTFALL,
        LIGHTBANE,
        RIDDLE,
        ECLIPSE,
        COMETFALL,
        RETRIBUTION,
        SHADOWFALL,
        QUICKSILVER,
        SOULSHARD,
        DUSKSONG,
        LIFEBINDER,
        ATARAXIA,
        SOULSLIVER,
        STARFALL,
        BRILLIANCE,
        DOOMWARD,
        MOONSHADOW,
        CELESTIA,
        STEVE; //I've always wanted a warhammer named Steve

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<Name> values = ImmutableList.copyOf(values());
        /**
         * Statically stored size of this enum
         */
        private static final int size = values.size();

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return StringUtils.capitalize(this.name());
        }

        /**
         * @return a random value instance of this enum
         */
        public static Name random() {
            return values.get(d(size));
        }

    }

    /**
     * Enum containing distinctive qualities for a weapon
     */
    @SuppressWarnings("JavaDoc")
    private enum EpicAdjective implements WeaponAdjective {
        GLOWING("emitting a strange, ethereal glow"),
        SHINING("shining brightly in the light"),
        GLEAMING("gleaming, even with no light"),
        HUMMING("emitting a soft, low hum");

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<EpicAdjective> values = ImmutableList.copyOf(values());
        /**
         * Statically stored size of this enum
         */
        private static final int size = values.size();
        /**
         * A longer description of this weapon's distinctive quality
         */
        private final String description;

        /**
         * Constructor
         * @param description a longer description of this weapon's distinctive quality
         */
        EpicAdjective(String description) {
            this.description = description;
        }

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
        public static EpicAdjective random() {
            return values.get(d(size));
        }


        /**
         * @return a longer description of this weapon's distinctive quality.
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * Enum containing values for possible weapon materials
     */
    @SuppressWarnings("JavaDoc")
    private enum EpicMaterial implements WeaponMaterial {
        GLASS,
        STEEL,
        SILVER,
        GOLD,
        AETHER,
        LIQUID;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<EpicMaterial> values = ImmutableList.copyOf(values());
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
        public static EpicMaterial random() {
            return values.get(d(size));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        if (hasName) {
            return String.format("a %s named %s, made of %s and is %s", type, name, material, ((EpicAdjective) adjective).getDescription());
        }
        return String.format("a %s made of %s and is %s", type, material, ((EpicAdjective) adjective).getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        if (hasName) {
            return String.format("%s, a %s %s %s", name, adjective, material, type);
        }
        return String.format("a %s %s %s", adjective, material, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamageRoll() {
        return BASE_ATTACK_DAMAGE + d(10, 2) + d(20);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        if (hasName) {
            return name.toString();
        }
        return type.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<String> getAdjectives() {
        HashSet<String> adjectives = new HashSet<>(super.getAdjectives());
        if (hasName) {
            adjectives.add(type.toString());
        }
        return adjectives;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String takeFeedback() {
        if (hasName) {
            return String.format("You take %s.", getSingleWordDescription());
        }
        hasName = true;
        return String.format("You take the %s %s %s. You decide such a fine weapon deserves a name: %s", adjective, material, type, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String dropFeedback() {
        return String.format("You drop %s", name);
    }
}
