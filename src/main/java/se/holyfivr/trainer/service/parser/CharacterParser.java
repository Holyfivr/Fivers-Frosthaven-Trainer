package se.holyfivr.trainer.service.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.service.ActiveSessionData;

@Component
public class CharacterParser {
    private final ActiveSessionData activeSession;

    public CharacterParser(ActiveSessionData activeSession) {
        this.activeSession = activeSession;
    }


    /* ============================================================================================ */
    /*                                  PARSE CHARACTER BLOCK                                       */
    /*                                                                                              */
    /* This method extracts the relevant data from character blocks. It looks for specific lines    */
    /* that contain the data we want, extracts that data, and stores it in a PlayerCharacter POJO   */
    /*                                                                                              */
    /* Refactored for readability: Logic is split into semantic helper methods.                     */
    /* ============================================================================================ */
    public void parseCharacterBlock(String currentBlock) {

        // Variables to hold extracted data
        String name = null;
        String cardAmount = null;
        String[] hpPerLevel = new String[9];

        // Split the block into lines to process them individually
        String[] lines = currentBlock.split("\n");

        /* ========================================================================= */
        /* In this loop, we check each line in the block for relevant data.          */
        /* I want to add something for clarity: This loop never runs more than       */
        /* one time. Each character block corresponds to one character.              */
        /* However, this is still the most "readable" way to extract the data.       */
        /* If the line starts with ID:, HealthTable:, or                             */
        /* NumberAbilityCardsInBattle, we extract the corresponding value.           */
        /* - ID will be used to identify the character                               */
        /* - HealthTable contains the HP values for levels 1-9                       */
        /* - NumberAbilityCardsInBattle contains the max ability card count          */
        /* ========================================================================= */
        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("ID:")) {
                name = removePrefix(line, "ID:");

                // There are tutorial characters, a "lumberpile" character (lol), 
                // and a null character in the ruleset. We want to ignore these.
                if (filterCharacter(name)) {
                    return;
                }

            } else if (line.startsWith("HealthTable:")) {
                hpPerLevel = parseHealthTable(line);

            } else if (line.startsWith("NumberAbilityCardsInBattle:")) {
                cardAmount = removePrefix(line, "NumberAbilityCardsInBattle:");
            }
        }

        // Create and store the character object in the ActiveSessionData
        createAndAddCharacter(name, cardAmount, hpPerLevel);
    }

    // Helper: Extracts a value from a line by removing the prefix
    private String removePrefix(String line, String prefix) {
        return line.replace(prefix, "").trim();
    }

    // Helper: Parses the HealthTable line into an array of strings
    private String[] parseHealthTable(String line) {
        String[] hpPerLevel = new String[9];
        
        // Extract content between brackets [ ... ]
        int start = line.indexOf('[') + 1;
        int end = line.indexOf(']');
        
        if (start > 0 && end > start) {
            String allHpValues = line.substring(start, end);
            String[] hpValuesSeparated = allHpValues.split(",");

            for (int i = 0; i < 9 && i < hpValuesSeparated.length; i++) {
                hpPerLevel[i] = hpValuesSeparated[i].trim();
            }
        }
        return hpPerLevel;
    }

    // Breakout method for more readable code. Determines if a character should be ignored based on its name 
    private boolean filterCharacter(String name) {
        // Must check for null BEFORE calling toLowerCase() to avoid NullPointerException
        if (name == null) return true;
        
        String lowerName = name.toLowerCase();
        return lowerName.contains("tutorial") || lowerName.contains("lumberpile");
    }

    // Breakout method for more readable code. Creates the POJO and adds it to the state
    private void createAndAddCharacter(String name, String cardAmount, String[] hpPerLevel) {
        PlayerCharacter character = new PlayerCharacter(
                name,
                cardAmount,
                hpPerLevel[0],
                hpPerLevel[1],
                hpPerLevel[2],
                hpPerLevel[3],
                hpPerLevel[4],
                hpPerLevel[5],
                hpPerLevel[6],
                hpPerLevel[7],
                hpPerLevel[8]);
        activeSession.addCharacter(character);
    }


}
