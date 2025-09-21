package tetris;

import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tetris.common.ConfigManager;
import tetris.controller.config.ConfigurationController;
import tetris.factory.GameFactory;
import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;
import tetris.view.Configuration;
import tetris.view.GameView;
import tetris.view.HighScore;
import tetris.view.SplashWindow;

public class Main extends Application {

    // UI constants
    private static final double BUTTON_WIDTH = 220;
    private static final double BUTTON_HEIGHT = 50;
    private static final double MENU_PADDING = 60;
    private static final double MENU_SPACING = 20;
    private static final double TITLE_SPACING = 40;

    private final GameSetting settings = new GameSetting();
    
    // Store player names for later use
    private String player1Name = null;
    private String player2Name = null;

    @Override
    public void start(Stage primaryStage){
        ConfigManager.clear();
        new SplashWindow().show(primaryStage, () -> showMainMenu(primaryStage));
    }

    public void showMainMenu(Stage primaryStage) {
        VBox menuLayout = new VBox(MENU_SPACING);
        menuLayout.setPadding(new Insets(MENU_PADDING));
        menuLayout.setAlignment(Pos.CENTER);

        // Main title of the game
        Label titleLabel = new Label("TETRIS GAME");
        titleLabel.getStyleClass().add("label-title");

        // Navigation buttons
        Button playButton = createMenuButton("Play", () -> {
            // Prompt for player names BEFORE hiding the main menu
            if (!promptPlayerNames()) {
                return; // User cancelled, stay on main menu
            }

            primaryStage.hide();

            // Create controllers and event handler using factory
            GameView gameView = GameFactory.createGameViewForSettings(
                    primaryStage,
                    settings,
                    () -> showMainMenu(primaryStage)
            );
            
            // Set player names (already validated)
            setPlayerNames(gameView);
            
            gameView.startGame();
        });

        Button configButton = createMenuButton("Configuration", () -> {
            //Create configuration controller and view
            ConfigurationController configController = GameFactory.createConfigurationController(settings);
            Configuration config = GameFactory.createConfiguration(configController,
                    () -> showMainMenu(primaryStage)
            );
            config.startConfig(primaryStage);
        });

        Button highScoreButton = createMenuButton("High Score", () -> {
            primaryStage.hide();
            //Pass a "back to menu" callback
            HighScore highScore = GameFactory.createHighScore(
                    () -> showMainMenu(primaryStage)
            );
            highScore.startHighScore(primaryStage);
        });

        Button exitButton = createMenuButton("Exit", this::showExitConfirmation);
        primaryStage.setOnCloseRequest(evt -> {
            evt.consume();
            showExitConfirmation();
        });

        // Author credit label
        Label authorLabel = new Label("Author: G11");
        authorLabel.getStyleClass().add("label-author");

        // Layout containers for proper spacing
        VBox titleContainer = new VBox(TITLE_SPACING);
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.getChildren().add(titleLabel);

        VBox buttonContainer = new VBox(MENU_SPACING);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(playButton, configButton, highScoreButton, exitButton);

        menuLayout.getChildren().addAll(titleContainer, buttonContainer, authorLabel);

        // Main background container
        StackPane rootPane = new StackPane(menuLayout);
        rootPane.getStyleClass().add("main-background");

        Scene mainScene = new Scene(rootPane, 500, 650);

        // Apply shared stylesheet
        mainScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Tetris Game");
        primaryStage.show();
    }

    // Creates a styled menu button with the given text and action
    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setPrefHeight(BUTTON_HEIGHT);
        button.getStyleClass().add("styled-button");
        button.setOnAction(e -> action.run());
        return button;
    }

    // Prompts for player names based on player types - returns true if successful, false if cancelled
    private boolean promptPlayerNames() {
        if (settings.isExtendOn()) {
            // 2-player mode: prompt for each human/external player
            if (settings.getPlayerOneType() == PlayerType.HUMAN || settings.getPlayerOneType() == PlayerType.EXTERNAL) {
                player1Name = promptPlayerName("Player 1");
                if (player1Name == null) {
                    return false; // User cancelled
                }
            }
            
            if (settings.getPlayerTwoType() == PlayerType.HUMAN || settings.getPlayerTwoType() == PlayerType.EXTERNAL) {
                player2Name = promptPlayerName("Player 2");
                if (player2Name == null) {
                    return false; // User cancelled
                }
            }
        } else {
            // Single player mode: only prompt if human/external
            if (settings.getPlayerOneType() == PlayerType.HUMAN || settings.getPlayerOneType() == PlayerType.EXTERNAL) {
                player1Name = promptPlayerName("Player");
                if (player1Name == null) {
                    return false; // User cancelled
                }
            }
        }
        return true; // All prompts completed successfully
    }

    // Set player names in the game view
    private void setPlayerNames(GameView gameView) {
        if (settings.isExtendOn()) {
            if (player1Name != null) {
                gameView.setPlayer1Name(player1Name);
            }
            if (player2Name != null) {
                gameView.setPlayer2Name(player2Name);
            }
        } else {
            if (player1Name != null) {
                gameView.setPlayerName(player1Name);
            }
        }
    }

    // Helper method to prompt for a single player name
    private String promptPlayerName(String defaultName) {
        TextInputDialog nameDialog = new TextInputDialog(defaultName);
        nameDialog.setTitle("Player Name");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Please enter " + defaultName.toLowerCase() + " name:");
        
        // Set minimum width to prevent text truncation
        nameDialog.getDialogPane().setMinWidth(300);
        
        Optional<String> result = nameDialog.showAndWait();
        
        if (!result.isPresent()) {
            return null; // User cancelled
        }
        
        String playerName = result.get().trim();
        return playerName.isEmpty() ? defaultName : playerName;
    }

    // Shows a confirmation dialog before exiting the application.
    private void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit the game?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ConfigManager.save(settings);
            Platform.exit();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}