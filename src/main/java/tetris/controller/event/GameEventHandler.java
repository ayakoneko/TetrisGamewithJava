package tetris.controller.event;

import tetris.common.Action;
import tetris.common.UiGameState;
import tetris.controller.audio.AudioController;
import tetris.controller.game.GameController;
import tetris.dto.GameSettingsData;
import tetris.dto.GameStateData;
import tetris.dto.TetrominoData;
import tetris.common.ConfigManager;
import tetris.model.setting.GameSetting;

/**
 * GameEventHandler provides a clean interface between Views and Controllers.
 * Views should use this to communicate with the game system rather than
 * directly manipulating controllers or models.
 * <p>
 * This class eliminates direct View-Model coupling by providing DTOs.
 */
public class GameEventHandler {

    private final GameController gameController;
    private final GameSetting settings;
    private final AudioController audioController;

    public GameEventHandler(GameController gameController, GameSetting settings) {
        this.gameController = gameController;
        this.settings = settings;
        this.audioController = new AudioController();
    }

    // Game control events
    public void startGame() {
        gameController.start();
    }

    public void pauseGame() {
        gameController.togglePause();
    }

    public void restartGame() {
        if (gameController.state() == UiGameState.GAME_OVER) {
            gameController.restart();
        }
    }

    public void resetGame() {
        gameController.reset();
    }

    public void handlePlayerAction(Action action) {
        gameController.handle(action);
    }

    public void tick() {
        gameController.tick();
    }

    // Audio events - properly delegated to AudioController
    public void toggleMusic() {
        boolean newValue = !settings.isMusicOn();
        settings.setMusicOn(newValue);
        audioController.setBackgroundMusicEnabled(newValue, "background.mp3");
        ConfigManager.save(settings);
    }

    public void toggleSfx() {
        boolean newValue = !settings.isSfxOn();
        settings.setSfxOn(newValue);
        ConfigManager.save(settings);
    }

    public void playMoveTurnSound() {
        if (settings.isSfxOn()) {
            audioController.playMoveTurnSound();
        }
    }

    public void playLineClearSound() {
        if (settings.isSfxOn()) {
            audioController.playLineClearSound();
        }
    }

    // Data access for Views - returns DTOs instead of direct model access
    public GameStateData getGameStateData() {
        TetrominoData currentPiece = TetrominoData.fromTetrominoSafe(
                gameController.board().current()
        );

        UiGameState uiState = gameController.state();

        return GameStateData.create(
                gameController.board().cells(),
                currentPiece,
                uiState,
                gameController.getCurrentScore(),
                settings.isMusicOn(),
                settings.isSfxOn()
        );
    }

    public GameSettingsData getGameSettingsData() {
        return GameSettingsData.fromGameSetting(settings);
    }

    public int getBoardWidth() {
        return gameController.board().getWidth();
    }

    public int getBoardHeight() {
        return gameController.board().getHeight();
    }
    
    // Check if AI is currently active
    public boolean isAIActive() {
        return gameController.getPlayerType() == tetris.model.setting.PlayerType.AI;
    }

    // Audio control for Views
    public void startBackgroundMusic() {
        if (settings.isMusicOn()) {
            audioController.playBackgroundMusic("background.mp3");
        }
    }
    
    public void stopBackgroundMusic() {
        audioController.stopBackgroundMusic();
    }
    
    // Save current score to high score manager
    public void saveCurrentScore() {
        int currentScore = gameController.getCurrentScore();
        if (currentScore > 0) {
            gameController.submitFinalScore("Player");
        }
    }

}