package phase2.Entity;

import phase2.Tile.Tile;
import phase2.Tile.TileManager;
import phase2.UI.GamePanel;

import java.util.*;

public class Pathfinder {
    private TileManager tileManager;

    public Pathfinder(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    /**
     * Find a path from start tile to goal tile using A* algorithm
     * 
     * @param start Starting tile
     * @param goal  Goal/target tile
     * @return List of tiles representing the path, or empty list if no path found
     */
    public List<Tile> findPath(Tile start, Tile goal) {
        if (start == null || goal == null || !goal.isWalkable()) {
            return new ArrayList<>();
        }

        // Reset pathfinding data
        tileManager.resetPathfinding();

        PriorityQueue<Tile> openSet = new PriorityQueue<>(Comparator.comparingInt(t -> t.fCost));
        HashSet<Tile> closedSet = new HashSet<>();

        openSet.add(start);
        start.gCost = 0;
        start.setHCost(goal);
        start.calculateFCost();

        while (!openSet.isEmpty()) {
            Tile current = openSet.poll();

            if (current.equals(goal)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            // Check all 4 neighbors (up, down, left, right)
            for (Tile neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor) || !neighbor.isWalkable()) {
                    continue;
                }

                int tentativeGCost = current.gCost + 1; // Cost is 1 per tile

                if (!openSet.contains(neighbor) || tentativeGCost < neighbor.gCost) {
                    neighbor.parent = current;
                    neighbor.gCost = tentativeGCost;
                    neighbor.setHCost(goal);
                    neighbor.calculateFCost();

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();
        int col = tile.col;
        int row = tile.row;

        // Up, Down, Left, Right
        addNeighborIfValid(neighbors, col, row - 1);
        addNeighborIfValid(neighbors, col, row + 1);
        addNeighborIfValid(neighbors, col - 1, row);
        addNeighborIfValid(neighbors, col + 1, row);

        return neighbors;
    }

    private void addNeighborIfValid(List<Tile> neighbors, int col, int row) {
        Tile tile = tileManager.getTile(col, row);
        if (tile != null) {
            neighbors.add(tile);
        }
    }

    private List<Tile> reconstructPath(Tile goal) {
        List<Tile> path = new ArrayList<>();
        Tile current = goal;

        while (current != null) {
            path.add(current);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }
}
