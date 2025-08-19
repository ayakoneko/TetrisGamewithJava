package tetris.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Configuration {

    // UI constants for consistency with Main Menu
    private static final double BUTTON_WIDTH = 180;
    private static final double BUTTON_HEIGHT = 40;
    private static final double SECTION_SPACING = 15;
    private static final double CONTROL_SPACING = 8;
    private static final double SLIDER_WIDTH = 400; // Increased slider width

    public void startConfig(Stage stage) {
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
        widthSlider.setPrefWidth(SLIDER_WIDTH); // Set preferred width
        Label widthValueLabel = createStyledValueLabel(widthSlider.getValue());
        widthSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            widthValueLabel.setText(String.format("%.0f", newVal))
        );
        HBox widthControlBox = new HBox(CONTROL_SPACING, widthSlider, widthValueLabel);
        widthControlBox.setAlignment(Pos.CENTER_LEFT);

        Label heightLabel = createStyledLabel("Field Height (No of cells):");
        Slider heightSlider = createStyledSlider(15, 30, 20);
        heightSlider.setPrefWidth(SLIDER_WIDTH); // Set preferred width
        Label heightValueLabel = createStyledValueLabel(heightSlider.getValue());
        heightSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            heightValueLabel.setText(String.format("%.0f", newVal))
        );
        HBox heightControlBox = new HBox(CONTROL_SPACING, heightSlider, heightValueLabel);
        heightControlBox.setAlignment(Pos.CENTER_LEFT);

        Label levelLabel = createStyledLabel("Game Level:");
        Slider levelSlider = createStyledSlider(1, 10, 6);
        levelSlider.setPrefWidth(SLIDER_WIDTH); // Set preferred width
        Label levelValueLabel = createStyledValueLabel(levelSlider.getValue());
        levelSlider.valueProperty().addListener((obs, oldVal, newVal) ->
            levelValueLabel.setText(String.format("%.0f", newVal))
        );
        HBox levelControlBox = new HBox(CONTROL_SPACING, levelSlider, levelValueLabel);
        levelControlBox.setAlignment(Pos.CENTER_LEFT);

        slidersBox.getChildren().addAll(
                slidersTitle,
                widthLabel, widthControlBox,
                heightLabel, heightControlBox,
                levelLabel, levelControlBox
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

        HBox musicControlBox = createStyledCheckBoxWithLabel("Music", true);
        HBox soundControlBox = createStyledCheckBoxWithLabel("Sound Effect", true);
        HBox aiControlBox = createStyledCheckBoxWithLabel("AI Play", false);
        HBox extendControlBox = createStyledCheckBoxWithLabel("Extend Mode", false);

        checkboxBox.getChildren().addAll(optionsTitle, musicControlBox, soundControlBox, aiControlBox, extendControlBox);

        // Button to return to Main Menu
        Button button_back = createStyledButton("Back");
        button_back.setOnAction(e -> new tetris.Main().showMainMenu(stage));

        layout.getChildren().addAll(title, slidersBox, checkboxBox, button_back);

        // Main background container
        BorderPane root = new BorderPane();
        root.setCenter(layout);
        root.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #263238, #37474F);"
        );

        Scene scene = new Scene(root, 500, 600);
        stage.setScene(scene);
        stage.setTitle("Configuration");

        stage.setOnCloseRequest(evt -> {
            evt.consume();
            new tetris.Main().showMainMenu(stage);
        });

        stage.show();
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

    // Creates a styled label to display the slider's value
    private Label createStyledValueLabel(double initialValue) {
        Label label = new Label(String.format("%.0f", initialValue));
        label.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #E0E0E0;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 0 0 0 10;"
        );
        return label;
    }

    // Creates a styled slider with tick marks and labels
    private Slider createStyledSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setStyle(
            "-fx-control-inner-background: #455A64;" +
            "-fx-background-color: transparent;" +
            "-fx-tick-label-fill: #B0BEC5;" +
            "-fx-text-fill: #E0E0E0;"
        );
        return slider;
    }

    // Creates a styled HBox containing a checkbox and a label that shows "On/Off" status
    private HBox createStyledCheckBoxWithLabel(String text, boolean initialSelected) {
        // HBox to contain the checkbox and the status label
        HBox controlBox = new HBox(CONTROL_SPACING);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        
        // The CheckBox control with the text directly on it
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(initialSelected);
        
        checkBox.setStyle(
            "-fx-font-family: 'Arial', sans-serif;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" + 
            "-fx-text-fill: #E0E0E0;" + 
            "-fx-padding: 3 0 3 0;"
        );
        
        // The status label that shows "On" or "Off"
        Label statusLabel = new Label(initialSelected ? "On" : "Off");
        
        // Initial style for the status label based on the initial state
        if (initialSelected) {
            statusLabel.setStyle("-fx-font-family: 'Arial', sans-serif; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 0 0 0 10;");
        } else {
            statusLabel.setStyle("-fx-font-family: 'Arial', sans-serif; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #FF3232; -fx-padding: 0 0 0 10;");
        }
        
        // Listener to update the label text and color when the checkbox is toggled
        checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            statusLabel.setText(isSelected ? "On" : "Off");
            // Set text color based on the new state
            if (isSelected) {
                statusLabel.setStyle("-fx-font-family: 'Arial', sans-serif; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: green; -fx-padding: 0 0 0 10;");
            } else {
                statusLabel.setStyle("-fx-font-family: 'Arial', sans-serif; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #FF3232; -fx-padding: 0 0 0 10;");
            }
        });

        // Add components to the HBox. The Region is a flexible spacer.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        controlBox.getChildren().addAll(checkBox, spacer, statusLabel);
        
        return controlBox;
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