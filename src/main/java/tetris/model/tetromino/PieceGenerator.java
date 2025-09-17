package tetris.model.tetromino;

import java.util.*;

public class PieceGenerator {
    private final Random rng;
    private final Deque<TetrominoType> bag = new ArrayDeque<>();

    public PieceGenerator(long seed) {
        this.rng = new Random(seed);
    }

    private void refill() {
        TetrominoType[] all = TetrominoType.values();
        List<TetrominoType> list = new ArrayList<>(List.of(all));
        Collections.shuffle(list, rng);
        bag.addAll(list);
    }

    public TetrominoType next() {
        if (bag.isEmpty()) refill();
        return bag.removeFirst();
    }
}