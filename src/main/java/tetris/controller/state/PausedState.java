package tetris.controller.state;

import tetris.common.Action;
import tetris.common.UiGameState;
import tetris.controller.game.GameController;

public class PausedState implements PlayState {
    private final PlayState previousState;     // state to resume
    private final PlayStateFactory factory;    // factory to reconstruct/resume

    public PausedState(PlayState previousState, PlayStateFactory factory) {
        this.previousState = previousState;
        this.factory = factory;
    }

    @Override public void start(GameController c) { /* no-op while paused */ }
    @Override public void tick(GameController c)  { /* no-op while paused */ }
    @Override public void handle(GameController c, Action action) { /* ignore inputs while paused */ }

    @Override
    public void togglePause(GameController c) {
        if (previousState != null && factory != null) {
            c.setState(factory.resume(c.getGameSetting(), previousState));
        } else {
            c.setPlayerType(c.getPlayerType());
        }
    }

    @Override
    public void restart(GameController c) {
        c.board().reset();
        c.setPlayerType(c.getPlayerType());
        c.getState().start(c);
    }

    @Override public void reset(GameController c) { c.board().reset(); }
    @Override public UiGameState uiState() { return UiGameState.PAUSE; }
}
