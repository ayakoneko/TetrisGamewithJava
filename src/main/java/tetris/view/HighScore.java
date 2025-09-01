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
        title.setStyle(
            "-fx-font-family: 'Arial Black', 'Arial', sans-serif;" +
            "-fx-font-size: 32px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: linear-gradient(to right, #4FC3F7, #29B6F6);" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 2, 2);"
        );

        // High score data entries
        String[] dummyScores = {
                "1. Homer - 1500", "2. Marge - 1400", "3. Bart - 1300", "4. Lisa - 1200",
                "5. Maggie - 1100", "6. Ned Flanders - 1000", "7. Mr. Burns - 900", "8. Moe Szyslak - 800",
                "9. Milhouse - 700", "10. Ralph Wiggum - 600"
        };

        // Container for score list display
        VBox scoresContainer = new VBox(SCORE_SPACING);
        scoresContainer.setAlignment(Pos.CENTER);
        scoresContainer.setStyle(
            "-fx-background-color: rgba(55, 71, 79, 0.8);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 20;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 4);"
        );

        // Individual score entries with ranking-based styling
        for (int i = 0; i < dummyScores.length; i++) {
            Label scoreLabel = new Label(dummyScores[i]);
            
            // Highlight top 3 scores with gold color
            if (i < 3) {
                scoreLabel.setStyle(
                    "-fx-font-family: 'Arial', sans-serif;" +
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #FFC107;" +
                    "-fx-padding: 8 0 8 0;"
                );
            } else {
                scoreLabel.setStyle(
                    "-fx-font-family: 'Arial', sans-serif;" +
                    "-fx-font-size: 16px;" +
                    "-fx-text-fill: #E0E0E0;" +
                    "-fx-padding: 6 0 6 0;"
                );
            }
            scoresContainer.getChildren().add(scoreLabel);
        }

        // Button to return to Main Menu
        Button backButton = createStyledButton("Back");
        backButton.setOnAction(e -> {
            if (onBack != null) onBack.run();
            else new tetris.Main().showMainMenu(stage);
        });

        layout.getChildren().addAll(title, scoresContainer, backButton);

        // Main background container
        StackPane root = new StackPane(layout);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #263238, #37474F);"
        );
        
        Scene scene = new Scene(root, 500, 700);

        stage.setScene(scene);
        stage.setTitle("High Scores");

        stage.setOnCloseRequest(evt -> {
            evt.consume();
            if (onBack != null) onBack.run();
            else new tetris.Main().showMainMenu(stage);
        });

        stage.show();
    }

    // Creates a styled button with the given text
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setPrefHeight(BUTTON_HEIGHT);
        
        // Base button styling
        button.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: linear-gradient(to bottom, #546E7A, #455A64);" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-border-color: #78909C;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 1, 1);" +
            "-fx-cursor: hand;"
        );
        
        // Button hover effects
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-font-family: 'Arial', sans-serif;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(to bottom, #607D8B, #546E7A);" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #90A4AE;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-font-family: 'Arial', sans-serif;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(to bottom, #546E7A, #455A64);" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #78909C;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 1, 1);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        // Button press effects
        button.setOnMousePressed(e -> {
            button.setStyle(
                "-fx-font-family: 'Arial', sans-serif;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(to bottom, #455A64, #37474F);" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #607D8B;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 1, 0, 0, 0);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 0.98;" +
                "-fx-scale-y: 0.98;"
            );
        });
        
        return button;
    }
}
