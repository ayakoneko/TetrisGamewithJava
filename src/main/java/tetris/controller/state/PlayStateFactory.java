package tetris.controller.state;

import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;

public interface PlayStateFactory {
    PlayState createInitial(GameSetting settings, PlayerType playerType);
    PlayState resume(GameSetting settings, PlayState previous);
}
