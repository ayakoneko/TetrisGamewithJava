package tetris.controller.state;

import tetris.common.Action;
import tetris.common.UiGameState;
import tetris.controller.game.GameController;

public class GameOverState implements PlayState {
    @Override public void start(GameController c) { 
        // Game over state - score submission will be handled by GameView
        // using the player name that was collected before the game started
    }

    @Override public void tick(GameController c)  { /* no tick */ }

    @Override public void handle(GameController c, Action action) { /* ignore */ }

    @Override public void togglePause(GameController c) { /* no-op */ }

    @Override public void restart(GameController c) {
        c.board().reset();
        // Restore the correct player type state
        c.setPlayerType(c.getPlayerType()); // This recreates the appropriate state
        c.getState().start(c);
    }

    @Override public void reset(GameController c) { c.board().reset(); }

    @Override public UiGameState uiState() { return UiGameState.GAME_OVER; }
}