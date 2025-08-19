package tetris.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import tetris.Main;
import tetris.controller.GameController;
import tetris.controller.IGameController;
import tetris.model.GameBoard;
import tetris.model.Tetromino;

import java.util.Optional;

/**
* GameView : connecting user input and the game logic (GameController) to the on-screen rendering.
 * startGame() : Set Scene, key event handler, play game
 * togglePause() : Change game state (Play / Pause)
 * askExitToMenu() : back button or Press Escape
 * draw() : draw board ( drawBoarCells + drawCell + Overlay(when game stopped))
 * drawBoardCells() : draw background cells and placed block
 * drawCell() : draw each cell
 * drawCenteredOverlay() : Show message(paused, game over)
 * colorOf : Define the color of blocks
*/
public class GameView {

    // === UI constants ===
    private static final int TILE = 30;        // Size of one tile (px)
    private static final int GAP  = 1;         // Gap between tiles (border effect)
    private static final int PADDING = 12;     // Padding around the board

    private final Stage stage;
    private final GameController controller;
    private final Canvas canvas;
    private final GameLoop loop;

    private long dropInterval = 500_000_000; //0.5sec

    public GameView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;

        // Canvas size based on board dimensions
        int w = GameBoard.W * TILE + PADDING * 2;
        int h = GameBoard.H * TILE + PADDING * 2;
        this.canvas = new Canvas(w, h);

        // Game loop: Every drop interval (default 500ms) → controller.tick() → draw()
        this.loop = new GameLoop(dropInterval) {
            @Override protected void update() { controller.tick(); }
            @Override protected void render() { draw(); }
        };
    }

    public Scene buildScreen() {
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> askExitToMenu());

        HBox bottomBar = new HBox(backBtn);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(8));

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, canvas.getWidth(), canvas.getHeight() + 40);    // added 40 to fit back button
        stage.setTitle("Tetris - Play");

        // Keyboard input handling
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case LEFT -> controller.moveLeft();
                case RIGHT -> controller.moveRight();
                case UP -> controller.rotateCW();
                case DOWN -> controller.softDrop();
                case SPACE -> controller.hardDrop();
                case P -> togglePause();
                case R -> {
                    if (controller.state() == IGameController.State.GAME_OVER) {
                        controller.restart();
                        loop.start();
                        draw();
                    }
                }
                case ESCAPE -> askExitToMenu();
                default -> { /* ignore */ }
            }
            draw();
            e.consume();
        });

        return scene;
    }

    public void startGame(){
        stage.setScene(buildScreen());
        controller.start();
        stage.show();
        loop.start();
        draw();
    }

    // Toggles pause state and updates the game loop accordingly.
    private void togglePause() {
        controller.togglePause();
        if (controller.state() == IGameController.State.PAUSE) {
            loop.stop();
        } else if (controller.state() == IGameController.State.PLAY) {
            loop.start();
        }
        draw(); // Update overlay text
    }

    // Shows confirmation dialog before returning to main menu.
    private void askExitToMenu() {
        boolean wasPlaying = (controller.state() == IGameController.State.PLAY);

        if (wasPlaying) {             // Pause game before show alert
            controller.togglePause(); // PLAY -> PAUSE
            loop.stop();
            draw();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Stop Game");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to stop the current game?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            loop.stop();
            controller.reset();
            // Go back to main menu
            new Main().showMainMenu(stage);
        } else {                // Press Exit -> select No
            if (wasPlaying) {   // Press Exit While playing game
                controller.togglePause(); // PAUSE -> PLAY
                loop.start();
                draw();
            }
        }
    }

    // ===== Drawing methods =====
    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();

        // Background
        g.setFill(Color.web("#111318"));
        g.fillRect(0, 0, W, H);

        // Board background frame
        double bx = PADDING, by = PADDING;
        double bw = GameBoard.W * TILE, bh = GameBoard.H * TILE;
        g.setFill(Color.web("#1b1f2a"));
        g.fillRoundRect(bx - 4, by - 4, bw + 8, bh + 8, 12, 12);

        // Draw fixed and current blocks
        drawBoardCells(g, bx, by);

        // Game state overlay
        switch (controller.state()) {
            case PAUSE -> drawCenteredOverlay(g, "Game is paused.\nPress P to continue. ");
            case GAME_OVER -> {
                drawCenteredOverlay(g, "GAME OVER\nPress R to Restart\nESC to Menu");
                loop.stop();
            }
            default -> { /* PLAY: no overlay */ }
        }
    }

    // Draws both fixed cells and the current tetromino.
    private void drawBoardCells(GraphicsContext g, double bx, double by) {
        int[][] cells = controller.board().cells();

        // Fixed blocks
        for (int y = 0; y < GameBoard.H; y++) {
            for (int x = 0; x < GameBoard.W; x++) {
                int id = cells[y][x];
                drawCell(g, bx, by, x, y, id);
            }
        }

        // Current tetromino overlay
        Tetromino cur = controller.board().current();
        if (cur != null) {
            int[][] s = cur.shape();
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    if (s[r][c] == 0) continue;
                    int gx = cur.x() + c;
                    int gy = cur.y() + r;
                    if (gy >= 0) {
                        drawCell(g, bx, by, gx, gy, cur.colorId());
                    }
                }
            }
        }

        // Optional grid lines
        g.setStroke(Color.web("#2a3142"));
        for (int x = 0; x <= GameBoard.W; x++) {
            double xx = bx + x * TILE + 0.5;
            g.strokeLine(xx, by, xx, by + GameBoard.H * TILE);
        }
        for (int y = 0; y <= GameBoard.H; y++) {
            double yy = by + y * TILE + 0.5;
            g.strokeLine(bx, yy, bx + GameBoard.W * TILE, yy);
        }
    }

    /**
     * Draws one cell at board coordinates (x, y) with a given block ID.
     */
    private void drawCell(GraphicsContext g, double bx, double by, int x, int y, int id) {
        // Empty cell background
        double px = bx + x * TILE;
        double py = by + y * TILE;
        g.setFill(Color.web("#0f1320"));        // using GraphicsContext, set the color
        g.fillRect(px, py, TILE, TILE);            // filled each cell

        if (id <= 0) return;

        // Fill color with defined block color
        g.setFill(colorOf(id));
        g.fillRect(px + GAP, py + GAP, TILE - GAP * 2, TILE - GAP * 2);

        // Border
        g.setStroke(Color.BLACK);                                           // set border color
        g.strokeRect(px + 0.5, py + 0.5, TILE - 1, TILE - 1); //filled block border
    }

    /**
     * Draws an overlay with centered text (e.g., "PAUSED", "GAME OVER").
     */
    private void drawCenteredOverlay(GraphicsContext g, String text) {
        g.setFill(Color.rgb(0, 0, 0, 0.55));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setFill(Color.WHITE);
        g.setFont(Font.font("Arial", 20));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(text, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    /**
     * Returns block color for given tetromino ID.
     */
    private Color colorOf(int id) {
        return switch (id) {
            case 1 -> Color.CYAN;        // I
            case 2 -> Color.YELLOW;      // O
            case 3 -> Color.MEDIUMPURPLE;// T
            case 4 -> Color.LIMEGREEN;   // S
            case 5 -> Color.RED;         // Z
            case 6 -> Color.ROYALBLUE;   // J
            case 7 -> Color.ORANGE;      // L
            default -> Color.TRANSPARENT;
        };
    }
}
