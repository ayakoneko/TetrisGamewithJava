package tetris.dto;

/**
 * GameSettingsData - Data Transfer Object for UI-safe game settings.
 * Provides read-only settings data without exposing internal GameSetting model to Views.
 */
public record GameSettingsData(
    int fieldWidth,
    int fieldHeight,
    int level,
    boolean musicOn,
    boolean sfxOn,
    boolean extendOn,
    String playerOneType,
    String playerTwoType
) {
    
    // Factory method to create from GameSetting model
    public static GameSettingsData fromGameSetting(tetris.model.setting.GameSetting settings) {
        return new GameSettingsData(
            settings.getFieldWidth(),
            settings.getFieldHeight(),
            settings.getLevel(),
            settings.isMusicOn(),
            settings.isSfxOn(),
            settings.isExtendOn(),
            settings.getPlayerOneType().name(),
            settings.getPlayerTwoType().name()
        );
    }
}