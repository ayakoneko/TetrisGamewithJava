package tetris.model.board;

import tetris.model.tetromino.Tetromino;

/**
 * IGameBoard: abstraction for the Tetris playfield & core game logic.
 *
 * Typical usage:
 *   - newPiece()         : Spawn a new tetromino; return false if spawn blocked.
 *   - canMove(...)       : Collision check for tentative moves/rotations.
 *   - moveLeft/Right()   : Horizontal movement for the current piece.
 *   - rotateCW()         : Rotate the current piece clockwise.
 *   - softDropStep()     : One-row soft drop step.
 *   - hardDrop()         : Drop to bottom and lock.
 *   - lockCurrent()      : Fix current piece into the grid; false if overflow.
 *   - clearFullLines()   : Clear completed lines; return count.
 *   - reset()            : Clear the board and start a new piece.
 */

public interface IGameBoard {
    /** Read-only view of board cells (H x W). 0 = empty; >0 = colorId/block. */
    int[][] cells();

    /** Currently falling tetromino or null if none. */
    Tetromino current();

    /** Dimensions so callers don’t depend on concrete constants. */
    int getWidth();
    int getHeight();

    boolean newPiece();

    boolean canMove(Tetromino t, int dx, int dy, int newRot);

    void moveLeft();
    void moveRight();
    void rotateCW();

    boolean softDropStep();
    void hardDrop();

    boolean lockCurrent();

    int clearFullLines();

    void reset();
}
