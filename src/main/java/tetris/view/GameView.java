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
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import tetris.common.Action;
import tetris.common.UiGameState;
import tetris.controller.command.CommandBindings;
import tetris.controller.command.GameCommand;
import tetris.controller.event.GameEventHandler;
import tetris.dto.GameSettingsData;
import tetris.dto.GameStateData;
import tetris.dto.TetrominoData;
import tetris.viewmodel.GameViewModel;

import java.util.Map;
import java.util.Optional;

/**
 * GameView: Main game view that renders the Tetris game using JavaFX UI components.
 * 
 * Features:
 * - Canvas-based game board rendering with GraphicsContext
 * - JavaFX UI controls for HUD (side panels with player info, audio controls, next block preview)
 * - CSS styling support for modern UI appearance
 * - Support for both single-player and two-player modes
 * - Real-time updates for scores, levels, lines, and audio states
 * 
 * Key Methods:
 * - startGame(): Initialize and start the game loop
 * - buildScreen(): Create the main game scene with layout
 * - draw(): Render the game board and tetrominos
 * - updateHUD(): Update side panel information in real-time
 */

/**
 * GameView: Pure UI rendering component following MVC pattern.
 * 
 * Responsibilities:
 * - UI rendering only (Canvas drawing, HUD updates)
 * - User input forwarding to GameEventHandler
 * - No direct business logic or game state management
 * 
 * MVC Pattern:
 * - View: This class (pure UI)
 * - Controller: GameEventHandler (handles user input and business logic)
 * - Model: GameController + GameBoard (game state and data)
 */
public class GameView {
    // ==================== UI CONSTANTS ====================
    private static final int TILE = 30;        // Size of one tile (px)
    private static final int GAP  = 1;         // Gap between tiles (border effect)
    private static final int PADDING = 12;     // Padding around the board
    private static final int SIDE_PANEL_WIDTH = 240; // Increased width for better text visibility

    private final Stage stage;

    // Support multiple handlers (Player 1 + Player 2)
    private final GameEventHandler p1Handler;
    private final GameEventHandler p2Handler;
    private final Canvas p1Canvas;
    private final Canvas p2Canvas;
    private final VBox p1SidePanel;
    private final VBox p2SidePanel;

    private final GameLoop loop;
    private final Runnable onExitToMenu;
    private final GameViewModel viewModel;

    // Flag to ensure scores are only submitted once per game over
    private boolean scoresAlreadySubmitted = false;

    // ==================== CONSTRUCTOR & INITIALIZATION ====================
    
    public GameView(Stage stage, GameEventHandler p1Handler,GameEventHandler p2Handler, GameSettingsData settings, Runnable onExitToMenu) {
        this.stage = stage;
        this.p1Handler = p1Handler;
        this.p2Handler = p2Handler;
        this.onExitToMenu = onExitToMenu;
        this.viewModel = new GameViewModel();

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

        // Create side panel VBoxes
        GameViewModel.CanvasDimensions d1Panel = viewModel.calculateCanvasDimensions(
                p1Handler.getBoardWidth(), p1Handler.getBoardHeight(), TILE, PADDING);
        this.p1SidePanel = createSidePanel(p1Handler, d1Panel.height);
        
        if (isTwoPlayer()) {
            GameViewModel.CanvasDimensions d2Panel = viewModel.calculateCanvasDimensions(
                    p2Handler.getBoardWidth(), p2Handler.getBoardHeight(), TILE, PADDING);
            this.p2SidePanel = createSidePanel(p2Handler, d2Panel.height);
        } else {
            this.p2SidePanel = null;
        }

        // Game loop: Pure rendering loop - no business logic
        long interval = viewModel.calculateDropInterval(settings.level());
        this.loop = new GameLoop(interval) {
            @Override protected void update() {
                // Delegate game logic to controllers
                p1Handler.tick();
                if (isTwoPlayer()) p2Handler.tick();
            }

            @Override protected void render() {
                // Pure UI rendering - no business logic
                renderGameBoard(p1Canvas, p1Handler);
                if (isTwoPlayer()) renderGameBoard(p2Canvas, p2Handler);
                updateHUD();
            }
        };
    }

    // ==================== HUD CREATION METHODS ====================
    
    private VBox createSidePanel(GameEventHandler handler, double height) {
        VBox sidePanel = new VBox(20);
        sidePanel.setPrefWidth(SIDE_PANEL_WIDTH);
        sidePanel.setPrefHeight(height);
        sidePanel.getStyleClass().add("hud-side-panel");
        
        // Player Info Section
        VBox playerInfoSection = createPlayerInfoSection(handler);
        sidePanel.getChildren().add(playerInfoSection);
        
        // Audio Controls Section
        VBox audioControlsSection = createAudioControlsSection(handler);
        sidePanel.getChildren().add(audioControlsSection);
        
        // Next Block Section
        VBox nextBlockSection = createNextBlockSection(handler);
        sidePanel.getChildren().add(nextBlockSection);
        
        return sidePanel;
    }

    private VBox createPlayerInfoSection(GameEventHandler handler) {
        VBox section = new VBox(10);
        section.getStyleClass().addAll("hud-section", "hud-player-info");

        // Title
        Label title = new Label("PLAYER INFO");
        title.getStyleClass().add("hud-title");
        section.getChildren().add(title);

        // Player Name
        Label playerName = new Label(handler.getPlayerName());
        playerName.getStyleClass().add("hud-player-name");
        section.getChildren().add(playerName);

        // Player Type
        Label playerType = new Label("Type: " + handler.getPlayerTypeDisplay());
        playerType.getStyleClass().add("hud-stat");
        section.getChildren().add(playerType);

        // Score
        Label scoreLabel = new Label("Score: " + handler.getCurrentScore());
        scoreLabel.getStyleClass().add("hud-stat");
        section.getChildren().add(scoreLabel);

        // Level
        Label levelLabel = new Label("Level: " + handler.getCurrentLevel());
        levelLabel.getStyleClass().add("hud-stat");
        section.getChildren().add(levelLabel);

        // Lines
        Label linesLabel = new Label("Lines: " + handler.getTotalLinesCleared());
        linesLabel.getStyleClass().add("hud-stat");
        section.getChildren().add(linesLabel);

        return section;
    }

    private VBox createAudioControlsSection(GameEventHandler handler) {
        VBox section = new VBox(10);
        section.getStyleClass().addAll("hud-section", "hud-audio-controls");
        
        // Title
        Label title = new Label("AUDIO CONTROLS");
        title.getStyleClass().add("hud-title");
        section.getChildren().add(title);
        
        // Audio Controls
        String musicKey = handler.getPlayerNumber() == 1 ? "[M1]" : "[M2]";
        String sfxKey = handler.getPlayerNumber() == 1 ? "[S1]" : "[S2]";
        String musicStatus = handler.isMusicOn() ? "On" : "Off";
        String sfxStatus = handler.isSfxOn() ? "On" : "Off";
        
        Label musicLabel = new Label("🎶 Music " + musicKey + ": " + musicStatus);
        musicLabel.getStyleClass().addAll("hud-audio-text", 
            musicStatus.equals("On") ? "hud-audio-on" : "hud-audio-off");
        section.getChildren().add(musicLabel);
        
        Label sfxLabel = new Label("SFX " + sfxKey + ": " + sfxStatus);
        sfxLabel.getStyleClass().addAll("hud-audio-text", 
            sfxStatus.equals("On") ? "hud-audio-on" : "hud-audio-off");
        section.getChildren().add(sfxLabel);
        
        return section;
    }

    private VBox createNextBlockSection(GameEventHandler handler) {
        VBox section = new VBox(10);
        section.getStyleClass().addAll("hud-section", "hud-next-block");
        
        // Title
        Label title = new Label("NEXT BLOCK");
        title.getStyleClass().add("hud-next-title");
        section.getChildren().add(title);
        
        // Next Block Preview
        StackPane previewPane = new StackPane();
        previewPane.getStyleClass().add("hud-next-preview");
        previewPane.setPrefSize(60, 60);
        
        // Create a small canvas for tetromino preview
        Canvas previewCanvas = new Canvas(60, 60);
        drawTetrominoInGrid(previewCanvas.getGraphicsContext2D(), 
            handler.getNextTetrominoType(), 30, 30);
        previewPane.getChildren().add(previewCanvas);
        
        section.getChildren().add(previewPane);
        
        return section;
    }

    private void updateHUD() {
        updateSidePanel(p1SidePanel, p1Handler);
        if (isTwoPlayer()) {
            updateSidePanel(p2SidePanel, p2Handler);
        }
    }

    private void updateSidePanel(VBox sidePanel, GameEventHandler handler) {
        // Update Player Info Section
        VBox playerInfoSection = (VBox) sidePanel.getChildren().get(0);
        updatePlayerInfoSection(playerInfoSection, handler);
        
        // Update Audio Controls Section
        VBox audioControlsSection = (VBox) sidePanel.getChildren().get(1);
        updateAudioControlsSection(audioControlsSection, handler);
        
        // Update Next Block Section
        VBox nextBlockSection = (VBox) sidePanel.getChildren().get(2);
        updateNextBlockSection(nextBlockSection, handler);
    }

    private void updatePlayerInfoSection(VBox section, GameEventHandler handler) {
        // Update player name
        Label playerNameLabel = (Label) section.getChildren().get(1);
        playerNameLabel.setText(handler.getPlayerName());

        // Update player type
        Label playerTypeLabel = (Label) section.getChildren().get(2);
        playerTypeLabel.setText("Type: " + handler.getPlayerTypeDisplay());

        // Update stats
        Label scoreLabel = (Label) section.getChildren().get(3);
        scoreLabel.setText("Score: " + handler.getCurrentScore());

        Label levelLabel = (Label) section.getChildren().get(4);
        levelLabel.setText("Level: " + handler.getCurrentLevel());

        Label linesLabel = (Label) section.getChildren().get(5);
        linesLabel.setText("Lines: " + handler.getTotalLinesCleared());
    }

    private void updateAudioControlsSection(VBox section, GameEventHandler handler) {
        String musicKey = handler.getPlayerNumber() == 1 ? "[M1]" : "[M2]";
        String sfxKey = handler.getPlayerNumber() == 1 ? "[S1]" : "[S2]";
        String musicStatus = handler.isMusicOn() ? "On" : "Off";
        String sfxStatus = handler.isSfxOn() ? "On" : "Off";
        
        // Update music label
        Label musicLabel = (Label) section.getChildren().get(1);
        musicLabel.setText("Music " + musicKey + ": " + musicStatus);
        musicLabel.getStyleClass().clear();
        musicLabel.getStyleClass().addAll("hud-audio-text", 
            musicStatus.equals("On") ? "hud-audio-on" : "hud-audio-off");
        
        // Update SFX label
        Label sfxLabel = (Label) section.getChildren().get(2);
        sfxLabel.setText("SFX " + sfxKey + ": " + sfxStatus);
        sfxLabel.getStyleClass().clear();
        sfxLabel.getStyleClass().addAll("hud-audio-text", 
            sfxStatus.equals("On") ? "hud-audio-on" : "hud-audio-off");
    }

    private void updateNextBlockSection(VBox section, GameEventHandler handler) {
        // Update tetromino preview
        StackPane previewPane = (StackPane) section.getChildren().get(1);
        Canvas previewCanvas = (Canvas) previewPane.getChildren().get(0);
        
        // Clear and redraw tetromino
        GraphicsContext g = previewCanvas.getGraphicsContext2D();
        g.clearRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());
        drawTetrominoInGrid(g, handler.getNextTetrominoType(), 30, 30);
    }

    // ==================== GAME CONTROL DELEGATION ====================
    // All game control logic is delegated to GameEventHandler (Controller)
    
    private void pauseBothPlayers() {
        p1Handler.pauseGame();
        if (isTwoPlayer()) p2Handler.pauseGame();
    }
    
    private void startBothPlayers() {
        p1Handler.startGame();
        if (isTwoPlayer()) p2Handler.startGame();
    }

    private boolean isTwoPlayer() { return p2Handler != null; }

    // Set player name in the game event handler
    public void setPlayerName(String playerName) {
        p1Handler.setPlayerName(playerName);
        if (isTwoPlayer()) {
            p2Handler.setPlayerName(playerName);
        }
    }

    // Set individual player names for two-player mode
    public void setPlayer1Name(String playerName) {
        p1Handler.setPlayerName(playerName);
    }

    public void setPlayer2Name(String playerName) {
        if (isTwoPlayer()) {
            p2Handler.setPlayerName(playerName);
        }
    }

    // ==================== SCENE BUILDING ====================
    
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
            boards.getChildren().addAll(p1SidePanel, p1Canvas, p2Canvas, p2SidePanel);
            root.setCenter(boards);

            // Calculate proper window size for 2P mode: side panels + canvases + spacing + padding
            double w = p1SidePanel.getPrefWidth() + p1Canvas.getWidth() + p2Canvas.getWidth() + p2SidePanel.getPrefWidth() + 48 + 32;
            double h = Math.max(p1Canvas.getHeight(), p2Canvas.getHeight()) + 60; // Extra space for bottom bar
            Scene scene = new Scene(root, w, h);
            scene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
            root.setBottom(bottomBar);
            wireInput(scene);
            stage.setTitle("Tetris - Play (2P)");
            stage.setOnCloseRequest(evt -> { evt.consume(); askExitToMenu(); });
            return scene;
        } else {
            HBox singlePlayerLayout = new HBox(16);
            singlePlayerLayout.setAlignment(Pos.CENTER);
            singlePlayerLayout.getChildren().addAll(p1Canvas, p1SidePanel);
            root.setCenter(singlePlayerLayout);
            root.setBottom(bottomBar);
            // Calculate proper window size for 1P mode: canvas + side panel + spacing + padding
            Scene scene = new Scene(root, p1Canvas.getWidth() + p1SidePanel.getPrefWidth() + 32, p1Canvas.getHeight() + 60);
            scene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
            wireInput(scene);
            stage.setTitle("Tetris - Play");
            stage.setOnCloseRequest(evt -> { evt.consume(); askExitToMenu(); });
            return scene;
        }
    }

    // ==================== INPUT HANDLING ====================
    // Pure input forwarding to Controller - no business logic in View
    
    private void wireInput(Scene scene) {
        // choose bindings per mode
        final Map<KeyCode, GameCommand> p1Map;
        final Map<KeyCode, GameCommand> p2Map;

        if (isTwoPlayer()) {
            // assignment's external-mapping requirement
            p1Map = CommandBindings.player1External();
            p2Map = CommandBindings.player2External();
        } else {
            p1Map = CommandBindings.singlePlayerHuman();
            p2Map = java.util.Collections.emptyMap();
        }

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            // P1
            GameCommand cmd = p1Map.get(e.getCode());
            if (cmd != null) {
                cmd.execute(p1Handler);
                renderOnce();
                e.consume();
                return;
            }
            // P2
            if (isTwoPlayer()) {
                cmd = p2Map.get(e.getCode());
                if (cmd != null) {
                    cmd.execute(p2Handler);
                    renderOnce();
                    e.consume();
                    return;
                }
            }
            // system keys (common)
            switch (e.getCode()) {
                case P -> { forwardPauseToggle(); e.consume(); }
                case R -> { forwardRestart(); e.consume(); }
                case M -> { p1Handler.toggleMusic(); renderOnce(); e.consume(); }
                case S -> { p1Handler.toggleSfx();   renderOnce(); e.consume(); }
                case ESCAPE -> { askExitToMenu(); e.consume(); }
                default -> {}
            }
        });
    }
    
    private void handleTwoPlayerInput(KeyEvent e) {
        switch (e.getCode()) {
            case COMMA -> forwardPlayerAction(p1Handler, Action.MOVE_LEFT, e);
            case PERIOD -> forwardPlayerAction(p1Handler, Action.MOVE_RIGHT, e);
            case SPACE -> forwardPlayerAction(p1Handler, Action.SOFT_DROP, e);
            case L -> forwardPlayerAction(p1Handler, Action.ROTATE_CW, e);
            case LEFT -> forwardPlayerAction(p2Handler, Action.MOVE_LEFT, e);
            case RIGHT -> forwardPlayerAction(p2Handler, Action.MOVE_RIGHT, e);
            case DOWN -> forwardPlayerAction(p2Handler, Action.SOFT_DROP, e);
            case UP -> forwardPlayerAction(p2Handler, Action.ROTATE_CW, e);
            case P -> { forwardPauseToggle(); e.consume(); }
            case R -> { forwardRestart(); e.consume(); }
            case M -> { forwardAudioToggle('M'); e.consume(); }
            case S -> { forwardAudioToggle('S'); e.consume(); }
            case ESCAPE -> { forwardExitToMenu(); e.consume(); }
            default -> {}
        }
    }

    // Pure input forwarding methods - no business logic
    private void forwardPlayerAction(GameEventHandler handler, Action action, KeyEvent e) {
        if (!handler.isAIActive()) {
            handler.handlePlayerAction(action);
            handler.playMoveTurnSound();
            renderOnce();
        }
        e.consume();
    }
    
    private void forwardPauseToggle() {
        pauseBothPlayers();
        updateGameLoopState();
        renderOnce();
    }
    
    private void forwardRestart() {
        // Reset score submission flag for restarted game
        scoresAlreadySubmitted = false;

        p1Handler.restartGame();
        if (isTwoPlayer()) p2Handler.restartGame();
        loop.start();
        renderOnce();
    }
    
    private void forwardAudioToggle(char key) {
        switch (key) {
            case 'M' -> p1Handler.toggleMusic();
            case 'S' -> p1Handler.toggleSfx();
        }
        renderOnce();
    }
    
    private void forwardExitToMenu() {
        askExitToMenu();
    }

    private void renderOnce() {
        renderGameBoard(p1Canvas, p1Handler);
        if (isTwoPlayer()) renderGameBoard(p2Canvas, p2Handler);
        updateHUD();
    }
    
    private void updateGameLoopState() {
        GameStateData gameData = p1Handler.getGameStateData();
        if (gameData.gameState() == UiGameState.PAUSE) {
            loop.stop();
        } else if (gameData.gameState() == UiGameState.PLAY) {
            loop.start();
        }
    }

    // Submit scores for all players, checking each independently for top 10 qualification
    private void submitAllPlayerScores() {
        // Always check Player 1
        p1Handler.submitStoredScore();

        // Check Player 2 if in two-player mode
        if (isTwoPlayer()) {
            p2Handler.submitStoredScore();
        }
    }



    // ==================== GAME LIFECYCLE ====================
    // Pure UI lifecycle management - business logic delegated to Controller
    
    public void startGame(){
        stage.setScene(buildScreen());
        startBothPlayers();
        stage.show();

        // Reset score submission flag for new game
        scoresAlreadySubmitted = false;

        // Audio handled by event handler
        p1Handler.startBackgroundMusic();

        loop.start();
        renderOnce();
    }


    // Shows confirmation dialog before returning to main menu.
    private void askExitToMenu() {
        GameStateData gameData = p1Handler.getGameStateData();
        boolean wasPlaying = (gameData.gameState() == UiGameState.PLAY);

        if (wasPlaying) {             // Pause game before show alert
            pauseBothPlayers();       // PLAY -> PAUSE
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

            // Submit scores before exiting if game was in progress
            if (wasPlaying) {
                submitAllPlayerScores();
            }

            // Reset game before exiting to menu - delegate to controller
            p1Handler.resetGame();
            if (isTwoPlayer()) {
                p2Handler.resetGame();
            }
            p1Handler.stopBackgroundMusic();
            onExitToMenu.run();
        } else {                // Press Exit -> select No
            if (wasPlaying) {   // Press Exit While playing game
                pauseBothPlayers(); // PAUSE -> PLAY
                loop.start();
                renderOnce();
            }
        }
    }

    // ==================== RENDERING METHODS ====================
    // Pure UI rendering - no business logic, only visual representation
    
    private void renderGameBoard(Canvas canvas, GameEventHandler handler) {
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

        // Game state overlay - pure UI rendering
        switch (gameData.gameState()) {
            case PAUSE -> drawCenteredOverlay(g, canvas, "Game is paused.\nPress P to continue. ");
            case GAME_OVER -> {
                drawCenteredOverlay(g, canvas, "GAME OVER\nPress R to Restart\nESC to Menu");
                loop.stop();
                // Score submission for all players when any player reaches game over (only once)
                if (!scoresAlreadySubmitted) {
                    submitAllPlayerScores();
                    scoresAlreadySubmitted = true;
                }
            }
            case PLAY -> {
                // Show warning for external players when server unavailable
                if (handler.isExternalPlayer() && !handler.isExternalServerAvailable()) {
                    drawWarningOverlay(g, canvas, "EXTERNAL SERVER UNAVAILABLE\nNo player control\nStart TetrisServer.jar to resume");
                }
            }
            default -> { /* Other states */ }
        }
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

    /**
     * Draws a warning overlay with semi-transparent background (for server unavailable warning).
     */
    private void drawWarningOverlay(GraphicsContext g, Canvas canvas, String text) {
        // Semi-transparent orange background
        g.setFill(javafx.scene.paint.Color.rgb(255, 165, 0, 0.7));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setFill(javafx.scene.paint.Color.WHITE);
        g.setFont(Font.font("Arial", 16));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(text, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    private void drawTetrominoInGrid(GraphicsContext g, tetris.model.tetromino.TetrominoType type, double centerX, double centerY) {
        if (type == null) return;
        
        // Get the tetromino shape and color
        tetris.model.tetromino.Tetromino previewTetromino = new tetris.model.tetromino.Tetromino(type, 0, 0);
        int[][] shape = previewTetromino.shape();
        int colorId = previewTetromino.colorId();

        // Calculate grid size - smaller for preview
        double gridSize = 60;
        double cellSize = gridSize / 4;
        double offsetX = centerX - gridSize / 2;
        double offsetY = centerY - gridSize / 2;

        // Draw grid background
        g.setFill(GameViewModel.EMPTY_CELL_COLOR);
        g.fillRect(offsetX, offsetY, gridSize, gridSize);
        
        // Grid border
        g.setStroke(GameViewModel.BORDER_COLOR);
        g.setLineWidth(1);
        g.strokeRect(offsetX, offsetY, gridSize, gridSize);

        // Draw grid lines
        g.setStroke(GameViewModel.GRID_LINE_COLOR);
        g.setLineWidth(0.5);
        for (int i = 1; i < 4; i++) {
            double lineX = offsetX + i * cellSize;
            double lineY = offsetY + i * cellSize;
            g.strokeLine(lineX, offsetY, lineX, offsetY + gridSize);
            g.strokeLine(offsetX, lineY, offsetX + gridSize, lineY);
        }

        // Draw the tetromino shape with original game styling
        g.setFill(viewModel.getTetrominoColor(colorId));
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    double x = offsetX + c * cellSize;
                    double y = offsetY + r * cellSize;
                    
                    // Draw cell like in the main game - simple rectangle with border
                    g.fillRect(x + 0.5, y + 0.5, cellSize - 1, cellSize - 1);
                    
                    // Add border like main game cells
                    g.setStroke(GameViewModel.BORDER_COLOR);
                    g.setLineWidth(0.5);
                    g.strokeRect(x + 0.5, y + 0.5, cellSize - 1, cellSize - 1);
                }
            }
        }
    }
}
