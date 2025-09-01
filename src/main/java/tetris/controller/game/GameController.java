package tetris.controller.game;

import tetris.common.Action;
import tetris.common.GameState;
import tetris.controller.api.IGameController;
import tetris.model.IGameBoard;

/**
 * GameController: Game flow management. GameView - Controller - data handling(GameBoard)
 */
public class GameController implements IGameController {
    private final IGameBoard board;
    private GameState state = GameState.PLAY;

    public GameController(IGameBoard board) {this.board = board;}

    @Override
    public IGameBoard board() {return board;}

    @Override
    public GameState state() {
        return state;
    }

    @Override
    public void handle(Action action) {
        if (state != GameState.PLAY) return;

        switch (action) {
            case MOVE_LEFT   -> board.moveLeft();
            case MOVE_RIGHT  -> board.moveRight();
            case SOFT_DROP   -> board.softDropStep();
            case HARD_DROP   -> hardDrop();
            case ROTATE_CW   -> board.rotateCW();
        }
    }

    @Override
    public void start(){
        state = GameState.PLAY;
        if (!board.newPiece()) state = GameState.GAME_OVER;
    }

    @Override
    public void togglePause(){
        if (state==GameState.PLAY) state = GameState.PAUSE;
        else if (state==GameState.PAUSE) state = GameState.PLAY;
    }

    @Override
    public void restart() {
        state = GameState.PLAY;
        board.reset();
        if (!board.newPiece()) state = GameState.GAME_OVER;
    }

    @Override
    public void reset() {
        board.reset();
        state = GameState.PLAY;
    }

    @Override
    public void tick(){
        if (state != GameState.PLAY) return;

        if (!board.softDropStep()) {
            if (!board.lockCurrent()) {
                state = GameState.GAME_OVER;
                return;
            }
            board.clearFullLines();
            if (!board.newPiece()) state = GameState.GAME_OVER;
        }
    }

    public void hardDrop() {
        if (state != GameState.PLAY) return;
        board.hardDrop();
        board.clearFullLines();
        if (!board.newPiece()) state = GameState.GAME_OVER;
    }
}
