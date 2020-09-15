package za.ac.mandela.WRPV301.Capstone.UI;

import com.google.common.eventbus.Subscribe;
import com.google.common.graph.EndpointPair;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import za.ac.mandela.WRPV301.Capstone.Action.Describable;
import za.ac.mandela.WRPV301.Capstone.Event.MapChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Event.PlayerChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Game;
import za.ac.mandela.WRPV301.Capstone.Main;
import za.ac.mandela.WRPV301.Capstone.Map.Access.Accessway;
import za.ac.mandela.WRPV301.Capstone.Map.Access.Lockable;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Direction;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;
import za.ac.mandela.WRPV301.Capstone.Event.LocationChangeEvent;
import za.ac.mandela.WRPV301.Capstone.Event.AccesswayChangeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

/**
 * A class to manage map rendering to a {@link Canvas}
 */
public class CanvasManager {
    /**
     * The {@link GraphicsContext} of the generated {@link Canvas}
     */
    private final GraphicsContext graphicsContext;
    /**
     * The generated {@link Canvas}
     */
    private final Canvas canvas;
    /**
     * The pixel width of {@link Accessway}s and {@link za.ac.mandela.WRPV301.Capstone.Map.Location.Passage}s in the
     * rendered map
     */
    private final static double EDGE_SIZE = 25;
    /**
     * The number of times to multiply {@link #EDGE_SIZE} by to get the pixel width of a rendered {@link MapLocation}
     * this must <b>ALWAYS</b> be an odd number
     */
    private final static double MULTIPLIER = 7;
    /**
     * The pixel width of a rendered {@link MapLocation}, including {@link Accessway}s and gutters
     */
    private final static double NODE_SIZE = MULTIPLIER * EDGE_SIZE;
    /**
     * The {@link Color} for {@link Accessway}s and {@link za.ac.mandela.WRPV301.Capstone.Map.Location.Passage}s
     */
    private final static Color EDGE_COLOR = Color.GRAY;
    /**
     * A generated array of {@link Color}s for groups of {@link MapLocation}s
     */
    private final Color[] colours;
    /**
     * {@link Image} resource for locked {@link Lockable}s
     */
    private final Image locked = new Image(Main.class.getResourceAsStream("padlock.png"));
    /**
     * {@link Image} resource for unlocked {@link Lockable}s
     */
    private final Image unlocked = new Image(Main.class.getResourceAsStream("padlock-open.png"));
    /**
     * Tooltip used to show room information
     */
    private final Tooltip tooltip = new Tooltip();

    /**
     * Constructor
     * <b>KNOWN ISSUE</b>: a bug in the OpenJDK does not allow for a large {@link Canvas} (<a href="https://bugs.openjdk.java.net/browse/JDK-8090178">https://bugs.openjdk.java.net/browse/JDK-8090178</a>)
     * This is caused by a texture factory returning an unchecked null value due to a failed resource pool allocation,
     * causing an NPE to bubble through the entire stack and crashing the application. It is unknown if this can be reproduced
     * with the Oracle JDK. Fixing this issue is outside the scope of this project.
     * {@see J2DResourceFactory#createRTTexture(int, int, com.sun.prism.Texture.WrapMode)}
     *
     * @param scrollPane the {@link ScrollPane} the generated {@link Canvas} must be contained by
     */
    public CanvasManager(ScrollPane scrollPane) {
        double canvasSize = Game.getMapData().getGridSideSize() * NODE_SIZE;
        canvas = new Canvas(canvasSize, canvasSize);
        Tooltip.install(canvas, tooltip);
        tooltip.setShowDelay(Duration.millis(300));
        canvas.setOnMouseMoved(e -> {
            MapLocation node = getNode(XY.of(e.getX(), e.getY()));
            if (node.isVisible()) {
                tooltip.setText(node.getDescription());
            } else {
                tooltip.setText("You have not explored this location yet.");
            }
        });
        graphicsContext = canvas.getGraphicsContext2D();
        scrollPane.setContent(canvas);
        colours = new Color[Game.getMapData().getNumGroups()];
        Color curr = Color.RED;
        for (int i = 0; i < colours.length; i++) {
            colours[i] = curr;
            curr = Color.hsb(curr.getHue() + 147.00  % 360.00, curr.getSaturation(), curr.getBrightness());
        }
        eventBus.register(this);
    }

    /**
     * Draws the game map
     * @param event the {@link MapChangeEvent} consumed
     */
    @Subscribe
    public void draw(MapChangeEvent event) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < Game.getMapData().getGridSideSize(); i++) {
            for (int j = 0; j < Game.getMapData().getGridSideSize(); j++) {
                drawNode(Game.getMapData().get(i, j));
            }
        }
        for (Accessway accessway : Game.getMapData().edges()) {
            if (accessway instanceof Lockable) {
                drawLockable((Lockable) accessway);
            }
        }
        PlayerChangeEvent.post();
    }

    /**
     * Renders a {@link Lockable} and any icons associated with it
     * @param lockable the {@link Lockable} to render
     */
    private void drawLockable(Lockable lockable) {
        EndpointPair<MapLocation> nodes = Game.getMapData().incidentNodes(lockable);
        if ((nodes.nodeU().isVisible() || nodes.nodeV().isVisible() || Game.cheatsOn()) && lockable.keyExists()) {
            MapLocation node = nodes.nodeU();
            Direction facingDirection = lockable.getFacingDirection(node);
            XY drawXY;
            switch (facingDirection) {
                case NORTH:
                    drawXY = XY.of((NODE_SIZE / 2.0) - (EDGE_SIZE / 2.0), -(EDGE_SIZE / 2.0)).translate(getNodeXY(node));
                    break;
                case SOUTH:
                    drawXY = XY.of((NODE_SIZE / 2.0) - (EDGE_SIZE / 2.0), NODE_SIZE - (EDGE_SIZE / 2.0)).translate(getNodeXY(node));
                    break;
                case WEST:
                    drawXY = XY.of(-(EDGE_SIZE / 2.0), (NODE_SIZE / 2.0) - (EDGE_SIZE / 2.0)).translate(getNodeXY(node));
                    break;
                default:
                    drawXY = XY.of(NODE_SIZE - (EDGE_SIZE / 2.0), (NODE_SIZE / 2.0) - (EDGE_SIZE / 2.0)).translate(getNodeXY(node));
                    break;
            }
            graphicsContext.drawImage(lockable.isUnlocked() ? unlocked : locked, drawXY.X, drawXY.Y, EDGE_SIZE, EDGE_SIZE);
        }
    }

    /**
     * Consumes {@link AccesswayChangeEvent}s from the subscribed {@link com.google.common.eventbus.EventBus} and calls
     * {@link #refreshAccessway(Accessway)} for the changed {@link Accessway}
     * @param event the received {@link AccesswayChangeEvent}
     */
    @Subscribe
    public void refreshAccessway(AccesswayChangeEvent event){
        refreshAccessway(event.getAccessway());
    }

    /**
     * Redraws the {@link MapLocation}s and icons associated with a given {@link Accessway}
     * @param edge the {@link Accessway} to refresh
     */
    private void refreshAccessway(Accessway edge) {
        for (MapLocation node : Game.getMapData().incidentNodes(edge)) {
            refreshNode(node);
        }
        if (edge instanceof Lockable) {
            drawLockable((Lockable) edge);
        }
    }

    /**
     * Consumes {@link LocationChangeEvent}s from the subscribed {@link com.google.common.eventbus.EventBus} and calls
     * {@link #refreshNode(MapLocation)} for the changed {@link MapLocation}
     * @param event the received {@link LocationChangeEvent}
     */
    @Subscribe
    public void refreshNode(LocationChangeEvent event) {
        refreshNode(event.getLocation());
    }

    /**
     * Re-draws a specified {@link MapLocation}
     * @param node the {@link MapLocation} to redraw
     */
    private void refreshNode(MapLocation node) {
        XYWH.of(getNodeXY(node), NODE_SIZE, NODE_SIZE).rect(graphicsContext, Color.BLACK);
        drawNode(node);
    }

    /**
     * Gets the generated {@link Color} associated with a given {@link MapLocation}
     * @param node the {@link MapLocation} to get the associated {@link Color} for
     * @return the {@link Color} associated with the given {@link MapLocation}
     */
    private Color getNodeSegmentColor(MapLocation node) {
        return colours[Game.getMapData().getNodeGroup(node)];
    }

    /**
     * Transforms a {@link MapLocation}'s grid coordinates into {@link Canvas} coordinates
     * @param node the {@link MapLocation} to get {@link Canvas} coordinates for
     * @return the {@link Canvas} coordinates for the given {@link MapLocation}
     */
    private XY getNodeXY(MapLocation node) {
        return XY.of(node.getColumn() * NODE_SIZE, node.getRow() * NODE_SIZE);
    }

    /**
     * Gets the {@link MapLocation} at the given screenspace XY coordinates
     * @param nodeXY the screenspace XY coordinates of the node
     * @return the {@link MapLocation} at the given screenspace XY coordinates
     */
    public MapLocation getNode(XY nodeXY) {
        int column = Double.valueOf(Math.floor(nodeXY.X / NODE_SIZE)).intValue();
        int row = Double.valueOf(Math.floor(nodeXY.Y / NODE_SIZE)).intValue();
        return Game.getMapData().get(row, column);
    }

    /**
     * Renders a given {@link MapLocation} and its connected {@link Accessway}s
     * @param node the {@link MapLocation} to draw
     */
    private void drawNode(MapLocation node) {
        if (node.isVisible() || Game.cheatsOn()) {
            if (node instanceof Room) {
                drawRoom(node);
            } else {
                drawPassage(node);
            }
            drawNodeAccessways(node);
        }
    }

    /**
     * Draws a given {@link za.ac.mandela.WRPV301.Capstone.Map.Location.Passage}
     * @param node the {@link za.ac.mandela.WRPV301.Capstone.Map.Location.Passage} to draw
     */
    private void drawPassage(MapLocation node) {
        XYWH xywh = XYWH.of(getNodeXY(node), EDGE_SIZE, EDGE_SIZE)
                .translate((NODE_SIZE / 2.0) - (EDGE_SIZE / 2.0), (NODE_SIZE / 2.0) - (EDGE_SIZE / 2.0));
        xywh.rect(graphicsContext, EDGE_COLOR);
        for (Direction direction : node.getAccessways().keySet()) {
            XYWH transformed = xywh;
            for (int i = 0; i < MULTIPLIER - 3; i++) {
                transformed = transformed.translate(direction);
                transformed.rect(graphicsContext, EDGE_COLOR);
            }
        }
        if (node.equals(Game.getPlayer().getCurrentLocation())) {
            XYWH.of(getNodeXY(node), EDGE_SIZE, EDGE_SIZE)
                    .translate((NODE_SIZE / 2.0) - (EDGE_SIZE/ 2.0), (NODE_SIZE / 2.0) - (EDGE_SIZE/ 2.0))
                    .img(graphicsContext, Game.getPlayer());
        }
    }

    /**
     * Draws a given {@link Room}
     * @param node the {@link Room} to draw
     */
    private void drawRoom(MapLocation node) {
        XYWH.of(getNodeXY(node), NODE_SIZE - (2.0 * EDGE_SIZE), NODE_SIZE - (2.0 * EDGE_SIZE))
                .translate(EDGE_SIZE, EDGE_SIZE)
                .rect(graphicsContext, getNodeSegmentColor(node));
        HashSet<Describable> items = new HashSet<>(((Room) node).getPlayerItems());
        items.addAll(((Room) node).getEnemies());
        items.addAll(((Room) node).getRoomItems());
        ArrayList<XY> transformations = createGridTransformations(items.size());
        int i = 0;
        Iterator<Describable> iterator = items.iterator();
        while (i < items.size()) {
            XYWH.of(transformations.get(i), EDGE_SIZE, EDGE_SIZE)
                    .translate(getNodeXY(node))
                    .img(graphicsContext, iterator.next());
            i++;
        }
        if (node.equals(Game.getPlayer().getCurrentLocation())) {
            XYWH.of(getNodeXY(node), EDGE_SIZE, EDGE_SIZE)
                    .translate(EDGE_SIZE, EDGE_SIZE)
                    .img(graphicsContext, Game.getPlayer());
        }
    }

    /**
     * Draws the {@link Accessway}s connected to a {@link MapLocation}
     * @param node the {@link MapLocation} for which to draw the connected {@link Accessway}s
     */
    private void drawNodeAccessways(MapLocation node) {
        for (Direction direction : node.getAccessways().keySet()) {
            XYWH xywh;
            switch (direction) {
                case NORTH:
                    xywh = XYWH.of((NODE_SIZE / 2.0) - (EDGE_SIZE/ 2.0), 0, EDGE_SIZE, EDGE_SIZE);
                    break;
                case SOUTH:
                    xywh = XYWH.of((NODE_SIZE / 2.0) - (EDGE_SIZE/ 2.0), NODE_SIZE - EDGE_SIZE, EDGE_SIZE, EDGE_SIZE);
                    break;
                case WEST:
                    xywh = XYWH.of(0, (NODE_SIZE / 2.0) - (EDGE_SIZE/ 2.0), EDGE_SIZE, EDGE_SIZE);
                    break;
                default:
                    xywh = XYWH.of(NODE_SIZE - EDGE_SIZE, (NODE_SIZE / 2.0) - (EDGE_SIZE/ 2.0), EDGE_SIZE, EDGE_SIZE);
                    break;
            }
            xywh.translate(getNodeXY(node)).rect(graphicsContext, EDGE_COLOR);
        }
    }

    /**
     * Creates a series of transformations needed to center a grid containing the specified number of items within a node
     * @param numItems the number of items the grid will contain
     * @return a series of transformations needed to center a grid containing the specified number of items within a node
     */
    private ArrayList<XY> createGridTransformations(int numItems) {
        ArrayList<XY> transforms = new ArrayList<>();
        int rows = 0, columns = 0;
        boolean selector = true;
        while (rows * columns < numItems) {
            if (selector) {
                columns++;
            } else {
                rows++;
            }
            selector = !selector;
        }
        XY start = XY.of(NODE_SIZE / 2.0, NODE_SIZE / 2.0)
                .translate((EDGE_SIZE / -2.0 * columns), (EDGE_SIZE / -2.0) * rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                transforms.add(start.translate(j * EDGE_SIZE, i * EDGE_SIZE));
            }
        }
        return transforms;
    }

    /**
     * Transforms the current player position into scroll ratios required to center the player's position within the
     * main scroll pane
     * @param viewPortBounds the {@link Bounds} of the main scrollpane viewport
     * @return scroll ratios required to center the player's position within the main scrollpane
     */
    public XY getPlayerScrollCoordinates(Bounds viewPortBounds) {
        XY xy = getNodeXY(Game.getPlayer().getCurrentLocation())
                .translate(NODE_SIZE / 2.0, NODE_SIZE / 2.0)
                .translate(
                        viewPortBounds.getWidth() / (-2.0),
                        viewPortBounds.getHeight() / (-2.0)
                );
        return XY.of(xy.X / (canvas.getWidth() - viewPortBounds.getWidth()), xy.Y / (canvas.getHeight() - viewPortBounds.getHeight()));
    }

    /**
     * @return the Tooltip instance used to display room information
     */
    public Tooltip getTooltip() {
        return tooltip;
    }
}
