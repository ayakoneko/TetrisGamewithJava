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
import tetris.controller.game.GameController;
import tetris.model.GameBoard;
import tetris.setting.ConfigManager;
import tetris.setting.GameSetting;
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

    //private final GameSetting settings = new GameSetting();
    private final GameSetting settings = ConfigManager.loadOrDefault();

    @Override
    public void start(Stage primaryStage){
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

            // Build board from setting
            GameController controller = new GameController(
                    new GameBoard(settings.getFieldWidth(), settings.getFieldHeight())
            );

            //Pass a "back to menu" callback
            GameView gameView = new GameView(primaryStage, controller, settings,
                    () -> showMainMenu(primaryStage)
            );
            gameView.startGame();
        });

        Button configButton = createMenuButton("Configuration", () -> {
            //pass settings + a "back to menu" callback
            new Configuration(settings,
                    () -> showMainMenu(primaryStage)
            ).startConfig(primaryStage);
        });

        Button highScoreButton = createMenuButton("High Score", () -> {
            primaryStage.hide();
            //Pass a "back to menu" callback
            new HighScore(
                    () -> showMainMenu(primaryStage)
            ).startHighScore(primaryStage);
        });

        Button exitButton = createMenuButton("Exit", this::showExitConfirmation);
        primaryStage.setOnCloseRequest(evt -> {
            evt.consume();
            showExitConfirmation();
        });

        // Author credit label
        Label authorLabel = new Label("Author: G11");
        authorLabel.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #B0BEC5;" +
            "-fx-padding: 20 0 0 0;"
        );

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