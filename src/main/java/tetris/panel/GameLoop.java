package tetris.panel;

import javafx.animation.AnimationTimer;

public abstract class GameLoop extends AnimationTimer {
    private final long intervalNanos;
    private long lastUpdate = 0;

    protected GameLoop(long intervalNanos) {
        this.intervalNanos = intervalNanos;
    }

    @Override
    public final void handle(long now) {
        if (now - lastUpdate >= intervalNanos) {
            update();
            render();
            lastUpdate = now;
        }
    }

    protected abstract void update();
    protected abstract void render();

}