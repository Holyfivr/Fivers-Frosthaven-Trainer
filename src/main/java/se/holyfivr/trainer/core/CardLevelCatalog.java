package se.holyfivr.trainer.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

/** Printed level of each class-qualified ability card in the bundled ruleset. */
@Component
public class CardLevelCatalog {

    private final Map<String, Map<Integer, Integer>> levels = new HashMap<>();

    public CardLevelCatalog() {
        try (InputStream in = getClass().getResourceAsStream("/card-level.properties")) {
            if (in == null) throw new IllegalStateException("Missing card-level.properties");
            Properties properties = new Properties();
            properties.load(in);
            for (String key : properties.stringPropertyNames()) {
                int dot = key.lastIndexOf('.');
                if (dot <= 0) throw new IllegalStateException("Invalid card catalog key: " + key);
                String classId = key.substring(0, dot);
                int cardId = Integer.parseInt(key.substring(dot + 1));
                String printed = properties.getProperty(key);
                int level = printed.equals("X") ? 1 : Integer.parseInt(printed);
                if (level < 1 || level > 9) throw new IllegalStateException("Invalid card level: " + key);
                levels.computeIfAbsent(classId, ignored -> new HashMap<>()).put(cardId, level);
            }
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Could not load card level catalog", e);
        }
    }

    /** Returns null for an unknown class, avoiding guesses about its cards. */
    public Map<Integer, Integer> forClass(String classId) {
        return levels.get(classId);
    }
}
