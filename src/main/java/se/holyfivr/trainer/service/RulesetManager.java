package se.holyfivr.trainer.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.model.PlayerCharacter;

//====================================== RULESET MANAGER ======================================//
//  This class is responsible for most actions that handle the ruleset file. This includes:    //
//  - Loading the file                                                                         //
//  - Saving the file                                                                          //
//  - Parsing the file                                                                         //
//                                                                                             //
// It's the most central class in the program and will most likely be the most advanced class. //
//                                                                                             //
//=============================================================================================//

@Service
public class RulesetManager {

    // injected from the State class
    private final State appState;
    private final RulesetSaver rulesetSaver;

    public List<PlayerCharacter> getCharacterList() {
        return new ArrayList<>(appState.getCharacters().values());
    }

    // these hold the raw byte arrays of the ruleset and its parts
    private byte[] rawRulesetBytes; // the entire ruleset
    private byte[] headerBytes; // the binary header (DO NOT TOUCH)
    private byte[] contentBytes; // the text content (Editable)
    private byte[] footerBytes; // the binary footer (DO NOT TOUCH)

    // This string holds the original text content of the file.
    // We use this as a "template" when saving, to ensure we don't break
    // parts of the file we haven't parsed.
    private String originalContentString;

    // We store the total size of the original file to validate that our
    // output file is EXACTLY the same size before saving.
    private int originalTotalSize;

    // "appstate" is injected from the state.java component class, which holds the
    // path to the ruleset file (among other things)
    public RulesetManager(State appState, RulesetSaver rulesetSaver) {
        this.appState = appState;
        this.rulesetSaver = rulesetSaver;
    }

    /* ============================================================================================ */
    /*                                      SAVE RULESET                                            */
    /*                                                                                              */
    /* Delegates the saving process to the RulesetSaver service.                                    */
    /* ============================================================================================ */
    public void saveRuleset() {
        rulesetSaver.saveRuleset(
            appState.getHardcodedPath(),
            headerBytes,
            footerBytes,
            contentBytes,
            originalContentString,
            originalTotalSize,
            appState.getCharacters()
        );
    }






    /* ============================================================================================ */
    /*                                      LOAD RULESET                                            */
    /*                                                                                              */
    /* This method loads the ruleset file from disk and initiates the splitting process.            */
    /* It reads the file as raw bytes to preserve the binary structure of the header and footer.    */
    /* ============================================================================================ */
    public void loadRuleset() {

        // Gets the ruleset path
        Path filePath = appState.getHardcodedPath();

        // Opens inputstream and converts path to file
        try (FileInputStream ruleset = new FileInputStream(filePath.toFile())) {

            // 1. Read the entire file into a byte array
            this.rawRulesetBytes = (ruleset.readAllBytes());
            this.originalTotalSize = rawRulesetBytes.length;

            // 2. Split the file into Header, Content, and Footer using byte scanning
            splitRulesetContent();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't read file: " + e.getMessage()); // DEBUG
        }
    }






    /* ============================================================================================ */
    /*                                  SPLIT RULESET CONTENT                                       */
    /*                                                                                              */
    /* This method implements the "Split & Scan" algorithm to safely separate the binary parts      */
    /* from the editable text part.                                                                 */
    /*                                                                                              */
    /* STRATEGY:                                                                                    */
    /* 1. Find the start of the text section by looking for the unique string marker:               */
    /*    "Parser: HeroSummon"                                                                      */
    /* 2. Find the end of the text section by looking for the first NUL byte (0x00)                 */
    /*    that appears AFTER the start marker.                                                      */
    /*                                                                                              */
    /* ============================================================================================ */
    private void splitRulesetContent() {

        // The unique string that marks the beginning of the editable text section.
        // We use ISO_8859_1 to get the correct byte sequence (1:1 mapping).
        byte[] startMarker = "Parser: HeroSummon".getBytes(StandardCharsets.ISO_8859_1);
        
        int startIndex = -1;
        int endIndex = -1;

        /* ============================================================================================ */
        /* STEP 1: Find the Start Index                                                                 */
        /*                                                                                              */
        /* We scan through the raw bytes to find the sequence matching "Parser: HeroSummon".            */
        /* We stop at length - marker.length to avoid index out of bounds.                              */
        /* ============================================================================================ */
        for (int i = 0; i < rawRulesetBytes.length - startMarker.length; i++) {
            boolean match = true;

            // Check if the sequence matches at this position
            for (int j = 0; j < startMarker.length; j++) {
                if (rawRulesetBytes[i + j] != startMarker[j]) {
                    match = false;
                    break;
                }
            }

            // If we found the match, this is our start index.
            if (match) {
                startIndex = i;
                break;
            }
        }

        if (startIndex == -1) {
            throw new RuntimeException("CRITICAL ERROR: Could not find start marker 'Parser: HeroSummon' in ruleset file.");
        }

        /* ============================================================================================ */
        /* STEP 2: Find the End Index                                                                   */                           
        /*                                                                                              */                    
        /* The text section ends where the binary footer begins. The footer always starts with          */
        /* a NUL byte (0x00). We scan forward from the startIndex to find it.                           */
        /* ============================================================================================ */
        for (int i = startIndex; i < rawRulesetBytes.length; i++) {
            if (rawRulesetBytes[i] == 0x00) {
                endIndex = i;
                break;
            }
        }

        if (endIndex == -1) {
            throw new RuntimeException("CRITICAL ERROR: Could not find end marker (NUL byte) in ruleset file.");
        }

        /* ============================================================================================ */
        /* STEP 3: Extract the Sections                                                                 */  
        /*                                                                                              */  
        /* Now that we have the boundaries, we copy the bytes into their respective arrays.             */  
        /*                                                                                              */  
        /* Header: From start (0) to startIndex                                                         */  
        /* Content: From startIndex to EndIndex                                                         */
        /* Footer: From EndIndex to the end of the file                                                 */
        /* ============================================================================================ */
        this.headerBytes = Arrays.copyOfRange(rawRulesetBytes, 0, startIndex);
        this.contentBytes = Arrays.copyOfRange(rawRulesetBytes, startIndex, endIndex);
        this.footerBytes = Arrays.copyOfRange(rawRulesetBytes, endIndex, rawRulesetBytes.length);

        /* ============================================================================================ */
        /* STEP 4: Decode Content                                                                       */
        /*                                                                                              */
        /* We convert the content bytes to a String using ISO_8859_1.                                   */
        /* This string will be used for parsing and as a template for saving.                           */
        /* ============================================================================================ */
        this.originalContentString = new String(contentBytes, StandardCharsets.ISO_8859_1);

        // Debug prints
        System.out.println("Ruleset loaded and split successfully.");
        System.out.println("Header size: " + headerBytes.length + " bytes.");
        System.out.println("Content size: " + contentBytes.length + " bytes.");
        System.out.println("Footer size: " + footerBytes.length + " bytes.");
        System.out.println("Total Reconstructed Size: " + (headerBytes.length + contentBytes.length + footerBytes.length) + " bytes (Should match " + originalTotalSize + ")");

        // Now we can parse the content section
        parseRulesetContent();
    }







    /* ============================================================================================ */
    /*                                    PARSED RULESET CONTENT                                    */
    /*                                                                                              */
    /* This method restructures the data retrieved from the ruleset.                                */
    /* We use the 'originalContentString' which we decoded safely using UTF-8.                      */
    /* We split the content into smaller blocks based on the "Parser: " tag.                        */
    /* These blocks are then processed one by one, extracting the relevant data.                    */
    /*                                                                                              */
    /* ============================================================================================ */
    private void parseRulesetContent() {
        
        // Clear any existing characters to ensure a fresh state
        appState.clearCharacters();

        // We use the string we decoded in splitRulesetContent
        String contentString = this.originalContentString;

        // Splits up the content block into smaller blocks based on parser tags
        String[] allBlocks = contentString.split("Parser: ");

        // Debug variables
        int characterCount = 0;
        int itemCount = 0;
        int abilityCount = 0;

        // skip index 0, since it contains everything before the first "Parser: " tag
        for (int i = 1; i < allBlocks.length; i++) {
            String currentBlock = allBlocks[i].trim();

            // Check the type of block and process accordingly
            if (currentBlock.startsWith("Character")) {
                characterCount++;
                parseCharacterBlock(currentBlock);

            } else if (currentBlock.startsWith("Item")) {
                itemCount++;

            } else if (currentBlock.startsWith("AbilityCard")) {
                abilityCount++;
            }
        }

        for (PlayerCharacter character : appState.getCharacters().values()) {
            System.out.println("Character Loaded: " + character.getName()); // DEBUG
            System.out.println("  Card Amount: " + character.getCardAmount()); // DEBUG
            System.out.println("  Max HP Levels: " + character.getHpLvlOne() + ", " +
                    character.getHpLvlTwo() + ", " +
                    character.getHpLvlThree() + ", " +
                    character.getHpLvlFour() + ", " +
                    character.getHpLvlFive() + ", " +
                    character.getHpLvlSix() + ", " +
                    character.getHpLvlSeven() + ", " +
                    character.getHpLvlEight() + ", " +
                    character.getHpLvlNine()); // DEBUG
        }

        System.out.println("Total Characters Found: " + characterCount); // DEBUG
        System.out.println("Total Items Found: " + itemCount); // DEBUG
        System.out.println("Total Ability Cards Found: " + abilityCount); // DEBUG

    }

    /* ============================================================================================ */
    /*                                  PARSE CHARACTER BLOCK                                       */
    /*                                                                                              */
    /*                                                                                              */
    /* This method extracts the relevant data from character blocks. It looks for specific lines    */
    /* that contain the data we want, extracts that data, and stores it in a PlayerCharacter POJO   */
    /*                                                                                              */
    /* ============================================================================================ */
    private void parseCharacterBlock(String currentBlock) {

        String name = null;
        String cardAmount = null;

        // hp per level to be added to pojo
        String[] hpPerLevel = new String[9];

        // split the block into lines
        String[] lines = currentBlock.split("\n");

        // for every line
        for (String line : lines) {

            // get rid of leading/trailing whitespace
            line = line.trim();

            // if line starts with ID:, it's the unique identifier
            if (line.startsWith("ID:")) {

                // extract the name by removing "ID:" and trimming whitespace
                name = line.replace("ID:", "").trim();

            } else if (line.startsWith("HealthTable:")) {

                // extract the hp values between the brackets
                String allHpValues = line.substring(line.indexOf('[') + 1, line.indexOf(']'));

                // split by commas and trim whitespace
                String[] hpValuesSeparated = allHpValues.split(",");

                // assign to hpPerLevel array
                for (int i = 0; i < 9; i++) {
                    hpPerLevel[i] = hpValuesSeparated[i].trim();
                }

            } else if (line.startsWith("NumberAbilityCardsInBattle:")) {
                // extract the card amount
                cardAmount = line.replace("NumberAbilityCardsInBattle:", "").trim();

            }

        }

        // Filter out tutorial characters to prevent them from being loaded into State
        // This ensures they are never modified or written back to the file with incorrect values
        if (name != null && name.toLowerCase().contains("tutorial")) {
            System.out.println("Filtering out tutorial character: " + name);
            return;
        }

        if (name != null) {
            System.out.println("Parsed Character: " + name + " | Cards: " + cardAmount);
        }

        // create PlayerCharacter object and add to list of characters
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
        appState.addCharacter(character);

    }
}
