package tetris.controller.event;

import tetris.common.Action;
import tetris.common.ConfigManager;
import tetris.common.UiGameState;
import tetris.controller.audio.AudioController;
import tetris.controller.game.GameController;
import tetris.dto.GameSettingsData;
import tetris.dto.GameStateData;
import tetris.dto.TetrominoData;
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
    private String playerName = "Player"; // Default player name
    private int playerNumber = 1; // Player number (1 or 2)

    public GameEventHandler(GameController gameController, GameSetting settings) {
        this.gameController = gameController;
        this.settings = settings;
        this.audioController = new AudioController();
    }

    public GameEventHandler(GameController gameController, GameSetting settings, int playerNumber) {
        this.gameController = gameController;
        this.settings = settings;
        this.audioController = new AudioController();
        this.playerNumber = playerNumber;
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

    // Player name management
    public void setPlayerName(String name) {
        this.playerName = (name == null || name.trim().isEmpty()) ? "Player" : name.trim();
    }

    public String getPlayerName() {
        // Return the actual player name (entered by user or default)
        return playerName;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    // Score-related methods for high score management
    public boolean isEligibleForHighScore(int score) {
        return gameController.isEligibleForHighScore(score);
    }


    // Submit score with stored player name
    public boolean submitStoredScore() {
        int currentScore = gameController.getCurrentScore();
        // Only submit if score is non-zero and eligible for high scores
        if (currentScore > 0 && isEligibleForHighScore(currentScore)) {
            return gameController.submitFinalScore(playerName);
        }
        return false;
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

    // External player status methods
    public boolean isExternalPlayer() {
        return gameController.getPlayerType() == tetris.model.setting.PlayerType.EXTERNAL;
    }

    public boolean isExternalServerAvailable() {
        if (!isExternalPlayer()) return false;

        // Get server status from the current game state
        tetris.controller.state.PlayState currentState = gameController.getState();
        if (currentState instanceof tetris.controller.state.ExternalPlayingState externalState) {
            return externalState.isServerAvailable();
        }
        return false;
    }

    // Get player type display string for HUD
    public String getPlayerTypeDisplay() {
        return switch (gameController.getPlayerType()) {
            case HUMAN -> "HUMAN";
            case AI -> "AI";
            case EXTERNAL -> isExternalServerAvailable() ? "EXTERNAL" : "EXTERNAL (NO SERVER)";
        };
    }

    // Get current level and lines cleared
    public int getCurrentLevel() {
        return gameController.getCurrentLevel();
    }

    public int getTotalLinesCleared() {
        return gameController.getTotalLinesCleared();
    }

    public int getCurrentScore() {
        return gameController.getCurrentScore();
    }

    public tetris.model.tetromino.TetrominoType getNextTetrominoType() {
        if (gameController.board() instanceof tetris.model.board.GameBoard gameBoard) {
            return gameBoard.getNextTetrominoType();
        }
        return null;
    }

    // Get current audio state for real-time HUD updates
    public boolean isMusicOn() {
        return settings.isMusicOn();
    }

    public boolean isSfxOn() {
        return settings.isSfxOn();
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
    

}