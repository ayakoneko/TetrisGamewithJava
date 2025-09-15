package tetris.controller.score;

import tetris.model.score.HighScoreManager;

/**
 * ScoreController handles all scoring-related business logic and 
 * maintains separation between Views and the scoring Model.
 */
public class ScoreController {
    
    private final HighScoreManager highScoreManager;
    private int currentScore = 0;
    
    public ScoreController() {
        this.highScoreManager = HighScoreManager.getInstance();
    }
    
    // Score calculation methods
    public void addLinesScore(int linesCleared, int level) {
        // Standard Tetris scoring: More lines = exponentially more points
        // Level multiplier increases point value
        int basePoints = switch (linesCleared) {
            case 1 -> 100;   // Single
            case 2 -> 300;   // Double  
            case 3 -> 600;   // Triple
            case 4 -> 1000;  // Tetris!
            default -> 0;
        };
        
        int points = basePoints * level;
        currentScore += points;
    }

    /* Extra rule for score (soft/Hard drop)
    public void addSoftDropScore(int cellsDropped) {
        // 1 point per cell for soft drops
        currentScore += cellsDropped;
    }

    public void addHardDropScore(int cellsDropped) {
        // 2 points per cell for hard drops
        currentScore += (cellsDropped * 2);
    }
     */
    
    // Game end - record high score
    public boolean submitScore(String playerName) {
        if (currentScore <= 0) {
            return false;
        }
        
        boolean isHighScore = highScoreManager.addScore(playerName, currentScore);
        currentScore = 0; // Reset for next game
        return isHighScore;
    }
    
    // Score access
    public int getCurrentScore() {
        return currentScore;
    }
    
    public void resetScore() {
        currentScore = 0;
    }
    
    // High score access for Views (read-only)
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }
    
    public boolean isNewHighScore(int score) {
        return score > highScoreManager.getHighestScore();
    }
    
    public boolean canMakeHighScore(int score) {
        return !highScoreManager.isFull() || score > highScoreManager.getMinimumScore();
    }
}