package tetris.model.tetromino;

/**
 * TetrominoType : Defining tetris pieces
 * For each type, stores : color ID, rotation states (0°, 90°, 180°, 270°)
 */
public enum TetrominoType {
    I(1, new int[][]{
        {0,0,0,0},
        {1,1,1,1},
        {0,0,0,0},
        {0,0,0,0}
    }),
    O(2, new int[][]{
        {0,1,1,0},
        {0,1,1,0},
        {0,0,0,0},
        {0,0,0,0}
    }),
    T(3, new int[][]{
        {0,1,0,0},
        {1,1,1,0},
        {0,0,0,0},
        {0,0,0,0}
    }),
    S(4, new int[][]{
        {0,1,1,0},
        {1,1,0,0},
        {0,0,0,0},
        {0,0,0,0}
    }),
    Z(5, new int[][]{
        {1,1,0,0},
        {0,1,1,0},
        {0,0,0,0},
        {0,0,0,0}
    }),
    J(6, new int[][]{
        {1,0,0,0},
        {1,1,1,0},
        {0,0,0,0},
        {0,0,0,0}
    }),
    L(7, new int[][]{
        {0,0,1,0},
        {1,1,1,0},
        {0,0,0,0},
        {0,0,0,0}
    });

    public final int colorId;
    // rot[rotationIndex][row-4][column-4]
    public final int[][][] rot;

    TetrominoType(int colorId, int[][] base0) {
        this.colorId = colorId;
        this.rot = new int[4][4][4]; //create a rotation state of 4 (0/90/180/270) and each 4x4 grid
        this.rot[0] = base0;
        for (int r = 1; r < 4; r++) {
            this.rot[r] = rotateCW(this.rot[r-1]);
        }
    }

    // rotate block clockwise
    private static int[][] rotateCW(int[][] m){
        int[][] out = new int[4][4];
        for (int y=0;y<4;y++)
            for (int x=0;x<4;x++)
                out[x][3-y] = m[y][x];
        return out;
    }
}
