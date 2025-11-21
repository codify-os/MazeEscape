package phase2.Tile;

import org.junit.jupiter.api.Test;
import phase2.Entity.Pathfinder;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.net.URL;

/**
 * Simple unit test for Pathfinder: straight-line path.
 * - Creates a tiny corridor in the existing TileManager grid
 * - Verifies path length and adjacency
 */
public class PathfinderTest {

    @Test
    public void findPath_straightLine_returnsDirectPath() {
    // Ensure the test map resource is available on the test classpath
    URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
    assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

    phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        // Build a clear horizontal corridor at row 0 for cols 0..3
        int startCol = 0, startRow = 0;
        int goalCol = 3, goalRow = 0;

        for (int c = startCol; c <= goalCol; c++) {
            Tile t = new Tile();
            t.col = c;
            t.row = startRow;
            t.collision = false; // walkable
            tm.mapTiles[c][startRow] = t;
        }

        // Ensure the goal tile exists
        Tile start = tm.getTile(startCol, startRow);
        Tile goal = tm.getTile(goalCol, goalRow);
        assertNotNull(start, "Start tile should not be null");
        assertNotNull(goal, "Goal tile should not be null");

        Pathfinder pf = new Pathfinder(tm);
        List<Tile> path = pf.findPath(start, goal);

        // Expect a path of length 4 (tiles at cols 0,1,2,3)
        assertNotNull(path, "Path should not be null");
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(4, path.size(), "Path length should be 4 for straight line 0->3");

        // Start and goal match
        assertEquals(start, path.get(0), "First tile should be start");
        assertEquals(goal, path.get(path.size() - 1), "Last tile should be goal");

        // Check adjacency (4-way) between consecutive tiles
        for (int i = 0; i < path.size() - 1; i++) {
            Tile a = path.get(i);
            Tile b = path.get(i + 1);
            int dx = Math.abs(a.col - b.col);
            int dy = Math.abs(a.row - b.row);
            assertTrue((dx + dy) == 1, () -> String.format("Tiles not adjacent: (%d,%d)->(%d,%d)", a.col, a.row, b.col, b.row));
            assertTrue(b.isWalkable(), "All path tiles must be walkable");
        }
    }

    @Test
    public void findPath_singleObstacle_reroutesAroundObstacle() {
        // Ensure test map resource present
        URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
        assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

        // Create a GamePanel to get a TileManager instance
        phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        // Setup: start (0,0), goal (2,0). Make a small 3x2 walkable area so path can route around obstacle
        int startCol = 0, startRow = 0;
        int goalCol = 2, goalRow = 0;

        // Ensure tiles for rows 0..1 and cols 0..2 exist and are walkable (we'll later set the obstacle)
        for (int r = 0; r <= 1; r++) {
            for (int c = startCol; c <= goalCol; c++) {
                Tile t = tm.getTile(c, r);
                if (t == null) {
                    t = new Tile();
                    t.col = c;
                    t.row = r;
                    tm.mapTiles[c][r] = t;
                }
                t.collision = false;
            }
        }

        // Place obstacle at (1,0)
        Tile obstacle = tm.getTile(1, 0);
        final int obsCol = 1, obsRow = 0;
        if (obstacle == null) {
            obstacle = new Tile();
            obstacle.col = obsCol;
            obstacle.row = obsRow;
            tm.mapTiles[obsCol][obsRow] = obstacle;
        }
        obstacle.collision = true;

        Tile start = tm.getTile(startCol, startRow);
        Tile goal = tm.getTile(goalCol, goalRow);
        assertNotNull(start, "Start tile should not be null");
        assertNotNull(goal, "Goal tile should not be null");

        Pathfinder pf = new Pathfinder(tm);
        List<Tile> path = pf.findPath(start, goal);

        // Basic assertions
        assertNotNull(path, "Path should not be null");
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(start, path.get(0), "First tile should be start");
        assertEquals(goal, path.get(path.size() - 1), "Last tile should be goal");

        // Path must not include the obstacle
    boolean touchesObstacle = path.stream().anyMatch(t -> t.col == obsCol && t.row == obsRow);
    assertFalse(touchesObstacle, "Path must avoid the obstacle at (1,0)");

        // Check adjacency and walkability for each step
        for (int i = 0; i < path.size() - 1; i++) {
            Tile a = path.get(i);
            Tile b = path.get(i + 1);
            int dx = Math.abs(a.col - b.col);
            int dy = Math.abs(a.row - b.row);
            assertTrue((dx + dy) == 1, () -> String.format("Tiles not adjacent: (%d,%d)->(%d,%d)", a.col, a.row, b.col, b.row));
            assertTrue(b.isWalkable(), "All path tiles must be walkable");
        }
    }

    @Test
    public void findPath_start_and_goal_same() {
    // Ensure the test map resource is available on the test classpath
    URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
    assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

    phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        // Build a clear horizontal corridor at row 0 for cols 0..3
        int startCol = 0, startRow = 0;
        int goalCol = 0, goalRow = 0;

        for (int c = startCol; c <= goalCol; c++) {
            Tile t = new Tile();
            t.col = c;
            t.row = startRow;
            t.collision = false; // walkable
            tm.mapTiles[c][startRow] = t;
        }

        // Ensure the goal tile exists
        Tile start = tm.getTile(startCol, startRow);
        Tile goal = tm.getTile(goalCol, goalRow);
        assertNotNull(start, "Start tile should not be null");
        assertNotNull(goal, "Goal tile should not be null");

    Pathfinder pf = new Pathfinder(tm);
    List<Tile> path = pf.findPath(start, goal);

    // Expect a single-tile path when start == goal
    assertNotNull(path, "Path should not be null");
    assertFalse(path.isEmpty(), "Path should not be empty");
    assertEquals(1, path.size(), "Path length should be 1 when start and goal are the same");

        // Start and goal match
        assertEquals(start, path.get(0), "First tile should be start");
        assertEquals(goal, path.get(path.size() - 1), "Last tile should be goal");

        // Check adjacency (4-way) between consecutive tiles
        for (int i = 0; i < path.size() - 1; i++) {
            Tile a = path.get(i);
            Tile b = path.get(i + 1);
            int dx = Math.abs(a.col - b.col);
            int dy = Math.abs(a.row - b.row);
            assertTrue((dx + dy) == 1, () -> String.format("Tiles not adjacent: (%d,%d)->(%d,%d)", a.col, a.row, b.col, b.row));
            assertTrue(b.isWalkable(), "All path tiles must be walkable");
        }
    }

    @Test
    public void findPath_noPathAvailable_returnsEmptyList() {
        URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
        assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

        phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        // Create isolated tiles - start at (0,0) and goal at (2,2) with obstacles blocking
        for (int r = 0; r <= 2; r++) {
            for (int c = 0; c <= 2; c++) {
                Tile t = new Tile();
                t.col = c;
                t.row = r;
                t.collision = (c == 1 || r == 1); // Block middle row and column
                tm.mapTiles[c][r] = t;
            }
        }

        Tile start = tm.getTile(0, 0);
        Tile goal = tm.getTile(2, 2);

        Pathfinder pf = new Pathfinder(tm);
        List<Tile> path = pf.findPath(start, goal);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.isEmpty(), "Path should be empty when no path exists");
    }

    @Test
    public void findPath_nullStart_returnsEmptyList() {
        URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
        assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

        phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        Tile goal = new Tile();
        goal.col = 1;
        goal.row = 1;
        goal.collision = false;
        tm.mapTiles[1][1] = goal;

        Pathfinder pf = new Pathfinder(tm);
        List<Tile> path = pf.findPath(null, goal);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.isEmpty(), "Path should be empty when start is null");
    }

    @Test
    public void findPath_nullGoal_returnsEmptyList() {
        URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
        assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

        phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        Tile start = new Tile();
        start.col = 0;
        start.row = 0;
        start.collision = false;
        tm.mapTiles[0][0] = start;

        Pathfinder pf = new Pathfinder(tm);
        List<Tile> path = pf.findPath(start, null);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.isEmpty(), "Path should be empty when goal is null");
    }

    @Test
    public void findPath_unwalkableGoal_returnsEmptyList() {
        URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
        assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

        phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        // Create walkable start and unwalkable goal
        Tile start = new Tile();
        start.col = 0;
        start.row = 0;
        start.collision = false;
        tm.mapTiles[0][0] = start;

        Tile goal = new Tile();
        goal.col = 2;
        goal.row = 0;
        goal.collision = true; // Unwalkable
        tm.mapTiles[2][0] = goal;

        Pathfinder pf = new Pathfinder(tm);
        List<Tile> path = pf.findPath(start, goal);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.isEmpty(), "Path should be empty when goal is unwalkable");
    }

    @Test
    public void findPath_lShapedPath_findsCorrectPath() {
        URL mapUrl = getClass().getClassLoader().getResource("maps/map2.txt");
        assertNotNull(mapUrl, "Test resource missing from classpath: maps/map2.txt");

        phase2.UI.GamePanel gp = new phase2.UI.GamePanel();
        TileManager tm = gp.tileManager;

        // Create L-shaped walkable path
        for (int c = 0; c <= 2; c++) {
            Tile t = new Tile();
            t.col = c;
            t.row = 0;
            t.collision = false;
            tm.mapTiles[c][0] = t;
        }
        for (int r = 1; r <= 2; r++) {
            Tile t = new Tile();
            t.col = 2;
            t.row = r;
            t.collision = false;
            tm.mapTiles[2][r] = t;
        }

        Tile start = tm.getTile(0, 0);
        Tile goal = tm.getTile(2, 2);

        Pathfinder pf = new Pathfinder(tm);
        List<Tile> path = pf.findPath(start, goal);

        assertNotNull(path, "Path should not be null");
        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(start, path.get(0), "First tile should be start");
        assertEquals(goal, path.get(path.size() - 1), "Last tile should be goal");
        assertEquals(5, path.size(), "L-shaped path should have 5 tiles");

        // Verify all tiles are walkable and adjacent
        for (int i = 0; i < path.size() - 1; i++) {
            Tile a = path.get(i);
            Tile b = path.get(i + 1);
            assertTrue(a.isWalkable(), "All path tiles must be walkable");
            int distance = Math.abs(a.col - b.col) + Math.abs(a.row - b.row);
            assertEquals(1, distance, "Consecutive tiles must be adjacent");
        }
    }
}
