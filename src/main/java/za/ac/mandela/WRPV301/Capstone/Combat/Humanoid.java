package za.ac.mandela.WRPV301.Capstone.Combat;

import com.google.common.collect.ImmutableList;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Armour.Armour;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Armour.CommonArmour;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Armour.CrudeArmour;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Armour.RareArmour;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon.*;
import za.ac.mandela.WRPV301.Capstone.Item.Room.Corpse;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

import java.util.HashSet;
import java.util.Set;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;
import static za.ac.mandela.WRPV301.Capstone.Util.Utils.p;

/**
 * Class representing a Humanoid-type {@link Enemy}
 */
public class Humanoid extends Enemy {
    /**
     * The {@link Weapon} carried by this Humanoid
     */
    private final Weapon weapon;
    /**
     * The {@link Armour} worn by this Humanoid
     */
    private final Armour armour;
    /**
     * A descriptive {@link HumanoidAdjective} for this Humanoid
     */
    private final HumanoidAdjective adjective;

    /**
     * Constructor
     * @param location the location of this Humanoid
     * @param weapon the {@link Weapon} carried by this Humanoid
     * @param armour the {@link Armour} worn by this Humanoid
     * @param adjective a descriptive {@link HumanoidAdjective} for this Humanoid
     */
    private Humanoid(Room location, Weapon weapon, Armour armour, HumanoidAdjective adjective) {
        super(location, 100, 0, 0, 0);
        this.weapon = weapon;
        this.armour = armour;
        this.adjective = adjective;
        inventory.add(weapon);
        inventory.add(armour);
    }

    /**
     * Spawns a minion-type Humanoid for the specified {@code location}
     * @param location the {@link Room} where the resultant Humanoid will be placed
     * @return a minion-type Humanoid for the specified {@code location}
     */
    public static Humanoid minion(Room location) {
        Weapon weapon;
        Armour armour;
        if (p(10)) {
            weapon = CommonWeapon.random();
        } else {
            weapon = CrudeWeapon.random();
        }
        if (p(10)) {
            armour = CommonArmour.random();
        } else {
            armour = CrudeArmour.random();
        }
        return new Humanoid(location, weapon, armour, HumanoidAdjective.random());
    }

    /**
     * Spawns a soldier-type Humanoid for the specified {@code location}
     * @param location the {@link Room} where the resultant Humanoid will be placed
     * @return a soldier-type Humanoid for the specified {@code location}
     */
    public static Humanoid soldier(Room location) {
        Weapon weapon;
        Armour armour;
        if (p(50)) {
            weapon = CommonWeapon.random();
        } else {
            weapon = CrudeWeapon.random();
        }
        if (p(50)) {
            armour = CommonArmour.random();
        } else {
            armour = CrudeArmour.random();
        }
        return new Humanoid(location, weapon, armour, HumanoidAdjective.random());
    }

    /**
     * Spawns a miniboss-type Humanoid for the specified {@code location}
     * @param location the {@link Room} where the resultant Humanoid will be placed
     * @return a miniboss-type Humanoid for the specified {@code location}
     */
    public static Humanoid chief(Room location) {
        Weapon weapon;
        Armour armour;
        if (p(10)) {
            weapon = RareWeapon.random();
        } else {
            weapon = CommonWeapon.random();
        }
        armour = CommonArmour.random();
        return new Humanoid(location, weapon, armour, HumanoidAdjective.random());
    }

    /**
     * Spawns a boss-type Humanoid for the specified {@code location}
     * @param location the {@link Room} where the resultant Humanoid will be placed
     * @return a boss-type Humanoid for the specified {@code location}
     */
    public static Humanoid boss(Room location) {
        Weapon weapon;
        Armour armour;
        if (p(10)) {
            weapon = EpicWeapon.random();
        } else {
            weapon = RareWeapon.random();
        }
        armour = RareArmour.random();
        return new Humanoid(location, weapon, armour, HumanoidAdjective.random());
    }

    /**
     * Enum containing values for possible distinctive qualities for a Humanoid
     */
    @SuppressWarnings("JavaDoc")
    public enum HumanoidAdjective {
        ROTUND,
        GAUNT,
        LANKY,
        STOUT,
        TALL,
        SKINNY,
        PLUMP;

        /**
         * Static list of enum values, used to generate random values statically
         */
        private static final ImmutableList<HumanoidAdjective> values = ImmutableList.copyOf(values());
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
        public static HumanoidAdjective random() {
            return values.get(d(size));
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Corpse getCorpse() {
        return new Corpse(getSingleWordDescription(), location) {
            @Override
            protected void gore() {
                ConsoleEvent.output(String.format(
                        "Blood splatters over you and the floor around you as your attack crunches the lifeless %s humanoid body. If it wasn't dead, it is now.",
                        adjective));
            }

            @Override
            public String getDescription() {
                return String.format("The dead body of a %s-looking humanoid", adjective);
            }

            @Override
            public String getShortDescription() {
                return String.format("a %s humanoid corpse", adjective);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return String.format("An %s, almost-human looking creature, wielding %s and clad in %s", adjective, weapon.getShortDescription(), armour.getShortDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return String.format("a %s humanoid wielding %s", adjective, weapon.getShortDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "humanoid";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAdjectives() {
        HashSet<String> adjectives = new HashSet<>(weapon.getAdjectives());
        adjectives.add(adjective.toString());
        return adjectives;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAttackSuccessRoll(int defenseModifier) {
        return weapon.getAttackRoll(defenseModifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamageRoll() {
        return weapon.getDamageRoll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDefenseModifier() {
        return baseDefenseModifier + armour.getDefenseModifier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCombatantDescriptor() {
        return String.format("the %s humanoid", adjective);
    }
}
