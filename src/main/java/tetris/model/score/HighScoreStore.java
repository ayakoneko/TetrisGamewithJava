package tetris.model.score;

import java.util.List;

public interface HighScoreStore {
    List<ScoreEntry> load();
    void save(List<ScoreEntry> scores);
}
