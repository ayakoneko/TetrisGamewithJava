package tetris.viewmodel;

import javafx.scene.paint.Color;
import tetris.dto.GameSettingsData;

public class GameViewModel {
    
    // UI Color constants
    public static final Color BACKGROUND_COLOR = Color.web("#111318");
    public static final Color BOARD_FRAME_COLOR = Color.web("#1b1f2a");
    public static final Color GRID_LINE_COLOR = Color.web("#2a3142");
    public static final Color EMPTY_CELL_COLOR = Color.web("#0f1320");
    public static final Color BORDER_COLOR = Color.BLACK;
    public static final Color OVERLAY_BACKGROUND = Color.rgb(0, 0, 0, 0.55);
    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color HUD_BACKGROUND = Color.rgb(0, 0, 0, 0.35);
    
    private final GameSettingsData settings;
    
    public GameViewModel(GameSettingsData settings) {
        this.settings = settings;
    }
    
    // Returns the color for a given tetromino block ID
    public Color getTetrominoColor(int id) {
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
    
    // Game speed calculation based on level
    public long calculateDropInterval(int level) {
        long base = 700_000_000L;          // 700ms
        long step = 50_000_000L;           // 50ms per level
        int lvl = Math.max(1, Math.min(level, 10)); // clamp between Level 1–10
        return base - ((long)(lvl - 1) * step);
    }
    
    // Formats HUD text based on current settings
    public String formatHudText() {
        String musicTxt = settings.musicOn() ? "On" : "Off";
        String sfxTxt   = settings.sfxOn()   ? "On" : "Off";
        return "Music [M]: " + musicTxt + "    SFX [S]: " + sfxTxt;
    }
    
    // Calculates canvas dimensions based on board size
    public static class CanvasDimensions {
        public final double width;
        public final double height;
        
        public CanvasDimensions(double width, double height) {
            this.width = width;
            this.height = height;
        }
    }
    
    public CanvasDimensions calculateCanvasDimensions(int boardWidth, int boardHeight, 
                                                     int tileSize, int padding) {
        double w = boardWidth * tileSize + padding * 2;
        double h = boardHeight * tileSize + padding * 2;
        return new CanvasDimensions(w, h);
    }
    
    // Calculates pixel position for board coordinates
    public static class PixelPosition {
        public final double x;
        public final double y;
        
        public PixelPosition(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public PixelPosition getBoardPixelPosition(int boardX, int boardY, double baseX, 
                                             double baseY, int tileSize) {
        double px = baseX + boardX * tileSize;
        double py = baseY + boardY * tileSize;
        return new PixelPosition(px, py);
    }
}