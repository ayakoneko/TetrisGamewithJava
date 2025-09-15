package tetris.view;

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
import javafx.scene.text.Text;
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
    private final GameSettingsData settings; // UI-safe data

    // Support multiple handlers (Player 1 + Player 2)
    private final GameEventHandler p1Handler;
    private final GameEventHandler p2Handler;
    private final Canvas p1Canvas;
    private final Canvas p2Canvas;

    private final GameLoop loop;
    private final Runnable onExitToMenu;
    private final GameViewModel viewModel;


    //GameView with size canvas from dynamic board, build loop once using level
    public GameView(Stage stage, GameEventHandler p1Handler,GameEventHandler p2Handler, GameSettingsData settings, Runnable onExitToMenu) {
        this.stage = stage;
        this.p1Handler = p1Handler;
        this.p2Handler = p2Handler;
        this.settings = settings;
        this.onExitToMenu = onExitToMenu;
        this.viewModel = new GameViewModel(settings);

        // Canvas size based on board dimensions (dynamic) - get from eventHandler
        GameViewModel.CanvasDimensions d1 = viewModel.calculateCanvasDimensions(
                p1Handler.getBoardWidth(), p1Handler.getBoardHeight(), TILE, PADDING);
        this.p1Canvas = new Canvas(d1.width, d1.height);

        if (isTwoPlayer()) {
            GameViewModel.CanvasDimensions d2 = viewModel.calculateCanvasDimensions(
                    p2Handler.getBoardWidth(), p2Handler.getBoardHeight(), TILE, PADDING);
            this.p2Canvas = new Canvas(d2.width, d2.height);
        } else {
            this.p2Canvas = null;
        }

        // Game loop: Every drop interval (default 500ms) → eventHandler.tick() → draw()
        long interval = viewModel.calculateDropInterval(settings.level());
        this.loop = new GameLoop(interval) {
            @Override protected void update() {
                // Game update through proper event handler
                p1Handler.tick();
                if (isTwoPlayer()) p2Handler.tick();
            }

                // Check if lines were cleared and play sound
                // This will be handled automatically by the controller layer
            @Override protected void render() {
                draw(p1Canvas, p1Handler);
                if (isTwoPlayer()) draw(p2Canvas, p2Handler);
            }
        };
    }
    private boolean isTwoPlayer() { return p2Handler != null; }


    public Scene buildScreen() {
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> askExitToMenu());

        HBox bottomBar = new HBox(backBtn);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(8));

        BorderPane root = new BorderPane();

        if (isTwoPlayer()) {
            HBox boards = new HBox(16);
            boards.setAlignment(Pos.CENTER);
            boards.getChildren().addAll(p1Canvas, p2Canvas);
            root.setCenter(boards);

            double w = p1Canvas.getWidth() + p2Canvas.getWidth() + 16 + 32;
            double h = Math.max(p1Canvas.getHeight(), p2Canvas.getHeight()) + 40;
            Scene scene = new Scene(root, w, h);
            root.setBottom(bottomBar);
            wireInput(scene);
            stage.setTitle("Tetris - Play (2P)");
            stage.setOnCloseRequest(evt -> { evt.consume(); askExitToMenu(); });
            return scene;
        } else {
            root.setCenter(p1Canvas);
            root.setBottom(bottomBar);
            Scene scene = new Scene(root, p1Canvas.getWidth(), p1Canvas.getHeight() + 40);
            wireInput(scene);
            stage.setTitle("Tetris - Play");
            stage.setOnCloseRequest(evt -> { evt.consume(); askExitToMenu(); });
            return scene;
        }
    }

    // Keyboard input handling
    private void wireInput(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (isTwoPlayer()) {
                switch (e.getCode()) {
                    case COMMA -> {
                        if (!p1Handler.isAIActive()) {
                            p1Handler.handlePlayerAction(Action.MOVE_LEFT);
                            p1Handler.playMoveTurnSound();
                            renderOnce();
                        }
                        e.consume();
                    }
                    case PERIOD -> {
                        if (!p1Handler.isAIActive()) {
                            p1Handler.handlePlayerAction(Action.MOVE_RIGHT);
                            p1Handler.playMoveTurnSound();
                            renderOnce();
                        }
                        e.consume();
                    }
                    case SPACE -> {
                        if (!p1Handler.isAIActive()) {
                            p1Handler.handlePlayerAction(Action.SOFT_DROP);
                            renderOnce();
                        }
                        e.consume();
                    }
                    case L -> {
                        if (!p1Handler.isAIActive()) {
                            p1Handler.handlePlayerAction(Action.ROTATE_CW);
                            p1Handler.playMoveTurnSound();
                            renderOnce();
                        }
                        e.consume();
                    }
                    case LEFT -> {
                        if (!p2Handler.isAIActive()) {
                            p2Handler.handlePlayerAction(Action.MOVE_LEFT);
                            p2Handler.playMoveTurnSound();
                            renderOnce();
                        }
                        e.consume();
                    }
                    case RIGHT -> {
                        if (!p2Handler.isAIActive()) {
                            p2Handler.handlePlayerAction(Action.MOVE_RIGHT);
                            p2Handler.playMoveTurnSound();
                            renderOnce();
                        }
                        e.consume();
                    }
                    case DOWN -> {
                        if (!p2Handler.isAIActive()) {
                            p2Handler.handlePlayerAction(Action.SOFT_DROP);
                            renderOnce();
                        }
                        e.consume();
                    }
                    case UP -> {
                        if (!p2Handler.isAIActive()) {
                            p2Handler.handlePlayerAction(Action.ROTATE_CW);
                            p2Handler.playMoveTurnSound();
                            renderOnce();
                        }
                        e.consume();
                    }
                    case P -> { togglePause(); e.consume(); }
                    case R -> {
                        p1Handler.saveCurrentScore();
                        p1Handler.restartGame();
                        p2Handler.saveCurrentScore();
                        p2Handler.restartGame();
                        loop.start();
                        renderOnce();
                        e.consume();
                    }
                    case M -> { p1Handler.toggleMusic(); renderOnce(); e.consume(); }
                    case S -> { p1Handler.toggleSfx();   renderOnce(); e.consume(); }
                    case ESCAPE -> { askExitToMenu(); e.consume(); }
                    default -> {}
                }

            } else {
                Action action = switch (e.getCode()) {
                    case LEFT  -> Action.MOVE_LEFT;
                    case RIGHT -> Action.MOVE_RIGHT;
                    case UP    -> Action.ROTATE_CW;
                    case DOWN  -> Action.SOFT_DROP;
                    case SPACE -> Action.HARD_DROP;
                    default -> null;
                };

            // Block human input during AI mode
            if (action != null) {
                if (p1Handler.isAIActive()) { e.consume(); return; }
                p1Handler.handlePlayerAction(action);
                // Sound effects handled by event handler
                if (action == Action.MOVE_LEFT || action == Action.MOVE_RIGHT || action == Action.ROTATE_CW)
                    p1Handler.playMoveTurnSound();
                renderOnce();
                e.consume(); // Ignore the input
                return;
            }

                switch (e.getCode()) {
                    case P -> togglePause();
                    case R -> {
                        p1Handler.saveCurrentScore();
                        p1Handler.restartGame();
                        loop.start();
                        renderOnce();
                    }
                    case M -> { p1Handler.toggleMusic(); renderOnce(); }
                    case S -> { p1Handler.toggleSfx();   renderOnce(); }
                    case ESCAPE -> askExitToMenu();
                    default -> {}
                }
                e.consume();
            }
        });
    }

    private void renderOnce() {
        draw(p1Canvas, p1Handler);
        if (isTwoPlayer()) draw(p2Canvas, p2Handler);
    }



    public void startGame(){
        stage.setScene(buildScreen());

        p1Handler.startGame();
        if (isTwoPlayer()) p2Handler.startGame();

        stage.show();

        // Audio handled by event handler
        p1Handler.startBackgroundMusic();

        loop.start();
        renderOnce();
    }


    // Toggles pause state and updates the game loop accordingly.
    private void togglePause() {
        // Pause/resume both to keep them in sync
        p1Handler.pauseGame();
        if (isTwoPlayer()) p2Handler.pauseGame();

        GameStateData gameData = p1Handler.getGameStateData();
        if (gameData.gameState() == GameStateData.GameState.PAUSE) {
            loop.stop();
        } else if (gameData.gameState() == GameStateData.GameState.PLAY) {
            loop.start();
        }
        renderOnce(); // Update overlay text
    }


    // Shows confirmation dialog before returning to main menu.
    private void askExitToMenu() {
        GameStateData gameData = p1Handler.getGameStateData();
        boolean wasPlaying = (gameData.gameState() == GameStateData.GameState.PLAY);

        if (wasPlaying) {             // Pause game before show alert
            p1Handler.pauseGame();    // PLAY -> PAUSE
            if (isTwoPlayer()) p2Handler.pauseGame();
            loop.stop();
            renderOnce();
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Stop Game");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to stop the current game?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            loop.stop();
            // Save score before exiting to menu
            p1Handler.saveCurrentScore();
            p1Handler.resetGame();
            if (isTwoPlayer()) {
                p2Handler.saveCurrentScore();
                p2Handler.resetGame();
            }
            p1Handler.stopBackgroundMusic();
            onExitToMenu.run();
        } else {                // Press Exit -> select No
            if (wasPlaying) {   // Press Exit While playing game
                p1Handler.pauseGame(); // PAUSE -> PLAY
                if (isTwoPlayer()) p2Handler.pauseGame();
                loop.start();
                renderOnce();
            }
        }
    }

    // ===== Drawing methods =====
    private void draw(Canvas canvas, GameEventHandler handler) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();

        // Background
        g.setFill(GameViewModel.BACKGROUND_COLOR);
        g.fillRect(0, 0, W, H);

        GameStateData gameData = handler.getGameStateData();
        int BW = handler.getBoardWidth();
        int BH = handler.getBoardHeight();

        // Board background frame
        double bx = PADDING, by = PADDING;
        double bw = BW * TILE, bh = BH * TILE;

        g.setFill(GameViewModel.BOARD_FRAME_COLOR);
        g.fillRoundRect(bx - 4, by - 4, bw + 8, bh + 8, 12, 12);

        // Draw fixed and current blocks
        drawBoardCells(g, bx, by, gameData, BW, BH);

        // Game state overlay
        switch (gameData.gameState()) {
            case PAUSE -> drawCenteredOverlay(g, canvas, "Game is paused.\nPress P to continue. ");
            case GAME_OVER -> {
                drawCenteredOverlay(g, canvas, "GAME OVER\nPress R to Restart\nESC to Menu");
                loop.stop();
            }
            default -> { /* PLAY: no overlay */ }
        }

        drawHud(g, canvas, gameData.currentScore());
    }


    // Draws both fixed cells and the current tetromino.
    private void drawBoardCells(GraphicsContext g, double bx, double by,
                                GameStateData gameData, int BW, int BH) {
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
                    if (gx >= 0 && gx < BW && gy >= 0 && gy < BH) {
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

        if (pos == null) {
            System.err.printf("drawCell(): null pos at (%d,%d) bx=%.1f by=%.1f TILE=%d%n",
                    x, y, bx, by, TILE);
            return;
        }

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

    // === HUD width fix ===
    private double textWidth(String s, Font f) {
        Text t = new Text(s);
        t.setFont(f);
        return Math.ceil(t.getLayoutBounds().getWidth());
    }
    // word-wrap the HUD label into multiple lines that fit maxWidth
    private java.util.List<String> wrapText(String text, Font font, double maxWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            String candidate = (line.isEmpty()) ? word : (line + " " + word);
            if (textWidth(candidate, font) <= maxWidth) {
                line = new StringBuilder(candidate);
            } else {
                if (!line.isEmpty()) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    // single long token: put it alone to avoid infinite loop
                    lines.add(word);
                }
            }
        }
        if (!line.isEmpty()) lines.add(line.toString());
        return lines;
    }

    private Font fitFontToWidth(String text, double maxWidth, Font baseFont) {
        double w = textWidth(text, baseFont);
        if (w <= maxWidth) return baseFont;
        double scale = maxWidth / Math.max(1.0, w);
        double newSize = Math.max(10, baseFont.getSize() * scale); // don't go smaller than 10
        return Font.font(baseFont.getFamily(), newSize);
    }

    /**
     * Draws an overlay with centered text (e.g., "PAUSED", "GAME OVER").
     */
    private void drawCenteredOverlay(GraphicsContext g, Canvas canvas, String text) {
        g.setFill(GameViewModel.OVERLAY_BACKGROUND);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setFill(GameViewModel.TEXT_COLOR);
        g.setFont(Font.font("Arial", 20));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(text, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }


    private void drawHud(GraphicsContext g, Canvas canvas, int score) {
        String full = viewModel.formatHudText(score);

        double pad = PADDING;
        double x = pad, y = 6;
        double w = canvas.getWidth() - pad * 2;

        // === HUD wrap fix ===
        Font font = Font.font("Arial", 14);
        java.util.List<String> lines = wrapText(full, font, w - 10);
        double lineHeight = font.getSize() + 2;
        double rectHeight = Math.max(24, 4 + lines.size() * lineHeight + 4);

        g.setFill(GameViewModel.HUD_BACKGROUND);
        g.fillRoundRect(x, y, w, rectHeight, 10, 10);

        g.setFill(GameViewModel.TEXT_COLOR);
        g.setFont(font);
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.TOP);

        double baseY = y + 4;
        for (int i = 0; i < lines.size(); i++) {
            g.fillText(lines.get(i), canvas.getWidth() / 2, baseY + i * lineHeight);
        }
    }
}
