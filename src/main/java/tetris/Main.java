package tetris;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tetris.controller.GameController;
import tetris.panel.Configuration;
import tetris.panel.GameView;
import tetris.panel.HighScore;

import java.net.URL;
import java.util.Optional;

public class Main extends Application {

    // UI constants
    private static final double BUTTON_WIDTH = 200;
    private static final double MENU_PADDING = 100;
    private static final double MENU_SPACING = 10;
    private static final double SPLASH_IMAGE_WIDTH = 300;
    private static final int SPLASH_DELAY_SECONDS = 3;

    @Override
    public void start(Stage primaryStage) {
        showSplashScreen(primaryStage);
    }

    /**
     * Displays the splash screen at app startup.
     * 
     * This version includes validation to handle missing image resources.
     * If the splash image (TetrisSplash.png) is available in src/main/resources,
     * it will be displayed. If not, a fallback text message will be shown instead
     * to prevent the application from crashing with a NullPointerException.
     * 
     * After a short delay, the app transitions to the main menu.
     */
    private void showSplashScreen(Stage primaryStage) {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
    
        Node splashContent;
        URL imageUrl = getClass().getResource("/TetrisSplash.png");
        if (imageUrl != null) {
            Image splashImage = new Image(imageUrl.toExternalForm());
            ImageView splashImageView = new ImageView(splashImage);
            splashImageView.setPreserveRatio(true);
            splashImageView.setFitWidth(SPLASH_IMAGE_WIDTH);
            splashImageView.setSmooth(true);
            splashContent = splashImageView;
        } else {
            Label errorLabel = new Label("[Splash image not found]");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            splashContent = errorLabel;
        }

        Label groupLabel = new Label("Group G11 - Tetris Project");
        Label courseLabel = new Label("7010ICT - Object Oriented Software Development");
        Label uniLabel = new Label("Griffith University - Trimester 2, 2025");
        Label loadingLabel = new Label("Loading, please wait...");

        groupLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        courseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        uniLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        loadingLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: orange;");
    
        VBox splashLayout = new VBox(splashContent, groupLabel, courseLabel, uniLabel, loadingLabel);
        splashLayout.setAlignment(Pos.CENTER);
        splashLayout.setStyle("-fx-background-color: #111318; -fx-padding: 20;");
    
        Scene splashScene = new Scene(splashLayout);
        splashStage.setScene(splashScene);
        splashStage.sizeToScene();
        splashStage.show();
    
        PauseTransition delay = new PauseTransition(Duration.seconds(SPLASH_DELAY_SECONDS));
        delay.setOnFinished(event -> {
            splashStage.close();
            showMainMenu(primaryStage);
        });
        delay.play();
    }
    

    //Displays the main menu screen.
    public void showMainMenu(Stage primaryStage) {
        VBox menuLayout = new VBox(MENU_SPACING);
        menuLayout.setPadding(new Insets(MENU_PADDING));
        menuLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Main Page");

        // Buttons
        Button playButton = createMenuButton("Play", () -> {
            primaryStage.hide();

            //new Game().startGame(primaryStage);
            GameController controller = new GameController();
            GameView gameView = new GameView(primaryStage, controller);
            gameView.startGame();
        });

        Button configButton = createMenuButton("Configuration", () -> {
            primaryStage.hide();
            new Configuration().startConfig(primaryStage);
        });

        Button highScoreButton = createMenuButton("High Score", () -> {
            primaryStage.hide();
            new HighScore().startHighScore(primaryStage);
        });

        Button exitButton = createMenuButton("Exit", this::showExitConfirmation);

        Label authorLabel = new Label("Author: G11");

        menuLayout.getChildren().addAll(
                titleLabel,
                playButton,
                configButton,
                highScoreButton,
                exitButton,
                authorLabel
        );

        StackPane rootPane = new StackPane(menuLayout);
        Scene mainScene = new Scene(rootPane, 400, 200);

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Tetris Game");
        primaryStage.show();
    }

    //Creates a standard menu button with a fixed width and action.
    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(BUTTON_WIDTH);
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
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}