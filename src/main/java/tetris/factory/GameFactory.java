package tetris.factory;

import javafx.stage.Stage;
import tetris.common.HighScoreManager;
import tetris.controller.config.ConfigurationController;
import tetris.controller.event.GameEventHandler;
import tetris.controller.game.GameController;
import tetris.controller.score.ScoreController;
import tetris.dto.GameSettingsData;
import tetris.model.board.GameBoard;
import tetris.model.score.HighScoreService;
import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;
import tetris.model.tetromino.PieceGenerator;
import tetris.view.Configuration;
import tetris.view.GameView;
import tetris.view.HighScore;

public class GameFactory {

    private static ScoreController sharedScoreController;
    
    // Creates a new GameController with the specified settings
    public static GameController createGameController(GameSetting settings,PlayerType type,PieceGenerator shared) {
        GameBoard board = new GameBoard(settings.getFieldWidth(), settings.getFieldHeight(), shared);
        return new GameController(board, settings, type);
    }

    // Creates a new ConfigurationController for settings management
    public static ConfigurationController createConfigurationController(GameSetting settings) {
        return new ConfigurationController(settings);
    }
    
    // Creates a new GameEventHandler for View-Controller communication
    public static GameEventHandler createGameEventHandler(GameController gameController, GameSetting settings) {
        return new GameEventHandler(gameController, settings);
    }

    // Creates a new GameEventHandler with player number
    public static GameEventHandler createGameEventHandler(GameController gameController, GameSetting settings, int playerNumber) {
        return new GameEventHandler(gameController, settings, playerNumber);
    }
    
    // Creates a new GameView with proper event handling
    public static GameView createGameView(Stage stage,
                                          GameEventHandler p1Handler,
                                          GameEventHandler p2Handler, // may be null
                                          GameSetting settings,
                                          Runnable onExitToMenu) {
        GameSettingsData settingsData = GameSettingsData.fromGameSetting(settings);
        return new GameView(stage, p1Handler, p2Handler, settingsData, onExitToMenu);
    }

    public static GameView createGameView(Stage stage,
                                          GameEventHandler handler,
                                          GameSetting settings,
                                          Runnable onExitToMenu) {
        return createGameView(stage, handler, null, settings, onExitToMenu);
    }

    public static GameView createGameViewForSettings(Stage stage,
                                                     GameSetting settings,
                                                     Runnable onExitToMenu) {

        if (settings.isExtendOn()) { // 2P
            long seed = System.currentTimeMillis();
            System.out.printf("[2P] seed=%d%n", seed);
            PieceGenerator g1 = new PieceGenerator(seed);
            PieceGenerator g2 = new PieceGenerator(seed);

            GameController p1 = createGameController(settings, settings.getPlayerOneType(), g1);
            GameController p2 = createGameController(settings, settings.getPlayerTwoType(), g2);

            GameEventHandler h1 = createGameEventHandler(p1, settings, 1);
            GameEventHandler h2 = createGameEventHandler(p2, settings, 2);
            return createGameView(stage, h1, h2, settings, onExitToMenu);
        } else { // 1P
            GameController p1 = createGameController(settings, settings.getPlayerOneType(),
                    new PieceGenerator(System.nanoTime()));
            GameEventHandler h1 = createGameEventHandler(p1, settings, 1);
            return createGameView(stage, h1, settings, onExitToMenu);
        }
    }

    // Creates a new Configuration view with proper controller
    public static Configuration createConfiguration(ConfigurationController configController, Runnable onBackToMenu) {
        return new Configuration(configController, onBackToMenu);
    }
    
    // Creates a new ScoreController for scoring management
    public static ScoreController createScoreController() {
        if (sharedScoreController == null) {
            sharedScoreController = new ScoreController(new HighScoreService(new HighScoreManager()));
        }
        return sharedScoreController;
    }

    // Creates a new HighScore view
    public static HighScore createHighScore(Runnable onBackToMenu) {
        return new HighScore(onBackToMenu, createScoreController()); // 컨트롤러 주입
    }
}