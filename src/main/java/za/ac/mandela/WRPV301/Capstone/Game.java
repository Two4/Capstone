package za.ac.mandela.WRPV301.Capstone;

import za.ac.mandela.WRPV301.Capstone.Event.ConsoleEvent;
import za.ac.mandela.WRPV301.Capstone.Event.MapChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Event.PlayerChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Map.MapData;


/**
 * Overarching class for containing static members and methods specific to game logic
 */
public class Game {
    /**
     * The current {@link Player instance};
     */
    private static Player player;
    /**
     * The current {@link MapData} instance
     */
    private static MapData mapData;
    /**
     * GOD_MODE = 1; NOCLIP = 1; SET MONEY 99999999999;
     */
    private static boolean cheats = false;


    /**
     * Performs the tasks necessary to create a fresh game state
     * @param gridSize the size of the map grid to be generated
     */
    public static void newGame(int gridSize) {
        mapData = MapData.build(gridSize);
        player = new Player();
        player.setCurrentLocation(mapData.getStartingPoint());
    }

    /**
     * Prints a familiarization message to the console upon a new game instantiation
     */
    public static void newGameInit(){
        PlayerChangeEvent.post();
        ConsoleEvent.output("Welcome to UNTITLED GAME!");
        ConsoleEvent.output("You survey your surroundings.");
        ConsoleEvent.output(Game.getPlayer().getCurrentLocation().getDescription());
        ConsoleEvent.output(String.format("You are wielding %s and wearing %s", player.getCurrentWeapon().getShortDescription(), player.getCurrentArmour().getShortDescription()));
        ConsoleEvent.output("Try typing a command and press return to get started.");
    }

    /**
     * Sets new The current {@link Player instance};.
     *
     * @param player New value of The current {@link Player instance};.
     */
    public static void setPlayer(Player player) {
        Game.player = player;
    }

    /**
     * Gets The current {@link MapData} instance.
     *
     * @return Value of The current {@link MapData} instance.
     */
    public static MapData getMapData() {
        return mapData;
    }

    /**
     * Sets new The current {@link MapData} instance.
     *
     * @param mapData New value of The current {@link MapData} instance.
     */
    public static void setMapData(MapData mapData) {
        Game.mapData = mapData;
    }


    /**
     * Gets The current {@link Player instance};.
     *
     * @return Value of The current {@link Player instance};.
     */
    public static Player getPlayer() {
        return player;
    }


    /**
     * Gets whether cheats are on or not.
     *
     * @return true if cheats are on
     */
    public static boolean cheatsOn() {
        return cheats;
    }

    /**
     * Sets whether cheats are on or not.
     *
     * @param cheats true if cheats should be on, false if not
     */
    public static void setCheats(boolean cheats) {
        Game.cheats = cheats;
        MapChangeEvent.post();
    }
}
