package tetris.model.tetromino;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

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

    public TetrominoType peekNext() {
        if (bag.isEmpty()) refill();
        return bag.peekFirst();
    }
}