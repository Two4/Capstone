package za.ac.mandela.WRPV301.Capstone.Util;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import za.ac.mandela.WRPV301.Capstone.Action.Action;
import za.ac.mandela.WRPV301.Capstone.Action.Actionable;
import za.ac.mandela.WRPV301.Capstone.Action.Describable;
import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class that parses, disambiguates and executes player command inputs
 */
public class InputParser {
    /**
     * A set of common prepositions. These words make sense to humans, but just clutter input strings for the parser.
     */
    private static final HashSet<String> prepositions = new HashSet<>(Arrays.asList("above", "across", "against", "along", "among",
            "around", "at", "before", "behind", "below", "beneath", "beside", "between", "by", "down", "from", "in",
            "into", "near", "of", "off", "on", "to", "toward", "under", "upon", "with", "within"));
    /**
     * Words that refer to objects, i.e. articles and demonstrative pronouns. These words make sense to humans, but just
     * clutter input strings for the parser.
     */
    private static final HashSet<String> subjectReferences = new HashSet<>(Arrays.asList("a", "an", "the", "this", "these", "those", "that"));

    /**
     * Static method that accepts user input and tries to resolve it to a single action. If resolution is successful,
     * the action is executed.
     * @param userInput user input string
     */
    public static void parseAndExecute(String userInput) {
        ListMultimap<Actionable, Action> actionMap = Game.getPlayer().getAvailablePlayerActions();
        String input = userInput.toUpperCase().replaceAll("[^A-Z\\s]", "");
        disambiguate(actionMap, input);
        switch (actionMap.size()) {
            case 0:
                ConsoleEvent.output("I'm afraid you can't do that.");
                break;
            case 1:
                actionMap.values().stream().findFirst().ifPresent(action -> {
                    if (userInput.endsWith("?")) {
                        ConsoleEvent.output(action.getDescription());
                    } else {
                        ConsoleEvent.output(action.getFeedBack());
                        action.execute();
                    }
                });
                break;
            default:
                ConsoleEvent.output("You are referring to more than one possible action. Try being more specific");
                break;
        }
    }

    /**
     * Filters out possible actions by subject and action: if the user specifies a subject, actions not pertaining to it
     * are removed; actions which do not have an alias present in the user input are also removed.
     * @param actionMap a {@link Multimap} keyed by actionable subjects and populated with available actions
     * @param userInput processed user input string
     */
    private static void validateActions(ListMultimap<Actionable, Action> actionMap, String userInput) {
        boolean containsSubject = false;
        for (Actionable actionable : actionMap.keySet()) {
            if (actionable instanceof Describable && userInput.matches(String.format("(s?).*(\\b%s)(\\b|\\W).*", ((Describable) actionable).getSingleWordDescription().toUpperCase()))) {
                containsSubject = true;
                break;
            }
        }
        if (containsSubject) {
            actionMap.entries().removeIf(entry -> !subjectActionIsValid(entry, userInput));
        } else {
            actionMap.entries().removeIf(entry -> !actionIsValid(entry, userInput));
        }
    }

    /**
     * Predicate function that tests actions by subject and alias
     * @param entry {@link Multimap} {@link Map.Entry} keyed by actionable subject, valued by an {@link Action}
     * @param userInput processed user input string
     * @return true if the current entry matches the subject and action alias in the user input string; false otherwise
     */
    private static boolean subjectActionIsValid(Map.Entry<Actionable, Action> entry, String userInput) {
        if (entry.getKey() instanceof Describable) {
            if (!userInput.matches(String.format("(s?).*(\\b%s)(\\b|\\W).*", ((Describable) entry.getKey()).getSingleWordDescription().toUpperCase()))) {
                return false;
            }
        }
        return actionIsValid(entry, userInput);
    }

    /**
     * Predicate function that tests actions by alias
     * @param entry {@link Multimap} {@link Map.Entry} keyed by actionable subject, valued by an {@link Action}
     * @param userInput processed user input string
     * @return true if the current entry value matches the action alias in the user input string; false otherwise
     */
    private static boolean actionIsValid(Map.Entry<Actionable, Action> entry, String userInput) {
        for (String alias : entry.getValue().getAliases()) {
            if (userInput.matches(String.format("(s?).*(\\b%s)(\\b|\\W).*", alias.toUpperCase()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reduces the space of possible actions the user input potentially translates to, first by testing for subject and
     * action alias, then, if necessary, by adjectives associated with remaining candidate actionable subjects
     * @param possibleActions a {@link Multimap} keyed by actionable subjects and populated with available actions
     * @param userInput processed user input string
     */
    private static void disambiguate(ListMultimap<Actionable, Action> possibleActions, String userInput) {
        validateActions(possibleActions, userInput);
        if (possibleActions.size() > 1) {
            possibleActions.entries().removeIf(entry -> {
                Actionable actionable = entry.getKey();
                Action action = entry.getValue();
                String[] remaining = stripAndSplit(userInput, action, actionable);
                    if (actionable instanceof Describable) {
                        return !hasAdjectives(((Describable) actionable), remaining);
                    }
                return true;
            });
        }
    }

    /**
     * Tests if a {@link Describable} object has all supplied adjectives contained within the object's adjective set
     * @param describable a {@link Describable} object to test
     * @param adjectives the (presumed) adjectives extracted from the user input string
     * @return true if all supplied adjectives are contained within the object's adjective set; false otherwise.
     */
    private static boolean hasAdjectives(Describable describable, String[] adjectives) {
        if (adjectives.length < 1) {
            return true;
        }
        Set<String> subjectAdjectives = describable.getAdjectives();
        subjectAdjectives = subjectAdjectives.stream().map(String::toUpperCase).collect(Collectors.toCollection(HashSet::new));
        return subjectAdjectives.containsAll(Arrays.asList(adjectives));
    }

    /**
     * Strips out the subject and all possible action aliases from a user input string, as well as common prepositions,
     * articles and demonstrative pronouns, then splits the remaining words into an array of presumed adjectives
     * @param userInput processed user input string
     * @param action the {@link Action} to use when stripping aliases from the input string
     * @param subject the {@link Actionable} object to use when stripping out action subjects
     * @return an array of presumed adjectives present in the user input
     */
    private static String[] stripAndSplit(String userInput, Action action, Actionable subject) {
        if (subject instanceof Describable) {
            userInput = userInput.replaceAll(String.format("(\\b%s)(\\b|\\W)",((Describable) subject).getSingleWordDescription().toUpperCase()),"");
        }
        for (String alias : action.getAliases()) {
            if (userInput.matches(String.format("(s?).*(\\b%s)(\\b|\\W).*", alias.toUpperCase()))) {
                userInput = userInput.replaceAll(String.format("(\\b%s)(\\b|\\W)", alias.toUpperCase()), "");
                break;
            }
        }
        for (String subjectReference : subjectReferences) {
            userInput = userInput.replaceAll(String.format("(\\b%s)(\\b|\\W)", subjectReference.toUpperCase()), "");
        }
        for (String preposition: prepositions) {
            userInput = userInput.replaceAll(String.format("(\\b%s)(\\b|\\W)", preposition.toUpperCase()), "");
        }
        userInput = userInput.trim();
        if (Strings.isNullOrEmpty(userInput)) {
            return new String[0];
        }
        return userInput.split("\\s+");
    }

}
