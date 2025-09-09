package tetris.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tetris.controller.config.ConfigurationController;
import tetris.dto.GameSettingsData;

import java.net.URL;
import java.util.function.Consumer;

public class Configuration {

    // UI constants
    private static final double BUTTON_WIDTH = 180;
    private static final double BUTTON_HEIGHT = 40;
    private static final double SECTION_SPACING = 15;
    private static final double CONTROL_SPACING = 8;
    private static final double SLIDER_WIDTH = 400;

    private final ConfigurationController configController;
    private final GameSettingsData settingsData; // UI-safe DTO access
    private final Runnable onBack;

    private HBox playerTwoRow;

    public Configuration(ConfigurationController configController, Runnable onBack) {
        this.configController = configController;
        this.settingsData = configController.getSettingsData(); // UI-safe DTO access
        this.onBack = onBack;
    }

    public void startConfig(Stage stage) {
        VBox layout = new VBox(SECTION_SPACING);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("CONFIGURATION");
        title.getStyleClass().add("label-title");

        // === Game Settings (sliders) ===
        VBox slidersBox = new VBox(CONTROL_SPACING);
        slidersBox.setAlignment(Pos.CENTER_LEFT);
        slidersBox.getStyleClass().add("box-section");

        Label slidersTitle = new Label("Game Settings");
        slidersTitle.getStyleClass().add("label-section");

        // Field width
        Label widthLabel = createFieldLabel("Field Width (No of cells):");
        Slider widthSlider = createStyledSlider(5, 15, settingsData.fieldWidth());
        Label widthValueLabel = createValueLabel(widthSlider.getValue());
        widthSlider.valueProperty().addListener((obs, o, v) -> {
            int val = v.intValue();
            widthValueLabel.setText(String.format("%.0f", v));
            configController.updateFieldWidth(val);
        });
        HBox widthControlBox = row(widthSlider, widthValueLabel);

        // Field height
        Label heightLabel = createFieldLabel("Field Height (No of cells):");
        Slider heightSlider = createStyledSlider(15, 30, settingsData.fieldHeight());
        Label heightValueLabel = createValueLabel(heightSlider.getValue());
        heightSlider.valueProperty().addListener((obs, o, v) -> {
            int val = v.intValue();
            heightValueLabel.setText(String.format("%.0f", v));
            configController.updateFieldHeight(val);
        });
        HBox heightControlBox = row(heightSlider, heightValueLabel);

        // Level
        Label levelLabel = createFieldLabel("Game Level:");
        Slider levelSlider = createStyledSlider(1, 10, settingsData.level());
        Label levelValueLabel = createValueLabel(levelSlider.getValue());
        levelSlider.valueProperty().addListener((obs, o, v) -> {
            int val = v.intValue();
            levelValueLabel.setText(String.format("%.0f", v));
            configController.updateLevel(val);
        });
        HBox levelControlBox = row(levelSlider, levelValueLabel);

        slidersBox.getChildren().addAll(
                slidersTitle,
                widthLabel, widthControlBox,
                heightLabel, heightControlBox,
                levelLabel, levelControlBox
        );

        // === Game Options (checkboxes) ===
        VBox checkboxBox = new VBox(CONTROL_SPACING);
        checkboxBox.setAlignment(Pos.CENTER_LEFT);
        checkboxBox.getStyleClass().add("box-section");

        Label optionsTitle = new Label("Game Options");
        optionsTitle.getStyleClass().add("label-section");

        HBox musicControlBox = makeToggle("Music", settingsData.musicOn(),
                isSel -> configController.updateMusicSetting(isSel));

        HBox soundControlBox = makeToggle("Sound Effect", settingsData.sfxOn(),
                isSel -> configController.updateSfxSetting(isSel));

        HBox extendControlBox = makeToggle("Extend Mode", settingsData.extendOn(),
                isSel -> {
                    configController.updateExtendSetting(isSel);
                    if (playerTwoRow != null) playerTwoRow.setDisable(!isSel);
                });

        checkboxBox.getChildren().addAll(
                optionsTitle, musicControlBox, soundControlBox, extendControlBox
        );

        // === Player Options (radio buttons) ===
        VBox playerTypeBox = new VBox(CONTROL_SPACING);
        playerTypeBox.setAlignment(Pos.CENTER_LEFT);
        playerTypeBox.getStyleClass().add("box-section");

        Label playerOptionsTitle = new Label("Player Options");
        playerOptionsTitle.getStyleClass().add("label-section");

        HBox playerOneRow = buildPlayerTypeRow(
                "Player One Type:",
                settingsData.playerOneType(),
                ptString -> configController.updatePlayerOneType(ptString)
        );
        playerTwoRow = buildPlayerTypeRow(
                "Player Two Type:",
                settingsData.playerTwoType(),
                ptString -> configController.updatePlayerTwoType(ptString)
        );
        playerTwoRow.setDisable(!settingsData.extendOn()); // Disable when extend mode is OFF

        playerTypeBox.getChildren().addAll(playerOptionsTitle, playerOneRow, playerTwoRow);

        // Back
        Button button_back = new Button("Back");
        button_back.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button_back.getStyleClass().add("styled-button");
        button_back.setOnAction(e -> onBack.run());

        // Layout
        layout.getChildren().addAll(title, slidersBox, checkboxBox, playerTypeBox, button_back);

        BorderPane root = new BorderPane(layout);

        Scene scene = new Scene(root, 500, 730);
        URL css = getClass().getResource("/css/Style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        else root.setStyle("-fx-background-color: linear-gradient(to bottom, #263238, #37474F);");

        stage.setScene(scene);
        stage.setTitle("Configuration");
        stage.setOnCloseRequest(evt -> {
            evt.consume();
            onBack.run();
        });
        stage.show();
    }

    // ---- helpers ----
    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-field");
        return label;
    }

    private Label createValueLabel(double initialValue) {
        Label label = new Label(String.format("%.0f", initialValue));
        label.getStyleClass().add("label-value");
        return label;
    }

    private Slider createStyledSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setPrefWidth(SLIDER_WIDTH);
        return slider;
    }

    private HBox row(Slider s, Label v) {
        HBox box = new HBox(CONTROL_SPACING);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(s, Priority.ALWAYS);
        v.setPrefWidth(44);
        v.setMinWidth(Region.USE_PREF_SIZE);
        v.setMaxWidth(Region.USE_PREF_SIZE);
        v.setAlignment(Pos.CENTER_RIGHT);
        box.getChildren().addAll(s, v);
        return box;
    }

    private HBox makeToggle(String text, boolean initialSelected, Consumer<Boolean> onChange) {
        HBox controlBox = new HBox(CONTROL_SPACING);
        controlBox.setAlignment(Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox(text);
        checkBox.getStyleClass().add("checkbox-label");
        checkBox.setSelected(initialSelected);

        Label status = new Label(initialSelected ? "On" : "Off");
        status.getStyleClass().add(initialSelected ? "status-label-on" : "status-label-off");

        checkBox.selectedProperty().addListener((obs, was, isSel) -> {
            status.setText(isSel ? "On" : "Off");
            status.getStyleClass().removeAll("status-label-on", "status-label-off");
            status.getStyleClass().add(isSel ? "status-label-on" : "status-label-off");
            if (onChange != null) onChange.accept(isSel);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        controlBox.getChildren().addAll(checkBox, spacer, status);
        return controlBox;
    }

    private HBox buildPlayerTypeRow(String labelText, String currentType, Consumer<String> onChange) {
        HBox row = new HBox(CONTROL_SPACING);
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.getStyleClass().add("label-field");

        RadioButton rbHuman = new RadioButton("Human   ");
        RadioButton rbAI = new RadioButton("AI   ");
        RadioButton rbExternal = new RadioButton("External  ");

        rbHuman.getStyleClass().add("label-field");
        rbAI.getStyleClass().add("label-field");
        rbExternal.getStyleClass().add("label-field");

        ToggleGroup tg = new ToggleGroup();
        rbHuman.setToggleGroup(tg);
        rbAI.setToggleGroup(tg);
        rbExternal.setToggleGroup(tg);

        switch (currentType) {
            case "AI" -> rbAI.setSelected(true);
            case "EXTERNAL" -> rbExternal.setSelected(true);
            default -> rbHuman.setSelected(true);
        }

        tg.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) return;
            if (newT == rbHuman) onChange.accept("HUMAN");
            else if (newT == rbAI) onChange.accept("AI");
            else onChange.accept("EXTERNAL");
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(label, spacer, rbHuman, rbAI, rbExternal);
        return row;
    }
}
