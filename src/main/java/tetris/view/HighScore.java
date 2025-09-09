package tetris.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tetris.model.score.HighScoreManager;
import tetris.model.score.ScoreEntry;

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

        // Get real score data from HighScoreManager
        List<ScoreEntry> topScores = HighScoreManager.getInstance().getTopScores();

        // Container for score list display
        VBox scoresContainer = new VBox(SCORE_SPACING);
        scoresContainer.setAlignment(Pos.CENTER);
        scoresContainer.getStyleClass().add("box-score-list");

        // Render actual high scores if available
        for (int i = 0; i < topScores.size() && i < 10; i++) {
            ScoreEntry entry = topScores.get(i);
            String scoreText = String.format("%d. %s - %d", i + 1, entry.getPlayerName(), entry.getScore());
            Label scoreLabel = new Label(scoreText);

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