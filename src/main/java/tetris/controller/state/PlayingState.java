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
            // ★ 지워진 줄 수를 반환받는다
            int cleared = b.clearFullLines();     // ← int 반환으로 변경
            c.setClearedLinesLastTick(cleared);   // ★ 컨트롤러에 기록

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
                b.hardDrop();
                // ★ 하드드랍 후 라인 삭제 개수 기록
                int cleared = b.clearFullLines();   // ← int 반환
                c.setClearedLinesLastTick(cleared);
                if (!b.newPiece()) c.setState(new GameOverState());
            }
            case ROTATE_CW  -> b.rotateCW();
        }
    }

    @Override public void togglePause(GameController c) { c.setState(new PausedState()); }
    @Override public void restart(GameController c) { c.setState(new PlayingState()); c.board().reset(); start(c); }
    @Override public void reset(GameController c) { c.board().reset(); }  // keep playing after reset
    @Override public GameState uiState() { return GameState.PLAY; }
}
