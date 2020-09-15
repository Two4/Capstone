package za.ac.mandela.WRPV301.Capstone.Map.Location;

import com.google.common.collect.ListMultimap;
import za.ac.mandela.WRPV301.Capstone.Action.Action;
import za.ac.mandela.WRPV301.Capstone.Action.Actionable;
import za.ac.mandela.WRPV301.Capstone.Combat.*;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Item.Player.PlayerItem;
import za.ac.mandela.WRPV301.Capstone.Event.LocationChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Item.Room.HealthChest;
import za.ac.mandela.WRPV301.Capstone.Item.Room.RoomItem;

import java.util.HashSet;
import java.util.Objects;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

/**
 * Concrete extension of the MapLocation class that can hold items and mobs
 */
public class Room extends MapLocation {
    /**
     * A {@link HashSet} of the {@link PlayerItem}s in this Room
     */
    private final HashSet<PlayerItem> playerItems;
    /**
     * A {@link HashSet} of the {@link Enemy} instances in this room
     */
    private final HashSet<Enemy> enemies;
    /**
     * A {@link HashSet} of the {@link RoomItem} instances in this room
     */
    private final HashSet<RoomItem> roomItems;

    /**
     * Constructor
     * @param row the grid row coordinate of this Room
     * @param column the grid column coordinate of this Room
     * @param floorMaterial the {@link Material} of the floor of the current location
     * @param wallMaterial the {@link Material} of the wall of the current location
     * @param lighting the {@link Lighting} of this Room
     */
    protected Room(int row, int column, Material floorMaterial, Material wallMaterial, Lighting lighting) {
        super(row, column, floorMaterial, wallMaterial, lighting);
        playerItems = new HashSet<>();
        enemies = new HashSet<>();
        roomItems = new HashSet<>();
    }

    /**
     * Creates a randomly generated Room with the given grid coordinates that may contain items and enemies
     * @param row the grid row coordinate of the resultant Room
     * @param column the grid column coordinate of the resultant Room
     * @return a randomly generated Room with the given grid coordinates that may contain items and enemies
     */
    public static Room random(int row, int column) {
        Room room = randomEmpty(row, column);
        if (p(20)) {
            room.addEnemy(Insect.InsectType.WORKER.create(room));
        }
        if (p(20)) {
            room.addEnemy(Ghost.GhostType.SHADE.create(room));
        }
        if (p(20)) {
            room.addEnemy(Humanoid.minion(room));
        }
        if (p(10)) {
            switch (d(3)) {
                case 0:
                    room.addEnemy(Insect.InsectType.SOLDIER.create(room));
                    break;
                case 1:
                    room.addEnemy(Ghost.GhostType.POLTERGEIST.create(room));
                    break;
                default:
                    room.addEnemy(Humanoid.soldier(room));
                    break;
            }
        }
        if (p(10)) {
            room.addRoomItem(new HealthChest(room));
        }
        return room;
    }

    /**
     * Creates a randomly generated empty Room with the given grid coordinates
     * @param row the grid row coordinate of the resultant Room
     * @param column the grid column coordinate of the resultant Room
     * @return a randomly generated empty Room with the given grid coordinates
     */
    public static Room randomEmpty(int row, int column) {
        return new Room(row, column, Material.random(), Material.random(), Lighting.random());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListMultimap<Actionable, Action> getLocationActions() {
        ListMultimap<Actionable, Action> actionMap = super.getLocationActions();
        for (PlayerItem item : playerItems) {
            actionMap.putAll(item, item.getAvailableActions());
        }
        for (RoomItem item : roomItems) {
            actionMap.putAll(item, item.getAvailableActions());
        }
        for (Enemy enemy : enemies) {
            actionMap.putAll(enemy, enemy.getAvailableActions());
        }
        return actionMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder(super.getDescription());
        if (roomItems.size() > 0 || enemies.size() > 0 || playerItems.size() > 0) {
            builder.append("In the room you see:\n");
            for (RoomItem roomItem : roomItems) {
                builder.append(roomItem.getShortDescription());
                builder.append("\n");
            }
            for (Enemy enemy : enemies) {
                builder.append(enemy.getShortDescription());
                builder.append("\n");
            }
            for (PlayerItem playerItem : playerItems) {
                builder.append(playerItem.getShortDescription());
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSingleWordDescription() {
        return "room";
    }

    /**
     * Adds a {@link PlayerItem} to the room
     * @param item the item to add
     */
    public void addPlayerItem(PlayerItem item) {
        this.playerItems.add(item);
        LocationChangeEvent.on(this);
    }

    /**
     * Removes a {@link PlayerItem} from the room
     * @param item the item to remove
     */
    public void removePlayerItem(PlayerItem item) {
        this.playerItems.remove(item);
        LocationChangeEvent.on(this);
    }

    /**
     * @return a {@link HashSet} of the {@link PlayerItem}s in this Room.
     */
    public HashSet<PlayerItem> getPlayerItems() {
        return playerItems;
    }

    /**
     * Adds an {@link Enemy} to this Room
     * @param enemy the {@link Enemy} to add
     */
    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    /**
     * Removes an @link Enemy} from this Room
     * @param enemy the {@link Enemy} to remove
     */
    public void removeEnemy(Enemy enemy) {
        this.enemies.remove(enemy);
        LocationChangeEvent.on(this);
    }

    /**
     * @return a {@link HashSet} of the {@link Enemy} instances in this room
     */
    public HashSet<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Adds a {@link RoomItem} to this Room
     * @param roomItem the {@link RoomItem} to add
     */
    public void addRoomItem(RoomItem roomItem) {
        this.roomItems.add(roomItem);
        LocationChangeEvent.on(this);
    }

    /**
     * Removes a {@link RoomItem} from this room
     * @param roomItem the {@link RoomItem} to remove
     */
    public void removeRoomItem(RoomItem roomItem) {
        roomItems.remove(roomItem);
        LocationChangeEvent.on(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter() {
        super.enter();
        if (enemies.size() > 0 && Objects.isNull(Game.getPlayer().getCurrentFight())) {
            Fight.attackingPlayer(this);
        }
    }

    /**
     * @return the {@link RoomItem}s in this Room
     */
    public HashSet<RoomItem> getRoomItems() {
        return roomItems;
    }
}
