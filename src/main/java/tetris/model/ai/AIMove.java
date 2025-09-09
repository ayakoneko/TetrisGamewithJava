package tetris.model.ai;

/**
 * AIMove represents a planned move for the AI player in Tetris.
 * 
 * This immutable record encapsulates the complete intention of the AI for a single piece:
 * - Where to place the piece horizontally (targetX)
 * - How to orient the piece (targetRotation)
 * 
 * Key features:
 * - Immutable: Cannot be modified after creation (thread-safe)
 * - Validated: Constructor ensures values are within reasonable bounds
 * - Serializable: Can be easily logged, stored, or transmitted
 * - Debuggable: toString() provides clear representation for debugging
 * 
 * The targetX can be negative to represent pieces placed partially off the left edge
 * of the board - this is valid in Tetris when part of the piece extends onto the board.
 * 
 * @param targetX The target column position (can be negative for edge placements)
 * @param targetRotation The target rotation state (0-3, where 0=0°, 1=90°, 2=180°, 3=270°)
 */
public record AIMove(int targetX, int targetRotation) {
    
    /**
     * Creates an AIMove with validation to ensure reasonable values.
     * 
     * @param targetX Target column (can be negative for pieces extending beyond left edge)
     * @param targetRotation Target rotation (0-3 representing 0°, 90°, 180°, 270°)
     * @throws IllegalArgumentException if parameters are outside reasonable bounds
     */
    public AIMove {
        // Allow negative X positions for valid edge placements, but prevent extreme values
        if (targetX < -10 || targetX > 50) {
            throw new IllegalArgumentException("targetX out of reasonable range (-10 to 50): " + targetX);
        }
        // Rotation must be valid (0=0°, 1=90°, 2=180°, 3=270°)
        if (targetRotation < 0 || targetRotation > 3) {
            throw new IllegalArgumentException("targetRotation must be 0-3: " + targetRotation);
        }
    }
    
    /**
     * Returns a human-readable representation of this move for debugging.
     * 
     * @return String representation showing target position and rotation
     */
    @Override
    public String toString() {
        return String.format("AIMove{x=%d, rot=%d°}", targetX, targetRotation * 90);
    }
}