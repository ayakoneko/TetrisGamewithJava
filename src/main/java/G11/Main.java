package G11;

import G11.panel.Configuration;
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

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
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
        Scene scene = new Scene(root, 400, 200);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tetris Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
