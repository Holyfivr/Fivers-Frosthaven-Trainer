package se.holyfivr.trainer.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.model.PlayerCharacter;

//====================================== RULESET SAVER ========================================//
//  This class is responsible for the complex logic of saving the ruleset file safely.         //
//  It implements the "Filler Bank" strategy to ensure the file size remains exactly unchanged.//
//=============================================================================================//

@Service
public class RulesetSaver {

    /* ============================================================================================ */
    /*                                      SAVE RULESET                                            */
    /*                                                                                              */
    /* This method orchestrates the saving process. It reconstructs the content with new values,    */
    /* optimizes it to save space, creates a filler block to match the original file size,          */
    /* and finally writes the binary-safe result back to disk.                                      */
    /* ============================================================================================ */
    public void saveRuleset(Path filePath, 
                            byte[] headerBytes, 
                            byte[] footerBytes, 
                            byte[] originalContentBytes, 
                            String originalContentString, 
                            int originalTotalSize, 
                            Map<String, PlayerCharacter> characters) {
        try {
            System.out.println("Starting save process...");

            // 1. Reconstruct the content string with updated values from AppState
            // This now handles Local Banking (padding/trimming) internally to preserve offsets.
            String newContentString = reconstructContentWithUpdates(originalContentString, characters);

            // 2. Encode to bytes (ISO_8859_1)
            byte[] newContentBytes = newContentString.getBytes(StandardCharsets.ISO_8859_1);

            // 3. Construct the Final Byte Array
            // Structure: [Header] + [New Content] + [Footer]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(headerBytes);
            outputStream.write(newContentBytes);
            outputStream.write(footerBytes);

            byte[] finalFileBytes = outputStream.toByteArray();

            // 4. Final Validation
            if (finalFileBytes.length != originalTotalSize) {
                throw new SecurityException("SAFETY CHECK FAILED: Final file size (" + finalFileBytes.length + ") does not match original size (" + originalTotalSize + "). Aborting save.");
            }

            // 5. Write to Disk
            Files.write(filePath, finalFileBytes);
            System.out.println("File saved successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save ruleset: " + e.getMessage());
        }
    }

    /* ============================================================================================ */
    /*                               RECONSTRUCT CONTENT WITH UPDATES                               */
    /*                                                                                              */
    /* This method iterates through the original text blocks. If a block belongs to a Character,    */
    /* it injects the current values from the AppState. Otherwise, it keeps the block as is.        */
    /* ============================================================================================ */
    private String reconstructContentWithUpdates(String originalContentString, Map<String, PlayerCharacter> characters) {
        StringBuilder builtContent = new StringBuilder();
        
        // We split by "Parser: " just like when reading.
        // Use -1 limit to preserve trailing empty strings if any
        String[] allBlocks = originalContentString.split("Parser: ", -1);

        for (int i = 0; i < allBlocks.length; i++) {
            String originalBlock = allBlocks[i];
            
            // Skip empty blocks (usually the first one)
            if (originalBlock.isEmpty()) continue;

            String currentBlock = originalBlock;

            // Check if this block is a Character that we need to update
            if (currentBlock.trim().startsWith("Character")) {
                // Extract name to find the matching object in our State
                String name = extractNameFromBlock(currentBlock);
                PlayerCharacter pc = characters.get(name);

                if (pc != null) {
                    // Inject the new values into the block string
                    currentBlock = updateCharacterBlockString(currentBlock, pc);
                }
            }

            // ========================================================================================
            // LOCAL BANKING STRATEGY (Padding/Trimming)
            // ========================================================================================
            // We must ensure currentBlock has the EXACT same length as originalBlock.
            
            int originalLength = originalBlock.length();
            int currentLength = currentBlock.length();
            int diff = originalLength - currentLength;

            if (diff > 0) {
                // New block is shorter. Pad with spaces.
                StringBuilder padded = new StringBuilder(currentBlock);
                for (int k = 0; k < diff; k++) {
                    padded.append(" ");
                }
                currentBlock = padded.toString();
            } else if (diff < 0) {
                // New block is longer. We must trim spaces.
                int charsToRemove = Math.abs(diff);
                // Try to remove double spaces first
                while (charsToRemove > 0 && currentBlock.contains("  ")) {
                    currentBlock = currentBlock.replaceFirst("  ", " ");
                    charsToRemove--;
                }
                // Try to remove spaces before newlines
                while (charsToRemove > 0 && currentBlock.contains(" \n")) {
                    currentBlock = currentBlock.replaceFirst(" \n", "\n");
                    charsToRemove--;
                }
                 while (charsToRemove > 0 && currentBlock.contains(" \r\n")) {
                    currentBlock = currentBlock.replaceFirst(" \r\n", "\r\n");
                    charsToRemove--;
                }
                
                if (charsToRemove > 0) {
                    throw new RuntimeException("CRITICAL ERROR: Could not trim enough bytes from block to match original size. Overflow by " + charsToRemove + " bytes.");
                }
            }

            // Re-add the delimiter
            builtContent.append("Parser: ");
            builtContent.append(currentBlock);
        }

        return builtContent.toString();
    }

    /* ============================================================================================ */
    /*                                  UPDATE CHARACTER BLOCK STRING                               */
    /*                                                                                              */
    /* Uses Regex to safely replace specific values in the text block without touching structure.   */
    /* ============================================================================================ */
    private String updateCharacterBlockString(String block, PlayerCharacter pc) {
        
        System.out.println("Updating block for character: " + pc.getName());
        System.out.println("  -> New Card Amount: " + pc.getCardAmount());

        // 1. Update Card Amount
        // Regex looks for "NumberAbilityCardsInBattle:" followed by any whitespace and digits
        if (pc.getCardAmount() != null) {
            block = block.replaceAll("NumberAbilityCardsInBattle:\\s*\\d+", "NumberAbilityCardsInBattle: " + pc.getCardAmount());
        }

        // 2. Update Health Table
        // Construct the new array string: [6, 7, 8, ...]
        String newHealthTable = "[" + pc.getHpLvlOne() + ", " + pc.getHpLvlTwo() + ", " + pc.getHpLvlThree() + ", " +
                                      pc.getHpLvlFour() + ", " + pc.getHpLvlFive() + ", " + pc.getHpLvlSix() + ", " +
                                      pc.getHpLvlSeven() + ", " + pc.getHpLvlEight() + ", " + pc.getHpLvlNine() + "]";
        
        // Regex looks for "HealthTable:" followed by whitespace and anything inside brackets [...]
        block = block.replaceAll("HealthTable:\\s*\\[.*?\\]", "HealthTable: " + newHealthTable);

        return block;
    }

    /* ============================================================================================ */
    /*                                      CREATE FILLER BLOCK                                     */
    /*                                                                                              */
    /* Generates a byte array consisting of a comment start " #" followed by spaces.                */
    /* This is used to pad the file to the exact correct size.                                      */
    /* ============================================================================================ */
    private byte[] createFillerBlock(int length) {
        if (length == 0) return new byte[0];

        StringBuilder filler = new StringBuilder();
        
        // We start with a space and a hash to mark it as a comment in the YAML-like structure
        // This ensures the parser ignores our filler data.
        if (length >= 2) {
            filler.append(" #");
        } else {
            // Fallback for extremely rare edge case where we only need 1 byte (just a space)
            filler.append(" ");
        }

        // Fill the rest with spaces
        while (filler.length() < length) {
            filler.append(" ");
        }

        return filler.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    // Helper to quickly get name from a raw block string (reused logic from parseCharacterBlock)
    private String extractNameFromBlock(String block) {
        String[] lines = block.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("ID:")) {
                return line.replace("ID:", "").trim();
            }
        }
        return null;
    }
}
