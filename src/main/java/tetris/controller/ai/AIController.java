package tetris.controller.ai;

import tetris.common.Action;
import tetris.controller.game.GameController;
import tetris.model.Tetromino;
import tetris.model.ai.AIMove;
import tetris.model.ai.TetrisAI;

/**
 * AIController manages AI decision-making and move execution for intelligent Tetris gameplay.
 * 
 * This controller bridges the gap between AI decision-making and actual game actions.
 * It works in two phases:
 * 1. PLANNING: Uses TetrisAI to determine the optimal move for the current piece
 * 2. EXECUTION: Breaks down the planned move into sequential game actions (rotate, move, drop)
 * 
 * Key features:
 * - Lazy planning: Only calculates new moves when pieces change
 * - Sequential execution: Performs rotations first, then horizontal movement, finally dropping
 * - State management: Tracks execution progress and resets between pieces
 * - Error handling: Gracefully handles invalid moves and game state changes
 * 
 * The AI execution is deliberately sequential to make the moves visible and debuggable.
 */
public class AIController {
    
    private final TetrisAI ai = new TetrisAI();          // Core AI decision engine
    private AIMove plannedMove;                          // Currently planned move
    private boolean needNewMove = true;                  // Flag to trigger new planning
    
    // AI execution state tracking
    private boolean isExecuting = false;                // Whether we're currently executing a move
    private int executionStep = 0;                      // Current step in move execution (unused in current implementation)
    
    /**
     * Plans the next move for the current piece if needed.
     * Uses lazy evaluation - only calculates when a new piece appears or planning is needed.
     * This is called every game tick but only does work when necessary.
     * 
     * @param gameController Current game controller with board state
     */
    public void planNextMove(GameController gameController) {
        // Only plan if we need a new move and there's a current piece
        if (needNewMove && gameController.board().current() != null) {
            // Use TetrisAI to find the optimal move for current board state
            plannedMove = ai.findBestMove(gameController.board(), 
                                        gameController.board().current());
            
            // Reset planning and execution state
            needNewMove = false;
            isExecuting = false;
            executionStep = 0;
        }
    }
    
    /**
     * Gets the next action for the AI to perform based on the planned move.
     * Executes moves in optimal sequence: rotation → horizontal movement → drop.
     * Returns null when no action is needed.
     * 
     * @param gameController Current game controller
     * @return Next action to perform, or null if move is complete/invalid
     */
    public Action getNextAction(GameController gameController) {
        if (plannedMove == null) return null; // No move planned
        
        Tetromino current = gameController.board().current();
        if (current == null) return null; // No current piece
        
        // Initialize execution state if needed
        if (!isExecuting) {
            isExecuting = true;
            executionStep = 0;
        }
        
        // Execute moves in optimal sequence for best results:
        // 1. Rotation first (while piece has room to rotate)
        // 2. Horizontal movement (to correct column)  
        // 3. Drop (when positioned correctly)
        
        // Phase 1: Achieve correct rotation
        if (current.rot != plannedMove.targetRotation()) {
            return Action.ROTATE_CW; // Keep rotating until we reach target
        }
        
        // Phase 2: Achieve correct horizontal position
        if (current.x() < plannedMove.targetX()) {
            return Action.MOVE_RIGHT; // Move right to target
        } else if (current.x() > plannedMove.targetX()) {
            return Action.MOVE_LEFT;  // Move left to target
        }
        
        // Phase 3: Drop when perfectly positioned
        if (current.x() == plannedMove.targetX() && current.rot == plannedMove.targetRotation()) {
            return Action.HARD_DROP; // Execute the final placement
        }
        
        // Should not reach here under normal circumstances
        return null;
    }
    
    /**
     * Called when a piece has been placed (locked) into the board.
     * Resets AI state to trigger planning for the next piece.
     * Must be called by the game controller when pieces are locked.
     */
    public void onPiecePlaced() {
        needNewMove = true;    // Trigger new planning for next piece
        plannedMove = null;    // Clear current plan
        isExecuting = false;   // Reset execution state
        executionStep = 0;     // Reset execution step counter
    }
    
    /**
     * Called when the game is reset or restarted.
     * Completely resets all AI state to initial conditions.
     */
    public void reset() {
        needNewMove = true;    // Need to plan for first piece
        plannedMove = null;    // No current plan
        isExecuting = false;   // Not executing anything
        executionStep = 0;     // Reset step counter
    }
    
    /**
     * Returns whether the AI is currently executing a planned move.
     * Useful for debugging and status display.
     * 
     * @return true if AI is in the middle of executing a move sequence
     */
    public boolean isExecuting() {
        return isExecuting;
    }
    
    /**
     * Returns the currently planned move for debugging and display purposes.
     * Can be used to show what the AI intends to do.
     * 
     * @return Current planned move, or null if no move is planned
     */
    public AIMove getPlannedMove() {
        return plannedMove;
    }
}