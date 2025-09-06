package tetris.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tetris.setting.ConfigManager;
import tetris.setting.GameSetting;

import java.net.URL;
import java.util.function.Consumer;

public class Configuration {

    // UI constants
    private static final double BUTTON_WIDTH = 180;
    private static final double BUTTON_HEIGHT = 40;
    private static final double SECTION_SPACING = 15;
    private static final double CONTROL_SPACING = 8;
    private static final double SLIDER_WIDTH = 400;

    private final GameSetting settings;
    private final Runnable onBack;

    public Configuration(GameSetting settings, Runnable onBack) {
        this.settings = settings;
        this.onBack = onBack;
    }

    public void startConfig(Stage stage) {
        VBox layout = new VBox(SECTION_SPACING);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("CONFIGURATION");
        title.getStyleClass().add("label-title");

        // === sliders ===
        VBox slidersBox = new VBox(CONTROL_SPACING);
        slidersBox.setAlignment(Pos.CENTER_LEFT);
        slidersBox.getStyleClass().add("box-section");

        Label slidersTitle = new Label("Game Settings");
        slidersTitle.getStyleClass().add("label-section");

        // Field width
        Label widthLabel = createFieldLabel("Field Width (No of cells):");
        Slider widthSlider = createStyledSlider(5, 15, settings.getFieldWidth());
        Label widthValueLabel = createValueLabel(widthSlider.getValue());
        widthSlider.valueProperty().addListener((obs, o, v) -> {
            int val = v.intValue();
            widthValueLabel.setText(String.format("%.0f", v));
            settings.setFieldWidth(val);
            System.out.println("[UI] width -> " + val);
            ConfigManager.save(settings);
        });
        HBox widthControlBox = row(widthSlider, widthValueLabel);

        // Field height
        Label heightLabel = createFieldLabel("Field Height (No of cells):");
        Slider heightSlider = createStyledSlider(15, 30, settings.getFieldHeight());
        Label heightValueLabel = createValueLabel(heightSlider.getValue());
        heightSlider.valueProperty().addListener((obs, o, v) -> {
            int val = v.intValue();
            heightValueLabel.setText(String.format("%.0f", v));
            System.out.println("[UI] height -> " + val);
            settings.setFieldHeight(val);
            ConfigManager.save(settings);
        });
        HBox heightControlBox = row(heightSlider, heightValueLabel);

        // Level
        Label levelLabel = createFieldLabel("Game Level:");
        Slider levelSlider = createStyledSlider(1, 10, settings.getLevel());
        Label levelValueLabel = createValueLabel(levelSlider.getValue());
        levelSlider.valueProperty().addListener((obs, o, v) -> {
            int val = v.intValue();
            levelValueLabel.setText(String.format("%.0f", v));
            System.out.println("[UI] level -> " + val);
            settings.setLevel(val);
            ConfigManager.save(settings);
        });
        HBox levelControlBox = row(levelSlider, levelValueLabel);

        slidersBox.getChildren().addAll(
                slidersTitle,
                widthLabel, widthControlBox,
                heightLabel, heightControlBox,
                levelLabel, levelControlBox
        );

        // === checkboxes ===
        VBox checkboxBox = new VBox(CONTROL_SPACING);
        checkboxBox.setAlignment(Pos.CENTER_LEFT);
        checkboxBox.getStyleClass().add("box-section");

        Label optionsTitle = new Label("Game Options");
        optionsTitle.getStyleClass().add("label-section");

        HBox musicControlBox  = makeToggle("Music", settings.isMusicOn(),
                isSel -> { settings.setMusicOn(isSel); ConfigManager.save(settings); });

        HBox soundControlBox  = makeToggle("Sound Effect", settings.isSfxOn(),
                isSel -> { settings.setSfxOn(isSel); ConfigManager.save(settings); });

        HBox aiControlBox     = makeToggle("AI Play", settings.isAiOn(),
                isSel -> { settings.setAiOn(isSel); ConfigManager.save(settings); });

        HBox extendControlBox = makeToggle("Extend Mode", settings.isExtendOn(),
                isSel -> { settings.setExtendOn(isSel); ConfigManager.save(settings); });

        checkboxBox.getChildren().addAll(
                optionsTitle, musicControlBox, soundControlBox, aiControlBox, extendControlBox
        );

        // Back button
        Button button_back = new Button("Back");
        button_back.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button_back.getStyleClass().add("styled-button");
        button_back.setOnAction(e -> onBack.run());

        layout.getChildren().addAll(title, slidersBox, checkboxBox, button_back);

        BorderPane root = new BorderPane();
        root.setCenter(layout);

        Scene scene = new Scene(root, 500, 600);

        URL css = getClass().getResource("/css/Style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        else root.setStyle("-fx-background-color: linear-gradient(to bottom, #263238, #37474F);");

        stage.setScene(scene);
        stage.setTitle("Configuration");
        stage.setOnCloseRequest(evt -> { evt.consume(); onBack.run(); });
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

    // Toggle with callback
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
}
