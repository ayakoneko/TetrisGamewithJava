package tetris.controller.external;

import tetris.common.Action;
import tetris.controller.game.GameController;
import tetris.model.external.OpMove;
import tetris.model.external.PureGame;
import tetris.model.tetromino.Tetromino;

/**
 * ExternalController manages gameplay controlled by an external TetrisServer.
 *
 * This controller communicates with TetrisServer at localhost:3000 to get optimal moves.
 * It handles:
 * - Server communication and error handling
 * - Connection status monitoring (server available/unavailable)
 * - Graceful degradation when server is not running
 * - Automatic reconnection when server becomes available
 * - Move execution similar to AIController
 *
 * Requirements:
 * - Show warning when server not running + no control
 * - Resume control immediately when server starts mid-game
 */
public class ExternalController {

    private final TetrisServerClient client;

    // Move planning and execution
    private OpMove plannedMove;
    private boolean needNewMove = true;
    private boolean isExecuting = false;

    // Connection status
    private boolean lastKnownServerStatus = false;

    public ExternalController() {
        this.client = new TetrisServerClient();
    }

    /**
     * Plans the next move by communicating with the external server.
     * Only makes server requests when needed (new piece or failed previous attempt).
     */
    public void planNextMove(GameController gameController) {
        // Only plan if we need a new move and there's a current piece
        if (!needNewMove || gameController.board().current() == null) {
            return;
        }

        // Check if server status changed
        boolean currentServerStatus = client.isServerAvailable();
        if (currentServerStatus != lastKnownServerStatus) {
            if (currentServerStatus) {
                System.out.println("[EXTERNAL] Server connection established - resuming control");
            } else {
                System.out.println("[EXTERNAL] Server unavailable - no external control");
            }
            lastKnownServerStatus = currentServerStatus;
        }

        // Convert current game state to PureGame format
        PureGame gameState = createPureGameState(gameController);

        // Request optimal move from server
        OpMove serverMove = client.getOptimalMove(gameState);

        if (serverMove != null) {
            plannedMove = serverMove;
            needNewMove = false;
            isExecuting = false;

            System.out.println("[EXTERNAL] Planned move: X=" + serverMove.opX() +
                             ", Rotate=" + serverMove.opRotate());
        } else {
            // Server communication failed - will retry next tick
            plannedMove = null;
            // Don't set needNewMove = false, so we'll try again
        }
    }

    /**
     * Gets the next action based on the server's planned move.
     * Returns null when server is unavailable or no action needed.
     */
    public Action getNextAction(GameController gameController) {
        // No control when server unavailable
        if (!client.isServerAvailable()) {
            return null;
        }

        if (plannedMove == null) {
            return null; // No move planned or server unavailable
        }

        Tetromino current = gameController.board().current();
        if (current == null) {
            return null; // No current piece
        }

        // Initialize execution
        if (!isExecuting) {
            isExecuting = true;
        }

        // Execute server's planned move in sequence:
        // 1. Achieve correct rotation (opRotate times)
        // 2. Move to correct X position (opX)
        // 3. Drop when positioned correctly

        // Phase 1: Rotation - apply opRotate rotations
        int targetRotations = plannedMove.opRotate() % 4; // Normalize to 0-3
        int currentRotations = current.rot % 4;

        if (currentRotations != targetRotations) {
            return Action.ROTATE_CW;
        }

        // Phase 2: Horizontal positioning
        int targetX = plannedMove.opX();

        if (current.x() < targetX) {
            return Action.MOVE_RIGHT;
        } else if (current.x() > targetX) {
            return Action.MOVE_LEFT;
        }

        // Phase 3: Drop when correctly positioned
        if (current.x() == targetX && currentRotations == targetRotations) {
            return Action.HARD_DROP;
        }

        return null;
    }

    /**
     * Called when a piece is placed. Triggers planning for next piece.
     */
    public void onPiecePlaced() {
        needNewMove = true;
        plannedMove = null;
        isExecuting = false;
    }

    /**
     * Called when game is reset.
     */
    public void reset() {
        needNewMove = true;
        plannedMove = null;
        isExecuting = false;
        client.resetConnectionStatus();
    }

    /**
     * Returns whether the external server is available for control.
     */
    public boolean isServerAvailable() {
        return client.isServerAvailable();
    }

    /**
     * Returns whether currently executing a move.
     */
    public boolean isExecuting() {
        return isExecuting;
    }

    /**
     * Gets the current planned move for debugging.
     */
    public OpMove getPlannedMove() {
        return plannedMove;
    }

    /**
     * Converts current game state to PureGame format for server communication.
     */
    private PureGame createPureGameState(GameController gameController) {
        var board = gameController.board();

        // Get board dimensions
        int width = board.getWidth();
        int height = board.getHeight();

        // Get board cells (copy to avoid modification)
        int[][] originalCells = board.cells();
        int[][] cells = new int[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(originalCells[y], 0, cells[y], 0, width);
        }

        // Get current shape (if any)
        int[][] currentShape = null;
        if (board.current() != null) {
            currentShape = board.current().shape();
        }

        // Get next shape (if any) - need to check if GameBoard has this method
        int[][] nextShape = null;
        if (board instanceof tetris.model.board.GameBoard gameBoard) {
            var nextType = gameBoard.getNextTetrominoType();
            if (nextType != null) {
                // Create temporary tetromino to get its shape
                var tempNext = new tetris.model.tetromino.Tetromino(nextType, 0, 0);
                nextShape = tempNext.shape();
            }
        }

        return new PureGame(width, height, cells, currentShape, nextShape);
    }
}