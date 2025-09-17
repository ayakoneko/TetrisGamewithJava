package tetris.model.score;

public final class ScoreCalculator {
    private ScoreCalculator() {}

    public static int computeLineClearScore(int linesCleared, int level) {
        return switch (linesCleared) {
            case 1 -> 100 * level;
            case 2 -> 300 * level;
            case 3 -> 600 * level;
            case 4 -> 1000 * level;
            default -> 0;
        };
    }
}
