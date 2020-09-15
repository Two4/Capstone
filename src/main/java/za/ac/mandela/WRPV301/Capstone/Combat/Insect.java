package za.ac.mandela.WRPV301.Capstone.Combat;

import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Item.Player.HealthPotion;
import za.ac.mandela.WRPV301.Capstone.Item.Room.Corpse;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;
import static za.ac.mandela.WRPV301.Capstone.Util.Utils.p;

/**
 * A class representing Insect-type {@link Enemy}s
 */
public class Insect extends Enemy {
    /**
     * The {@link InsectSize} (i.e. size descriptor) of the this Insect
     */
    private final InsectSize size;
    /**
     * The number of sides for damage dice for this Insect instance
     */
    private final int damageDiceSides;
    /**
     * The number of damage dice rolled for each attack by this Insect instance
     */
    private final int damageDiceCount;

    /**
     * Enum containing values for the possible size of an Insect instance
     */
    @SuppressWarnings("JavaDoc")
    private enum InsectSize {
        DOG_SIZED,
        MAN_SIZED,
        HUGE,
        GARGANTUAN;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return this.name().replaceAll("_", "-").toLowerCase();
        }
    }

    /**
     * Enum containing values for possible Insect types, from which instances can be constructed
     */
    @SuppressWarnings("JavaDoc")
    public enum InsectType {
        WORKER(InsectSize.DOG_SIZED, 4, 2, 20, 0, 10, 0),
        SOLDIER(InsectSize.MAN_SIZED, 6, 3, 40, 0, 20, 20),
        DESTROYER(InsectSize.HUGE, 8, 3, 80, 0, 40, 30),
        QUEEN(InsectSize.GARGANTUAN, 12, 3, 160, 0, 80, 50);

        /**
         * The {@link InsectSize} (i.e. size descriptor) of the the resultant Insect
         */
        private final InsectSize size;
        private final int damageDiceSides;
        private final int damageDiceCount;
        private final int maxHealth;
        private final int baseAttackDamage;
        private final int baseSuccessModifier;
        private final int baseDefenseModifier;

        /**
         * Constructor
         * {@see Insect constructor}
         */
        InsectType(InsectSize size, int damageDiceSides, int damageDiceCount, int maxHealth, int baseAttackDamage, int baseSuccessModifier, int baseDefenseModifier) {
            this.size = size;
            this.damageDiceSides = damageDiceSides;
            this.damageDiceCount = damageDiceCount;
            this.maxHealth = maxHealth;
            this.baseAttackDamage = baseAttackDamage;
            this.baseSuccessModifier = baseSuccessModifier;
            this.baseDefenseModifier = baseDefenseModifier;
        }

        /**
         * Creates a new Insect instance at the specified location
         * @param location the location of the resultant Insect instance
         * @return a new Insect instance at the specified location
         */
        public Insect create(Room location) {
            return new Insect(location, size, damageDiceSides, damageDiceCount, maxHealth, baseAttackDamage, baseSuccessModifier, baseDefenseModifier);
        }
    }


    /**
     * Constructor
     * @param location the location of this Enemy
     * @param size the size of this Insect
     * @param damageDiceSides the number of sides for damage dice for this Insect instance
     * @param damageDiceCount the number of damage dice rolled for each attack by this Insect instance
     * @param maxHealth the maximum amount of health for this Enemy
     * @param baseAttackDamage the base damage dealt by this Enemy
     * @param baseSuccessModifier the base modifier for attack success checks for this Enemy
     * @param baseDefenseModifier the base modifier for mitigating attacking {@link Combatant}s' attack success checks
     */
    private Insect(Room location, InsectSize size, int damageDiceSides, int damageDiceCount, int maxHealth, int baseAttackDamage, int baseSuccessModifier, int baseDefenseModifier) {
        super(location, maxHealth, baseAttackDamage, baseSuccessModifier, baseDefenseModifier);
        this.size = size;
        this.damageDiceSides = damageDiceSides;
        this.damageDiceCount = damageDiceCount;
        this.inventory.add(new HealthPotion(damageDiceSides));
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
    protected Corpse getCorpse() {
        return new Corpse(getSingleWordDescription(), location) {
            @Override
            public String getDescription() {
                return String.format("The %s body of some sort of very large insect species; it looks to be leaking insect juices", size);
            }

            @Override
            public String getShortDescription() {
                return String.format("a %s insect corpse", size);
            }

            @Override
            protected void gore() {
                ConsoleEvent.output("Insect juices splatter everywhere; you reflect that you may be a violent sociopath of some kind. Oh well...");
            }

            @Override
            public Set<String> getAdjectives() {
                Set<String> adjectives = super.getAdjectives();
                adjectives.add("insect");
                return adjectives;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("A %s insect, with razor sharp mandibles and long, spindly, legs", size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return String.format("a %s insect", size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "insect";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCombatantDescriptor() {
        return String.format("the %s insect", size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAdjectives() {
        return new HashSet<>(Arrays.asList("bug", "insectoid", size.toString()));
    }
}
