package tetris.common;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class AudioManager {

    // ----- BGM -----
    private static MediaPlayer bgmPlayer;
    private static String currentBgm;

    private static void ensureLoaded(String filename, boolean loop) {
        if (bgmPlayer != null && filename.equals(currentBgm)) return;

        stopBGM();
        URL resource = AudioManager.class.getResource("/audio/" + filename);
        if (resource == null) return;

        Media media = new Media(resource.toString());
        bgmPlayer = new MediaPlayer(media);
        bgmPlayer.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1);
        currentBgm = filename;
    }

    public static void playBGM(String filename, boolean loop) {
        ensureLoaded(filename, loop);
        if (bgmPlayer != null) {
            bgmPlayer.play();  // pause면 이어서, stop/ready면 처음부터
        }
    }

    public static void pauseBGM() { if (bgmPlayer != null) bgmPlayer.pause(); }

    public static void resumeBGM() { if (bgmPlayer != null) bgmPlayer.play(); }

    public static void stopBGM() {
        if (bgmPlayer != null) {
            try { bgmPlayer.stop(); } finally { bgmPlayer.dispose(); }
        }
        bgmPlayer = null;
        currentBgm = null;
    }

    public static boolean isPlaying() {
        return bgmPlayer != null && bgmPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public static boolean isPaused() {
        return bgmPlayer != null && bgmPlayer.getStatus() == MediaPlayer.Status.PAUSED;
    }

    public static boolean toggleBGM(String filename, boolean loop) {
        ensureLoaded(filename, loop);
        if (bgmPlayer == null) return false;

        switch (bgmPlayer.getStatus()) {
            case PLAYING -> { pauseBGM(); return false; }
            case PAUSED, STOPPED, READY -> { resumeBGM(); return true; }
            default -> { resumeBGM(); return true; }
        }
    }

    // ----- SFX -----
    public static void playSfx(String filename) {
        URL resource = AudioManager.class.getResource("/audio/" + filename);
        if (resource == null) return;
        AudioClip clip = new AudioClip(resource.toString());
        clip.play();
    }
}
