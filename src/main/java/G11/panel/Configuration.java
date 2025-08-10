package G11.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Configuration {

    public void startConfig(Stage mainStage) {
        Stage configStage = new Stage(); // New page

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Configuration");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Slider
        VBox slidersBox = new VBox(10);
        slidersBox.setAlignment(Pos.CENTER_LEFT);

        // Field Width
        Label widthLabel = new Label("Field Width (No of cells):");
        Slider widthSlider = new Slider(5, 15, 10);
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(1);
        widthSlider.setSnapToTicks(true);

        // Field Height
        Label heightLabel = new Label("Field Height (No of cells):");
        Slider heightSlider = new Slider(15, 30, 20);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(1);
        heightSlider.setSnapToTicks(true);

        // Game Level
        Label levelLabel = new Label("Game Level:");
        Slider levelSlider = new Slider(1, 10, 6);
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setSnapToTicks(true);

        slidersBox.getChildren().addAll(
            widthLabel, widthSlider,
            heightLabel, heightSlider,
            levelLabel, levelSlider
        );

        // Checkbox 
        VBox checkboxBox = new VBox(10);
        checkboxBox.setAlignment(Pos.CENTER_LEFT);

        CheckBox musicCheck = new CheckBox("Music (On/Off)");
        musicCheck.setSelected(true);

        CheckBox soundCheck = new CheckBox("Sound Effect (On/Off)");
        soundCheck.setSelected(true);

        CheckBox aiCheck = new CheckBox("AI Play (On/Off)");
        aiCheck.setSelected(false);

        CheckBox extendCheck = new CheckBox("Extend Mode (On/Off)");
        extendCheck.setSelected(false);

        checkboxBox.getChildren().addAll(musicCheck, soundCheck, aiCheck, extendCheck);

        // Back button
        Button button_back = new Button("Back");
        button_back.setPrefWidth(200);
        button_back.setOnAction(event -> {
            configStage.close();
            mainStage.show();
        });

        layout.getChildren().addAll(title, slidersBox, checkboxBox, button_back);

        Scene scene = new Scene(layout, 500, 500);
        configStage.setScene(scene);
        configStage.setTitle("Configuration");
        configStage.show();
    }
}