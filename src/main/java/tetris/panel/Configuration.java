package tetris.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Configuration {

    // UI constants for consistency with Main Menu
    private static final double BUTTON_WIDTH = 180;
    private static final double BUTTON_HEIGHT = 40;
    private static final double SECTION_SPACING = 15;
    private static final double CONTROL_SPACING = 8;

    public void startConfig(Stage mainStage) {
        Stage configStage = new Stage(); // New page

        VBox layout = new VBox(SECTION_SPACING);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        // Title header of the configuration panel
        Label title = new Label("CONFIGURATION");
        title.setStyle(
            "-fx-font-family: 'Arial Black', 'Arial', sans-serif;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: linear-gradient(to right, #4FC3F7, #29B6F6);" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);"
        );

        // Section for field and level configuration sliders
        VBox slidersBox = new VBox(CONTROL_SPACING);
        slidersBox.setAlignment(Pos.CENTER_LEFT);
        slidersBox.setStyle(
            "-fx-background-color: rgba(55, 71, 79, 0.6);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"
        );

        // Section title for game settings
        Label slidersTitle = new Label("Game Settings");
        slidersTitle.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #B0BEC5;" +
            "-fx-padding: 0 0 5 0;"
        );

        // Game configuration controls
        Label widthLabel = createStyledLabel("Field Width (No of cells):");
        Slider widthSlider = createStyledSlider(5, 15, 10);

        Label heightLabel = createStyledLabel("Field Height (No of cells):");
        Slider heightSlider = createStyledSlider(15, 30, 20);

        Label levelLabel = createStyledLabel("Game Level:");
        Slider levelSlider = createStyledSlider(1, 10, 6);

        slidersBox.getChildren().addAll(
                slidersTitle,
                widthLabel, widthSlider,
                heightLabel, heightSlider,
                levelLabel, levelSlider
        );

        // Container for game option toggles
        VBox checkboxBox = new VBox(CONTROL_SPACING);
        checkboxBox.setAlignment(Pos.CENTER_LEFT);
        checkboxBox.setStyle(
            "-fx-background-color: rgba(55, 71, 79, 0.6);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);"
        );

        // Section title for game options
        Label optionsTitle = new Label("Game Options");
        optionsTitle.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #B0BEC5;" +
            "-fx-padding: 0 0 5 0;"
        );

        CheckBox musicCheck = createStyledCheckBox("Music (On/Off)", true);
        CheckBox soundCheck = createStyledCheckBox("Sound Effect (On/Off)", true);
        CheckBox aiCheck = createStyledCheckBox("AI Play (On/Off)", false);
        CheckBox extendCheck = createStyledCheckBox("Extend Mode (On/Off)", false);

        checkboxBox.getChildren().addAll(optionsTitle, musicCheck, soundCheck, aiCheck, extendCheck);

        // Button to return to Main Menu
        Button button_back = createStyledButton("Back");
        button_back.setOnAction(event -> {
            configStage.close();
            mainStage.show();
        });

        layout.getChildren().addAll(title, slidersBox, checkboxBox, button_back);

        // Main background container
        BorderPane root = new BorderPane();
        root.setCenter(layout);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #263238, #37474F);"
        );

        Scene scene = new Scene(root, 500, 600);
        configStage.setScene(scene);
        configStage.setTitle("Configuration");
        configStage.show();
    }

    // Creates a styled label for form controls
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #E0E0E0;" +
            "-fx-padding: 3 0 3 0;"
        );
        return label;
    }

    // Creates a styled slider with tick marks and labels
    private Slider createStyledSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setSnapToTicks(true);
        slider.setStyle(
            "-fx-control-inner-background: #455A64;" +
            "-fx-background-color: transparent;" +
            "-fx-tick-label-fill: #B0BEC5;" +
            "-fx-text-fill: #E0E0E0;"
        );
        return slider;
    }

    // Creates a styled checkbox with the given text and selection state
    private CheckBox createStyledCheckBox(String text, boolean selected) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(selected);
        checkBox.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #E0E0E0;" +
            "-fx-padding: 3 0 3 0;"
        );
        return checkBox;
    }

    // Creates a styled button with the given text
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(BUTTON_WIDTH);
        button.setPrefHeight(BUTTON_HEIGHT);
        
        // Base button styling
        button.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: linear-gradient(to bottom, #546E7A, #455A64);" +
            "-fx-background-radius: 6;" +
            "-fx-border-radius: 6;" +
            "-fx-border-color: #78909C;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 1, 1);" +
            "-fx-cursor: hand;"
        );
        
        // Button hover effects
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-font-family: 'Arial', sans-serif;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(to bottom, #607D8B, #546E7A);" +
                "-fx-background-radius: 6;" +
                "-fx-border-radius: 6;" +
                "-fx-border-color: #90A4AE;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.02;" +
                "-fx-scale-y: 1.02;"
            );
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-font-family: 'Arial', sans-serif;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(to bottom, #546E7A, #455A64);" +
                "-fx-background-radius: 6;" +
                "-fx-border-radius: 6;" +
                "-fx-border-color: #78909C;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 1, 1);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        // Button press effects
        button.setOnMousePressed(e -> {
            button.setStyle(
                "-fx-font-family: 'Arial', sans-serif;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;" +
                "-fx-background-color: linear-gradient(to bottom, #455A64, #37474F);" +
                "-fx-background-radius: 6;" +
                "-fx-border-radius: 6;" +
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