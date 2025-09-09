package tetris.controller.state;

import tetris.common.Action;
import tetris.common.GameState;
import tetris.controller.game.GameController;

public class PausedState implements PlayState {
    private final PlayState previousState; // Remember what state we paused from
    
    public PausedState() {
        this.previousState = null; // For backwards compatibility
    }
    
    public PausedState(PlayState previousState) {
        this.previousState = previousState;
    }
    
    @Override public void start(GameController c) { /* ignore while paused */ }
    @Override public void tick(GameController c)  { /* no game tick */ }
    @Override public void handle(GameController c, Action action) { /* ignore inputs while paused */ }
    @Override public void togglePause(GameController c) { 
        // Resume with correct player type state
        if (previousState != null) {
            c.setState(previousState);
        } else {
            // Fallback: recreate appropriate state based on player type
            c.setPlayerType(c.getPlayerType());
        }
    }
    @Override public void restart(GameController c) { 
        c.board().reset(); 
        c.setPlayerType(c.getPlayerType()); // Recreate correct state
        c.getState().start(c); 
    }
    @Override public void reset(GameController c) { c.board().reset(); }
    @Override public GameState uiState() { return GameState.PAUSE; }
}
