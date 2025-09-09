package tetris.model.score;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HighScoreManager {
    // Maximum number of top scores
    private static final int MAX_SCORES = 10;
    // JSON file path for persistence
    private static final String HIGHSCORES_FILE = "data/highscores.json";
    // Singleton instance
    private static HighScoreManager instance;
    // List to store score entries
    private final List<ScoreEntry> scores;
    // Jackson ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper;

    // Private constructor to enforce singleton pattern
    private HighScoreManager() {
        this.scores = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
        loadFromFile(); // Load existing scores on startup
    }
    // Gets the singleton instance of HighScoreManager
    public static synchronized HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }
    // Adds a new score entry to the high score list
    public boolean addScore(String playerName, int score) {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Anonymous";
        }
        ScoreEntry newEntry = new ScoreEntry(playerName.trim(), score);
        scores.add(newEntry);
        scores.sort((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));
        // If the high score list is full, remove the lowest score
        if (scores.size() > MAX_SCORES) {
            scores.subList(MAX_SCORES, scores.size()).clear();
        }

        // Automatically save to file after adding score
        saveToFile();

        return scores.contains(newEntry);
    }
    // Gets the list of top scores in descending order
    public List<ScoreEntry> getTopScores() {
        return new ArrayList<>(scores);
    }
    // Gets the highest score currently recorded
    public int getHighestScore() {
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(0).getScore();
    }
    // Clears all scores from the high score list
    public void clearScores() {
        scores.clear();
    }
    // Gets the number of scores currently stored
    public int getScoreCount() {
        return scores.size();
    }
    // Checks if the high score list is empty
    public boolean isEmpty() {
        return scores.isEmpty();
    }
    // Checks if the high score list is full (has 10 entries)
    public boolean isFull() {
        return scores.size() >= MAX_SCORES;
    }
    // Gets the minimum score required to enter the top 10
    public int getMinimumScore() {
        if (!isFull()) {
            return 0;
        }
        return scores.get(scores.size() - 1).getScore();
    }
    // Returns a string representation of all high scores
    @Override
    public String toString() {
        if (scores.isEmpty()) {
            return "No high scores recorded yet.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("High Scores:\n");
        for (int i = 0; i < scores.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, scores.get(i).toString()));
        }
        return sb.toString();
    }

    // JSON persistence methods

    /**
     * Saves the current high scores to the JSON file.
     * Creates the data directory if it doesn't exist.
     */
    private void saveToFile() {
        try {
            File file = new File(HIGHSCORES_FILE);
            File parentDir = file.getParentFile();

            // Create data directory if it doesn't exist
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Write scores to JSON file
            objectMapper.writeValue(file, scores);
        } catch (IOException e) {
            // Fail silently or log if necessary
        }
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
            // Fail silently or log if necessary
        }
    }
}