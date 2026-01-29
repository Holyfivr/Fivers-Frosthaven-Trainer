package se.holyfivr.trainer.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import se.holyfivr.trainer.model.enums.RulesetFileName;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.utils.BlockUpdater;
import se.holyfivr.trainer.core.utils.PatternRepository;
import se.holyfivr.trainer.core.utils.SaveUtils;
import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.PlayerCharacter;

/* ====================================== RULESET SAVER ======================================== */
/*                                                                                               */
/* This class is responsible for the complex logic of saving the ruleset file safely.            */
/* It is without a doubt the most complex part of the entire application, and the one who gave   */ 
/* me the most headaches. At the time of writing this, I was studying my first semester to       */
/* become a Java developer, and if I knew in advance how difficult this would prove, I probably  */
/* wouldn't have made this application so early into my studies. But here we are. ¯\_(ツ)_/¯     */
/* It implements the "Filler Bank" strategy to ensure the file size remains exactly unchanged.   */
/*                                                                                               */
/* ============================================================================================= */

@Service
public class RulesetSaver {

    private final ActiveSessionData activeSessionData;
    private final SaveUtils saveUtils;
    private final BlockUpdater blockUpdater;

        /**
         * Constructs the saver with its collaborators.
         *
         * @param activeSessionData in-memory state used for updated values
         * @param saveUtils helper utilities for block-level operations
         * @param validator validation and sanitation logic
         */
        RulesetSaver(ActiveSessionData activeSessionData, SaveUtils saveUtils, BlockUpdater blockUpdater) {
            this.activeSessionData = activeSessionData;
            this.saveUtils = saveUtils;
            this.blockUpdater = blockUpdater;
        }


    /* ============================================================================================ */
    /*                                        SAVE RULESET                                          */
    /*                                                                                              */
    /* This method is responsible the saving process. It reconstructs the content with new values,  */
    /* optimizes it to save space, creates a "filler block" to match the original file size,        */
    /* and finally writes the binary-safe result back to disk.                                      */
    /* ============================================================================================ */
    public void saveRuleset(Path filePath,
            // these are all the original parts of the file needed for safe saving
            byte[] headerBytes,
            byte[] footerBytes,
            byte[] originalContentBytes,    // content as bytes
            String originalContentString,  // content as string
            int originalTotalSize)        // original total file size
    {
           
        try {

            // First we reconstruct the content string with updated values from ActiveSessionData.
            // This now handles Filler Bank (padding/trimming) internally to preserve offsets.
            String newContentString = reconstructContentWithUpdates(originalContentString);

            // We encode the string back to bytes (ISO_8859_1) to match original encoding
            byte[] newContentBytes = newContentString.getBytes(StandardCharsets.ISO_8859_1);

            // This part puts the file back together with header, content (with updated values), and footer.
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(newContentBytes);
            outputStream.write(footerBytes);

            // we then calculate the current size of the new data
            byte[] finalFileBytes = outputStream.toByteArray();

            // and compare it to the original size.
            // If they don't match, we throw an exception to avoid corrupting the file.
            if (finalFileBytes.length != originalTotalSize) {
                throw new SecurityException("Error: Final file size (" + finalFileBytes.length
                        + ") does not match original size (" + originalTotalSize + "). Aborting save."); // debug
            }

            // Finally, if everything is good, we write the bytes back to disk
            
            
            
            // This part ensures that no matter what backup is being edited, it will always update the Base.ruleset file
            // when saving, as well as the current file being edited. This way, you don't need to manually rename
            // any backup to Base.ruleset to have it be the file that the game uses.
            String fileName = filePath.getFileName().toString();
            if (fileName.equals(RulesetFileName.BASE_RULESET.getFileName())) {
                Files.write(filePath, finalFileBytes);
            } else if (fileName.equals(RulesetFileName.ORIGINAL_BACKUP.getFileName())) {
                // If we are editing the original backup, we DO NOT want to overwrite it.
                // We only want to apply the changes to the active game file (Base.ruleset).
                Files.write(filePath.resolveSibling(RulesetFileName.BASE_RULESET.getFileName()), finalFileBytes);
            } else {
                Files.write(filePath, finalFileBytes);
                Files.copy(filePath, filePath.resolveSibling(RulesetFileName.BASE_RULESET.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("File saved successfully!"); // debug

        } catch (IOException e) {
            System.err.println("Failed to save ruleset: " + e.getMessage()); // debug
        }
    }

    /* ============================================================================================ */
    /* RECONSTRUCT CONTENT WITH UPDATES                                                             */
    /*                                                                                              */
    /* This is the core of the saving logic. We iterate through the file content block by block.    */
    /* We then fetch the latest data from our ActiveSessionData.java class and inject it into       */
    /* the block.                                                                                   */
    /*                                                                                              */
    /* Something very important to remember before editing this part: We use a "Filler Bank"        */
    /* strategy here. This means every single block MUST maintain its exact original byte length    */
    /* to avoid breaking the file's structure.                                                      */
    /* We handle this by calling 'adjustBlockSize' for every block before appending it.             */
    /*                                                                                              */
    /* Initially I attempted to use a strategy where i would just remove all the targeted           */
    /* whitespace from the entire content string at once, but I couldn't get it to work. Even if    */
    /* I maintained a perfect byte count, the file would still break. So I had to resort to         */      
    /* this more surgical approach.                                                                 */
    /* ============================================================================================ */
    private String reconstructContentWithUpdates(String originalContentString) {
        StringBuilder builtContent = new StringBuilder();

        // We split by "Parser: " just like when reading.
        // Use -1 limit to preserve trailing empty strings if any
        String[] allBlocks = originalContentString.split("Parser: ", -1);

        // We iterate through each block to process them one by one
        for (int i = 0; i < allBlocks.length; i++) {

            // Get the original block for size reference
            String originalBlock = allBlocks[i]; 

            // Skip empty blocks (the first one)
            if (originalBlock.isEmpty())
                continue;

            // We will work on a copy of the original block
            String currentBlock = originalBlock;

            // If the current block is a character object
            // Extract name to find the matching object in our ActiveSessionData
            // Then update with new values
            if (isParser(currentBlock, PatternRepository.CHARACTER_PARSER_PATTERN)) {
                String identifier = saveUtils.extractIdentifier(currentBlock, "ID:");
                PlayerCharacter pc = activeSessionData.getCharacters().get(identifier);
                currentBlock = blockUpdater.updateBlock(currentBlock, pc); 
            }

            // same with gamemode
            if (isParser(currentBlock, PatternRepository.GAMEMODE_PARSER_PATTERN)) {   
                List<String> unlockedCharacters = activeSessionData.getUnlockedCharacterList();
                currentBlock = blockUpdater.updateBlock(currentBlock, unlockedCharacters);
            }

            // with items
            if (isParser(currentBlock, PatternRepository.ITEM_PARSER_PATTERN)) {
                String identifier = saveUtils.extractIdentifier(currentBlock, "StringID:");
                Item item = activeSessionData.getItems().get(identifier);
                currentBlock = blockUpdater.updateBlock(currentBlock, null, item, null);
            }

            // fh items
            if (isParser(currentBlock, PatternRepository.FH_ITEM_PARSER_PATTERN)) {
                String identifier = saveUtils.extractIdentifier(currentBlock, "ScenarioItemID:");      
                currentBlock = blockUpdater.updateBlock(currentBlock, identifier);
            }

            // ability cards
            if (isParser(currentBlock, PatternRepository.ABILITY_CARD_PARSER_PATTERN)) {
                String identifier = saveUtils.extractIdentifier(currentBlock, "Name:");
                AbilityCard abilityCard = activeSessionData.getAbilityCards().get(identifier);
                Map<String, AbilityCard> abilityCardMap = activeSessionData.getAbilityCards();
                currentBlock = blockUpdater.updateBlock(currentBlock, abilityCard, null, abilityCardMap); 
            }
            

            // Checks for potentially duped comments and removes them
            currentBlock = saveUtils.stripDoubleHashComments(currentBlock);
            
            /* ============================================================================= */
            /*                              FILLER BANK STRATEGY                             */
            /* ============================================================================= */
            // We must ensure currentBlock has the EXACT same length as originalBlock.
            currentBlock = adjustBlockSize(currentBlock, originalBlock.length());
            
            // Re-add the delimiter
            builtContent.append("Parser: ");
            builtContent.append(currentBlock);
        }
        return builtContent.toString();
    }


    /* ================================================================= */
    /*                         HELPER: ISPARSER                          */
    /*                 Helper to make parser checks cleaner              */
    /* ================================================================= */
    public boolean isParser(String currentBlock, Pattern parserPattern) {
        return parserPattern.matcher(currentBlock).matches();
    }


    /* ============================================================================================ */
    /*                                      ADJUST BLOCK SIZE                                       */
    /*                                                                                              */
    /* This is our "Filler Bank" implementation. For the file to remain valid, every section        */
    /* must be exactly the same size (in bytes) as it was when we read it.                          */
    /*                                                                                              */
    /* Logic is split into 'padBlock' and 'trimBlock' for better readability.                       */
    /* padBlock adds spaces to the end of the block until it matches the original size.             */
    /* trimBlock removes unnecessary whitespace to shrink the block to the original size.           */
    /* In this first version, trimBlock has a huge weakness: If there are not enough removable      */
    /* spaces,                                                                                      */
    /* it will throw an exception and abort the saving process. This is to avoid corrupting         */ 
    /* the file. This will be addressed in the future, by making the fillerbanks more dynamic.      */
    /* It is far too complex to address now, and is not needed in this current version. In a future */
    /* update I will add the feature to add more starting characters, and this must be addressed at */
    /* that point, since those blocks have far too few whitespaces to trim.                         */
    /* ============================================================================================ */
    /**
     * Ensures the current block matches the original byte length.
     *
     * Any differences are corrected by padding or trimming to keep the
     * ruleset file byte-perfect.
     */
    private String adjustBlockSize(String currentBlock, int originalLength) {
        //System.out.println(currentBlock + " | Original Length: " + originalLength); // debug
        // I'm leaving this debug in, because it's a very useful debug. It should be commented out unless specifically needed.
        // When saving, it will print every block and its original length, which helps identify which block is causing size mismatches, 
        // but it slows down the saving process a lot.

        // checks current length against original length
        int currentLength = currentBlock.length();
        int diff = originalLength - currentLength;

        if (diff > 0) {
            // New block is shorter. Pad with spaces.
            return padBlock(currentBlock, diff);
        } else if (diff < 0) {
            // New block is longer. We must trim characters.
            return trimBlock(currentBlock, Math.abs(diff));
        }
        
        // Exact match, no adjustment needed
        return currentBlock;
    }

    /**
     * Pads the block with trailing spaces to reach the target length.
     */
    private String padBlock(String block, int amount) {
        StringBuilder padded = new StringBuilder(block);
        for (int k = 0; k < amount; k++) {
            padded.append(" ");
        }
        return padded.toString();
    }

    /* ============================================================================================ */
    /*                                   COOL TRICK HERE                                            */
    /*          Removes unnecessary whitespace to shrink the block to the original size             */
    /*                                                                                              */
    /* This part was actually pretty cool to figure out. When looking at the HEX-code for the       */
    /* ruleset file, I noticed that all the linebreaks ended with both a carriage return (0x0D)     */
    /* and a newline (0x0A). But you don't actually need both in a text file.                       */
    /* A simple newline is enough.                                                                  */
    /* So by removing carriage returns, we can save a lot of space. Every single line has at least  */
    /* one extra byte that we can steal. This discovery was a major breakthrough when I was trying  */
    /* to figure out the safest way to edit the file.                                               */
    /* Without this discovery, any change that increases the number of digits (like hp 6 -> 10),    */
    /* would mean I HAD to find spaces to take. And I can't just steal spaces from indentations and */
    /* many other places, since it would corrupt the file. I would NOT have been able to create     */
    /* such an advanced version of this application without this technique.                         */
    /* ============================================================================================ */
    
    /**
     * Trims whitespace (and also comments) in a prioritized order to reduce block length.
     * This method intentionally removes the least risky characters first
     * to preserve formatting and semantic meaning.
     */
    private String trimBlock(String currentBlock, int amount) {

        // We need to remove 'amount' characters from the currentBlock
        int charsToRemove = amount;

    
        // Strategy 0: Use comment text as filler bytes 
        // This was implemented after all the other ones, when I realised
        // that I could just start picking bytes from comments. So, to start with
        // thats what we do, since they are basically just free bytes.
        // Removes characters AFTER '#' but never removes the '#'
        while (charsToRemove > 0 && currentBlock.contains("#")) {
            String newBlock = currentBlock.replaceFirst("(?m)(#).", "$1");
            // If nothing changed, there is nothing more to trim safely
            if (newBlock.equals(currentBlock)) {
                break;
            }
            currentBlock = newBlock;
            charsToRemove--;
        }


        // Strategy 1: Remove spaces before newlines
        // Safest: Removes invisible "trailing whitespace" that serves no purpose.
        // This strategy, and strategy 3 look very similar, but there is one small difference.
        // This one only removes spaces on lines that do not have a colon.
        while (charsToRemove > 0 && (currentBlock.contains(" \n") || currentBlock.contains(" \r\n"))) {
            if (currentBlock.contains(" \n")) {
                currentBlock = currentBlock.replaceFirst(" \n", "\n");
            } else {
                currentBlock = currentBlock.replaceFirst(" \r\n", "\r\n");
            }
            charsToRemove--;
        }

        // Strategy 2: Remove carriage returns
        while (charsToRemove > 0 && currentBlock.contains("\r\n")) {
            currentBlock = currentBlock.replaceFirst("\r\n", "\n");
            charsToRemove--;
        }

        // Strategy 3: Remove any double spaces that exist after a : on a lina
        while (charsToRemove > 0 &&
                currentBlock.matches("(?m).*:[^\\n]* {2,}.*")) {

            currentBlock = currentBlock.replaceFirst(
                    "(?m)(:[^\\n]*?) {2,}",
                    "$1 ");
            charsToRemove--;
        }

        // Strategy 4: Remove double spaces anywhere
        // Least Safe: Affects indentation and visual formatting. Last resort.
        while (charsToRemove > 0 && currentBlock.contains("  ")) {
            currentBlock = currentBlock.replaceFirst("  ", " ");
            charsToRemove--;
        }

        // If we still haven't removed enough characters, we can't save safely.
        // This will throw an exception and abort the saving process.
        if (charsToRemove > 0) {
            throw new RuntimeException(
                    "Error: Could not trim enough bytes from currentBlock to match original size. Overflow by "
                            + charsToRemove + " bytes.");
        }

        return currentBlock;
    }

}
