package tetris.model.setting;

public class GameSetting {
    public static final int DEFAULT_W = 10;
    public static final int DEFAULT_H = 20;
    public static final int DEFAULT_LEVEL = 6;

    private int fieldWidth  = DEFAULT_W;
    private int fieldHeight = DEFAULT_H;
    private int level       = DEFAULT_LEVEL;

    private boolean musicOn  = true;
    private boolean sfxOn    = true;
    private boolean extendOn = false;

    private int players = 1;
    private PlayerType playerOneType = PlayerType.HUMAN;
    private PlayerType playerTwoType = PlayerType.HUMAN;

    public void resetToDefaults() {
        fieldWidth  = DEFAULT_W;
        fieldHeight = DEFAULT_H;
        level       = DEFAULT_LEVEL;
        musicOn = true;
        sfxOn   = true;
        extendOn = false;
        players = 1;
        playerOneType = PlayerType.HUMAN;
        playerTwoType = PlayerType.HUMAN;
    }

    public int  getFieldWidth()  { return fieldWidth; }
    public void setFieldWidth(int w)  { this.fieldWidth = w; }

    public int  getFieldHeight() { return fieldHeight; }
    public void setFieldHeight(int h) { this.fieldHeight = h; }

    public int  getLevel()       { return level; }
    public void setLevel(int l)  { this.level = l; }

    public boolean isMusicOn()   { return musicOn; }
    public void setMusicOn(boolean v) { this.musicOn = v; }

    public boolean isSfxOn()     { return sfxOn; }
    public void setSfxOn(boolean v) { this.sfxOn = v; }

    public boolean isExtendOn()  { return extendOn; }
    public void setExtendOn(boolean v) { this.extendOn = v; }

    public int getPlayers() { return players; }
    public void setPlayers(int players) { this.players = Math.max(1, Math.min(players, 2)); }

    public PlayerType getPlayerOneType() { return playerOneType; }
    public void setPlayerOneType(PlayerType t) { this.playerOneType = t; }

    public PlayerType getPlayerTwoType() { return playerTwoType; }
    public void setPlayerTwoType(PlayerType t) { this.playerTwoType = t; }

    // Game speed calculation based on level
    public long calculateDropInterval() {
        long base = 700_000_000L;          // 700ms
        long step = 50_000_000L;           // 50ms per level
        int lvl = Math.max(1, Math.min(level, 10)); // clamp between Level 1–10
        return base - ((long)(lvl - 1) * step);
    }
}
