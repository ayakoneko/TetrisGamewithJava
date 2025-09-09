package tetris.dto;

/**
 * GameStateData - Data Transfer Object for UI-safe game state information.
 * Provides read-only game state data without exposing internal models to Views.
 */
public record GameStateData(
    int[][] boardCells,
    TetrominoData currentPiece,
    GameState gameState,
    int currentScore,
    boolean musicOn,
    boolean sfxOn
) {
    
    public enum GameState {
        PLAY, PAUSE, GAME_OVER
    }
    
    // Factory method to create from game components
    public static GameStateData create(
        int[][] cells,
        TetrominoData piece, 
        GameState state,
        int score,
        boolean music,
        boolean sfx
    ) {
        // Defensive copy of board cells to prevent mutation
        int[][] cellsCopy = new int[cells.length][];
        for (int i = 0; i < cells.length; i++) {
            cellsCopy[i] = cells[i].clone();
        }
        
        return new GameStateData(cellsCopy, piece, state, score, music, sfx);
    }
}