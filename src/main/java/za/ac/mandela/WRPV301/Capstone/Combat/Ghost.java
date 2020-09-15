package za.ac.mandela.WRPV301.Capstone.Combat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Item.Room.Corpse;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

import java.util.Set;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;
import static za.ac.mandela.WRPV301.Capstone.Util.Utils.p;

/**
 * Class to represent Ghost-type {@link Enemy}s (sic)
 */
public class Ghost extends Enemy {
    /**
     * The {@link GhostType} of this Ghost
     */
    private final GhostType type;
    /**
     * A distinctive quality for this Ghost
     */
    private final GhostAdjective adjective;
    /**
     * The number of sides for damage dice for this Ghost instance
     */
    private final int damageDiceSides;
    /**
     * The number of damage dice rolled for each attack by this Ghost instance
     */
    private final int damageDiceCount;

    /**
     * Enum containing values for possible Ghost types, from which instances can be constructed
     */
    @SuppressWarnings("JavaDoc")
    public enum GhostType {
        SHADE(6, 2, 20, 10, 30, 10),
        POLTERGEIST( 6, 3, 50, 20, 40, 20),
        WRAITH( 8, 3, 80, 20, 40, 30),
        GHOUL(12, 3, 100, 30, 50, 50);

        private final int damageDiceSides, damageDiceCount, maxHealth, baseAttackDamage, baseSuccessModifier, baseDefenseModifier;

        /**
         * Constructor
         * {@see Ghost constructor}
         */
        GhostType(int damageDiceSides, int damageDiceCount, int maxHealth, int baseAttackDamage, int baseSuccessModifier, int baseDefenseModifier) {
            this.damageDiceSides = damageDiceSides;
            this.damageDiceCount = damageDiceCount;
            this.maxHealth = maxHealth;
            this.baseAttackDamage = baseAttackDamage;
            this.baseSuccessModifier = baseSuccessModifier;
            this.baseDefenseModifier = baseDefenseModifier;
        }

        /**
         * Creates a new Ghost instance at the specified location
         * @param location the location of the resultant Ghost instance
         * @return a new Ghost instance at the specified location
         */
        public Ghost create(Room location) {
            return new Ghost(location, this, GhostAdjective.random(), damageDiceSides, damageDiceCount, maxHealth, baseAttackDamage, baseSuccessModifier, baseDefenseModifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Enum containing values for possible distinctive qualities for a Ghost instance
     */
    @SuppressWarnings("JavaDoc")
    public enum GhostAdjective {
        BLOODSTAINED,
        HAGGARD,
        GRIM,
        DREADFUL,
        GROTESQUE,
        FOUL,
        SINISTER;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<GhostAdjective> values = ImmutableList.copyOf(values());
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
        public static GhostAdjective random() {
            return values.get(d(size));
        }

    }

    /**
     * Constructor
     * @param location the location of this Enemy
     * @param type the {@link GhostType} of this Ghost
     * @param adjective a distinctive quality for this Ghost
     * @param damageDiceSides the number of sides for damage dice for this Ghost instance
     * @param damageDiceCount the number of damage dice rolled for each attack by this Ghost instance
     * @param maxHealth the maximum amount of health for this Enemy
     * @param baseAttackDamage the base damage dealt by this Enemy
     * @param baseSuccessModifier the base modifier for attack success checks for this Enemy
     * @param baseDefenseModifier the base modifier for mitigating attacking {@link Combatant}s' attack success checks
     */
    private Ghost(Room location, GhostType type, GhostAdjective adjective, int damageDiceSides, int damageDiceCount, int maxHealth, int baseAttackDamage, int baseSuccessModifier, int baseDefenseModifier) {
        super(location, maxHealth, baseAttackDamage, baseSuccessModifier, baseDefenseModifier);
        this.type = type;
        this.adjective = adjective;
        this.damageDiceSides = damageDiceSides;
        this.damageDiceCount = damageDiceCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Corpse getCorpse() {
        return new Corpse(getSingleWordDescription(), location) {
            @Override
            protected void gore() {
                ConsoleEvent.output(String.format("The ectoplasmic remains of the %s swirl mistily as your attack passes through", type));
            }

            @Override
            public String getDescription() {
                return "The ectoplasmic remains of some sort of ghostly creature";
            }

            @Override
            public String getShortDescription() {
                return "a puddle of ectoplasm";
            }

            @Override
            public String getSingleWordDescription() {
                return "ectoplasm";
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("A %s levitating, semi-corporeal apparition that you would describe as a %s.", adjective, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return String.format("a %s %s", adjective, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "ghost";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAdjectives() {
        return ImmutableSet.of(type.toString(), adjective.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAttackSuccessRoll(int defenseModifier) {
        return p(baseSuccessModifier - defenseModifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamageRoll() {
        return baseAttackDamage + d(damageDiceSides, damageDiceCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDefenseModifier() {
        return baseDefenseModifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCombatantDescriptor() {
        return String.format("the %s %s", adjective, type);
    }
}
