package za.ac.mandela.WRPV301.Capstone;

import com.google.common.collect.ListMultimap;
import za.ac.mandela.WRPV301.Capstone.Action.Action;
import za.ac.mandela.WRPV301.Capstone.Action.Actionable;
import za.ac.mandela.WRPV301.Capstone.Combat.Combatant;
import za.ac.mandela.WRPV301.Capstone.Combat.Fight;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Event.GameLossEvent;
import za.ac.mandela.WRPV301.Capstone.Event.GameWinEvent;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Armour.Armour;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Armour.CrudeArmour;
import za.ac.mandela.WRPV301.Capstone.Item.Player.HealthPotion;
import za.ac.mandela.WRPV301.Capstone.Item.Player.PlayerItem;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon.CrudeWeapon;
import za.ac.mandela.WRPV301.Capstone.Item.Player.Weapon.Weapon;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;
import za.ac.mandela.WRPV301.Capstone.Event.PlayerChangeEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class representing the player
 */
public class Player implements Serializable, Combatant {
    /**
     * The currently equipped {@link Weapon}
     */
    private Weapon currentWeapon = CrudeWeapon.random();
    /**
     * The currently equipped {@link Armour}
     */
    private Armour currentArmour = CrudeArmour.random();
    /**
     * The {@link PlayerItem}s this Player is carrying
     */
    private final ArrayList<PlayerItem> inventory = new ArrayList<>(Arrays.asList(currentWeapon, currentArmour));
    /**
     * The current {@link MapLocation} of the player
     */
    private MapLocation currentLocation;
    /**
     * The current health of the player
     */
    private int currentHealth = 100;
    /**
     * The current {@link Fight} the player is participating in
     */
    private Fight currentFight = null;
    /**
     * Health potion instance specific to the player
     */
    private final HealthPotion healthPotion = HealthPotion.playerInstance();

    /**
     * @return the {@link PlayerItem}s this Player is carrying
     */
    public ArrayList<PlayerItem> getInventory() {
        return inventory;
    }

    /**
     * Checks if the player has a specific {@link PlayerItem}
     * @param playerItem the item to check for
     * @return true of the player has the specified item
     */
    public boolean hasItem(PlayerItem playerItem) {
        return inventory.contains(playerItem);
    }

    /**
     * @return the current {@link MapLocation} of the player
     */
    public MapLocation getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Sets the current {@link MapLocation} of the player
     * @param currentLocation the new {@link MapLocation} of the player
     */
    public void setCurrentLocation(MapLocation currentLocation) {
        this.currentLocation = currentLocation;
        currentLocation.setVisible(true);
        PlayerChangeEvent.post();
        if (currentLocation.equals(Game.getMapData().getEndingPoint())) {
            GameWinEvent.create();
        }
    }

    /**
     * @return a {@link ListMultimap} of {@link Action}s available to th player, keyed by their subjects
     */
    public ListMultimap<Actionable, Action> getAvailablePlayerActions() {
        ListMultimap<Actionable, Action> actionMap;
        if (Objects.isNull(currentFight)) {
            actionMap = currentLocation.getLocationActions();
            for (PlayerItem item : inventory) {
                actionMap.putAll(item, item.getAvailableActions());
            }
        } else {
            actionMap = currentFight.getFightActions();
        }
        actionMap.putAll(healthPotion, healthPotion.getAvailableActions());
        return actionMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAttackSuccessRoll(int defenseModifier) {
        return currentWeapon.getAttackRoll(defenseModifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamageRoll() {
        return currentWeapon.getDamageRoll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDefenseModifier() {
        return currentArmour.getDefenseModifier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveAttack(Combatant from) {
        if (from.getAttackSuccessRoll(getDefenseModifier())) {
            ConsoleEvent.output("The attack hits!");
            int damageRoll = from.getDamageRoll();
            if (Game.cheatsOn()) {
                ConsoleEvent.output(String.format("You would be hit for %d points of damage, but you're a DIRTY CHEATER", damageRoll));
            } else {
                currentHealth -= damageRoll;
                ConsoleEvent.output(String.format("You are hit for %d points of damage!", damageRoll));
                ConsoleEvent.output(String.format("You have %d health left", currentHealth));
            }
            if (currentHealth < 1) {
                GameLossEvent.create();
            }
        } else {
            ConsoleEvent.output("The attack misses!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initiateAttack(Combatant against) {
        against.receiveAttack(this);
        if (Objects.nonNull(currentFight)) {
            currentFight.endTurn();
        }
    }

    /**
     * Heals the player by a specified amount, up to 100
     * @param amount the amount to increase the player's health by
     */
    public void heal(int amount) {
        currentHealth = Math.min(currentHealth + amount, 100);
    }

    /**
     * @return the currently equipped {@link Armour}.
     */
    public Armour getCurrentArmour() {
        return currentArmour;
    }

    /**
     * @return the current Player health value.
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Equips another {@link Armour} instance
     *
     * @param armour the {@link Armour} to equip
     */
    public void setCurrentArmour(Armour armour) {
        currentArmour = armour;
    }

    /**
     * @return the currently equipped {@link Weapon}.
     */
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    /**
     * Equips another {@link Weapon} instance.
     *
     * @param weapon the {@link Weapon} equip.
     */
    public void setCurrentWeapon(Weapon weapon) {
        currentWeapon = weapon;
    }

    /**
     * @return the Player-specific {@link HealthPotion} instance
     */
    public HealthPotion getHealthPotion() {
        return healthPotion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fight getCurrentFight() {
        return currentFight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCurrentFight(Fight currentFight) {
        this.currentFight = currentFight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCombatantDescriptor() {
        return "you";
    }
}
