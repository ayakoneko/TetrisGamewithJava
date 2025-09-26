package tetris.controller.state;

import tetris.common.Action;
import tetris.common.UiGameState;
import tetris.controller.external.ExternalController;
import tetris.controller.game.GameController;
import tetris.model.board.IGameBoard;
import tetris.model.setting.GameSetting;

/**
 * ExternalPlayingState handles game logic when an external TetrisServer is controlling the game.
 *
 * This state extends the standard playing state with external server communication:
 * - Integrates ExternalController for server communication and move execution
 * - Handles connection failures gracefully (shows warning, no control)
 * - Resumes control immediately when server becomes available mid-game
 * - Manages timing to make server moves visible and responsive
 * - Supports fallback to manual control when server unavailable
 *
 * Requirements fulfilled:
 * - Shows warning when server not running + no control
 * - Resumes control immediately when server starts mid-game
 */
public class ExternalPlayingState implements PlayState {

    private final ExternalController externalController = new ExternalController();
    private int externalTickCounter = 0;

    // Timing constants for external player responsiveness
    private static final int EXTERNAL_SPEED_NORMAL = 3;  // Slightly slower than AI to see moves
    private static final int EXTERNAL_SPEED_FAST = 2;

    private final GameSetting gameSetting;

    public ExternalPlayingState(GameSetting gameSetting) {
        this.gameSetting = gameSetting;
    }

    @Override
    public void start(GameController c) {
        IGameBoard b = c.board();
        externalController.reset();

        if (!b.newPiece()) {
            c.setState(new GameOverState());
        } else {
            externalController.planNextMove(c);
        }
    }

    @Override
    public void tick(GameController c) {
        IGameBoard b = c.board();

        // Handle external server communication and control
        handleExternalTick(c);

        // ONLY allow piece dropping when server is available
        if (!externalController.isServerAvailable()) {
            // Server unavailable - block all game progression
            return;
        }

        // Standard piece dropping and locking logic (only when server available)
        if (!b.softDropStep()) {
            externalController.onPiecePlaced();

            if (!b.lockCurrent()) {
                c.setState(new GameOverState());
                return;
            }

            int cleared = b.clearFullLines();
            c.setClearedLinesLastTick(cleared);

            if (!b.newPiece()) {
                c.setState(new GameOverState());
            } else {
                externalController.planNextMove(c);
            }
        }
    }

    /**
     * Handles external server communication and move execution.
     * Only executes moves when server is available.
     */
    private void handleExternalTick(GameController c) {
        // Always try to plan (this handles server reconnection)
        externalController.planNextMove(c);

        // Only execute moves if server is available
        if (externalController.isServerAvailable()) {
            int externalSpeed = getExternalSpeed();
            if (++externalTickCounter >= externalSpeed) {
                Action externalAction = externalController.getNextAction(c);
                if (externalAction != null) {
                    handle(c, externalAction);
                }
                externalTickCounter = 0;
            }
        } else {
            // Reset tick counter when server unavailable to avoid delayed actions
            externalTickCounter = 0;
        }
    }

    /**
     * Gets appropriate speed for external player based on game level.
     */
    private int getExternalSpeed() {
        if (gameSetting != null && gameSetting.getLevel() >= 8) {
            return EXTERNAL_SPEED_FAST;
        }
        return EXTERNAL_SPEED_NORMAL;
    }

    @Override
    public void handle(GameController c, Action action) {
        IGameBoard b = c.board();
        switch (action) {
            case MOVE_LEFT  -> b.moveLeft();
            case MOVE_RIGHT -> b.moveRight();
            case SOFT_DROP  -> b.softDropStep();
            case HARD_DROP  -> {
                b.hardDrop();
                if (!b.lockCurrent()) {
                    c.setState(new GameOverState());
                    return;
                }
                externalController.onPiecePlaced();

                int cleared = b.clearFullLines();
                c.setClearedLinesLastTick(cleared);

                if (!b.newPiece()) {
                    c.setState(new GameOverState());
                } else {
                    externalController.planNextMove(c);
                }
            }
            case ROTATE_CW  -> b.rotateCW();
        }
    }

    @Override
    public void togglePause(GameController c) {
        c.setState(new PausedState(this, c.getStateFactory()));
    }

    @Override
    public void restart(GameController c) {
        externalController.reset();
        c.setState(new ExternalPlayingState(gameSetting));
        c.board().reset();
        start(c);
    }

    @Override
    public void reset(GameController c) {
        externalController.reset();
        c.board().reset();
    }

    @Override
    public UiGameState uiState() {
        return UiGameState.PLAY;
    }

    /**
     * Returns whether the external server is currently available.
     * Used for UI status display.
     */
    public boolean isServerAvailable() {
        return externalController.isServerAvailable();
    }

    /**
     * Returns whether currently executing an external move.
     */
    public boolean isExecutingMove() {
        return externalController.isExecuting();
    }
}