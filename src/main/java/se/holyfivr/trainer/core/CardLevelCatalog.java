package se.holyfivr.trainer.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

/* ==================================== CARD LEVEL CATALOG ===================================== */
/*                                                                                               */
/* Printed level of every ability card per class, loaded from card-level.properties (generated   */
/* once from Base.ruleset). Level X cards count as level 1, since they're in the starting pool.  */
/* ============================================================================================= */

@Component
public class CardLevelCatalog {

    private final Map<String, Map<Integer, Integer>> levels = new HashMap<>();

    public CardLevelCatalog() {
        try (InputStream in = getClass().getResourceAsStream("/card-level.properties")) {
            if (in == null) {
                throw new IllegalStateException("Missing card-level.properties");
            }
            Properties properties = new Properties();
            properties.load(in);
            for (String key : properties.stringPropertyNames()) {
                int dot = key.lastIndexOf('.');
                if (dot <= 0) {
                    throw new IllegalStateException("Invalid card catalog key: " + key);
                }
                String classId = key.substring(0, dot);
                int cardId = Integer.parseInt(key.substring(dot + 1));
                String printed = properties.getProperty(key);
                int level = printed.equals("X") ? 1 : Integer.parseInt(printed);
                if (level < 1 || level > 9) {
                    throw new IllegalStateException("Invalid card level: " + key);
                }
                levels.computeIfAbsent(classId, ignored -> new HashMap<>()).put(cardId, level);
            }
        } catch (IOException | NumberFormatException e) {
            throw new IllegalStateException("Could not load card level catalog", e);
        }
    }

    /* ======================================================================== */
    /* Card levels for one class, or null if the class is unknown, so callers   */
    /* never guess about cards they don't have data for.                        */
    /* ======================================================================== */
    public Map<Integer, Integer> forClass(String classId) {
        return levels.get(classId);
    }
}
