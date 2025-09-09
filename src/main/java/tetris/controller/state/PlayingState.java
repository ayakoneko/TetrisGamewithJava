package tetris.controller.state;

import tetris.common.Action;
import tetris.common.GameState;
import tetris.controller.game.GameController;
import tetris.model.IGameBoard;

public class PlayingState implements PlayState {

    @Override public void start(GameController c) {
        IGameBoard b = c.board();
        if (!b.newPiece()) c.setState(new GameOverState());
    }

    @Override public void tick(GameController c) {
        IGameBoard b = c.board();

        if (!b.softDropStep()) {
            if (!b.lockCurrent()) {
                c.setState(new GameOverState());
                return;
            }
            // Clear full lines and record count for scoring
            int cleared = b.clearFullLines();
            c.setClearedLinesLastTick(cleared);
            if (!b.newPiece()) c.setState(new GameOverState());
        }
    }

    @Override public void handle(GameController c, Action action) {
        IGameBoard b = c.board();
        switch (action) {
            case MOVE_LEFT  -> b.moveLeft();
            case MOVE_RIGHT -> b.moveRight();
            case SOFT_DROP  -> b.softDropStep();
            case HARD_DROP  -> {
                b.hardDrop(); // Drop piece to bottom
                // Now handle locking and line clearing consistently with normal tick
                if (!b.lockCurrent()) {
                    c.setState(new GameOverState());
                    return;
                }
                int cleared = b.clearFullLines();
                c.setClearedLinesLastTick(cleared);
                if (!b.newPiece()) c.setState(new GameOverState());
            }
            case ROTATE_CW  -> b.rotateCW();
        }
    }

    @Override public void togglePause(GameController c) { c.setState(new PausedState(this)); }
    @Override public void restart(GameController c) { c.setState(new PlayingState()); c.board().reset(); start(c); }
    @Override public void reset(GameController c) { c.board().reset(); }  // keep playing after reset
    @Override public GameState uiState() { return GameState.PLAY; }
}
