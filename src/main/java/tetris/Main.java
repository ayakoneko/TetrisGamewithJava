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
import tetris.controller.GameController;
import tetris.panel.Configuration;
import tetris.panel.GameView;
import tetris.panel.HighScore;
import tetris.panel.SplashWindow;
import java.util.Optional;

public class Main extends Application {

    // UI constants
    private static final double BUTTON_WIDTH = 200;
    private static final double MENU_PADDING = 100;
    private static final double MENU_SPACING = 10;

    @Override
   public void start(Stage primaryStage){
        new SplashWindow().show(primaryStage, () -> showMainMenu(primaryStage));
    }

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

        menuLayout.getChildren().addAll(titleLabel, playButton, configButton, highScoreButton, exitButton, authorLabel);

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