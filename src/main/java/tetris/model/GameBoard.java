package tetris.model;

import java.util.Random;

/**
 * GameBoard : Tetris playfield and manages all game logic
 *  newPiece()       : Spawn a new tetromino; returns false if spawn is blocked.
 *  canMove()        : Check if a tetromino can move/rotate without collisions.
 *  moveLeft/Right() : Move the current piece horizontally.
 *  rotateCW()       : Rotate the current piece clockwise.
 *  softDropStep()   : Move the piece down one cell.
 *  hardDrop()       : Drop the piece to the bottom.
 *  lockCurrent()    : Fix the current piece into the board; returns false if overflow occurred.
 *  clearFullLines() : Remove any complete lines and shift above lines down.
 *  reset()          : Clear the board and start with a new piece.
 */
public class GameBoard {
    public static final int W = 10, H = 20;         // Width and height of the board
    private final int[][] board = new int[H][W];

    private final Random rnd = new Random();        // create random block
    private Tetromino current;                      // Currently falling block

    public int[][] cells(){ return board; }
    public Tetromino current(){ return current; }

    public boolean newPiece() {
        TetrominoType[] tt = TetrominoType.values();
        TetrominoType t = tt[rnd.nextInt(tt.length)];
        Tetromino next = new Tetromino(t, (W/2)-2, -2);

        if (!canMove(next, 0, 0, next.rot)) {
            current = null;
            return false;
        }
        current = next;
        return true;
    }

    public boolean canMove(Tetromino t, int dx, int dy, int newRot){
        int[][] s = t.type.rot[newRot];
        for (int r=0;r<4;r++){
            for (int c=0;c<4;c++){
                if (s[r][c]==0) continue;
                int nx = t.x() + c + dx;
                int ny = t.y() + r + dy;

                // Check left, right end and bottom
                if (nx < 0 || nx >= W) return false;
                if (ny >= H) return false;

                // Allow blocks to start point
                if (ny < 0) continue;

                // Board and cell collision
                if (board[ny][nx] != 0) return false;
            }
        }
        return true;
    }

    public void moveLeft(){
        if (current!=null && canMove(current,-1,0,current.rot))
            current.moveBy(-1,0);
    }
    public void moveRight(){
        if (current!=null && canMove(current, +1,0,current.rot))
            current.moveBy(1,0);
    }
    public void rotateCW(){
        if (current==null) return;
        int nr = (current.rot+1) & 3;                            // current.rot+1 : rotate block 90° clockwise
        if (canMove(current, 0,0, nr)) current.rot = nr; // Check rotated block is not reach to the end
    }
    public boolean softDropStep(){
        if (current==null) return false;
        if (canMove(current,0,1,current.rot)) {
            current.moveBy(0,1); return true;
        }
        return false;
    }
    public void hardDrop() {
        if (current == null) return;
        while (canMove(current, 0, 1, current.rot)) current.moveBy(0,1);
        lockCurrent();
    }

    /** Locks the current tetromino into the board grid */
    public boolean lockCurrent(){
        if (current==null) return false;
        boolean overflow = false;
        int[][] s = current.shape();
        for (int r=0;r<4;r++){
            for (int c=0;c<4;c++){
                if (s[r][c]==0) continue;
                int bx = current.x() + c, by = current.y() + r;
                if (by < 0) { overflow = true; continue; }
                if (by < H && bx >= 0 && bx < W) board[by][bx] = current.colorId();
            }
        }
        current = null;
        return !overflow;
    }

    /** Removes full lines from the board and shifts lines above down */
    public int clearFullLines(){
        int write = H-1, cleared = 0;
        for (int read = H-1; read>=0; read--){
            boolean full = true;
            for (int x=0;x<W;x++){ if (board[read][x]==0){ full=false; break; } }
            if (!full){
                if (write != read) System.arraycopy(board[read],0,board[write],0,W);
                write--;
            } else {
                cleared++;
            }
        }
        for (int y=write; y>=0; y--){
            for (int x=0;x<W;x++) board[y][x]=0;
        }
        return cleared;
    }

    /** Game Restart */
    public void reset() {
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) board[y][x] = 0;
        }
        current = null;
        newPiece();
    }
}
