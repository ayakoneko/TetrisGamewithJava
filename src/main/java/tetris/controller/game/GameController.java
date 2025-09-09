package tetris.controller.game;

import tetris.common.Action;
import tetris.common.GameState;
import tetris.controller.api.IGameController;
import tetris.controller.score.ScoreController;
import tetris.controller.state.AIPlayingState;
import tetris.controller.state.PlayState;
import tetris.controller.state.PlayingState;
import tetris.model.IGameBoard;
import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;

public class GameController implements IGameController {
    private final IGameBoard board;
    private PlayState state = new PlayingState();

    private int clearedLinesLastTick = 0;
    
    // AI support
    private PlayerType playerType = PlayerType.HUMAN;
    private GameSetting gameSetting;
    
    // Scoring system
    private final ScoreController scoreController = new ScoreController();

    public GameController(IGameBoard board) {this.board = board;}
    
    public GameController(IGameBoard board, GameSetting gameSetting, PlayerType playerType) {
        this.board = board;
        this.gameSetting = gameSetting;
        this.playerType = playerType;
        setPlayerType(playerType);
    }

    public void setState(PlayState next) { this.state = next; }
    public PlayState getState() { return state; }

    public int getAndResetClearedLines() {
        int v = clearedLinesLastTick;
        clearedLinesLastTick = 0;
        return v;
    }

    public void setClearedLinesLastTick(int cleared) {
        this.clearedLinesLastTick = cleared;
        // Integrate scoring for cleared lines
        if (cleared > 0 && gameSetting != null) {
            scoreController.addLinesScore(cleared, gameSetting.getLevel());
        }
    }

    @Override public IGameBoard board() {return board;}
    @Override public GameState state() {return state.uiState();}

    @Override public void handle(Action action) { state.handle(this, action); }
    @Override public void start() { state.start(this); }
    @Override public void togglePause() { state.togglePause(this); }
    @Override public void restart() { 
        scoreController.resetScore(); 
        state.restart(this); 
    }
    @Override public void reset() { 
        scoreController.resetScore(); 
        state.reset(this); 
    }
    @Override public void tick() { state.tick(this); }
    
    // AI support methods
    public void setPlayerType(PlayerType type) {
        this.playerType = type;
        // Switch to appropriate state based on player type
        switch (type) {
            case AI -> this.state = new AIPlayingState(gameSetting, type);
            case HUMAN, EXTERNAL -> this.state = new PlayingState();
        }
    }
    
    public PlayerType getPlayerType() { return playerType; }
    
    public GameSetting getGameSetting() { return gameSetting; }
    
    public void setGameSetting(GameSetting gameSetting) { 
        this.gameSetting = gameSetting; 
    }
    
    // Scoring methods
    public ScoreController getScoreController() {
        return scoreController;
    }
    
    public int getCurrentScore() {
        return scoreController.getCurrentScore();
    }
    
    public boolean submitFinalScore(String playerName) {
        return scoreController.submitScore(playerName);
    }
}
