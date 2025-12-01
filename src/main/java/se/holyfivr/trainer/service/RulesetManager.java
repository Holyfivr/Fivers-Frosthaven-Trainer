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

    // list of all characters parsed from the ruleset
    private List<PlayerCharacter> characterList = new ArrayList<>();

    // these hold the raw byte arrays of the ruleset and its parts
    private byte[] rawRulesetBytes; // the entire ruleset
    private byte[] headerBytes; // the header
    private byte[] contentAndFillerBytes; // the main content + filler
    private byte[] footerBytes; // the footer

    // "appstate" is injected from the state.java component class, which holds the
    // path to the ruleset file
    public RulesetManager(State appState) {
        this.appState = appState;
    }

    // This method loads the ruleset (duh)
    public void loadRuleset() {

        // Gets the ruleset path
        Path filePath = appState.getHardcodedPath();

        // ADD FUNCTION TO SELECT PATH LOCATION HERE !!(TODO)!!

        // Opens inputstream and converts path to file
        try (FileInputStream ruleset = new FileInputStream(filePath.toFile())) {

            // The entire file as a byte-array
            this.rawRulesetBytes = (ruleset.readAllBytes());

            // converted to a string in ISO_5589_1 to guarante a 1:1 byte/character ratio
            String unParsedContent = new String(rawRulesetBytes, StandardCharsets.ISO_8859_1);

            // Splits the ruleset into parts
            splitRulesetContent(unParsedContent);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't read file: " + e.getMessage()); // DEBUG
        }
    }

    // This method splits the ruleset into parts and divides them into sections
    // depending on what type of block each part should be a part of.
    // to start with, we divide it in 4 parts: header, filler, content, footer
    private void splitRulesetContent(String unparsedContent) {

        // these delimiters mark the start and end of the content section. The first
        // object after the header is always Parser: HeroSummon
        // and the last type of objects in the contentblock are always PetCards. These
        // are not really of any interest to edit, so we are fine with
        // leaving them out of the content section.
        final String DELIMITER = "Parser: HeroSummon";
        final String FOOTER_DELIMITER = "Parser: PetCard";

        // finds the indexes of the delimiters.
        int splitIndex = unparsedContent.indexOf(DELIMITER);
        int splitIndexFooter = unparsedContent.indexOf(FOOTER_DELIMITER);

        // just a precaution to make sure the delimiters were found
        if (splitIndex != -1 && splitIndexFooter != -1) {

            // splits the raw byte array into 3 separate arrays: header, content + filler,
            // footer
            // using Arrays.copyOfRange to copy the relevant parts of the byte array
            // so the first line here is basically "copy the range from index 0, to the
            // index of the first delimiter, from the rawRulesetBytes array"
            this.headerBytes = Arrays.copyOfRange(rawRulesetBytes, 0, splitIndex);
            this.contentAndFillerBytes = Arrays.copyOfRange(rawRulesetBytes, splitIndex, splitIndexFooter);
            this.footerBytes = Arrays.copyOfRange(rawRulesetBytes, splitIndexFooter, rawRulesetBytes.length);

            // Debug prints
            System.out.println("Ruleset loaded and split successfully.");
            System.out.println("Header size: " + headerBytes.length + " bytes.");
            System.out.println("Content size: " + contentAndFillerBytes.length + " bytes.");
            System.out.println("Footer size: " + footerBytes.length + " bytes.");

            // Now we can parse the content section
            parseRulesetContent();

        } else if (splitIndex == -1) {
            System.err.println("Could not find the delimiter '" + DELIMITER + "' in the file.");
        } else {
            System.err.println("Could not find the footer delimiter '" + FOOTER_DELIMITER + "' in the file.");
        }
    }


    //==================================== parseRulesetContent ====================================//
    //  This method restructures the data retrieved from the ruleset. We start by creating a       //
    //  string, based on the content-block. We make sure to use ISO_8859_1 to maintain 1:1 byte    //
    //  ratio. We then split the content into smaller blocks, gathering the different objects in   //
    //  their own blocks. These blocks are then processed one by one, extracting the relevant      //
    //  data that we want to edit. This is then stores in POJOS for easy access in the rest of the //
    //  program.                                                                                   //
    //=============================================================================================//
    private void parseRulesetContent() {
        String contentString = new String(contentAndFillerBytes, StandardCharsets.ISO_8859_1);

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

        for (PlayerCharacter character : characterList) {
            System.out.println("Character Loaded: " + character.getName()); // DEBUG
            System.out.println("  Card Amount: " + character.getCardAmount()); // DEBUG
            System.out.println("  Max HP Levels: " + character.getMaxHealthLevelOne() + ", " +
                    character.getMaxHealthLevelTwo() + ", " +
                    character.getMaxHealthLevelThree() + ", " +
                    character.getMaxHealthLevelFour() + ", " +
                    character.getMaxHealthLevelFive() + ", " +
                    character.getMaxHealthLevelSix() + ", " +
                    character.getMaxHealthLevelSeven() + ", " +
                    character.getMaxHealthLevelEight() + ", " +
                    character.getMaxHealthLevelNine()); // DEBUG
        }

        System.out.println("Total Characters Found: " + characterCount); // DEBUG
        System.out.println("Total Items Found: " + itemCount); // DEBUG
        System.out.println("Total Ability Cards Found: " + abilityCount); // DEBUG

    }


    //==================================== parseCharacterBlock ===================================//
    //  This method extracts the relevant data from character blocks. It looks for specific       //
    //  lines that contain the data we want, extracts that data, and stores it in a               //
    //  PlayerCharacter POJO.                                                                     //
    //============================================================================================//
    private void parseCharacterBlock(String currentBlock) {

        // name to be added to pojo
        String name = null;

        // card amount to be added to pojo
        String cardAmount = null;

        // hp per level to be added to pojo
        String[] hpPerLevel = new String[9];

        // split the block into lines
        String[] lines = currentBlock.split("\n");

        // for every line
        for (String line : lines) {

            // get rid of leading/trailing whitespace
            line = line.trim();

            // if line starts with Model:, it's the name
            if (line.startsWith("Model:")) {
                
                // extract the name by removing "model:" and trimming whitespace
                name = line.replace("Model:", "").trim();

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
        characterList.add(character);

    }
}
