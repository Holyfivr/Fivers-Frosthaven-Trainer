package se.holyfivr.trainer.core.parser;

import org.springframework.stereotype.Component;
import se.holyfivr.trainer.core.ActiveSessionData;

/* ================================== UNLOCKED CHARACTER PARSER =============================== */
/* This class is responsible for parsing the unlocked characters section of the ruleset file    */
/* It extracts the list of unlocked character classes and stores them in the ActiveSessionData. */
/* ============================================================================================ */
@Component
public class UnlockedCharacterParser {

    private static final String GAME_NAME_MARKER        = "Name: FrostHaven";
    private static final String UNLOCKED_CLASSES_KEY    = "UnlockedClasses:";

    private final ActiveSessionData activeSessionData;

    public UnlockedCharacterParser(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;
    }

    /**
     * Entry point for parsing unlocked characters.
     * Only applies parsing if the current block belongs
     * to the Frosthaven game mode (as in, the main-campaign).
     */
    public void parseGameModeBlock(String currentBlock) {
        String[] lines = currentBlock.split("\n");

        if (isFrosthavenGameMode(lines)) {
            parseUnlockedCharacterArray(lines);
        }
    }

    /**
     * Checks whether the provided block represents
     * the Frosthaven game mode.
     */
    private boolean isFrosthavenGameMode(String[] lines) {
        for (String line : lines) {
            if (line.trim().equalsIgnoreCase(GAME_NAME_MARKER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts and applies unlocked character classes
     * to the active session.
     */
    private void parseUnlockedCharacterArray(String[] lines) {
        for (String line : lines) {
            if (line.startsWith(UNLOCKED_CLASSES_KEY)) {
                extractCharacters(line);
            }
        }
    }

    /**
     * Extracts individual character identifiers from
     * the unlocked classes array syntax.
     */
    private void extractCharacters(String line) {
        int start = line.indexOf('[') + 1;
        int end = line.indexOf(']');

        if (start > 0 && end > start) {
            String content = line.substring(start, end);
            String[] characters = content.split(",");

            for (String character : characters) {
                activeSessionData.addUnlockedCharacter(character.trim());
            }
        }
    }
}
