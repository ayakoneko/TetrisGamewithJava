package tetris.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HighScore {

    // UI constants for consistency with Main Menu
    private static final double BUTTON_WIDTH = 220;
    private static final double BUTTON_HEIGHT = 50;
    private static final double SCORE_SPACING = 12;
    private static final double SECTION_SPACING = 30;

    private final Runnable onBack;

    public HighScore(Runnable onBack) {
        this.onBack = onBack;
    }

    public void startHighScore(Stage stage) {
        VBox layout = new VBox(SECTION_SPACING);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);

        // Title header of the high scores panel
        Label title = new Label("HIGH SCORES");
        title.getStyleClass().add("label-title");

        // High score data entries
        String[] dummyScores = {
            "1. Homer - 1500", "2. Marge - 1400", "3. Bart - 1300", "4. Lisa - 1200",
            "5. Maggie - 1100", "6. Ned Flanders - 1000", "7. Mr. Burns - 900",
            "8. Moe Szyslak - 800", "9. Milhouse - 700", "10. Ralph Wiggum - 600"
        };

        // Container for score list display
        VBox scoresContainer = new VBox(SCORE_SPACING);
        scoresContainer.setAlignment(Pos.CENTER);
        scoresContainer.getStyleClass().add("box-score-list");

        // Individual score entries with ranking-based styling
        for (int i = 0; i < dummyScores.length; i++) {
            Label scoreLabel = new Label(dummyScores[i]);

            // Highlight top 3 scores with gold color
            if (i < 3) {
                scoreLabel.getStyleClass().add("label-score-top");
            } else {
                scoreLabel.getStyleClass().add("label-score");
            }
            scoresContainer.getChildren().add(scoreLabel);
        }

        // Button to return to Main Menu
        Button backButton = new Button("Back");
        backButton.setPrefWidth(BUTTON_WIDTH);
        backButton.setPrefHeight(BUTTON_HEIGHT);
        backButton.getStyleClass().add("styled-button");
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
            else new tetris.Main().showMainMenu(stage);
        });

        layout.getChildren().addAll(title, scoresContainer, backButton);

        // Main background container
        StackPane root = new StackPane(layout);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 500, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("High Scores");

        stage.setOnCloseRequest(evt -> {
            evt.consume();
            if (onBack != null) onBack.run();
            else new tetris.Main().showMainMenu(stage);
        });

        stage.show();
    }
} 