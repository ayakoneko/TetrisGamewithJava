package tetris.controller.command;

import tetris.controller.event.GameEventHandler;

/**
 * GameCommand : one input packaged as an executable command.
 * execute(handler) : run the action against the given player's handler.
 */
public interface GameCommand {
    void execute(GameEventHandler handler);
}