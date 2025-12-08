package se.holyfivr.trainer.service;

import org.springframework.stereotype.Service;
import se.holyfivr.trainer.model.PlayerCharacter;

//====================================== RULESET PARSER =======================================//
//  This class is responsible for parsing the text content of the ruleset file.                //
//  It extracts data into Java objects and stores them in the ActiveSessionData.               //
//=============================================================================================//

@Service
public class RulesetParser {

    // injected from the State class to store parsed data
    private final ActiveSessionData activeSession;

    public RulesetParser(ActiveSessionData activeSession) {
        this.activeSession = activeSession;
    }

    /* ============================================================================================ */
    /*                                    PARSE RULESET CONTENT                                     */
    /*                                                                                              */
    /* This method restructures the data retrieved from the ruleset.                                */
    /* We use the 'originalContentString' which we decoded safely using ISO_5589_1 format.          */
    /* In this file, while parsing and using the data we do not care about the format, so it will   */
    /* be UTF-16 from now on (which is java standard). It will be decoded back to ISO_5589_1, and   */
    /* then bytes again later before saving the file.                                               */
    /* We split the content into smaller blocks based on the "Parser: " tag.                        */
    /* These blocks are then processed one by one, extracting the relevant data.                    */
    /*                                                                                              */
    /* ============================================================================================ */
    public void parseRulesetContent(String contentString) {
        
        // Clear any existing characters to ensure a fresh state 
        // ( this is just a safety measure, it should really not be needed )
        activeSession.clearCharacters();

        // Splits up the content block into smaller blocks based on parser tags
        String[] allBlocks = contentString.split("Parser: ");

        // Debug variables just to check how many of each entity we found
        int characterCount = 0; // should be 20 (but we will only use 17 of them)
        int itemCount = 0;      // should be 264
        int abilityCount = 0;   // should be 531

        // skip index 0, as it is empty (the content string starts with the marker "Parser: ")
        for (int i = 1; i < allBlocks.length; i++) {
            String currentBlock = allBlocks[i].trim();

            // Check the type of block and process accordingly
            if (currentBlock.startsWith("Character")) {
                characterCount++;   //debug

                // this method extracts the relevant data from the character block
                // and stores it in a PlayerCharacter POJO.
                parseCharacterBlock(currentBlock);

            } else if (currentBlock.startsWith("Item")) {
                itemCount++;        //debug

            } else if (currentBlock.startsWith("AbilityCard")) {
                abilityCount++;     //debug
            }
        }


        // DEBUG: Print summary of loaded characters
        for (PlayerCharacter character : activeSession.getCharacters().values()) {
            System.out.println("Character Loaded: " + character.getName()); 
            System.out.println("  Card Amount: " + character.getCardAmount()); 
            System.out.println("  Max HP Levels: " + character.getHpLvlOne() + ", " +
                    character.getHpLvlTwo() + ", " +
                    character.getHpLvlThree() + ", " +
                    character.getHpLvlFour() + ", " +
                    character.getHpLvlFive() + ", " +
                    character.getHpLvlSix() + ", " +
                    character.getHpLvlSeven() + ", " +
                    character.getHpLvlEight() + ", " +
                    character.getHpLvlNine()); 
        }

        // DEBUG: Print totals
        System.out.println("Total Characters Found: " + characterCount); 
        System.out.println("Total Items Found: " + itemCount); 
        System.out.println("Total Ability Cards Found: " + abilityCount); 

    }

    /* ============================================================================================ */
    /*                                  PARSE CHARACTER BLOCK                                       */
    /*                                                                                              */
    /* This method extracts the relevant data from character blocks. It looks for specific lines    */
    /* that contain the data we want, extracts that data, and stores it in a PlayerCharacter POJO   */
    /*                                                                                              */
    /* Refactored for readability: Logic is split into semantic helper methods.                     */
    /* ============================================================================================ */
    private void parseCharacterBlock(String currentBlock) {

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