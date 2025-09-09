package tetris.dto;

/**
 * TetrominoData - Data Transfer Object for UI-safe tetromino information.
 * Provides read-only tetromino data without exposing internal Tetromino model to Views.
 */
public record TetrominoData(
    int[][] shape,
    int x,
    int y,
    int colorId
) {
    
    // Factory method to create from Tetromino model
    public static TetrominoData fromTetromino(tetris.model.Tetromino tetromino) {
        if (tetromino == null) {
            return null;
        }
        
        // Defensive copy of shape to prevent mutation
        int[][] shapeData = tetromino.shape();
        int[][] shapeCopy = new int[shapeData.length][];
        for (int i = 0; i < shapeData.length; i++) {
            shapeCopy[i] = shapeData[i].clone();
        }
        
        return new TetrominoData(
            shapeCopy,
            tetromino.x(),
            tetromino.y(), 
            tetromino.colorId()
        );
    }
    
    // Null-safe factory method
    public static TetrominoData fromTetrominoSafe(tetris.model.Tetromino tetromino) {
        return tetromino != null ? fromTetromino(tetromino) : null;
    }
}