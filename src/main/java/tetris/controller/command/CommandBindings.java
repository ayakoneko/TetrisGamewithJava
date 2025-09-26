package tetris.controller.command;

import javafx.scene.input.KeyCode;
import tetris.common.Action;

import java.util.EnumMap;
import java.util.Map;

/**
 * CommandBindings : key->command maps for each player/mode.
 * Provides the exact bindings required by the assignment.
 */
public final class CommandBindings {
    private CommandBindings() {}

    // 1P(HUMAN) default
    public static Map<KeyCode, GameCommand> singlePlayerHuman() {
        EnumMap<KeyCode, GameCommand> m = new EnumMap<>(KeyCode.class);
        m.put(KeyCode.LEFT,  cmd(Action.MOVE_LEFT,  true));
        m.put(KeyCode.RIGHT, cmd(Action.MOVE_RIGHT, true));
        m.put(KeyCode.UP,    cmd(Action.ROTATE_CW,  true));
        m.put(KeyCode.DOWN,  cmd(Action.SOFT_DROP,  false));
        m.put(KeyCode.SPACE, cmd(Action.HARD_DROP,  false));
        return m;
    }

    // Player 1 — External mode: ',', '.', SPACE, 'L'
    public static Map<KeyCode, GameCommand> player1External() {
        EnumMap<KeyCode, GameCommand> m = new EnumMap<>(KeyCode.class);
        m.put(KeyCode.COMMA,  cmd(Action.MOVE_LEFT,  true));
        m.put(KeyCode.PERIOD, cmd(Action.MOVE_RIGHT, true));
        m.put(KeyCode.SPACE,  cmd(Action.SOFT_DROP,  false));
        m.put(KeyCode.L,      cmd(Action.ROTATE_CW,  true));
        return m;
    }

    // Player 2 — External mode: arrow keys
    public static Map<KeyCode, GameCommand> player2External() {
        EnumMap<KeyCode, GameCommand> m = new EnumMap<>(KeyCode.class);
        m.put(KeyCode.LEFT,  cmd(Action.MOVE_LEFT,  true));
        m.put(KeyCode.RIGHT, cmd(Action.MOVE_RIGHT, true));
        m.put(KeyCode.DOWN,  cmd(Action.SOFT_DROP,  false));
        m.put(KeyCode.UP,    cmd(Action.ROTATE_CW,  true));
        return m;
    }

    private static GameCommand cmd(Action a, boolean sfx) {
        return new DispatchActionCommand(a, sfx);
    }
}
