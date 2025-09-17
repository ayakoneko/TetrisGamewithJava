package tetris.model.score;

import java.util.ArrayList;
import java.util.List;

public class HighScoreService {
    private static final int MAX = 10;
    private final HighScoreStore store;
    private final List<ScoreEntry> scores = new ArrayList<>();
    private int currentScore = 0;

    public HighScoreService(HighScoreStore store) {
        this.store = store;
        this.scores.addAll(store.load());
        this.scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        trim();
    }

    public void addLinesScore(int linesCleared, int level) {
        currentScore += ScoreCalculator.computeLineClearScore(linesCleared, level);
    }

    public boolean submitScore(String name) {
        if (currentScore <= 0) return false;

        String playerName = (name == null || name.isBlank()) ? "Anonymous" : name.trim();
        ScoreEntry entry = new ScoreEntry(playerName, currentScore);

        scores.add(entry);
        scores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        trim();
        store.save(scores);

        currentScore = 0;
        return true;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void resetScore() {
        currentScore = 0;
    }

    public List<ScoreEntry> getTopScores() {
        return List.copyOf(scores);
    }
        private void trim() {
        if (scores.size() > MAX) {
            scores.subList(MAX, scores.size()).clear();
        }
    }

    // tetris/model/score/HighScoreService.java
    public void refreshFromStore() {
        List<ScoreEntry> fresh = store.load();
        this.scores.clear();
        this.scores.addAll(fresh);
        this.scores.sort((a,b) -> Integer.compare(b.getScore(), a.getScore()));
        trim();
    }
}
