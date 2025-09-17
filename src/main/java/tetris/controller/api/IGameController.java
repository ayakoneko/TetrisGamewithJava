package tetris.controller.api;

import tetris.common.Action;
import tetris.common.UiGameState;
import tetris.model.IGameBoard;

public interface IGameController {

    IGameBoard board();
    UiGameState state();

    void start();
    void tick();
    void restart();
    void reset();
    void togglePause();
    void handle(Action action);
}
