package tetris.controller.game;

import tetris.common.Action;
import tetris.common.GameState;
import tetris.controller.api.IGameController;
import tetris.controller.state.PlayState;
import tetris.controller.state.PlayingState;
import tetris.model.IGameBoard;

public class GameController implements IGameController {
    private final IGameBoard board;
    private PlayState state = new PlayingState();

    private int clearedLinesLastTick = 0;

    public GameController(IGameBoard board) {this.board = board;}

    public void setState(PlayState next) { this.state = next; }
    public PlayState getState() { return state; }

    public int getAndResetClearedLines() {
        int v = clearedLinesLastTick;
        clearedLinesLastTick = 0;
        return v;
    }

    public void setClearedLinesLastTick(int cleared) {
        this.clearedLinesLastTick = cleared;
    }

    @Override public IGameBoard board() {return board;}
    @Override public GameState state() {return state.uiState();}

    @Override public void handle(Action action) { state.handle(this, action); }
    @Override public void start() { state.start(this); }
    @Override public void togglePause() { state.togglePause(this); }
    @Override public void restart() { state.restart(this); }
    @Override public void reset() { state.reset(this); }
    @Override public void tick() { state.tick(this); }
}
