package tetris.model.setting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class ConfigManager {
    private static final Path DIR  = Paths.get("data");
    private static final Path FILE = DIR.resolve("config.json");
    private static final ObjectMapper M = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private ConfigManager() {}

    public static GameSetting loadOrDefault() {
        try {
            if (Files.exists(FILE)) {
                System.out.println("[Config] load from -> " + FILE.toAbsolutePath());
                try (Reader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                    return M.readValue(r, GameSetting.class);
                }
            } else {
                System.out.println("[Config] no file, using defaults at -> " + FILE.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[Config] load error: " + e.getMessage());
        }
        return new GameSetting();
    }

    public static void save(GameSetting s) {
        try {
            if (!Files.exists(DIR)) Files.createDirectories(DIR);
            try (Writer w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                M.writeValue(w, s);
            }
            System.out.println("[Config] saved -> " + FILE.toAbsolutePath());
            System.out.println("[Config] state -> W=" + s.getFieldWidth() +
                    ", H=" + s.getFieldHeight() +
                    ", L=" + s.getLevel() +
                    ", music=" + s.isMusicOn() +
                    ", sfx=" + s.isSfxOn() +
                    ", player1=" + s.getPlayerOneType() +
                    ", extend=" + s.isExtendOn());
        } catch (Exception e) {
            System.err.println("[Config] save error: " + e.getMessage());
        }
    }
}
