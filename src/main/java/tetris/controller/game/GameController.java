package tetris.controller.game;

import tetris.common.Action;
import tetris.common.HighScoreManager;
import tetris.common.UiGameState;
import tetris.controller.api.IGameController;
import tetris.controller.score.ScoreController;
import tetris.controller.state.DefaultPlayStateFactory;
import tetris.controller.state.PlayState;
import tetris.controller.state.PlayStateFactory;
import tetris.model.board.IGameBoard;
import tetris.model.score.HighScoreService;
import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;

public class GameController implements IGameController {
    private final IGameBoard board;
    private final PlayStateFactory stateFactory;
    private final ScoreController scoreController; // unchanged

    private PlayState state; // init via setPlayerType(...)
    private int clearedLinesLastTick = 0;

    // player/config
    private PlayerType playerType = PlayerType.HUMAN;
    private GameSetting gameSetting;

    public GameController(
            IGameBoard board,
            GameSetting gameSetting,
            PlayerType playerType,
            PlayStateFactory stateFactory,
            ScoreController... scoreControllerOpt
    ) {
        this.board = board;
        this.gameSetting = gameSetting;
        this.stateFactory = stateFactory;

        this.scoreController =
                (scoreControllerOpt != null && scoreControllerOpt.length > 0 && scoreControllerOpt[0] != null)
                        ? scoreControllerOpt[0]
                        : new ScoreController(new HighScoreService(new HighScoreManager()));

        setPlayerType(playerType);
    }

    // Backward-compatible ctor
    public GameController(IGameBoard board, GameSetting gameSetting, PlayerType playerType) {
        this(board, gameSetting, playerType, new DefaultPlayStateFactory());
    }

    // ---- State accessors ----
    public void setState(PlayState next) { this.state = next; }
    public PlayState getState() { return state; }
    public PlayStateFactory getStateFactory() { return stateFactory; }

    public void setPlayerType(PlayerType type) {
        this.playerType = type;
        this.state = stateFactory.createInitial(gameSetting, type);
    }
    public PlayerType getPlayerType() { return playerType; }

    // Config access
    public GameSetting getGameSetting() { return gameSetting; }
    public void setGameSetting(GameSetting gameSetting) { this.gameSetting = gameSetting; }

    // ---- Tick/loop plumbing ----
    public int getAndResetClearedLines() {
        int v = clearedLinesLastTick;
        clearedLinesLastTick = 0;
        return v;
    }

    public void setClearedLinesLastTick(int cleared) {
        this.clearedLinesLastTick = cleared;
        if (cleared > 0 && gameSetting != null) {
            scoreController.addLinesScore(cleared, gameSetting.getLevel());
        }
    }

    // ---- IGameController ----
    @Override public IGameBoard board() { return board; }
    @Override public UiGameState state() { return state.uiState(); }
    @Override public void handle(Action action) { state.handle(this, action); }
    @Override public void start() { state.start(this); }
    @Override public void togglePause() { state.togglePause(this); }
    @Override public void restart() { scoreController.resetScore(); state.restart(this); }
    @Override public void reset() { scoreController.resetScore(); state.reset(this); }
    @Override public void tick() { state.tick(this); }

    // ---- Scoring façade ----
    public int getCurrentScore() { return scoreController.getCurrentScore(); }
    public boolean submitFinalScore(String playerName) { return scoreController.submitScore(playerName); }
}
