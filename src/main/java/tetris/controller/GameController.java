package tetris.controller;

import tetris.model.GameBoard;
import tetris.model.IGameBoard;

/**
 * GameController: Game flow management. GameView - Controller - data handling(GameBoard)
 */
public class GameController implements IGameController{
    private final IGameBoard board;
    private State state = State.PLAY;

    public GameController(IGameBoard board) {this.board = board;}

    public GameController() {this(new GameBoard());}

    @Override
    public IGameBoard board() {return board;}

    @Override
    public State state() {
        return state;
    }
    @Override
    public void start(){
        state = State.PLAY;
        if (!board.newPiece()) state = State.GAME_OVER;
    }

    @Override
    public void tick(){
        if (state != State.PLAY) return;

        if (!board.softDropStep()) {
            if (!board.lockCurrent()) {
                state = State.GAME_OVER;
                return;
            }
            board.clearFullLines();
            if (!board.newPiece()) state = State.GAME_OVER;
        }
    }

    // Key handling (arrow keys, Esc, Space bar, Restart, Pause)
    @Override public void moveLeft(){ if (state==State.PLAY) board.moveLeft(); }
    @Override public void moveRight(){ if (state==State.PLAY) board.moveRight(); }
    @Override public void rotateCW(){ if (state==State.PLAY) board.rotateCW(); }
    @Override public void softDrop(){ if (state==State.PLAY) board.softDropStep(); }

    @Override
    public void hardDrop() {
        if (state != State.PLAY) return;
        board.hardDrop();
        board.clearFullLines();
        if (!board.newPiece()) state = State.GAME_OVER;
    }

    @Override
    public void togglePause(){
        if (state==State.PLAY) state = State.PAUSE;
        else if (state==State.PAUSE) state = State.PLAY;
    }

    @Override
    public void restart() {
        state = State.PLAY;
        board.reset();
        if (!board.newPiece()) state = State.GAME_OVER;
    }

    @Override
    public void reset() {
        board.reset();
        state = State.PLAY;
    }
}
