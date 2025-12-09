package se.holyfivr.trainer.service;

import org.springframework.stereotype.Service;
import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.service.parser.CharacterParser;
import se.holyfivr.trainer.service.parser.UnlockedCharacterParser;

//====================================== RULESET PARSER =======================================//
//  This class is responsible for parsing the text content of the ruleset file.                //
//  It extracts data into Java objects and stores them in the ActiveSessionData.               //
//=============================================================================================//

@Service
public class RulesetParser {

    // injected from the State class to store parsed data
    private final ActiveSessionData activeSession;
    private final CharacterParser characterParser;
    private final UnlockedCharacterParser unlockedCharacterParser;

    public RulesetParser(ActiveSessionData activeSession, CharacterParser characterParser, UnlockedCharacterParser unlockedCharacterParser) {
        this.activeSession = activeSession;
        this.characterParser = characterParser;
        this.unlockedCharacterParser = unlockedCharacterParser;
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
        
        // Clear any existing characters 
        activeSession.clearCharacters();

        // Clear any existing unlocked characters
        activeSession.clearUnlockedCharacters();

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
                characterParser.parseCharacterBlock(currentBlock);

            } else if (currentBlock.startsWith("GameMode")) {
                
                unlockedCharacterParser.parseGameModeBlock(currentBlock);
            }
            else if (currentBlock.startsWith("Item")) {
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

    
}