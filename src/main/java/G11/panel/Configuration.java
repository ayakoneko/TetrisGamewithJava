package G11.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Configuration {

    public void startConfig(Stage mainStage) {
        Stage configStage = new Stage(); // new window

        VBox menuOption = new VBox(10);
        menuOption.setPadding(new Insets(100));
        menuOption.setAlignment(Pos.CENTER);

        Label title = new Label("Configuration");

        Button button_back = new Button("Back");
        button_back.setPrefWidth(200);

        button_back.setOnAction(event -> {
            configStage.close();
            mainStage.show();
        });

        Label author = new Label("Author: G11");

        menuOption.getChildren().addAll(title, button_back, author);
        StackPane root = new StackPane(menuOption);

        Scene scene = new Scene(root, 400, 200);
        configStage.setScene(scene);
        configStage.setTitle("Configuration");
        configStage.show();
    }
}
