package tetris.controller;

import tetris.model.IGameBoard;

public interface IGameController {
    enum State { PLAY, PAUSE, GAME_OVER }

    IGameBoard board();
    State state();

    void start();
    void tick();
    void restart();
    void reset();
    void togglePause();

    void moveLeft();
    void moveRight();
    void rotateCW();
    void softDrop();
    void hardDrop();
}