package tetris.view;

import java.util.Optional;

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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import tetris.common.Action;
import tetris.controller.event.GameEventHandler;
import tetris.dto.GameSettingsData;
import tetris.dto.GameStateData;
import tetris.dto.TetrominoData;
import tetris.viewmodel.GameViewModel;

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
    private final GameEventHandler eventHandler;
    private final GameSettingsData settings; // UI-safe data
    private final Canvas canvas;
    private final GameLoop loop;
    private final Runnable onExitToMenu;
    private final GameViewModel viewModel;


    //GameView with size canvas from dynamic board, build loop once using level
    public GameView(Stage stage, GameEventHandler eventHandler, GameSettingsData settings, Runnable onExitToMenu) {
        this.stage = stage;
        this.eventHandler = eventHandler;
        this.settings = settings;
        this.onExitToMenu = onExitToMenu;
        this.viewModel = new GameViewModel(settings);

        // Canvas size based on board dimensions (dynamic) - get from eventHandler
        GameViewModel.CanvasDimensions dimensions = viewModel.calculateCanvasDimensions(
                eventHandler.getBoardWidth(), eventHandler.getBoardHeight(), TILE, PADDING);
        this.canvas = new Canvas(dimensions.width, dimensions.height);

        // Game loop: Every drop interval (default 500ms) → eventHandler.tick() → draw()
        long interval = viewModel.calculateDropInterval(settings.level());
        this.loop = new GameLoop(interval) {
            @Override protected void update() {
                // Game update through proper event handler
                eventHandler.tick();
                
                // Check if lines were cleared and play sound
                // This will be handled automatically by the controller layer
            }
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

        //escape (askExitToMenu()) on screen close button
        stage.setOnCloseRequest(evt -> {
            evt.consume();
            askExitToMenu();
        });

        // Keyboard input handling
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            Action action = switch (e.getCode()) {
                case LEFT   -> Action.MOVE_LEFT;
                case RIGHT  -> Action.MOVE_RIGHT;
                case UP     -> Action.ROTATE_CW;
                case DOWN   -> Action.SOFT_DROP;
                case SPACE  -> Action.HARD_DROP;
                default     -> null;
            };

            if (action != null) {
                // Block human input during AI mode
                if (eventHandler.isAIActive()) {
                    e.consume(); // Ignore the input
                    return;
                }
                
                eventHandler.handlePlayerAction(action);

                // Sound effects handled by event handler
                if (action == Action.MOVE_LEFT || action == Action.MOVE_RIGHT || action == Action.ROTATE_CW) {
                    eventHandler.playMoveTurnSound();
                }

                draw();
                e.consume();
                return;
            }

            switch (e.getCode()) {
                case P -> togglePause();
                case R -> {
                    // Save current score before restarting
                    eventHandler.saveCurrentScore();
                    eventHandler.restartGame();
                    loop.start();
                    draw();
                }
                case M -> {
                    eventHandler.toggleMusic();
                    draw();
                }
                case S -> {
                    eventHandler.toggleSfx();
                    draw();
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
        eventHandler.startGame();
        stage.show();

        // Audio handled by event handler
        eventHandler.startBackgroundMusic();

        loop.start();
        draw();
    }

    // Toggles pause state and updates the game loop accordingly.
    private void togglePause() {
        eventHandler.pauseGame();
        GameStateData gameData = eventHandler.getGameStateData();
        if (gameData.gameState() == GameStateData.GameState.PAUSE) {
            loop.stop();
        } else if (gameData.gameState() == GameStateData.GameState.PLAY) {
            loop.start();
        }
        draw(); // Update overlay text
    }

    // Shows confirmation dialog before returning to main menu.
    private void askExitToMenu() {
        GameStateData gameData = eventHandler.getGameStateData();
        boolean wasPlaying = (gameData.gameState() == GameStateData.GameState.PLAY);

        if (wasPlaying) {             // Pause game before show alert
            eventHandler.pauseGame(); // PLAY -> PAUSE
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
            // Save score before exiting to menu
            eventHandler.saveCurrentScore();
            eventHandler.resetGame();
            eventHandler.stopBackgroundMusic();
            onExitToMenu.run();
        } else {                // Press Exit -> select No
            if (wasPlaying) {   // Press Exit While playing game
                eventHandler.pauseGame(); // PAUSE -> PLAY
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
        g.setFill(GameViewModel.BACKGROUND_COLOR);
        g.fillRect(0, 0, W, H);

        GameStateData gameData = eventHandler.getGameStateData();
        int BW = eventHandler.getBoardWidth();
        int BH = eventHandler.getBoardHeight();

        // Board background frame
        double bx = PADDING, by = PADDING;
        double bw = BW * TILE, bh = BH * TILE;

        g.setFill(GameViewModel.BOARD_FRAME_COLOR);
        g.fillRoundRect(bx - 4, by - 4, bw + 8, bh + 8, 12, 12);

        // Draw fixed and current blocks
        drawBoardCells(g, bx, by, gameData);

        // Game state overlay
        switch (gameData.gameState()) {
            case PAUSE -> drawCenteredOverlay(g, "Game is paused.\nPress P to continue. ");
            case GAME_OVER -> {
                drawCenteredOverlay(g, "GAME OVER\nPress R to Restart\nESC to Menu");
                loop.stop();
            }
            default -> { /* PLAY: no overlay */ }
        }

        drawHud(g);
    }

    // Draws both fixed cells and the current tetromino.
    private void drawBoardCells(GraphicsContext g, double bx, double by, GameStateData gameData) {
        int BW = eventHandler.getBoardWidth();
        int BH = eventHandler.getBoardHeight();
        int[][] cells = gameData.boardCells();

        // Fixed blocks
        for (int y = 0; y < BH; y++)
            for (int x = 0; x < BW; x++)
                drawCell(g, bx, by, x, y, cells[y][x]);

        // Current tetromino overlay
        TetrominoData current = gameData.currentPiece();
        if (current != null) {
            int[][] shape = current.shape();
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    if (shape[r][c] == 0) continue;
                    int gx = current.x() + c;
                    int gy = current.y() + r;
                    if (gy >= 0) {
                        drawCell(g, bx, by, gx, gy, current.colorId());
                    }
                }
            }
        }

        // grid lines
        g.setStroke(GameViewModel.GRID_LINE_COLOR);
        for (int x = 0; x <= BW; x++)
            g.strokeLine(bx + x * TILE + 0.5, by, bx + x * TILE + 0.5, by + BH * TILE);
        for (int y = 0; y <= BH; y++)
            g.strokeLine(bx, by + y * TILE + 0.5, bx + BW * TILE, by + y * TILE + 0.5);
    }

    /**
     * Draws one cell at board coordinates (x, y) with a given block ID.
     */
    private void drawCell(GraphicsContext g, double bx, double by, int x, int y, int id) {
        // Empty cell background
        GameViewModel.PixelPosition pos = viewModel.getBoardPixelPosition(x, y, bx, by, TILE);
        g.setFill(GameViewModel.EMPTY_CELL_COLOR);        // using GraphicsContext, set the color
        g.fillRect(pos.x, pos.y, TILE, TILE);            // filled each cell

        if (id <= 0) return;

        // Fill color with defined block color
        g.setFill(viewModel.getTetrominoColor(id));
        g.fillRect(pos.x + GAP, pos.y + GAP, TILE - GAP * 2, TILE - GAP * 2);

        // Border
        g.setStroke(GameViewModel.BORDER_COLOR);                                           // set border color
        g.strokeRect(pos.x + 0.5, pos.y + 0.5, TILE - 1, TILE - 1); //filled block border
    }

    /**
     * Draws an overlay with centered text (e.g., "PAUSED", "GAME OVER").
     */
    private void drawCenteredOverlay(GraphicsContext g, String text) {
        g.setFill(GameViewModel.OVERLAY_BACKGROUND);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setFill(GameViewModel.TEXT_COLOR);
        g.setFont(Font.font("Arial", 20));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(text, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }


    private void drawHud(GraphicsContext g) {
        GameStateData gameData = eventHandler.getGameStateData();
        String label = viewModel.formatHudText(gameData.currentScore());

        double pad = PADDING;
        double x = pad, y = 6;
        double w = canvas.getWidth() - pad * 2;
        double h = 24;

        g.setFill(GameViewModel.HUD_BACKGROUND);
        g.fillRoundRect(x, y, w, h, 10, 10);

        g.setFill(GameViewModel.TEXT_COLOR);
        g.setFont(Font.font("Arial", 14));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.TOP);
        g.fillText(label, canvas.getWidth() / 2, y + 4);
    }
}
