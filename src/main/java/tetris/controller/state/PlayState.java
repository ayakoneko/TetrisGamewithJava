package tetris.controller.state;

import tetris.common.Action;
import tetris.common.UiGameState;
import tetris.controller.game.GameController;

public interface PlayState {
    // lifecycle / loop
    void start(GameController c);
    void tick(GameController c);
    void handle(GameController c, Action action);
    void togglePause(GameController c);
    void restart(GameController c);
    void reset(GameController c);

    UiGameState uiState();
}
