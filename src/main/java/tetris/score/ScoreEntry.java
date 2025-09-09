package tetris.score;

public class ScoreEntry {
    // Player name
    private String playerName;
    // Score achieved by the player
    private int score;
    // Default constructor
    public ScoreEntry() {
        this.playerName = "";
        this.score = 0;
    }
    // Constructor with parameters
    public ScoreEntry(String playerName, int score) {
        this.playerName = playerName != null ? playerName : "";
        this.score = Math.max(0, score); // Ensure non-negative score
    }
    // Gets the player name
    public String getPlayerName() {
        return playerName;
    }
    // Sets the player name
    public void setPlayerName(String playerName) {
        this.playerName = playerName != null ? playerName : "";
    }
    // Gets the score
    public int getScore() {
        return score;
    }
    // Sets the score (must be non-negative)
    public void setScore(int score) {
        this.score = Math.max(0, score);
    }
    // Returns a string representation of this score entry
    @Override
    public String toString() {
        return String.format("%s: %d", playerName, score);
    }
    // Checks if this score entry is equal to another object
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ScoreEntry that = (ScoreEntry) obj;
        return score == that.score && 
               (playerName != null ? playerName.equals(that.playerName) : that.playerName == null);
    }
    // Returns the hash code for this score entry
    @Override
    public int hashCode() {
        int result = playerName != null ? playerName.hashCode() : 0;
        result = 31 * result + score;
        return result;
    }
}