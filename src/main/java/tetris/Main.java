package tetris;

import tetris.panel.Configuration;
import tetris.panel.HighScore;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
     * Displays the splash screen before showing the main menu.
     */
    private void showSplashScreen(Stage primaryStage) {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        // Splash image
        Image splashImage = new Image(getClass().getResource("/TetrisSplash.png").toExternalForm());
        ImageView splashImageView = new ImageView(splashImage);
        splashImageView.setPreserveRatio(true);
        splashImageView.setFitWidth(SPLASH_IMAGE_WIDTH);
        splashImageView.setSmooth(true);

        // Loading text
        Label loadingLabel = new Label("Loading, please wait...");

        // Layout
        VBox splashLayout = new VBox(splashImageView, loadingLabel);
        splashLayout.setAlignment(Pos.CENTER);

        Scene splashScene = new Scene(splashLayout);
        splashStage.setScene(splashScene);
        splashStage.sizeToScene();
        splashStage.show();

        // Delay before main menu
        PauseTransition delay = new PauseTransition(Duration.seconds(SPLASH_DELAY_SECONDS));
        delay.setOnFinished(event -> {
            splashStage.close();
            showMainMenu(primaryStage);
        });
        delay.play();
    }

    /**
     * Displays the main menu screen.
     */
    private void showMainMenu(Stage primaryStage) {
        VBox menuLayout = new VBox(MENU_SPACING);
        menuLayout.setPadding(new Insets(MENU_PADDING));
        menuLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Main Page");

        // Buttons
        Button playButton = createMenuButton("Play", () -> {
            // TODO: Implement Play logic
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

    /**
     * Creates a standard menu button with a fixed width and action.
     */
    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setOnAction(e -> action.run());
        return button;
    }

    /**
     * Shows a confirmation dialog before exiting the application.
     */
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
