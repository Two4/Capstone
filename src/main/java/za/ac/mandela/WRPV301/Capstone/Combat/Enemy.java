package za.ac.mandela.WRPV301.Capstone.Combat;

import org.apache.commons.lang3.StringUtils;
import za.ac.mandela.WRPV301.Capstone.Action.ActionBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.Describable;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableCallable;
import za.ac.mandela.WRPV301.Capstone.Action.SerializableRunnable;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Event.LocationChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Item.Room.Corpse;
import za.ac.mandela.WRPV301.Capstone.Item.Player.PlayerItem;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;

import java.util.ArrayList;
import java.util.Objects;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * Abstract class to represent an enemy {@link Combatant}
 */
public abstract class Enemy extends Describable implements Combatant {
    /**
     * The location of this Enemy
     */
    protected final Room location;
    /**
     * The current amount of health of this Enemy
     */
    private int currentHealth;
    /**
     * The base damage dealt by this Enemy
     */
    protected final int baseAttackDamage;
    /**
     * The base modifier for attack success checks for this Enemy
     */
    protected final int baseSuccessModifier;
    /**
     * The base modifier for mitigating attacking {@link Combatant}s' attack success checks
     */
    protected final int baseDefenseModifier;
    /**
     * The {@link PlayerItem}s carried by this Enemy
     */
    protected final ArrayList<PlayerItem> inventory;
    /**
     * The current {@link Fight} this Enemy is a member of
     */
    private Fight currentFight = null;

    /**
     * Constructor
     * @param location the location of this Enemy
     * @param maxHealth the maximum amount of health for this Enemy
     * @param baseAttackDamage the base damage dealt by this Enemy
     * @param baseSuccessModifier the base modifier for attack success checks for this Enemy
     * @param baseDefenseModifier the base modifier for mitigating attacking {@link Combatant}s' attack success checks
     */
    protected Enemy(Room location, int maxHealth, int baseAttackDamage, int baseSuccessModifier, int baseDefenseModifier) {
        this.location = location;
        this.baseAttackDamage = baseAttackDamage;
        this.baseSuccessModifier = baseSuccessModifier;
        this.baseDefenseModifier = baseDefenseModifier;
        inventory = new ArrayList<>();
        currentHealth = maxHealth;
        addAction(ActionBuilder.newInstance()
                .setAliases("Attack", "Charge", "Strike", "Hit", "Stab", "Cut", "Rush", "Assault", "Pound",
                        "Assault", "Assail", "Damage", "Kill", "Murder", "Terminate", "Massacre", "Eradicate",
                        "Execute", "Destroy", "Defeat", "Exterminate", "Assassinate", "Slay", "End", "Wipe Out",
                        "Take Out", "Butcher", "Extirpate", "Liquidate", "Decimate", "Whack"
                )
                .setActionRunnable(this::enterCombat)
                .setActivator(this::canInitiateCombat)
                .setFeedbackProvider(() -> String.format("You attack %s", getCombatantDescriptor()))
                .setDescriptor(() -> String.format("Try to do grievous bodily harm and/or inflict death upon %s", getCombatantDescriptor()))
                .build()
        );
        addAction(ActionBuilder.newInstance()
                .setAliases("Attack", "Charge", "Strike", "Hit", "Stab", "Cut", "Rush", "Assault", "Pound",
                        "Assault", "Assail", "Damage", "Kill", "Murder", "Terminate", "Massacre", "Eradicate",
                        "Execute", "Destroy", "Defeat", "Exterminate", "Assassinate", "Slay", "End", "Wipe Out",
                        "Take Out", "Butcher", "Extirpate", "Liquidate", "Decimate", "Whack"
                )
                .setActionRunnable(this::receivePlayerAttack)
                .setActivator(this::isInCombat)
                .setFeedbackProvider(() -> String.format("You attack %s", getCombatantDescriptor()))
                .setDescriptor(() -> String.format("Try to do grievous bodily harm and/or inflict death upon %s", getCombatantDescriptor()))
                .build());
    }

    /**
     * Generates a random minion-type Enemy
     * @param location the location of the resultant Enemy
     * @return a random minion-type Enemy
     */
    public static Enemy randomMinion(Room location) {
        switch (d(3)) {
            case 0:
                return Insect.InsectType.WORKER.create(location);
            case 1:
                return Ghost.GhostType.SHADE.create(location);
            default:
                return Humanoid.minion(location);
        }
    }

    /**
     * Generates a random soldier-type Enemy
     * @param location the location of the resultant Enemy
     * @return a random soldier-type Enemy
     */
    public static Enemy randomSoldier(Room location) {
        switch (d(3)) {
            case 0:
                return Insect.InsectType.SOLDIER.create(location);
            case 1:
                return Ghost.GhostType.POLTERGEIST.create(location);
            default:
                return Humanoid.soldier(location);
        }
    }

    /**
     * Generates a random miniboss-type Enemy
     * @param location the location of the resultant Enemy
     * @return a random miniboss-type Enemy
     */
    public static Enemy randomMiniBoss(Room location) {
        switch (d(3)) {
            case 0:
                return Insect.InsectType.DESTROYER.create(location);
            case 1:
                return Ghost.GhostType.WRAITH.create(location);
            default:
                return Humanoid.chief(location);
        }
    }

    /**
     * Generates a random boss-type Enemy
     * @param location the location of the resultant Enemy
     * @return a random boss-type Enemy
     */
    public static Enemy randomBoss(Room location) {
        switch (d(3)) {
            case 0:
                return Insect.InsectType.QUEEN.create(location);
            case 1:
                return Ghost.GhostType.GHOUL.create(location);
            default:
                return Humanoid.boss(location);
        }
    }

    /**
     * @return true if this Enemy is currently a member of a {@link Fight}
     */
    private boolean isInCombat() {
        return Objects.nonNull(currentFight);
    }

    /**
     * Convenience method to initiate an attack on behalf of the player
     */
    private void receivePlayerAttack() {
        Game.getPlayer().initiateAttack(this);
    }

    /**
     * Instantiates a fight and initiates an attack on behalf of the player
     */
    private void enterCombat() {
        Fight.playerAttacking(location);
        Game.getPlayer().initiateAttack(this);
    }

    /**
     * Called when this Enemy has no health left
     */
    protected void die() {
        ConsoleEvent.output(
                String.format(
                        "%s dies an agonizing death, staring deep inside your soul as the lights leave their eyes. Why did it have to be this way?",
                        StringUtils.capitalize(getCombatantDescriptor())
                )
        );
        if (!inventory.isEmpty()) {
            ConsoleEvent.output("Oh well, at least they dropped some loot:");
            for (PlayerItem item : inventory) {
                location.addPlayerItem(item);
                ConsoleEvent.output(item.getShortDescription());
            }
        }
        currentFight.removeCombatant(this);
        currentFight = null;
        location.removeEnemy(this);
        location.addRoomItem(getCorpse());
        LocationChangeEvent.on(location);
    }

    /**
     * @return a new {@link Corpse} instance for this Enemy
     */
    protected abstract Corpse getCorpse();

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveAttack(Combatant from) {
        if (from.getAttackSuccessRoll(getDefenseModifier())) {
            ConsoleEvent.output("The attack hits!");
            int damageRoll = from.getDamageRoll();
            currentHealth -= damageRoll;
            ConsoleEvent.output(String.format("%s is hit for %d points of damage!", StringUtils.capitalize(getCombatantDescriptor()), damageRoll));
            if (currentHealth < 1) {
                die();
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
        ConsoleEvent.output(String.format("%s attacks %s!", StringUtils.capitalize(getCombatantDescriptor()), against.getCombatantDescriptor()));
        against.receiveAttack(this);
        currentFight.endTurn();
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
     * @return true if this Enemy can currently initiate combat
     */
    private boolean canInitiateCombat() {
        return location.isVisible() && Objects.isNull(currentFight);
    }
}
