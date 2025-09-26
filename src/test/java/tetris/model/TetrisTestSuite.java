package tetris.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import tetris.model.board.Position;
import tetris.model.score.HighScoreService;
import tetris.model.score.HighScoreStore;
import tetris.model.score.ScoreCalculator;
import tetris.model.score.ScoreEntry;
import tetris.model.tetromino.Tetromino;
import tetris.model.tetromino.TetrominoType;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * 6 Essential JUnit Test Cases for Final Submission
 * Demonstrates core testing, parameterized tests, and Mockito usage
 */

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TetrisTestSuite {

    @Mock
    private HighScoreStore mockStore;

    /**
     * Test Case 1: Tetromino Creation and Movement
     * Tests core game piece functionality
     */
    @Test
    @DisplayName("Test Case 1: Tetromino creation and movement")
    void testTetrominoBasics() {
        // Test creation
        Tetromino tetromino = new Tetromino(TetrominoType.T, 5, 0);
        assertEquals(TetrominoType.T, tetromino.type);
        assertEquals(5, tetromino.x());
        assertEquals(0, tetromino.y());
        assertEquals(3, tetromino.colorId()); // T piece color

        // Test movement
        tetromino.moveBy(2, 1);
        assertEquals(7, tetromino.x());
        assertEquals(1, tetromino.y());

        // Test position setting
        tetromino.setPos(10, 15);
        assertEquals(10, tetromino.x());
        assertEquals(15, tetromino.y());
    }

    /**
     * Test Case 2: Score Calculation Logic
     * Tests the scoring system with different line clears
     */
    @Test
    @DisplayName("Test Case 2: Score calculation for different line clears")
    void testScoreCalculation() {
        // Single line
        assertEquals(100, ScoreCalculator.computeLineClearScore(1, 1));
        assertEquals(500, ScoreCalculator.computeLineClearScore(1, 5));

        // Double line
        assertEquals(300, ScoreCalculator.computeLineClearScore(2, 1));
        assertEquals(1500, ScoreCalculator.computeLineClearScore(2, 5));

        // Triple line
        assertEquals(600, ScoreCalculator.computeLineClearScore(3, 1));

        // Tetris (4 lines)
        assertEquals(1000, ScoreCalculator.computeLineClearScore(4, 1));
        assertEquals(5000, ScoreCalculator.computeLineClearScore(4, 5));

        // Invalid inputs
        assertEquals(0, ScoreCalculator.computeLineClearScore(0, 5));
        assertEquals(0, ScoreCalculator.computeLineClearScore(5, 5));
    }

    /**
     * Test Case 3: Parameterized Test for All Tetromino Types
     * Demonstrates parameterized testing with @EnumSource
     */
    @ParameterizedTest
    @DisplayName("Test Case 3: All tetromino types have valid properties")
    @EnumSource(TetrominoType.class)
    void testAllTetrominoTypes(TetrominoType type) {
        // Create piece of each type
        Tetromino tetromino = new Tetromino(type, 0, 0);

        // Verify basic properties
        assertNotNull(tetromino.type);
        assertEquals(type, tetromino.type);
        assertTrue(tetromino.colorId() >= 1 && tetromino.colorId() <= 7);

        // Verify shape is valid 4x4 grid
        int[][] shape = tetromino.shape();
        assertNotNull(shape);
        assertEquals(4, shape.length);
        assertEquals(4, shape[0].length);

        // Count blocks - each piece should have exactly 4 blocks
        int blockCount = 0;
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell == 1) blockCount++;
            }
        }
        assertEquals(4, blockCount, type + " should have exactly 4 blocks");
    }

    /**
     * Test Case 4: HighScoreService with Mock (Mockito demonstration)
     * Tests score service functionality using mocked storage
     */
    @Test
    @DisplayName("Test Case 4: HighScore service with mocked storage")
    void testHighScoreServiceWithMock() {
        // Setup mock behavior
        List<ScoreEntry> testScores = Arrays.asList(
                new ScoreEntry("Alice", 1000),
                new ScoreEntry("Bob", 800)
        );
        when(mockStore.load()).thenReturn(new ArrayList<>(testScores));
        doNothing().when(mockStore).save(any());

        // Create service with mock
        HighScoreService service = new HighScoreService(mockStore);

        // Wait for async initialization
        try { Thread.sleep(200); } catch (InterruptedException e) { }

        // Test score addition and current score
        service.addLinesScore(4, 5); // 4 lines * 1000 * level 5 = 5000 points
        assertEquals(5000, service.getCurrentScore());

        // Test score submission
        boolean result = service.submitScore("TestPlayer");
        assertTrue(result);
        assertEquals(0, service.getCurrentScore()); // Should reset after submission

        // Verify mock interactions
        verify(mockStore, atLeastOnce()).load(); // Called during initialization and submission
        verify(mockStore, atLeastOnce()).save(any()); // Called during submission

        // Test eligibility check with different scores
        assertTrue(service.isEligibleForHighScore(1500)); // Higher than existing scores
        assertFalse(service.isEligibleForHighScore(0)); // Zero score not eligible
        assertFalse(service.isEligibleForHighScore(-100)); // Negative score not eligible

        // Test getTopScores method
        List<ScoreEntry> topScores = service.getTopScores();
        assertNotNull(topScores);
        assertTrue(topScores.size() <= 10); // Should not exceed MAX

        // Cleanup
        service.shutdown();
    }


    /**
     * Test Case 5: Spy Test Double Demonstration
     */
    @Test
    @DisplayName("Test Case 5: Spy test double demonstration")
    void testSpyTestDouble() {
        // Create real list and spy wrapper
        List<ScoreEntry> realList = new ArrayList<>();
        List<ScoreEntry> spyList = spy(realList);

        // Spy allows real method calls while still enabling verification
        spyList.add(new ScoreEntry("SpyPlayer1", 1200));
        spyList.add(new ScoreEntry("SpyPlayer2", 900));

        // Verify the real methods work
        assertEquals(2, spyList.size());
        assertEquals("SpyPlayer1", spyList.get(0).getPlayerName());
        assertEquals(1200, spyList.get(0).getScore());

        // Spy can verify method calls like Mock
        verify(spyList, times(2)).add(any(ScoreEntry.class));
        verify(spyList, times(2)).get(0);
        verify(spyList, atLeastOnce()).size();

        // Can partially mock spy methods if needed
        doReturn(999).when(spyList).size();
        assertEquals(999, spyList.size()); // Now returns mocked value

        // Reset to real behavior - need to clear the actual list first
        reset(spyList);
        spyList.clear();
        spyList.add(new ScoreEntry("RealPlayer", 500));
        assertEquals(1, spyList.size()); // Back to real behavior

        // Demonstrate difference: Spy uses real methods, Mock doesn't
        List<ScoreEntry> mockList = mock(ArrayList.class);
        mockList.add(new ScoreEntry("MockPlayer", 100));
        // Mock returns 0 by default (not real behavior)
        assertEquals(0, mockList.size());

        // But spy returns real size
        assertEquals(1, spyList.size());
    }
    /**
     * Test Case 6: Position Record and TetrominoType Rotation
     * Tests Position record and rotation mechanics
     */
    @Test
    @DisplayName("Test Case 6: Position record and piece rotation validation")
    void testPositionAndRotation() {
        // Test Position record
        Position pos1 = new Position(5, 10);
        Position pos2 = new Position(5, 10);
        Position pos3 = new Position(3, 7);

        assertEquals(5, pos1.x());
        assertEquals(10, pos1.y());
        assertEquals(pos1, pos2); // Records have automatic equals
        assertNotEquals(pos1, pos3);

        // Test TetrominoType rotation states
        TetrominoType tPiece = TetrominoType.T;

        // Each piece should have 4 rotation states
        assertEquals(4, tPiece.rot.length);

        // Each rotation should be 4x4 grid
        for (int r = 0; r < 4; r++) {
            assertEquals(4, tPiece.rot[r].length);
            assertEquals(4, tPiece.rot[r][0].length);
        }

        // Verify all rotations maintain the same block count (even if position changes)
        TetrominoType oPiece = TetrominoType.O;
        int firstRotationBlockCount = countBlocks(oPiece.rot[0]);
        for (int r = 1; r < 4; r++) {
            int currentRotationBlockCount = countBlocks(oPiece.rot[r]);
            assertEquals(firstRotationBlockCount, currentRotationBlockCount,
                    "O piece should have same number of blocks in all rotations");
        }

        // Test unique color IDs
        Set<Integer> colorIds = new HashSet<>();
        for (TetrominoType type : TetrominoType.values()) {
            assertTrue(colorIds.add(type.colorId),
                    "Color ID " + type.colorId + " is not unique");
        }
    }

    /**
     * Helper method to count blocks in a shape
     */
    private int countBlocks(int[][] shape) {
        int count = 0;
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell == 1) count++;
            }
        }
        return count;
    }
}