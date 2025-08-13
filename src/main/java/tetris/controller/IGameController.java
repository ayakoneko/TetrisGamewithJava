package tetris.controller;

import tetris.model.GameBoard;

public interface IGameController {
    enum State { PLAY, PAUSE, GAME_OVER }

    GameBoard board();
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