package G11.panel;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Configuration extends Application {

    public void configuration(Stage primaryStage) {
        VBox menuOption = new VBox(10);
        menuOption.setPadding(new Insets(100));
        menuOption.setAlignment(Pos.CENTER);

        Label title = new Label("Configuration");

        Button button_back = new Button("Back");
        button_back.setPrefWidth(200);


        Label author = new Label("Author: G11");

        StackPane root = new StackPane();
        menuOption.getChildren().addAll(title, button_back, author);
        root.getChildren().setAll(menuOption);
        Scene scene = new Scene(root, 400, 200);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tetris game");
        primaryStage.show();
    }
}
