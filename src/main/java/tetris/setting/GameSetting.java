package tetris.setting;

public class GameSetting {
    public static final int DEFAULT_W = 10;
    public static final int DEFAULT_H = 20;
    public static final int DEFAULT_LEVEL = 6;

    private int fieldWidth  = DEFAULT_W;
    private int fieldHeight = DEFAULT_H;
    private int level       = DEFAULT_LEVEL;

    private boolean musicOn  = true;
    private boolean sfxOn    = true;
    private boolean aiOn     = false;
    private boolean extendOn = false;

    private PlayerType playerOneType = PlayerType.HUMAN;
    private PlayerType playerTwoType = PlayerType.HUMAN;

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

    public boolean isAiOn()      { return aiOn; }
    public void setAiOn(boolean v) { this.aiOn = v; }

    public boolean isExtendOn()  { return extendOn; }
    public void setExtendOn(boolean v) { this.extendOn = v; }

    public PlayerType getPlayerOneType() { return playerOneType; }
    public void setPlayerOneType(PlayerType t) { this.playerOneType = t; }

    public PlayerType getPlayerTwoType() { return playerTwoType; }
    public void setPlayerTwoType(PlayerType t) { this.playerTwoType = t; }
}
