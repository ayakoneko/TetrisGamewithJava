package tetris.controller.state;

import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;

public class DefaultPlayStateFactory implements PlayStateFactory {
    @Override
    public PlayState createInitial(GameSetting settings, PlayerType playerType) {
        return switch (playerType) {
            case AI      -> new AIPlayingState(settings); // drop PlayerType arg
            case HUMAN,
                 EXTERNAL -> new PlayingState();
        };
    }

    @Override
    public PlayState resume(GameSetting settings, PlayState previous) {
        return previous; // simple pass-through; customize if needed
    }
}
