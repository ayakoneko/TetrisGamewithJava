package tetris.view;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;

public class SplashWindow {

    private static final double SPLASH_IMAGE_WIDTH = 300;
    private static final int SPLASH_DELAY_SECONDS = 3;

    // Show a centered splash window for a few seconds, then run onFinish.
    public void show(Stage primaryStage, Runnable onFinish) {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        Node splashContent;
        URL imageUrl = getClass().getResource("/TetrisSplash.png");
        if (imageUrl != null) {
            ImageView img = new ImageView(new Image(imageUrl.toExternalForm()));
            img.setPreserveRatio(true);
            img.setFitWidth(SPLASH_IMAGE_WIDTH);
            img.setSmooth(true);
            splashContent = img;
        } else {
            Label errorLabel = new Label("[Splash image not found]");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            splashContent = errorLabel;
        }

        // Info labels
        Label groupLabel = new Label("Group G11 - Tetris Project");
        Label courseLabel = new Label("7010ICT - Object Oriented Software Development");
        Label uniLabel = new Label("Griffith University - Trimester 2, 2025");
        Label loadingLabel = new Label("Loading, please wait...");

        groupLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        courseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        uniLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        loadingLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: orange;");

        VBox root = new VBox(10, splashContent, groupLabel, courseLabel, uniLabel, loadingLabel);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #111318; -fx-padding: 20;");

        Scene scene = new Scene(root);
        splashStage.setScene(scene);
        splashStage.centerOnScreen();
        splashStage.show();

        // Close after delay, then continue
        PauseTransition delay = new PauseTransition(Duration.seconds(SPLASH_DELAY_SECONDS));
        delay.setOnFinished(e -> {
            splashStage.close();
            onFinish.run();
        });
        delay.play();
    }
}