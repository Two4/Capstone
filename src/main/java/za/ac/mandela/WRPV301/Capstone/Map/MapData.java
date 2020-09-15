package za.ac.mandela.WRPV301.Capstone.Map;
import static za.ac.mandela.WRPV301.Capstone.Util.Utils.*;

import com.google.common.collect.*;
import com.google.common.graph.*;
import za.ac.mandela.WRPV301.Capstone.Combat.Enemy;
import za.ac.mandela.WRPV301.Capstone.Item.Room.Crown;
import za.ac.mandela.WRPV301.Capstone.Map.Access.Accessway;
import za.ac.mandela.WRPV301.Capstone.Map.Access.Lockable;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Direction;
import za.ac.mandela.WRPV301.Capstone.Map.Location.MapLocation;
import za.ac.mandela.WRPV301.Capstone.Map.Location.Room;
import za.ac.mandela.WRPV301.Capstone.Event.GenerationEvent;
import za.ac.mandela.WRPV301.Capstone.Player;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrapper class for {@link MutableNetwork} that is grid-addressable and provides convenience methods for common graph
 * operations.
 */
@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class MapData implements MutableNetwork<MapLocation, Accessway> {
    /**
     * The backing {@link MutableNetwork}
     */
    private final MutableNetwork<MapLocation, Accessway> mapNetwork;
    /**
     * A 2D array of the mapNetwork {@link MapLocation} nodes, indexed by their coordinates in a [row][column] fashion
     */
    private final MapLocation[][] mapGrid;
    /**
     * The starting {@link MapLocation}s of the underlying maze
     */
    private MapLocation startingPoint;
    /**
     * The ending {@link MapLocation}s of the underlying maze
     */
    private MapLocation endingPoint;
    /**
     * an array table of groupings for every node in the map grid
     */
    private int[][] nodeGroups;
    /**
     * The number of groupings for map grid nodes
     */
    private int numGroups;

    /**
     * Internal class to allow serialisation and deserialisation of the enclosing class
     */
    public static class SerializableMapData implements Serializable {
        /**
         * A {@link HashBasedTable} of all edges in the serialised {@link MapData} network, keyed by their connected nodes
         */
        HashBasedTable<MapLocation, MapLocation, Accessway> edges;
        /**
         * The serialised {@link MapData#mapGrid}
         */
        private final MapLocation[][] mapGrid;
        /**
         * The starting {@link MapLocation}s of the underlying maze
         */
        private final MapLocation startingPoint;
        /**
         * The ending {@link MapLocation}s of the underlying maze
         */
        private final MapLocation endingPoint;
        /**
         * An array table of groupings for every node in the map grid
         */
        private final int[][] nodeGroups;
        /**
         * The number of groupings for map grid nodes
         */
        private final int numGroups;


        /**
         * Private constructor
         * @param mapNetwork the backing {@link MutableNetwork} of the {@link MapData} instance
         * @param mapGrid the serialised {@link MapData#mapGrid}
         * @param startingPoint the starting {@link MapLocation} of the underlying maze
         * @param endingPoint the ending {@link MapLocation} of the underlying maze
         * @param nodeGroups an array table of groupings for every node in the map grid
         * @param numGroups the number of groupings for map grid nodes
         */
        private SerializableMapData(MutableNetwork<MapLocation, Accessway> mapNetwork, MapLocation[][] mapGrid, MapLocation startingPoint, MapLocation endingPoint, int[][] nodeGroups, int numGroups) {
            this.mapGrid = mapGrid;
            this.startingPoint = startingPoint;
            this.endingPoint = endingPoint;
            this.nodeGroups = nodeGroups;
            this.numGroups = numGroups;
            edges = HashBasedTable.create();
            for (Accessway edge : mapNetwork.edges()) {
                EndpointPair<MapLocation> endpoints = mapNetwork.incidentNodes(edge);
                edges.put(endpoints.nodeU(), endpoints.nodeV(), edge);
            }
        }

        /**
         * Method to return a {@link MapData} instance once this instance is deserialised
         * @return a {@link MapData} instance identical to the one given to this object's constructor
         */
        @SuppressWarnings("ConstantConditions")
        public MapData toMapData() {
            MutableNetwork<MapLocation, Accessway> mapNetwork = NetworkBuilder.undirected()
                    .allowsParallelEdges(false)
                    .allowsSelfLoops(false)
                    .expectedNodeCount(mapGrid.length * mapGrid.length)
                    .build();
            for (Table.Cell<MapLocation, MapLocation, Accessway> edgeCell : edges.cellSet()) {
                mapNetwork.addNode(edgeCell.getColumnKey());
                mapNetwork.addNode(edgeCell.getRowKey());
                mapNetwork.addEdge(edgeCell.getColumnKey(), edgeCell.getRowKey(), edgeCell.getValue());
            }
            return new MapData(mapNetwork, mapGrid, startingPoint, endingPoint, nodeGroups, numGroups);
        }
    }

    /**
     * Private constructor
     * @param mapNetwork the backing {@link MutableNetwork}
     * @param mapGrid a 2D array of the mapNetwork {@link MapLocation} nodes, indexed by their coordinates in a [row][column] fashion
     * @param startingPoint the starting {@link MapLocation} of the underlying maze
     * @param endingPoint the ending {@link MapLocation} of the underlying maze
     * @param nodeGroups an array table of groupings for every node in the map grid
     * @param numGroups the number of groupings for map grid nodes
     */
    public MapData(MutableNetwork<MapLocation, Accessway> mapNetwork, MapLocation[][] mapGrid, MapLocation startingPoint, MapLocation endingPoint, int[][] nodeGroups, int numGroups) {
        this.mapNetwork = mapNetwork;
        this.mapGrid = mapGrid;
        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;
        this.nodeGroups = nodeGroups;
        this.numGroups = numGroups;
    }

    /**
     * Private constructor used during new map generation
     * @param gridSideSize the size, in {@link MapLocation} nodes, of any side of the resultant square map grid
     */
    private MapData(int gridSideSize) {
        GenerationEvent.of("Creating data structures...");
        this.mapNetwork = NetworkBuilder.undirected()
                .allowsParallelEdges(false) //only one connection between any two map nodes
                .allowsSelfLoops(false) //map locations can't connect to themselves
                .expectedNodeCount(gridSideSize * gridSideSize)
                .build();
        this.mapGrid = new MapLocation[gridSideSize][gridSideSize];
    }

    /**
     * @return a {@link SerializableMapData} instance that encapsulates this MapData instance in a serializable format
     */
    public SerializableMapData toSerializable() {
        return new SerializableMapData(mapNetwork, mapGrid, startingPoint, endingPoint, nodeGroups, numGroups);
    }

    /**
     * @return the player's starting location
     */
    public MapLocation getStartingPoint() {
        return startingPoint;
    }

    /**
     * @return the maze's endpoint, i.e, the winning location
     */
    public MapLocation getEndingPoint() {
        return endingPoint;
    }

    /**
     * Gets a {@link MapLocation} by its grid coordinates
     * @param row the row coordinate of the target {@link MapLocation}
     * @param column the column coordinate of the target {@link MapLocation}
     * @return the {@link MapLocation} at the given coordinates, or null if no such location exists
     */
    public MapLocation get(int row, int column) {
        try {
            return mapGrid[row][column];
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convenience method to replace a {@link MapLocation} node in this MapData instance
     * @param current the {@link MapLocation} node to replace
     * @param replacement the replacement {@link MapLocation}
     * @return the replacement {@link MapLocation}
     */
    public MapLocation replaceMapLocation(MapLocation current, MapLocation replacement) {
        HashSet<MapLocation> subGraphNodes = new HashSet<>(mapNetwork.adjacentNodes(current));
        subGraphNodes.add(current);
        MutableNetwork<MapLocation, Accessway> oldSubGraph = Graphs.inducedSubgraph(mapNetwork, subGraphNodes);
        mapNetwork.removeNode(current);
        mapNetwork.addNode(replacement);
        for (Accessway edge : oldSubGraph.incidentEdges(current)) {
            mapNetwork.addEdge(replacement, oldSubGraph.incidentNodes(edge).adjacentNode(current), edge);
        }
        mapGrid[current.getRow()][current.getColumn()] = replacement;
        return replacement;
    }

    /**
     * Convenience method to replace a {@link Accessway} edge in this MapData instance
     * @param current the {@link Accessway} edge to replace
     * @param replacement the replacement {@link Accessway}
     * @return the replacement {@link Accessway}
     */
    public Accessway replaceAccessway(Accessway current, Accessway replacement) {
        EndpointPair<MapLocation> connectingLocations = mapNetwork.incidentNodes(current);
        mapNetwork.removeEdge(current);
        mapNetwork.addEdge(connectingLocations, replacement);
        return replacement;
    }

    /**
     * @return the size, in {@link MapLocation} nodes, of any side of this MapData instance's square map grid
     */
    public int getGridSideSize() {
        return mapGrid.length;
    }

    /**
     * @return the number of map grid node groupings
     */
    public int getNumGroups() {
        return numGroups;
    }

    /**
     * Gets the node grouping of the specified node
     * @param node the node to find the grouping for
     * @return the node grouping of the specified node
     */
    public int getNodeGroup(MapLocation node) {
        return nodeGroups[node.getRow()][node.getColumn()];
    }

    /**
     * Static generation method to create a randomised MapData instance
     * @param gridSideSize the size, in {@link MapLocation} nodes, of any side of the resultant square map grid
     * @return a new randomised MapData instance
     */
    public static MapData build(int gridSideSize) {
        MapData mapData = new MapData(gridSideSize);
        mapData.fillNodes();
        mapData.buildMaze();
        return mapData;
    }

    /**
     * Populates this MapData instance with random {@link MapLocation}s
     */
    private void fillNodes() {
        int length = getGridSideSize();
        int lengthSquared = length * length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                addNode(MapLocation.random(i, j));
            }
            GenerationEvent.of(String.format("Creating map node %d of %d", i * length, lengthSquared));
        }
    }

    /**
     * Populates and groups edges by building a maze; some nodes may be replaced.
     */
    private void buildMaze() {
        int length = getGridSideSize();
        int lengthSquared = length * length;
        GenerationEvent.of("Generating maze structure...");
        TraversalGroupManager groupManager = new TraversalGroupManager();
        TraversalGroup currentGroup = groupManager.newGroup(null);
        TraversalGroup currentlyTraversing;
        startingPoint = randomStartingPosition();
        Stack<MapLocation> locationStack = new Stack<>();
        locationStack.push(startingPoint);
        MapLocation current;
        boolean endpointSentinel = false, backtrackSentinel = false;
        HashMap<Direction, MapLocation> neighbours;
        currentGroup.addNode(startingPoint);
        while (!locationStack.empty()) {
            current = locationStack.pop();
            currentlyTraversing = groupManager.groupFor(current);
            neighbours = getNeighboursOf(current);
            neighbours.values().removeIf(groupManager::contains);
            if (neighbours.size() > 0) {
                backtrackSentinel = false;
                locationStack.push(current);
                Direction[] availableNeighbourKeys = new Direction[neighbours.size()];
                neighbours.keySet().toArray(availableNeighbourKeys);
                Direction direction = availableNeighbourKeys[d(availableNeighbourKeys.length)];
                MapLocation neighbour = neighbours.get(direction);
                Accessway accessway = Accessway.randomUnlockedOrOpen();
                addEdge(current, neighbour, accessway);
                currentGroup.addNode(neighbour);
                locationStack.push(neighbour);
            } else  {
                if (!endpointSentinel) {
                    endpointSentinel = true;
                    endingPoint = get(current.getRow(), current.getColumn());
                }
                //make sure all leaves are rooms
                if (degree(current) == 1) {
                    MapLocation replacement;
                    if (current.equals(startingPoint) || current.equals(endingPoint)) {
                        replacement = replaceMapLocation(current, Room.randomEmpty(current.getRow(), current.getColumn()));
                    } else {
                        replacement = replaceMapLocation(current, Room.random(current.getRow(), current.getColumn()));
                    }
                    currentGroup.replaceNode(current, replacement);
                    if (current.equals(startingPoint)) {
                        startingPoint = replacement;
                    }
                    if (current.equals(endingPoint)) {
                        endingPoint = replacement;
                    }
                }
                if (!backtrackSentinel) {
                    currentGroup = groupManager.newGroup(currentlyTraversing);
                    GenerationEvent.of(String.format("Generating maze structure...(%d/%d)", groupManager.size(), lengthSquared));
                    backtrackSentinel = true;
                }
            }
        }
        nodeGroups = groupManager.processGroups(getGridSideSize());
        numGroups = groupManager.groupCount();
        distributeKeys();
        Room end = (Room) endingPoint;
        end.addRoomItem(new Crown(end));
        end.getEnemies().clear();
        endingPoint.setVisibleNoEvent(true);
    }

    /**
     * Distributes a chain of keys for rooms, each of which is a maze dead end, and holds the key for the next such room.
     * Rooms are chosen heuristically, namely the second closest by Pythagorean distance
     */
    private void distributeKeys() {
        GenerationEvent.of("Finding tree leaves (no, really)");
        ArrayList<MapLocation> leaves = nodes().stream().filter(node -> degree(node) < 2).collect(Collectors.toCollection(ArrayList::new));
        leaves.remove(startingPoint);
        leaves.remove(endingPoint);
        Lockable.Lock.Key currentKey = ((Lockable) replaceAccessway(incidentEdges(endingPoint).stream().findAny().orElseThrow(), Lockable.randomLocked())).getLock().getKey();
        MapLocation leaf = leaves.get(d(leaves.size()));
        int count = 0;
        int total = leaves.size();
        while (Objects.nonNull(leaf)) {
            GenerationEvent.of(String.format("Distributing locks and keys... (%d/%d)", ++count, total));
            leaves.remove(leaf);
            Room room = (Room) leaf;
            room.getEnemies().clear();
            switch ((count * 5) / total) {
                case 0:
                    room.addEnemy(Enemy.randomMinion(room));
                    break;
                case 1:
                    room.addEnemy(Enemy.randomSoldier(room));
                    break;
                case 2:
                    room.addEnemy(Enemy.randomMiniBoss(room));
                    break;
                default:
                    room.addEnemy(Enemy.randomBoss(room));
            }
            ((Room) leaf).addPlayerItem(currentKey);
            currentKey = ((Lockable) replaceAccessway(incidentEdges(leaf).stream().findAny().orElseThrow(), Lockable.randomLocked())).getLock().getKey();
            MapLocation finalLeaf = leaf;
            leaf = leaves.stream()
                    .sorted((a, b) -> {
                        double aPythagorus = Math.sqrt(Math.pow(a.getColumn() - finalLeaf.getColumn(), 2) + Math.pow(a.getRow() - finalLeaf.getRow(), 2));
                        double bPythagorus = Math.sqrt(Math.pow(b.getColumn() - finalLeaf.getColumn(), 2) + Math.pow(b.getRow() - finalLeaf.getRow(), 2));
                        return Double.compare(bPythagorus, aPythagorus);
                    })
                    .limit(2)
                    .reduce((a, b) -> b)
                    .orElse(null);
        }
    }

    /**
     * Gets a {@link HashMap} of the gridwise neighbours of a {@link MapLocation} node, excluding diagonal neighbours.
     * resultant map entries are keyed by their relative direction from the specified location
     * @param location the {@link MapLocation} to get the neighbours of
     * @return a {@link HashMap} of the gridwise neighbours of a {@link MapLocation} node, excluding diagonal
     * neighbours, keyed by relative direction
     */
    private HashMap<Direction, MapLocation> getNeighboursOf(MapLocation location) {
        HashMap<Direction, MapLocation> neighbours = new HashMap<>();
        for (Direction direction : Direction.values()) {
            MapLocation node = get(direction.translateRow(location.getRow()), direction.translateColumn(location.getColumn()));
            if (!Objects.isNull(node)) {
                neighbours.put(direction, node);
            }
        }
        return neighbours;
    }

    /**
     * Selects a random {@link MapLocation} in the MapData grid to be used as the starting position when generating a
     * maze. This position will also be the {@link Player} starting position
     * @return a random {@link MapLocation} in the MapData grid
     */
    private MapLocation randomStartingPosition() {
        int randomRow = d(mapGrid.length);
        int randomColumn = d(mapGrid.length);
        return replaceMapLocation(get(randomRow, randomColumn), Room.random(randomRow, randomColumn));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addNode(MapLocation node) {
        mapGrid[node.getRow()][node.getColumn()] = node;
        return mapNetwork.addNode(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(MapLocation nodeU, MapLocation nodeV, Accessway edge) {
        return mapNetwork.addEdge(nodeU, nodeV, edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(EndpointPair<MapLocation> endpoints, Accessway edge) {
        return mapNetwork.addEdge(endpoints, edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeNode(MapLocation node) {
        if (Objects.nonNull(mapGrid[node.getRow()][node.getColumn()]) && mapGrid[node.getRow()][node.getColumn()].equals(node)) {
            mapGrid[node.getRow()][node.getColumn()] = null;
            return mapNetwork.removeNode(node);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(Accessway edge) {
        return mapNetwork.removeEdge(edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MapLocation> nodes() {
        return mapNetwork.nodes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Accessway> edges() {
        return mapNetwork.edges();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph<MapLocation> asGraph() {
        return mapNetwork.asGraph();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirected() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowsParallelEdges() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowsSelfLoops() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElementOrder<MapLocation> nodeOrder() {
        return mapNetwork.nodeOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElementOrder<Accessway> edgeOrder() {
        return mapNetwork.edgeOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MapLocation> adjacentNodes(MapLocation node) {
        return mapNetwork.adjacentNodes(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MapLocation> predecessors(MapLocation node) {
        return mapNetwork.predecessors(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MapLocation> successors(MapLocation node) {
        return mapNetwork.successors(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Accessway> incidentEdges(MapLocation node) {
        return mapNetwork.incidentEdges(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Accessway> inEdges(MapLocation node) {
        return mapNetwork.inEdges(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Accessway> outEdges(MapLocation node) {
        return mapNetwork.outEdges(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int degree(MapLocation node) {
        return mapNetwork.degree(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inDegree(MapLocation node) {
        return mapNetwork.inDegree(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int outDegree(MapLocation node) {
        return mapNetwork.outDegree(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EndpointPair<MapLocation> incidentNodes(Accessway edge) {
        return mapNetwork.incidentNodes(edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Accessway> adjacentEdges(Accessway edge) {
        return mapNetwork.adjacentEdges(edge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Accessway> edgesConnecting(MapLocation nodeU, MapLocation nodeV) {
        return mapNetwork.edgesConnecting(nodeU, nodeV);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Accessway> edgesConnecting(EndpointPair<MapLocation> endpoints) {
        return mapNetwork.edgesConnecting(endpoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Accessway> edgeConnecting(MapLocation nodeU, MapLocation nodeV) {
        return mapNetwork.edgeConnecting(nodeU, nodeV);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Accessway> edgeConnecting(EndpointPair<MapLocation> endpoints) {
        return mapNetwork.edgeConnecting(endpoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessway edgeConnectingOrNull(MapLocation nodeU, MapLocation nodeV) {
        return mapNetwork.edgeConnectingOrNull(nodeU, nodeV);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Accessway edgeConnectingOrNull(EndpointPair<MapLocation> endpoints) {
        return mapNetwork.edgeConnectingOrNull(endpoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEdgeConnecting(MapLocation nodeU, MapLocation nodeV) {
        return mapNetwork.hasEdgeConnecting(nodeU, nodeV);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEdgeConnecting(EndpointPair<MapLocation> endpoints) {
        return mapNetwork.hasEdgeConnecting(endpoints);
    }

    /**
     * Inner class to represent a group of nodes being traversed by the maze generation algorithm
     */
    private static class TraversalGroup {
        /**
         * The nodes within this group
         */
        private final HashSet<MapLocation> nodes;
        /**
         * The TraversalGroup this group has branched from
         */
        private final TraversalGroup parentGroup;
        /**
         * The number of ancestors this TraversalGroup has
         */
        private final int depth;

        /**
         * Constructor
         * @param parentGroup the TraversalGroup this group has branched from
         */
        private TraversalGroup(TraversalGroup parentGroup) {
            this.parentGroup = parentGroup;
            if (Objects.nonNull(parentGroup)) {
                depth = parentGroup.depth + 1;
            } else {
                depth = 0;
            }
            nodes = new HashSet<>();
        }

        /**
         * Adds a {@link MapLocation} node to this group
         * @param node the {@link MapLocation} node to add
         */
        void addNode(MapLocation node) {
            nodes.add(node);
        }

        /**
         * @return the number of {@link MapLocation} nodes in this group
         */
        int size() {
            return nodes.size();
        }

        /**
         * Places all nodes in the current group into {@link #parentGroup}
         */
        void collapseToParent() {
            parentGroup.nodes.addAll(this.nodes);
            this.nodes.clear();
        }

        /**
         * @return the nodes in this group
         */
        public HashSet<MapLocation> getNodes() {
            return nodes;
        }

        /**
         * Replaces a node in this group with another
         * @param current the current node to be replaced
         * @param replacement the replacement node
         */
        public void replaceNode(MapLocation current, MapLocation replacement) {
            if (nodes.contains(current)) {
                nodes.remove(current);
                nodes.add(replacement);
            }
        }

        /**
         * @return the number of ancestors this TraversalGroup has
         */
        public int getDepth() {
            return depth;
        }
    }

    /**
     * Manages and provides utilities for a collection of {@link TraversalGroup}s
     */
    private static class TraversalGroupManager {
        /**
         * The managed {@link TraversalGroup}s
         */
        private final ArrayList<TraversalGroup> groups = new ArrayList<>();
        /**
         * Mapping of each node contained by each group being managed, to its containing group
         */
        private final HashMap<MapLocation, TraversalGroup> groupMappings = new HashMap<>();
        /**
         * The last {@link TraversalGroup} returned by {@link #newGroup(TraversalGroup)}
         */
        private TraversalGroup lastCreated;

        /**
         * Commits {@link #lastCreated} to be mapped, and returns a new {@link TraversalGroup} with the specified
         * {@link TraversalGroup} as the parent
         * @param currentlyTraversing the parent {@link TraversalGroup} of the new group to be returned
         * @return a new {@link TraversalGroup} with the specified {@link TraversalGroup} as the parent
         */
        TraversalGroup newGroup(TraversalGroup currentlyTraversing) {
            TraversalGroup traversalGroup = new TraversalGroup(currentlyTraversing);
            groups.add(traversalGroup);
            addGroup(lastCreated);
            lastCreated = traversalGroup;
            return traversalGroup;
        }

        /**
         * Maps the given group's nodes
         * @param group the group to add
         */
        private void addGroup(TraversalGroup group) {
            if (Objects.nonNull(group)) {
                for (MapLocation node : group.nodes) {
                    groupMappings.put(node, group);
                }
            }
        }

        /**
         * Checks if the given node is contained in any {@link TraversalGroup} currently under management
         * @param node the node to check if contained in any {@link TraversalGroup} currently under management
         * @return true if the node is present in any {@link TraversalGroup} currently under management
         */
        boolean contains(MapLocation node) {
            if (groupMappings.containsKey(node)) {
                return true;
            }
            for (TraversalGroup group : groups) {
                if (group.getNodes().contains(node)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Gets the containing {@link TraversalGroup} for a given node
         * @param node the node for which to find the containing {@link TraversalGroup}
         * @return the containing {@link TraversalGroup} for a given node
         */
        TraversalGroup groupFor(MapLocation node) {
            if (groupMappings.containsKey(node)){
                return groupMappings.get(node);
            } else {
                if (lastCreated.getNodes().contains(node)) {
                    return lastCreated;
                }
            }
            return null;
        }

        /**
         * @return the sum of the nodes in all managed {@link TraversalGroup}s
         */
        int size() {
            return groups.stream().mapToInt(TraversalGroup::size).sum();
        }

        /**
         * @return the number of {@link TraversalGroup}s under management
         */
        int groupCount() {
            return groups.size();
        }

        /**
         * Processes the {@link TraversalGroup}s into an integer group mapping table, by row and column
         * @param gridSideSize see {@link MapData#getGridSideSize()}
         * @return an integer group mapping table, by row and column
         */
        int[][] processGroups(int gridSideSize) {
            int[][] nodeGroups = new int[gridSideSize][gridSideSize];
            int lengthSquared = gridSideSize * gridSideSize;
            groups.sort(Comparator.comparingInt(TraversalGroup::getDepth));
            for (Iterator<TraversalGroup> iterator = groups.iterator(); iterator.hasNext(); ) {
                TraversalGroup group = iterator.next();
                if (group.size() < 10) {
                    group.collapseToParent();
                    iterator.remove();
                }
            }
            int k = 0;
            int count = 0;
            for (TraversalGroup group : groups) {
                for (MapLocation element : group.getNodes()) {
                    nodeGroups[element.getRow()][element.getColumn()] = k;
                    count++;
                }
                GenerationEvent.of(String.format("Grouping nodes... (%d/%d)", count, lengthSquared));
                k++;
            }
            return nodeGroups;
        }
    }
}
