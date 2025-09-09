package tetris.controller.state;

import tetris.controller.ai.AIController;
import tetris.common.Action;
import tetris.common.GameState;
import tetris.controller.game.GameController;
import tetris.model.IGameBoard;
import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;

/**
 * AIPlayingState handles the game logic when AI is actively playing Tetris.
 * 
 * This state extends the standard playing state with AI intelligence:
 * - Integrates AIController for move planning and execution
 * - Manages AI timing and responsiveness (tick-based execution)
 * - Handles piece placement and line clearing with AI state management
 * - Supports both AI and human override (for debugging/testing)
 * - Adapts AI speed based on game level for appropriate challenge
 * 
 * The AI execution is throttled using tick counters to make moves visible
 * and to prevent the AI from playing too fast to follow.
 * 
 * This state maintains all standard Tetris rules while adding intelligent automation.
 */
public class AIPlayingState implements PlayState {
    
    private final AIController aiController = new AIController();  // Core AI decision and execution engine
    private int aiTickCounter = 0;                                 // Counter for AI timing control
    
    // AI speed control - determines how often AI makes moves (lower = faster)
    // These values balance AI responsiveness with visibility of moves
    private static final int AI_SPEED_NORMAL = 2;  // AI acts every 2 ticks (good balance of speed/visibility)
    private static final int AI_SPEED_FAST = 1;    // AI acts every tick (maximum speed for high levels)
    
    private final GameSetting gameSetting;          // Game configuration settings
    private final PlayerType playerType;           // Type of player (should be AI for this state)
    
    /**
     * Creates a new AI playing state.
     * 
     * @param gameSetting Game configuration (used for AI speed adjustment)
     * @param playerType Type of player (should be PlayerType.AI)
     */
    public AIPlayingState(GameSetting gameSetting, PlayerType playerType) {
        this.gameSetting = gameSetting;
        this.playerType = playerType;
    }
    
    /**
     * Starts the AI playing state by spawning the first piece and initializing AI.
     * Called when transitioning to AI play mode.
     */
    @Override
    public void start(GameController c) {
        IGameBoard b = c.board();
        aiController.reset(); // Clear any previous AI state
        
        // Attempt to spawn the first piece
        if (!b.newPiece()) {
            c.setState(new GameOverState()); // Board is full - game over
        } else {
            aiController.planNextMove(c); // Start AI planning for first piece
        }
    }
    
    /**
     * Main game tick - handles both AI decision-making and standard Tetris mechanics.
     * Called every game cycle to advance the game state.
     */
    @Override
    public void tick(GameController c) {
        IGameBoard b = c.board();
        
        // Execute AI logic if this is an AI player
        if (playerType == PlayerType.AI) {
            handleAITick(c); // AI planning and move execution
        }
        
        // Standard Tetris game mechanics - piece gravity and landing
        if (!b.softDropStep()) {
            // Current piece has landed and can't fall further
            aiController.onPiecePlaced(); // Notify AI that piece placement is complete
            
            // Lock the piece into the board permanently
            if (!b.lockCurrent()) {
                c.setState(new GameOverState()); // Board overflow - game over
                return;
            }
            
            // Clear any complete lines and track count for scoring
            int cleared = b.clearFullLines();
            c.setClearedLinesLastTick(cleared);
            
            // Attempt to spawn the next piece
            if (!b.newPiece()) {
                c.setState(new GameOverState()); // No room for new piece - game over
            } else {
                // Start AI planning for the newly spawned piece
                if (playerType == PlayerType.AI) {
                    aiController.planNextMove(c);
                }
            }
        }
    }
    
    /**
     * Handles AI decision-making and action execution with timing control.
     * This method throttles AI actions to make them visible and controllable.
     * Called every game tick when AI is active.
     */
    private void handleAITick(GameController c) {
        // Ensure AI has a plan for the current piece (lazy planning)
        aiController.planNextMove(c);
        
        // Control AI execution speed using tick counter
        // This prevents AI from moving too fast to follow
        int aiSpeed = getAISpeed();
        if (++aiTickCounter >= aiSpeed) {
            // Get the next action from AI controller
            Action aiAction = aiController.getNextAction(c);
            if (aiAction != null) {
                handle(c, aiAction); // Execute the AI's chosen action
            }
            aiTickCounter = 0; // Reset counter for next action timing
        }
    }
    
    /**
     * Gets the AI speed based on current game settings.
     * Higher levels get faster AI to maintain appropriate challenge.
     * 
     * @return Number of ticks between AI actions (lower = faster)
     */
    private int getAISpeed() {
        if (gameSetting != null && gameSetting.getLevel() >= 8) {
            return AI_SPEED_FAST;   // Very fast AI for high levels (every tick)
        }
        return AI_SPEED_NORMAL;     // Normal AI speed for lower levels (every 2 ticks)
    }
    
    /**
     * Handles game actions - supports both AI-generated actions and manual overrides.
     * This allows for debugging and testing by allowing manual control even in AI mode.
     */
    @Override
    public void handle(GameController c, Action action) {
        IGameBoard b = c.board();
        
        // Execute the requested action (from AI or manual override)
        switch (action) {
            case MOVE_LEFT  -> b.moveLeft();           // Move piece left
            case MOVE_RIGHT -> b.moveRight();          // Move piece right
            case SOFT_DROP  -> b.softDropStep();       // Soft drop (one step down)
            case HARD_DROP  -> {                       // Hard drop (instant placement)
                b.hardDrop(); // Drop piece to bottom
                // Handle locking and line clearing consistently with normal tick
                if (!b.lockCurrent()) {
                    c.setState(new GameOverState());
                    return;
                }
                aiController.onPiecePlaced(); // Notify AI that piece is placed
                
                int cleared = b.clearFullLines();
                c.setClearedLinesLastTick(cleared);
                
                // Spawn next piece
                if (!b.newPiece()) {
                    c.setState(new GameOverState()); // Game over if no room
                } else if (playerType == PlayerType.AI) {
                    aiController.planNextMove(c); // Plan for next piece
                }
            }
            case ROTATE_CW  -> b.rotateCW();           // Rotate piece clockwise
        }
    }
    
    /** Pause the game - transitions to paused state */
    @Override
    public void togglePause(GameController c) {
        c.setState(new PausedState(this)); // Pass current state to remember AI mode
    }
    
    /** Restart the game completely - reset board and AI state */
    @Override
    public void restart(GameController c) {
        aiController.reset();                                        // Clear AI state
        c.setState(new AIPlayingState(gameSetting, playerType));     // Create fresh state
        c.board().reset();                                           // Clear the board
        start(c);                                                    // Start new game
    }
    
    /** Reset the board while keeping the same game session */
    @Override
    public void reset(GameController c) {
        aiController.reset();  // Reset AI planning
        c.board().reset();     // Clear the board
    }
    
    /** Returns the UI state for display purposes */
    @Override
    public GameState uiState() {
        return GameState.PLAY; // Show normal play UI
    }
}