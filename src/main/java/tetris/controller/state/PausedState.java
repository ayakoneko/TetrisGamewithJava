package tetris.controller.state;

import tetris.common.Action;
import tetris.common.GameState;
import tetris.controller.game.GameController;

public class PausedState implements PlayState {
    @Override public void start(GameController c) { /* ignore while paused */ }
    @Override public void tick(GameController c)  { /* no game tick */ }
    @Override public void handle(GameController c, Action action) { /* ignore inputs while paused */ }
    @Override public void togglePause(GameController c) { c.setState(new PlayingState()); }
    @Override public void restart(GameController c) { c.setState(new PlayingState()); c.board().reset(); c.getState().start(c); }
    @Override public void reset(GameController c) { c.board().reset(); }
    @Override public GameState uiState() { return GameState.PAUSE; }
}
