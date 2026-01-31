package se.holyfivr.trainer.core;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.parser.AbilityCardParser;
import se.holyfivr.trainer.core.parser.CharacterParser;
import se.holyfivr.trainer.core.parser.ItemParser;
import se.holyfivr.trainer.core.parser.UnlockedCharacterParser;

//====================================== RULESET PARSER =======================================//
//  This class is responsible for parsing the text content of the ruleset file.                //
//  It extracts data into Java objects and stores them in the ActiveSessionData.               //
//=============================================================================================//

@Service
public class RulesetParser {

    private static final String PARSER_BLOCK_MARKER = "Parser: ";

    // injected from the State class to store parsed data
    private final ActiveSessionData activeSession;
    private final CharacterParser characterParser;
    private final UnlockedCharacterParser unlockedCharacterParser;
    private final ItemParser itemParser;
    private final AbilityCardParser abilityCardParser;

    public RulesetParser(
            ActiveSessionData activeSession,
            CharacterParser characterParser,
            UnlockedCharacterParser unlockedCharacterParser,
            ItemParser itemParser,
            AbilityCardParser abilityCardParser) {
        this.activeSession = activeSession;
        this.characterParser = characterParser;
        this.unlockedCharacterParser = unlockedCharacterParser;
        this.itemParser = itemParser;
        this.abilityCardParser = abilityCardParser;
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

        // Prepare a clean session for this parsing run
        activeSession.reset();

        // Split the content into blocks using the parser marker
        String[] blocks = contentString.split(PARSER_BLOCK_MARKER);

        // Skip index 0, as the content starts with the parser marker
        for (int i = 1; i < blocks.length; i++) {
            routeBlock(blocks[i].trim());
        }
    }

    /**
     * Routes a single parser block to the appropriate parser
     * based on its leading identifier.
     */
    private void routeBlock(String block) {

        if (block.startsWith("Character")) {
            characterParser.parseCharacterBlock(block);

        } else if (block.startsWith("GameMode")) {
            unlockedCharacterParser.parseGameModeBlock(block);

        } else if (block.startsWith("ItemCard")) {
            itemParser.parseItemBlock(block);

        } else if (block.startsWith("AbilityCard")) {
            abilityCardParser.parseAbilityCardBlock(block);
        }
    }
    
}