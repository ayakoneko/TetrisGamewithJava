package tetris.model;

/**
 * Tetromino : State falling Tetris piece currently
 * TetrominoType contains the static shape/rotation/color definitions.
 */
public class Tetromino {
    public TetrominoType type;  // piece type (I, O, T, S, Z, J, L)
    public int x, y;            // position
    public int rot;             // rotation state

    public Tetromino(TetrominoType type, int spawnX, int spawnY) {
        this.type = type;
        this.x = spawnX;
        this.y = spawnY;
        this.rot = 0;
    }

    public int[][] shape(){ return type.rot[rot]; }
    public int colorId(){ return type.colorId; }
}
