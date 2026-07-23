package se.holyfivr.trainer.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

/* ==================================== CARD CLASS REPOSITORY ================================== */
/*                                                                                               */
/* Maps a numeric ability-card ID to its class name (e.g. 233 -> "Pyroclast").                   */
/*                                                                                               */
/* The map is bundled as a resource (card-class.properties), generated from the ruleset's        */
/* "$FH/<id>_character_ability_cards_<class>" card names. It's loaded once at startup so the      */
/* savefile enhancement-reset feature can attribute each enhanced card to a class WITHOUT         */
/* needing a ruleset open (savefiles and rulesets are edited in separate sessions).              */
/*                                                                                               */
/* Note: a small number of CardIDs shared by two classes are intentionally omitted from the      */
/* map, so classOf() returns null for them and they're never auto-attributed to either class.    */
/* ============================================================================================= */

@Component
public class CardClassRepository {

    private final Map<Integer, String> cardIdToClass = new HashMap<>();

    public CardClassRepository() {
        try (InputStream in = getClass().getResourceAsStream("/card-class.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                for (String key : props.stringPropertyNames()) {
                    try {
                        cardIdToClass.put(Integer.parseInt(key.trim()), props.getProperty(key).trim());
                    } catch (NumberFormatException ignored) {
                        // skip malformed keys
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Returns the class name for a card ID, or null if unknown/ambiguous. */
    public String classOf(int cardId) {
        return cardIdToClass.get(cardId);
    }

    public boolean isEmpty() {
        return cardIdToClass.isEmpty();
    }
}
