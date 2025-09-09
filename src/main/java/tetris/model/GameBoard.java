package tetris.model;

import tetris.util.BoardUtils;
import java.util.Random;

public class GameBoard implements IGameBoard {
    private final int width, height;
    private final int[][] board;
    private final Random rnd = new Random(); // create random block
    private Tetromino current; // Currently falling block

    // Gameboard Constructor with given width/height
    public GameBoard(int width, int height) {
        this.width  = width;
        this.height = height;
        this.board  = new int[height][width];
    }

    @Override public int[][] cells(){ return board; }
    @Override public Tetromino current(){ return current; }
    @Override public int getWidth()  { return width; }
    @Override public int getHeight() { return height; }

    @Override
    public boolean newPiece() {
        TetrominoType[] tt = TetrominoType.values();
        TetrominoType t = tt[rnd.nextInt(tt.length)];
        Tetromino next = new Tetromino(t, (width/2)-2, -2);

        if (!canMove(next, 0, 0, next.rot)) {
            current = null;
            return false;
        }
        current = next;
        return true;
    }

    @Override
    public boolean canMove(Tetromino t, int dx, int dy, int newRot){
        // Delegate to centralized utility - single source of truth for collision logic
        return BoardUtils.canMovePiece(board, t, dx, dy, newRot);
    }

    @Override
    public void moveLeft(){
        if (current!=null && canMove(current,-1,0,current.rot))
            current.moveBy(-1,0);
    }

    @Override
    public void moveRight(){
        if (current!=null && canMove(current, +1,0,current.rot))
            current.moveBy(1,0);
    }

    @Override
    public void rotateCW(){
        if (current==null) return;
        int nr = (current.rot+1) & 3;                            // current.rot+1 : rotate block 90° clockwise
        if (canMove(current, 0,0, nr)) current.rot = nr; // Check rotated block is not reach to the end
    }

    @Override
    public boolean softDropStep(){
        if (current==null) return false;
        if (canMove(current,0,1,current.rot)) {
            current.moveBy(0,1); return true;
        }
        return false;
    }

    @Override
    public void hardDrop() {
        if (current == null) return;
        // Drop piece to bottom position - let game state handle locking
        while (canMove(current, 0, 1, current.rot)) current.moveBy(0,1);
        // Don't lock here - let the game state handle locking and line clearing consistently
    }

    /** Locks the current tetromino into the board grid */
    @Override
    public boolean lockCurrent(){
        if (current==null) return false;
        
        // Check for overflow before locking (any part above board)
        boolean overflow = false;
        int[][] s = current.shape();
        for (int r=0;r<4;r++){
            for (int c=0;c<4;c++){
                if (s[r][c]==0) continue;
                int by = current.y() + r;
                if (by < 0) { 
                    overflow = true; 
                    break; 
                }
            }
            if (overflow) break;
        }
        
        // Use centralized locking logic
        BoardUtils.lockPieceIntoBoard(board, current);
        current = null;
        
        return !overflow;
    }

    /** Removes full lines from the board and shifts lines above down */
    @Override
    public int clearFullLines(){
        // Delegate to centralized utility - single source of truth for line clearing
        return BoardUtils.clearFullLines(board);
    }

    /** Game Restart */
    @Override
    public void reset() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) board[y][x] = 0;
        }
        current = null;
        newPiece();
    }
}
