package tetris.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HighScore {

    public void startHighScore(Stage mainStage) {
        Stage scoreStage = new Stage();

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(100));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("High Scores");

        // Array of dummy high score entries with Simpsons character names
        String[] dummyScores = {
                "1. Homer - 1500", "2. Marge - 1400", "3. Bart - 1300", "4. Lisa - 1200",
                "5. Maggie - 1100", "6. Ned Flanders - 1000", "7. Mr. Burns - 900", "8. Moe Szyslak - 800",
                "9. Milhouse - 700", "10. Ralph Wiggum - 600"
        };

        layout.getChildren().add(title);

        // Add each high score entry as a label
        for (String score : dummyScores) {
            Label scoreLabel = new Label(score);
            layout.getChildren().add(scoreLabel);
        }

        Button backButton = new Button("Back");
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> {
            scoreStage.close();
            mainStage.show();
        });

        layout.getChildren().add(backButton);

        StackPane root = new StackPane(layout);
        Scene scene = new Scene(root, 400, 400);

        scoreStage.setScene(scene);
        scoreStage.setTitle("High Scores");
        scoreStage.show();
    }
}
