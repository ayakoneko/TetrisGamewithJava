package G11;

import java.util.Optional;

import G11.panel.Configuration;
import G11.panel.HighScore;
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

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Splash Screen
        Stage splashStage = new Stage(StageStyle.UNDECORATED);
        ImageView splashImage = new ImageView(
                new Image(getClass().getResource("/TetrisSplash.png").toExternalForm())
        );
        splashImage.setFitWidth(300);
        splashImage.setFitHeight(300);
        splashImage.setPreserveRatio(true);
        splashImage.setSmooth(true);

        Label loadingLabel = new Label("Loading, please wait...");

        StackPane splashLayout = new StackPane(splashImage, loadingLabel);
        Scene splashScene = new Scene(splashLayout, 300, 300);
        splashStage.setScene(splashScene);
        splashStage.show();

        // Delay before showing main stage
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            splashStage.close();
            showMainStage(primaryStage);
        });
        delay.play();
    }

    public void showMainStage(Stage primaryStage) {
        VBox menuOption = new VBox(10);
        menuOption.setPadding(new Insets(100));
        menuOption.setAlignment(Pos.CENTER);

        Label title = new Label("Main Page");

        Button button_play = new Button("Play");
        button_play.setPrefWidth(200);

        Button button_config = new Button("Configuration");
        button_config.setPrefWidth(200);

        button_config.setOnAction(event -> {
            primaryStage.hide();
            Configuration config = new Configuration();
            config.startConfig(primaryStage); // pass primaryStage so we can show it again later
        });

        Button button_highScore = new Button("High Score");
        button_highScore.setPrefWidth(200);
        button_highScore.setOnAction(event -> {
            primaryStage.hide(); // Hide main
            HighScore highScore = new HighScore();
            highScore.startHighScore(primaryStage);
        });

        Button button_exit = new Button("Exit");
        button_exit.setPrefWidth(200);
        button_exit.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit the game?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Platform.exit();
            }
        });

        Label author = new Label("Author: G11");

        menuOption.getChildren().addAll(title, button_play, button_config, button_highScore, button_exit, author);

        StackPane root = new StackPane(menuOption);
        Scene mainScene = new Scene(root, 400, 200);

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Tetris Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
