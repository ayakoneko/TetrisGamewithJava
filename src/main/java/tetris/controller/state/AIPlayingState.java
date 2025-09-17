package tetris.controller.state;

import tetris.controller.ai.AIController;
import tetris.common.Action;
import tetris.common.UiGameState; // or tetris.ui.UiGameState if you placed it there
import tetris.controller.game.GameController;
import tetris.model.board.IGameBoard;
import tetris.model.setting.GameSetting;

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

    private final AIController aiController = new AIController();
    private int aiTickCounter = 0;

    private static final int AI_SPEED_NORMAL = 2;
    private static final int AI_SPEED_FAST = 1;

    private final GameSetting gameSetting;

    public AIPlayingState(GameSetting gameSetting) {
        this.gameSetting = gameSetting;
    }

    @Override
    public void start(GameController c) {
        IGameBoard b = c.board();
        aiController.reset();

        if (!b.newPiece()) {
            c.setState(new GameOverState());
        } else {
            aiController.planNextMove(c);
        }
    }

    @Override
    public void tick(GameController c) {
        IGameBoard b = c.board();

        // Always run AI in this state
        handleAITick(c);

        if (!b.softDropStep()) {
            aiController.onPiecePlaced();

            if (!b.lockCurrent()) {
                c.setState(new GameOverState());
                return;
            }

            int cleared = b.clearFullLines();
            c.setClearedLinesLastTick(cleared);

            if (!b.newPiece()) {
                c.setState(new GameOverState());
            } else {
                aiController.planNextMove(c);
            }
        }
    }

    private void handleAITick(GameController c) {
        aiController.planNextMove(c);

        int aiSpeed = getAISpeed();
        if (++aiTickCounter >= aiSpeed) {
            Action aiAction = aiController.getNextAction(c);
            if (aiAction != null) {
                handle(c, aiAction);
            }
            aiTickCounter = 0;
        }
    }

    private int getAISpeed() {
        if (gameSetting != null && gameSetting.getLevel() >= 8) {
            return AI_SPEED_FAST;
        }
        return AI_SPEED_NORMAL;
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
                aiController.onPiecePlaced();

                int cleared = b.clearFullLines();
                c.setClearedLinesLastTick(cleared);

                if (!b.newPiece()) {
                    c.setState(new GameOverState());
                } else {
                    aiController.planNextMove(c);
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
        aiController.reset();
        c.setState(new AIPlayingState(gameSetting));
        c.board().reset();
        start(c);
    }

    @Override
    public void reset(GameController c) {
        aiController.reset();
        c.board().reset();
    }

    @Override
    public UiGameState uiState() {
        return UiGameState.PLAY;
    }
}
