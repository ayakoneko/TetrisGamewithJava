package tetris.factory;

import tetris.controller.config.ConfigurationController;
import tetris.controller.event.GameEventHandler;
import tetris.controller.game.GameController;
import tetris.controller.score.ScoreController;
import tetris.model.GameBoard;
import tetris.dto.GameSettingsData;
import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;
import tetris.view.GameView;
import tetris.view.Configuration;
import tetris.view.HighScore;
import javafx.stage.Stage;

public class GameFactory {
    
    // Creates a new GameController with the specified settings
    public static GameController createGameController(GameSetting settings) {
        GameBoard board = new GameBoard(settings.getFieldWidth(), settings.getFieldHeight());
        return new GameController(board, settings, settings.getPlayerOneType());
    }
    
    // Creates a new ConfigurationController for settings management
    public static ConfigurationController createConfigurationController(GameSetting settings) {
        return new ConfigurationController(settings);
    }
    
    // Creates a new GameEventHandler for View-Controller communication
    public static GameEventHandler createGameEventHandler(GameController gameController, GameSetting settings) {
        return new GameEventHandler(gameController, settings);
    }
    
    // Creates a new GameView with proper event handling
    public static GameView createGameView(Stage stage, GameEventHandler eventHandler, 
                                        GameSetting settings, Runnable onExitToMenu) {
        GameSettingsData settingsData = GameSettingsData.fromGameSetting(settings);
        return new GameView(stage, eventHandler, settingsData, onExitToMenu);
    }
    
    // Creates a new Configuration view with proper controller
    public static Configuration createConfiguration(ConfigurationController configController, Runnable onBackToMenu) {
        return new Configuration(configController, onBackToMenu);
    }
    
    // Creates a new ScoreController for scoring management
    public static ScoreController createScoreController() {
        return new ScoreController();
    }
    
    // Creates a new HighScore view
    public static HighScore createHighScore(Runnable onBackToMenu) {
        return new HighScore(onBackToMenu);
    }
}