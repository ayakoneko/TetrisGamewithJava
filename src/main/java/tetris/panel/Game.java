package tetris.panel;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Game {

    public static final int TILE_SIZE = 30;
    public static final int WIDTH = 10;  // columns
    public static final int HEIGHT = 20; // rows
    public static final int WINDOW_WIDTH = TILE_SIZE * WIDTH;
    public static final int WINDOW_HEIGHT = TILE_SIZE * HEIGHT;

    private GraphicsContext gc;
    private int[][] board = new int[HEIGHT][WIDTH];

    public void startGame(Stage stage) {
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        Pane gamePane = new Pane(canvas);

        Pane root = new Pane(canvas);
        Button backButton = new Button("Back");
//        backButton.setOnAction(e -> {
//            gameStage.close();
//            mainStage.show();
//        });

        BorderPane layout = new BorderPane();
        layout.setCenter(gamePane);
        layout.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);

        Scene scene = new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Tetris Game");
        stage.show();

        // test block (4, 0)
        board[0][4] = 1;

        startGameLoop();
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
//                render();
            }
        };
        timer.start();
    }}