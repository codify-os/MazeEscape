package phase2.Bonus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class CrystalItemTest {

    private CrystalItem crystal;
    private Image dummyImage;

    @BeforeEach
    void setUp() {
        dummyImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        crystal = new CrystalItem(100, 200, dummyImage);
    }

    @Test
    void testConstructor_initialValues() {
        assertEquals(100, crystal.worldX);
        assertEquals(200, crystal.worldY);
        assertEquals(dummyImage, crystal.sprite);
        assertFalse(crystal.collected);
        assertFalse(crystal.despawned);
        assertTrue(crystal.isActive());
    }

    @Test
    void testStartTimer_setsSpawnTime() throws Exception {
        Field spawnTimeField = CrystalItem.class.getDeclaredField("spawnTime");
        spawnTimeField.setAccessible(true);

        assertEquals(0L, spawnTimeField.getLong(crystal));
        crystal.startTimer();
        long spawnTime = spawnTimeField.getLong(crystal);
        assertTrue(spawnTime > 0);
    }

    @Test
    void testUpdate_collectedDoesNothing() throws Exception {
        crystal.collected = true;
        crystal.update();
        assertFalse(crystal.despawned); // should not despawn if already collected
    }

    @Test
    void testUpdate_despawnedDoesNothing() throws Exception {
        crystal.despawned = true;
        crystal.update();
        assertTrue(crystal.despawned); // remains despawned
    }

    @Test
    void testUpdate_spawnTimeZeroDoesNothing() {
        crystal.update(); // spawnTime not started yet
        assertFalse(crystal.despawned);
    }

    @Test
    void testUpdate_doesNotDespawnBeforeDelay() throws Exception {
        crystal.startTimer();
        crystal.update();
        assertFalse(crystal.despawned);
    }

    @Test
    void testUpdate_despawnsAfterDelay() throws Exception {
        crystal.startTimer();

        Field spawnTimeField = CrystalItem.class.getDeclaredField("spawnTime");
        spawnTimeField.setAccessible(true);
        long pastTime = System.currentTimeMillis() - 16_000; // 16 seconds ago
        spawnTimeField.setLong(crystal, pastTime);

        crystal.update();
        assertTrue(crystal.despawned);
        assertFalse(crystal.isActive());
    }

    @Test
    void testIsActive_whenCollectedOrDespawned() {
        crystal.collected = true;
        assertFalse(crystal.isActive());

        crystal.collected = false;
        crystal.despawned = true;
        assertFalse(crystal.isActive());
    }

    @Test
    void testGetRemainingTime_beforeAndAfterDespawn() throws Exception {
        crystal.startTimer();

        Thread.sleep(1000);
        long remaining1 = crystal.getRemainingTime();
        assertTrue(remaining1 <= 14 && remaining1 > 0);

        Field spawnTimeField = CrystalItem.class.getDeclaredField("spawnTime");
        spawnTimeField.setAccessible(true);
        long pastTime = System.currentTimeMillis() - 16_000;
        spawnTimeField.setLong(crystal, pastTime);

        crystal.update();
        assertEquals(0, crystal.getRemainingTime());
    }

    @Test
    void testGetRemainingTime_collectedReturnsZero() {
        crystal.collected = true;
        assertEquals(0, crystal.getRemainingTime());
    }

    @Test
    void testGetRemainingTime_despawnedReturnsZero() {
        crystal.despawned = true;
        assertEquals(0, crystal.getRemainingTime());
    }

    @Test
    void testStartTimer_doesNotResetIfAlreadyStarted() throws Exception {
        crystal.startTimer();
        Field spawnTimeField = CrystalItem.class.getDeclaredField("spawnTime");
        spawnTimeField.setAccessible(true);
        long firstTime = spawnTimeField.getLong(crystal);

        Thread.sleep(5);
        crystal.startTimer();
        long secondTime = spawnTimeField.getLong(crystal);

        assertEquals(firstTime, secondTime);
    }
}
