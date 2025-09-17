package tetris.controller.audio;

import tetris.common.AudioManager;

/**
 * AudioController - Controller layer for audio management.
 * Provides a clean interface between Views and the AudioManager utility,
 * maintaining proper MVC separation.
 */
public class AudioController {
    // Background Music Control
    public void playBackgroundMusic(String filename) {
        AudioManager.playBGM(filename, true);
    }
    
    public void stopBackgroundMusic() {
        AudioManager.stopBGM();
    }
    
    public boolean toggleBackgroundMusic(String filename) {
        return AudioManager.toggleBGM(filename, true);
    }
    
    // Sound Effects Control
    public void playSoundEffect(String filename) {
        AudioManager.playSfx(filename);
    }
    
    // Convenience methods for common game sounds
    public void playLineClearSound() {
        AudioManager.playSfx("erase-line.wav");
    }
    
    public void playMoveTurnSound() {
        AudioManager.playSfx("move-turn.wav");
    }
    
    // Audio state management
    public void setBackgroundMusicEnabled(boolean enabled, String filename) {
        if (enabled) {
            playBackgroundMusic(filename);
        } else {
            stopBackgroundMusic();
        }
    }
}