package tetris.controller.config;

import tetris.dto.GameSettingsData;
import tetris.common.ConfigManager;
import tetris.model.setting.GameSetting;
import tetris.model.setting.PlayerType;

/**
 * ConfigurationController handles all configuration-related business logic
 * and persistence, keeping Views free from model persistence concerns.
 */
public class ConfigurationController {
    
    private final GameSetting settings;
    
    public ConfigurationController(GameSetting settings) {
        this.settings = settings;
    }
    
    // Field size operations
    public void updateFieldWidth(int width) {
        settings.setFieldWidth(width);
        persistSettings();
    }
    
    public void updateFieldHeight(int height) {
        settings.setFieldHeight(height);
        persistSettings();
    }
    
    public void updateLevel(int level) {
        settings.setLevel(level);
        persistSettings();
    }
    
    // Audio settings operations
    public void updateMusicSetting(boolean enabled) {
        settings.setMusicOn(enabled);
        persistSettings();
    }
    
    public void updateSfxSetting(boolean enabled) {
        settings.setSfxOn(enabled);
        persistSettings();
    }
    
    // Player type operations
    public void updatePlayerOneType(PlayerType playerType) {
        settings.setPlayerOneType(playerType);
        persistSettings();
    }
    
    public void updatePlayerOneType(String playerTypeString) {
        PlayerType playerType = PlayerType.valueOf(playerTypeString);
        updatePlayerOneType(playerType);
    }
    
    public void updatePlayerTwoType(PlayerType playerType) {
        settings.setPlayerTwoType(playerType);
        persistSettings();
    }
    
    public void updatePlayerTwoType(String playerTypeString) {
        PlayerType playerType = PlayerType.valueOf(playerTypeString);
        updatePlayerTwoType(playerType);
    }
    
    // Advanced settings operations
    public void updateExtendSetting(boolean enabled) {
        settings.setExtendOn(enabled);
        persistSettings();
    }
    
    // Centralized persistence - only Controllers should save settings
    private void persistSettings() {
        ConfigManager.save(settings);
    }
    
    // Read-only access for Views
    public GameSetting getSettings() {
        return settings;
    }
    
    // DTO access for Views - proper MVC separation
    public GameSettingsData getSettingsData() {
        return GameSettingsData.fromGameSetting(settings);
    }
}