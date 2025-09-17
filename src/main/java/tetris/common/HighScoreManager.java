package tetris.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import tetris.model.score.HighScoreStore;
import tetris.model.score.ScoreEntry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HighScoreManager (I/O only version) – handles loading/saving high scores to JSON.
 * This replaces the old all-in-one version and is used by HighScoreService.
 */
public class HighScoreManager implements HighScoreStore {

    // JSON file path for persistence
    private static final String HIGHSCORES_FILE = "data/highscores.json";
    // Jackson ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper;
    // Local cache of scores (optional use in Service)
    private final List<ScoreEntry> scores;

    /**
     * Constructor initializes JSON mapper and loads any existing score data.
     */
    public HighScoreManager() {
        this.scores = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
        loadFromFile(); // Load existing scores on startup
    }

    /**
     * Loads high scores from the JSON file.
     * If the file doesn't exist or is invalid, starts with an empty list.
     */
    private void loadFromFile() {
        try {
            File file = new File(HIGHSCORES_FILE);
            if (file.exists() && file.length() > 0) {
                List<ScoreEntry> loadedScores = objectMapper.readValue(file, new TypeReference<List<ScoreEntry>>() {});
                scores.clear();
                scores.addAll(loadedScores);
            }
        } catch (IOException e) {
            System.err.println("[HighScoreManager] Load failed: " + e.getMessage());
        }
    }

    /**
     * Saves the given score list to the JSON file.
     * Creates the data directory if it doesn't exist.
     */
    @Override
    public void save(List<ScoreEntry> scores) {
        try {
            File file = new File(HIGHSCORES_FILE);
            File parentDir = file.getParentFile();

            // Create data directory if it doesn't exist
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            objectMapper.writeValue(file, scores);
        } catch (IOException e) {
            System.err.println("[HighScoreManager] Save failed: " + e.getMessage());
        } finally {
            this.scores.clear();
            this.scores.addAll(scores);
        }
    }

    /**
     * Returns the loaded high scores in memory.
     * @return list of scores
     */
    @Override
    public List<ScoreEntry> load() {
        try {
            File file = new File(HIGHSCORES_FILE);
            System.out.println("[HighScoreManager] load <- " + file.getAbsolutePath()
                    + " (exists=" + file.exists() + ", size=" + file.length() + ")");
            if (file.exists() && file.length() > 0) {
                return objectMapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<List<ScoreEntry>>(){});
            }
        } catch (IOException e) {
            System.err.println("[HighScoreManager] Load failed: " + e.getMessage());
        }
        return java.util.List.of();
    }
}
