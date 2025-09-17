package tetris;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tetris.controller.config.ConfigurationController;
import tetris.factory.GameFactory;
import tetris.common.ConfigManager;
import tetris.model.setting.GameSetting;
import tetris.view.Configuration;
import tetris.view.GameView;
import tetris.view.HighScore;
import tetris.view.SplashWindow;

import java.util.Optional;

public class Main extends Application {

    // UI constants
    private static final double BUTTON_WIDTH = 220;
    private static final double BUTTON_HEIGHT = 50;
    private static final double MENU_PADDING = 60;
    private static final double MENU_SPACING = 20;
    private static final double TITLE_SPACING = 40;

    private final GameSetting settings = new GameSetting();

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
            primaryStage.hide();

            // Create controllers and event handler using factory
            GameView gameView = GameFactory.createGameViewForSettings(
                    primaryStage,
                    settings,
                    () -> showMainMenu(primaryStage)
            );
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