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

    // Create a GamePanel to get a TileManager instance (uses project resources)
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
}
