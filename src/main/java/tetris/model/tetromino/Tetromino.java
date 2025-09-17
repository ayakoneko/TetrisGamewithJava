package tetris.model.tetromino;

import tetris.model.board.Position;

/**
 * Tetromino : State falling Tetris piece currently
 * TetrominoType contains the static shape/rotation/color definitions.
 */
public class Tetromino {
    public TetrominoType type;  // piece type (I, O, T, S, Z, J, L)
    public Position pos;            // position
    public int rot;             // rotation state

    public Tetromino(TetrominoType type, int spawnX, int spawnY) {
        this.type = type;
        this.pos  = new Position(spawnX, spawnY);
        this.rot = 0;
    }

    public int[][] shape(){ return type.rot[rot]; }
    public int colorId(){ return type.colorId; }

    public int x() { return pos.x(); }
    public int y() { return pos.y(); }

    public void setPos(int x, int y) { this.pos = new Position(x, y); }
    public void moveBy(int dx, int dy) { this.pos = new Position(pos.x() + dx, pos.y() + dy); }

}