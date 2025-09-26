package tetris.controller.command;

import tetris.common.Action;
import tetris.controller.event.GameEventHandler;

/**
 * DispatchActionCommand : wraps an Action and dispatches it to the handler.
 * Optionally plays move/turn SFX for L/R/Rotate.
 */
public final class DispatchActionCommand implements GameCommand {
    private final Action action;
    private final boolean playMoveSfx;

    public DispatchActionCommand(Action action, boolean playMoveSfx) {
        this.action = action;
        this.playMoveSfx = playMoveSfx;
    }

    @Override
    public void execute(GameEventHandler handler) {
        if (handler.isAIActive()) return;          // ignore during AI
        handler.handlePlayerAction(action);
        if (playMoveSfx) handler.playMoveTurnSound();
    }
}
