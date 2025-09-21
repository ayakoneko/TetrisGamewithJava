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
import tetris.controller.score.ScoreController;
import tetris.model.score.ScoreEntry;

public class HighScore {

    // UI constants for consistency with Main Menu
    private static final double BUTTON_WIDTH = 220;
    private static final double BUTTON_HEIGHT = 50;
    private static final double SCORE_SPACING = 12;
    private static final double SECTION_SPACING = 30;

    private final Runnable onBack;
    private final ScoreController scoreController;

    public HighScore(Runnable onBack, ScoreController scoreController) {
        this.onBack = onBack;
        this.scoreController = scoreController;
    }

    public void startHighScore(Stage stage) {
        VBox layout = new VBox(SECTION_SPACING);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.CENTER);

        // Title header of the high scores panel
        Label title = new Label("HIGH SCORES");
        title.getStyleClass().add("label-title");

        // refresh high score data
        scoreController.refreshHighScores();

        // get Data from ScoreController
        List<ScoreEntry> topScores = scoreController.getTopScores();

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

        // Button to clear all high scores
        Button clearButton = new Button("Clear High Scores");
        clearButton.setPrefWidth(BUTTON_WIDTH);
        clearButton.setPrefHeight(BUTTON_HEIGHT);
        clearButton.getStyleClass().add("styled-button");
        clearButton.setOnAction(e -> {
            // Show confirmation dialog before clearing
            javafx.scene.control.Alert confirmDialog = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Clear High Scores");
            confirmDialog.setHeaderText("Are you sure you want to clear all high scores?");
            confirmDialog.setContentText("This action cannot be undone.");
            
            java.util.Optional<javafx.scene.control.ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                scoreController.clearHighScores();
                // Rebuild the scene to show updated scores
                startHighScore(stage);
            }
        });

        // Button to return to Main Menu
        Button backButton = new Button("Back");
        backButton.setPrefWidth(BUTTON_WIDTH);
        backButton.setPrefHeight(BUTTON_HEIGHT);
        backButton.getStyleClass().add("styled-button");
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
            else new tetris.Main().showMainMenu(stage);
        });

        layout.getChildren().addAll(title, scoresContainer, clearButton, backButton);

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