package tetris.util;

import tetris.model.Tetromino;

/**
 * BoardUtils provides centralized Tetris game logic utilities.
 * 
 * This class eliminates code duplication by providing static methods for common
 * board operations that are needed by both the main game logic and AI simulation.
 * 
 * All methods work on raw int[][] board arrays for maximum flexibility and performance.
 * Board format: 0 = empty cell, positive integers = filled cells (piece color IDs)
 * 
 * Design principles:
 * - Pure functions: No side effects except on passed arrays
 * - Static methods: No state, just utilities
 * - Performance focused: Optimized for frequent AI simulation use
 * - Single source of truth: One implementation of each game rule
 */
public class BoardUtils {
    
    // Private constructor to prevent instantiation of utility class
    private BoardUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }
    
    /**
     * Checks if a tetromino piece can move to the specified position and rotation.
     * This is the core collision detection logic used throughout the game.
     * 
     * @param board The game board (0 = empty, >0 = filled)
     * @param piece The tetromino piece to check
     * @param dx Horizontal movement offset from piece's current position
     * @param dy Vertical movement offset from piece's current position  
     * @param rotation Target rotation state (0-3)
     * @return true if the move is valid (no collisions), false otherwise
     */
    public static boolean canMovePiece(int[][] board, Tetromino piece, int dx, int dy, int rotation) {
        int[][] shape = piece.type.rot[rotation];
        
        // Check each block of the piece shape
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape[r][c] == 0) continue; // Skip empty spaces in piece
                
                // Calculate world coordinates for this block
                int worldX = piece.x() + c + dx;
                int worldY = piece.y() + r + dy;
                
                // Check horizontal boundaries
                if (worldX < 0 || worldX >= board[0].length) return false;
                
                // Check bottom boundary
                if (worldY >= board.length) return false;
                
                // Allow pieces to exist above the visible board (spawn area)
                if (worldY < 0) continue;
                
                // Check collision with existing pieces
                if (board[worldY][worldX] != 0) return false;
            }
        }
        return true; // No collisions detected
    }
    
    /**
     * Locks a tetromino piece into the board at its current position.
     * This permanently places the piece blocks onto the board.
     * 
     * @param board The game board to modify
     * @param piece The piece to lock into place
     */
    public static void lockPieceIntoBoard(int[][] board, Tetromino piece) {
        int[][] shape = piece.shape();
        
        // Place each block of the piece onto the board
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (shape[r][c] == 0) continue; // Skip empty spaces in piece
                
                int boardX = piece.x() + c;
                int boardY = piece.y() + r;
                
                // Only place blocks within board boundaries
                if (boardY >= 0 && boardY < board.length && 
                    boardX >= 0 && boardX < board[0].length) {
                    board[boardY][boardX] = piece.colorId();
                }
            }
        }
    }
    
    /**
     * Counts the number of complete (full) lines in the board.
     * A complete line has no empty cells (all cells contain pieces).
     * 
     * @param board The game board to check
     * @return Number of complete horizontal lines
     */
    public static int countFullLines(int[][] board) {
        int fullLines = 0;
        
        // Check each row from top to bottom
        for (int y = 0; y < board.length; y++) {
            boolean isLineFull = true;
            
            // Check if every cell in this row is filled
            for (int x = 0; x < board[0].length; x++) {
                if (board[y][x] == 0) {
                    isLineFull = false;
                    break; // Found empty cell, line is not complete
                }
            }
            
            if (isLineFull) {
                fullLines++;
            }
        }
        
        return fullLines;
    }
    
    /**
     * Clears all complete lines from the board and shifts remaining lines down.
     * This is the standard Tetris line clearing algorithm.
     * 
     * Implementation uses a two-pointer approach:
     * - Read pointer scans from bottom to top
     * - Write pointer tracks where to place kept lines
     * - Full lines are automatically discarded by not copying them
     * 
     * @param board The game board to modify
     * @return Number of lines that were cleared
     */
    public static int clearFullLines(int[][] board) {
        int height = board.length;
        int width = board[0].length;
        int write = height-1, cleared = 0;
        
        for (int read = height-1; read>=0; read--){
            boolean full = true;
            for (int x=0;x<width;x++){ 
                if (board[read][x]==0){ 
                    full=false; 
                    break; 
                } 
            }
            if (!full){
                if (write != read) System.arraycopy(board[read],0,board[write],0,width);
                write--;
            } else {
                cleared++;
            }
        }
        for (int y=write; y>=0; y--){
            for (int x=0;x<width;x++) board[y][x]=0;
        }
        return cleared;
    }
    
    /**
     * Creates a deep copy of a game board.
     * Essential for AI simulation to avoid modifying the real game state.
     * 
     * @param board Original board to copy
     * @return Independent copy of the board
     */
    public static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        
        // Copy each row independently to ensure complete independence
        for (int y = 0; y < board.length; y++) {
            System.arraycopy(board[y], 0, copy[y], 0, board[0].length);
        }
        
        return copy;
    }
}