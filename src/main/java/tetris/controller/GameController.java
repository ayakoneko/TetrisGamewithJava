package tetris.controller;

import tetris.model.GameBoard;
/**
 * GameController: Game flow management. GameView - Controller - data handling(GameBoard)
 */
public class GameController {
    public enum State { PLAY, PAUSE, GAME_OVER }
    private final GameBoard board = new GameBoard();
    private State state = State.PLAY;

    public GameBoard board(){ return board; }
    public State state(){ return state; }

    public void start(){
        state = State.PLAY;
        if (!board.newPiece()) state = State.GAME_OVER;
    }

    /** Handle the main game loop
     *  Current piece down each tick*/
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
    public void moveLeft(){ if (state==State.PLAY) board.moveLeft(); }
    public void moveRight(){ if (state==State.PLAY) board.moveRight(); }
    public void rotateCW(){ if (state==State.PLAY) board.rotateCW(); }
    public void softDrop(){ if (state==State.PLAY) board.softDropStep(); }
    public void hardDrop() {
        if (state != State.PLAY) return;
        board.hardDrop();
        int cleared = board.clearFullLines();
        if (!board.newPiece()) state = State.GAME_OVER;
    }

    public void togglePause(){
        if (state==State.PLAY) state = State.PAUSE;
        else if (state==State.PAUSE) state = State.PLAY;
    }

    public void restart() {
        state = State.PLAY;
        board.reset();
        if (!board.newPiece()) state = State.GAME_OVER;
    }

    public void reset() {
        board.reset();
        state = State.PLAY;
    }
}
