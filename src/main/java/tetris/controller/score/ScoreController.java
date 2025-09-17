package tetris.controller.score;

import tetris.model.score.HighScoreService;
import tetris.model.score.ScoreEntry;

import java.util.List;

public class ScoreController {
    private final HighScoreService highScoreService;

    public ScoreController(HighScoreService service) {
        this.highScoreService = service;
    }

    public List<ScoreEntry> getTopScores() {
        return highScoreService.getTopScores();
    }

    public void addLinesScore(int linesCleared, int level) {
        highScoreService.addLinesScore(linesCleared, level);
    }

    public boolean submitScore(String playerName) {
        return highScoreService.submitScore(playerName);
    }

    public int getCurrentScore() {
        return highScoreService.getCurrentScore();
    }

    public void resetScore() {
        highScoreService.resetScore();
    }

    public void refreshHighScores() {
        highScoreService.refreshFromStore();
    }
}
