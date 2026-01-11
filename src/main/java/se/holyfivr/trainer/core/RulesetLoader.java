// Flyttad till core-paketet
package se.holyfivr.trainer.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.model.PlayerCharacter;

/* ====================================== RULESET LOADER ======================================= */
/*                                                                                               */
/*  This class is responsible for loading and parsing the Base.ruleset file.                     */
/*  Together with the RulesetSaver and RulesetParser, it's one of the most central classes in    */
/*  the program and one of the more advanced classes.                                            */
/*                                                                                               */
/* ============================================================================================= */

@Service
public class RulesetLoader {

    // injected from the State/RulesetParser/RulesetSaver classes to access their data
    private final ActiveSessionData activeSession;
    private final RulesetSaver rulesetSaver;
    private final RulesetParser rulesetParser;


    // Returns a list of all loaded characters
    public List<PlayerCharacter> getCharacterList() {
        return new ArrayList<>(activeSession.getCharacters().values());
    }


    // these hold the raw byte arrays of the ruleset and its parts
    private byte[] rawRulesetBytes;// the entire ruleset
    private byte[] headerBytes;   // the binary header (DO NOT TOUCH)
    private byte[] contentBytes; // the text content (Editable)
    private byte[] footerBytes; // the binary footer (DO NOT TOUCH)


    // This string holds the original text content of the file.
    // We use this as a "template" when saving, to ensure we don't break
    // parts of the file we haven't parsed.
    private String originalContentString;


    // We store the total size of the original file to validate that our
    // output file is EXACTLY the same size before saving.
    private int originalTotalSize;


    // "activeSession" is injected from the ActiveSession.java component class, which holds the
    // path to the ruleset file (among other things)
    public RulesetLoader(ActiveSessionData activeSession, RulesetSaver rulesetSaver, RulesetParser rulesetParser) {
        this.activeSession = activeSession;
        this.rulesetSaver = rulesetSaver;
        this.rulesetParser = rulesetParser;
    }


    /* ============================================================================================ */
    /*                                      SAVE RULESET                                            */
    /*                                                                                              */
    /* Delegates the saving process to the RulesetSaver service.                                    */
    /* It provides all necessary data, including the original file structure and the modified       */
    /* character data.                                                                              */
    /* ============================================================================================ */
    public void saveRuleset() {
        rulesetSaver.saveRuleset(
            activeSession.getRulesetPath(),
            headerBytes,
            footerBytes,
            contentBytes,
            originalContentString,
            originalTotalSize,
            activeSession.getCharacters()
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
        Path filePath = activeSession.getRulesetPath();

        // Opens inputstream and converts path to file
        try (FileInputStream ruleset = new FileInputStream(filePath.toFile())) {

            // 1. Read the entire file into a byte array
            this.rawRulesetBytes = (ruleset.readAllBytes());

            // This is used later to validate the saved file size
            this.originalTotalSize = rawRulesetBytes.length;

            // 2. Split the file into Header, Content, and Footer using byte scanning
            splitRulesetContent();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* ============================================================================================ */
    /*                                  SPLIT RULESET CONTENT                                       */
    /*                                                                                              */
    /* This method scans the data from the ruleset and separates it into three different parts:     */
    /* - Header: The initial binary data that sets up the file structure                            */
    /* - Content: The main text content that holds all the game data we want to edit                */
    /* - Footer: The ending binary data that finalizes the file structure                           */
    /* ============================================================================================ */
    private void splitRulesetContent() {

        // These are the markers we use to find the content block.
        // Parser: HeroSummon is the first block after the header.
        // The content ends at the first NUL byte (0x00) after that.
        byte[] startMarker = "Parser: HeroSummon".getBytes(StandardCharsets.ISO_8859_1);
        byte endMarker = 0x00; // NUL byte

        // Find marker positions:
        // Checks the raw byte array for the retrieved markers index-positions
        int startIndex = findSequenceIndex(rawRulesetBytes, startMarker, 0); 
        int endIndex = findByteIndex(rawRulesetBytes, endMarker, startIndex);

        // Check if markers were found:
        // This is just for debugging. If we can't find the markers, the default value of the index is -1.
        // If we can't find them, something is wrong with the ruleset file.
        if (startIndex == -1) {
            throw new RuntimeException("Error: Could not find start marker 'Parser: HeroSummon' in ruleset file.");
        }
        if (endIndex == -1) {
            throw new RuntimeException("Error: Could not find end marker (NUL byte) in ruleset file.");
        }

        // Extract blocks:
        // documentStart = start of document
        // headerend = start of content-block
        int documentStart = 0;
        int headerEnd = startIndex;
        
        // contentstart = start of content-block
        // contentend = end of content-block
        int contentStart = startIndex;
        int contentEnd = endIndex;
        
        // footerstart = end of content-block
        // documentEnd = end of document
        int footerStart = endIndex;
        int documentEnd = rawRulesetBytes.length;

        // Now we can create the byte arrays for each part
        // we need to use bytes to preserve the binary data
        this.headerBytes = Arrays.copyOfRange(rawRulesetBytes, documentStart, headerEnd);
        this.contentBytes = Arrays.copyOfRange(rawRulesetBytes, contentStart, contentEnd);
        this.footerBytes = Arrays.copyOfRange(rawRulesetBytes, footerStart, documentEnd);

        // Decode Content:
        // We decode the content bytes into a string using ISO-8859-1 encoding
        // This encoding preserves all byte values, allowing us to reconstruct 
        // the original byte array later.
        this.originalContentString = new String(contentBytes, StandardCharsets.ISO_8859_1);

        // once all the above steps are done, we can parse the content
        // this is done by the RulesetParser service-class
        rulesetParser.parseRulesetContent(this.originalContentString);
    }


    // Helper: Find a byte sequence in a byte array
    // Returns the index of the first occurrence of the sequence, or -1 if not found
    // This is the method used to find the "Parser: HeroSummon" marker
    private int findSequenceIndex(byte[] source, byte[] sequence, int fromIndex) {
        for (int i = fromIndex; i < source.length - sequence.length; i++) {
            boolean match = true;
            for (int j = 0; j < sequence.length; j++) {
                if (source[i + j] != sequence[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }

    // Helper: Find a single byte in a byte array
    // Returns the index of the first occurrence of the byte, or -1 if not found
    // This is the method used to find the NUL byte marking the end of the content-section
    private int findByteIndex(byte[] source, byte target, int fromIndex) {
        for (int i = fromIndex; i < source.length; i++) {
            if (source[i] == target) {
                return i;
            }
        }
        return -1;
    }   
}