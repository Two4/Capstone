package za.ac.mandela.WRPV301.Capstone.Combat;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import za.ac.mandela.WRPV301.Capstone.Action.Action;
import za.ac.mandela.WRPV301.Capstone.Action.Actionable;
import za.ac.mandela.WRPV301.Capstone.Event.AccesswayChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;
import za.ac.mandela.WRPV301.Capstone.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.d;

/**
 * Class to represent a fight between {@link Combatant}s
 */
public class Fight implements Serializable {
    /**
     * The turn order of the {@link Combatant}s
     */
    private final ArrayList<Combatant> initiativeOrder;
    /**
     * Iterator for the {@code initiativeOrder}, used to keep track of the {@code currentCombatant}
     */
    private ListIterator<Combatant> initiativeOrderIterator;
    /**
     * The {@link Combatant} for which it is the current turn
     */
    private Combatant currentCombatant;
    /**
     * The number of actions the player has remaining in their turn
     */
    private static int numPlayerActionsRemaining;

    /**
     * Constructor
     * @param fightLocation the {@link Room} the fight is taking place in
     * @param playerInitiated true of the player initiated this fight
     */
    private Fight(Room fightLocation, boolean playerInitiated) {
        numPlayerActionsRemaining = 2;
        initiativeOrder = new ArrayList<>(fightLocation.getEnemies());
        if (playerInitiated) {
            initiativeOrder.add(0, Game.getPlayer()); //place player at the top of the initiative order
        } else {
            initiativeOrder.add(d(initiativeOrder.size()) + 1, Game.getPlayer()); //place player randomly in the initiative order, but not at the top
        }
        for (Combatant combatant : initiativeOrder) {
            combatant.setCurrentFight(this);
        }
        initiativeOrderIterator = initiativeOrder.listIterator();
        currentCombatant = initiativeOrderIterator.next();
    }

    /**
     * Static constructor used when an {@link Enemy} initiates the fight
     * @param fightLocation the {@link Room} the fight is taking place in
     */
    public static void attackingPlayer(Room fightLocation) {
        Fight fight = new Fight(fightLocation, false);
        fight.currentCombatant.initiateAttack(Game.getPlayer());
    }

    /**
     * Static constructor used when the player initiates the fight
     * @param fightLocation the {@link Room} the fight is taking place in
     */
    public static void playerAttacking(Room fightLocation) {
        new Fight(fightLocation, true);
        MapLocation playerLocation = Game.getPlayer().getCurrentLocation();
        if (!playerLocation.equals(fightLocation)) {
            Game.getPlayer().setCurrentLocation(fightLocation);
            AccesswayChangeEvent.on(Game.getMapData().edgeConnecting(fightLocation, playerLocation).orElseThrow());
        }
    }

    /**
     * Ends the turn of the {@link #currentCombatant}
     */
    public void endTurn() {
        if (currentCombatant instanceof Player) {
            if (--numPlayerActionsRemaining > 0){
                ConsoleEvent.output(String.format("You have %d actions remaining this turn", numPlayerActionsRemaining));
                return;
            } else {
                numPlayerActionsRemaining = 2;
            }
        }
        if (!initiativeOrderIterator.hasNext()) {
            initiativeOrderIterator = initiativeOrder.listIterator();
        }
        currentCombatant = initiativeOrderIterator.next();
        if (!(currentCombatant instanceof Player)) {
            currentCombatant.initiateAttack(getDisparateCombatant(currentCombatant));
        } else {
            ConsoleEvent.output("Your turn! You have an opportunity to do something!");
        }
    }

    /**
     * Removes the specified {@link Combatant} from this Fight
     * @param combatant the {@link Combatant} to remove
     */
    public void removeCombatant(Combatant combatant) {
        initiativeOrder.remove(combatant);
        if (initiativeOrder.size() < 2 && initiativeOrder.contains(Game.getPlayer())) {
            Game.getPlayer().setCurrentFight(null);
            ConsoleEvent.output("You have won the battle!");
        } else {
            initiativeOrderIterator = initiativeOrder.listIterator(initiativeOrder.indexOf(currentCombatant));
        }
    }

    /**
     * @return the {@link Combatant}s in this Fight
     */
    public ArrayList<Combatant> getCombatants() {
        return initiativeOrder;
    }

    /**
     * Finds an enemy that is not of the same type as the specified {@link Combatant}
     * @param from the {@link Combatant} for which to find a disparate {@link Combatant} member
     * @return an enemy that is not of the same type as the specified {@link Combatant}
     */
    public Combatant getDisparateCombatant(Combatant from) {
        ArrayList<Combatant> otherCombatants = initiativeOrder.stream().filter(combatant -> !(from.getClass().isInstance(combatant))).collect(Collectors.toCollection(ArrayList::new));
        return otherCombatants.get(d(otherCombatants.size()));
    }

    /**
     * @return {@link ListMultimap} of {@link Action}s available to the player in this fight
     */
    public ListMultimap<Actionable, Action> getFightActions() {
        ListMultimap<Actionable, Action> fightActionMap = MultimapBuilder
                .linkedHashKeys()
                .arrayListValues()
                .build();
        if (currentCombatant.equals(Game.getPlayer())) {
            for (Combatant combatant : initiativeOrder) {
                if (combatant instanceof Enemy) {
                    Enemy enemy = (Enemy) combatant;
                    fightActionMap.putAll(enemy, enemy.getAvailableActions());
                }
            }
        }
        return fightActionMap;
    }
}
