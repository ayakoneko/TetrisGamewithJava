package tetris.model.external;

/**
 * OpMove: Represents the optimal move response from TetrisServer.
 *
 * Contains the optimal X position and number of rotations for the current tetromino.
 * - opX = 0 means place at left-most position
 * - opRotate = 0 means no rotation needed
 */
public record OpMove(int opX, int opRotate) {
}