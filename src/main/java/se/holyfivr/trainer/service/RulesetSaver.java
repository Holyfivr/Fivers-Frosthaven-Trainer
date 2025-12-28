package se.holyfivr.trainer.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.stereotype.Service;

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

    private static final String BASE_RULESET = "Base.ruleset";
    

    // Regex patterns for finding character attributes
    private static final Pattern CARD_AMOUNT_PATTERN = Pattern.compile("\\bNumberAbilityCardsInBattle:\\s*\\d+");
    private static final Pattern HEALTH_TABLE_PATTERN = Pattern.compile("\\bHealthTable:\\s*\\[.*?\\]");
    private static final Pattern UNLOCKED_CHARACTER_PATTERN = Pattern.compile("\\bUnlockedClasses:\\s*\\[.*?\\]");

    // Item Patterns
    private static final Pattern TOTAL_IN_GAME_PATTERN = Pattern.compile("\\bTotalInGame:\\s*\\d+");
    private static final Pattern COST_PATTERN = Pattern.compile("\\bCost:\\s*\\d+");
    private static final Pattern USAGE_PATTERN = Pattern.compile("\\bUsage:\\s*\\w+");
    private static final Pattern PROSPERITY_PATTERN = Pattern.compile("\\bProsperityRequirement:\\s*\\d+");
    private static final Pattern CONSUMES_PATTERN = Pattern.compile("\\bConsumes:\\s*\\w+");
    private static final Pattern INFUSE_PATTERN = Pattern.compile("\\bInfuse:\\s*(\\[.*?\\]|\\w+)");
    
    // Item Data Patterns
    private static final Pattern JUMP_PATTERN = Pattern.compile("\\bJump:\\s*(True|False)");
    private static final Pattern CONDITIONS_PATTERN = Pattern.compile("\\bConditions:\\s*(\\[.*?\\]|\\w+)");

    // FHItem Patterns
    private static final Pattern GOLD_COST_PATTERN = Pattern.compile("\\bGoldCost:\\s*\\d+");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("\\bQuantity:\\s*\\d+");

    RulesetSaver(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;
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
            String originalContentString,   // content as string
            int originalTotalSize,          // original total file size
            Map<String, PlayerCharacter> characters) { // and a map containing all characters with updated data
           
        try {
            System.out.println("Starting save process..."); // debug

            // First we reconstruct the content string with updated values from ActiveSessionData.
            // This now handles Filler Bank (padding/trimming) internally to preserve offsets.
            String newContentString = reconstructContentWithUpdates(originalContentString, characters);

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
            if (filePath.getFileName().toString().equals(BASE_RULESET)){
                Files.write(filePath, finalFileBytes);
        
            } else if (filePath.getFileName().toString().equals("ORIGINAL_BACKUP.ruleset")){
                // If we are editing the original backup, we DO NOT want to overwrite it.
                // We only want to apply the changes to the active game file (Base.ruleset).
                Files.write(filePath.resolveSibling(BASE_RULESET), finalFileBytes);
                
            } else {
                Files.write(filePath, finalFileBytes);
                Files.copy(filePath, filePath.resolveSibling(BASE_RULESET), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("File saved successfully!"); // debug

        } catch (IOException e) {
            e.printStackTrace();
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
    private String reconstructContentWithUpdates(String originalContentString,
            Map<String, PlayerCharacter> characters) {
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

            // Check if this block is an object that we need to update
            if (currentBlock.trim().startsWith("Character")) {

                // Extract name to find the matching object in our ActiveSessionData
                String name = extractNameFromBlock(currentBlock);

                // Fetch the corresponding PlayerCharacter from the map
                PlayerCharacter pc = characters.get(name);

                if (pc != null) {

                    // replace relevant fields in the block with updated values
                    currentBlock = updateCharacterBlockString(currentBlock, pc);
                }
            }
            if (currentBlock.trim().startsWith("GameMode")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("UnlockedClasses: [");
                List<String> unlockedCharacters = activeSessionData.getUnlockedCharacterList();
                for (int index = 0; index < unlockedCharacters.size(); index++) {
                    stringBuilder.append(" ").append(unlockedCharacters.get(index).trim());
                    if (index < unlockedCharacters.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.append("]");
                currentBlock = UNLOCKED_CHARACTER_PATTERN.matcher(currentBlock).replaceAll(stringBuilder.toString());
            }
            if (currentBlock.trim().startsWith("ItemCard")){
                String stringId = extractStringIdFromBlock(currentBlock);
                if (stringId != null) {
                    Item item = activeSessionData.getItems().get(stringId);
                    if (item != null) {
                        currentBlock = updateItemBlockString(currentBlock, item);
                    }
                }
            }
            if (currentBlock.trim().startsWith("FHItem")) {
                String scenarioItemId = extractScenarioItemIdFromBlock(currentBlock);
                if (scenarioItemId != null) {
                    Item item = activeSessionData.getItems().values().stream()
                            .filter(itm -> scenarioItemId.equals(itm.getId()))
                            .findFirst()
                            .orElse(null);
                    
                    if (item != null) {
                        currentBlock = updateFHItemBlockString(currentBlock, item);
                    }
                }
            }

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

    /* ============================================================================================ */
    /*                                  UPDATE CHARACTER BLOCK STRING                               */
    /*                                                                                              */
    /* Here we use Regex to replace specific values inside a text block without touching the        */
    /* rest of the structure. This one is fairly straight forward.                                  */
    /*                                                                                              */
    /* 1. We replace the card count (NumberAbilityCardsInBattle).                                   */
    /* 2. We reconstruct the entire HealthTable array string and replace the old one.               */
    /* 3. We return the updated block.                                                              */
    /*                                                                                              */
    /* ============================================================================================ */
    private String updateCharacterBlockString(String block, PlayerCharacter pc) {

        // First we update Card Amount
        // Regex looks for "NumberAbilityCardsInBattle:" followed by any whitespace and digits
        if (pc.getCardAmount() != null) {
            block = CARD_AMOUNT_PATTERN.matcher(block).replaceAll("NumberAbilityCardsInBattle: " + pc.getCardAmount());
        }

        // Then we Update Health Table with the new values
        String newHealthTable = "[" + pc.getHpLvlOne() + ", " + pc.getHpLvlTwo() + ", " + pc.getHpLvlThree() + ", " +
                pc.getHpLvlFour() + ", " + pc.getHpLvlFive() + ", " + pc.getHpLvlSix() + ", " +
                pc.getHpLvlSeven() + ", " + pc.getHpLvlEight() + ", " + pc.getHpLvlNine() + "]";

        // Regex looks for "HealthTable:" followed by whitespace and anything inside brackets [...]
        block = HEALTH_TABLE_PATTERN.matcher(block).replaceAll("HealthTable: " + newHealthTable);

        return block;
    }

    /* ============================================================================== */
    /* This method handles updating item blocks in the ruleset file. It replaces      */
    /* specific attributes of an item with new values from the provided Item object.  */
    /* The method takes the current block of text representing an item and the Item   */
    /* object containing updated values. It uses regex patterns to find and replace   */
    /* each attribute in the block with the new values.                               */
    /* ============================================================================== */

    private String updateItemBlockString(String block, Item item) {

        if (item.getTotalInGame() != null)
            block = TOTAL_IN_GAME_PATTERN.matcher(block).replaceAll("TotalInGame: " + item.getTotalInGame());
        if (item.getCost() != null)
            block = COST_PATTERN.matcher(block).replaceAll("Cost: " + item.getCost());
        if (item.getUsage() != null)
            block = USAGE_PATTERN.matcher(block).replaceAll("Usage: " + item.getUsage());
        if (item.getProsperityRequirement() != null)
            block = PROSPERITY_PATTERN.matcher(block)
                    .replaceAll("ProsperityRequirement: " + item.getProsperityRequirement());
        if (item.getConsumes() != null)
            block = CONSUMES_PATTERN.matcher(block).replaceAll("Consumes: " + item.getConsumes());
        if (item.getInfuses() != null)
            block = INFUSE_PATTERN.matcher(block).replaceAll("Infuse: " + item.getInfuses());
        
        // Complex attributes that might be nested (e.g. Move: 3 OR Move: \n Amount: 3)
        if (item.getHeal() != null)
            block = updateAttribute(block, "Heal", item.getHeal());
        if (item.getAttack() != null)
            block = updateAttribute(block, "Attack", item.getAttack());
        if (item.getRange() != null)
            block = updateAttribute(block, "Range", item.getRange());
        if (item.getTarget() != null)
            block = updateAttribute(block, "Target", item.getTarget());
        if (item.getShield() != null)
            block = updateAttribute(block, "Shield", item.getShield());
        if (item.getShieldValue() != null)
            block = updateAttribute(block, "ShieldValue", item.getShieldValue());
        if (item.getRetaliate() != null)
            block = updateAttribute(block, "Retaliate", item.getRetaliate());
        if (item.getMove() != null)
            block = updateAttribute(block, "Move", item.getMove());
        if (item.getOMove() != null)
            block = updateAttribute(block, "OMove", item.getOMove());
        if (item.getAMove() != null)
            block = updateAttribute(block, "AMove", item.getAMove());
        if (item.getPull() != null)
            block = updateAttribute(block, "Pull", item.getPull());
        if (item.getPush() != null)
            block = updateAttribute(block, "Push", item.getPush());
        if (item.getJump() != null)
            block = JUMP_PATTERN.matcher(block).replaceAll("Jump: " + item.getJump());

        if (item.getConditions() != null) {
             block = CONDITIONS_PATTERN.matcher(block).replaceAll("Conditions: " + item.getConditions());
        }

        return block;
    }

    /* ============================================================================================ */
    /*                                      UPDATE ATTRIBUTE HELPER                                 */
    /*                                                                                              */
    /* Handles updating attributes that can appear in multiple formats:                             */
    /* 1. Key: Value                                                                                */
    /* 2. Key: \n Amount: Value                                                                     */
    /* 3. Key: \n Strength: Value                                                                   */
    /* ============================================================================================ */
    private String updateAttribute(String block, String key, String value) {
        // Matches "Key: Value" OR "Key:\n...Amount: Value" OR "Key:\n...Strength: Value"
        // Handles variable whitespace and newlines.
        Pattern pattern = Pattern.compile("\\b" + key + ":\\s*(?:(?:\\r?\\n\\s*)+(?:Amount|Strength):\\s*)?(-?\\d+)");
        Matcher matcher = pattern.matcher(block);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String oldValue = matcher.group(1);
            
            // Replace the last occurrence of oldValue in fullMatch with newValue
            // This preserves the prefix (Key: \n Amount: )
            int lastIndex = fullMatch.lastIndexOf(oldValue);
            if (lastIndex != -1) {
                String newMatch = fullMatch.substring(0, lastIndex) + value + fullMatch.substring(lastIndex + oldValue.length());
                matcher.appendReplacement(sb, Matcher.quoteReplacement(newMatch));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
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
    /* update I will add the feature to add starting characters, and this must be addressed at      */
    /* that point, since those blocks have far too few whitespaces to trim.                         */
    /* ============================================================================================ */
    private String adjustBlockSize(String currentBlock, int originalLength) {

        // checks current length against original length
        int currentLength = currentBlock.length();
        int diff = originalLength - currentLength;

        if (diff > 0) {
            // New block is shorter. Pad with spaces.
            return padBlock(currentBlock, diff);
        } else if (diff < 0) {
            // New block is longer. We must trim spaces.
            return trimBlock(currentBlock, Math.abs(diff));
        }
        
        // Exact match, no adjustment needed
        return currentBlock;
    }

    // Helper: Adds spaces to the end of the block to match the original size
    private String padBlock(String block, int amount) {
        StringBuilder padded = new StringBuilder(block);
        for (int k = 0; k < amount; k++) {
            padded.append(" ");
        }
        return padded.toString();
    }

    /*                                   COOL TRICK HERE 
    /* ============================================================================================ */
    /*          Removes unnecessary whitespace to shrink the block to the original size             */
    /*                                                                                              */
    /* This part was actually pretty cool to figure out. When looking at the HEX-code for the       */
    /* ruleset file, I noticed that all the linebreaks ended with both a carriage return (0x0D)     */
    /* and a newline (0x0A). But you don't actually need both in a text file.                       */
    /* A simple newline is enough.                                                                  */
    /* So by removing carriage returns, we can save a lot of space. Every single line has at least  */
    /* one extra byte that we can steal. This discovery was a major breakthrough when I was trying  */
    /* to figure out the safest way to edit the file.                                               */
    /* Without this discovery, any change that increased the number of digits (like hp 6 -> 10),    */
    /* would mean I HAD to find spaces to take. And I can't just steal spaces from indentations and */
    /* many other places, since it would corrupt the file. I would NOT have been able to create     */
    /* such an advanced version of this application without this technique.                         */
    /* ============================================================================================ */
    
    private String trimBlock(String block, int amount) {

        // We need to remove 'amount' characters from the block
        int charsToRemove = amount;

        // Strategy 1: Remove spaces before newlines
        // Safest: Removes invisible "trailing whitespace" that serves no purpose.
        while (charsToRemove > 0 && (block.contains(" \n") || block.contains(" \r\n"))) {
            if (block.contains(" \n")) {
                block = block.replaceFirst(" \n", "\n");
            } else {
                block = block.replaceFirst(" \r\n", "\r\n");
            }
            charsToRemove--;
        }

        // Strategy 2: Remove carriage returns
        while (charsToRemove > 0 && block.contains("\r\n")) {
            block = block.replaceFirst("\r\n", "\n");
            charsToRemove--;
        }

        // Strategy 3: Remove double spaces
        // Least Safe: Affects indentation and visual formatting. Last resort.
        while (charsToRemove > 0 && block.contains("  ")) {
            block = block.replaceFirst("  ", " ");
            charsToRemove--;
        }

        // If we still haven't removed enough characters, we can't save safely.
        if (charsToRemove > 0) {
            throw new RuntimeException(
                    "Error: Could not trim enough bytes from block to match original size. Overflow by "
                            + charsToRemove + " bytes.");
        }

        return block;
    }

    
    /* ============================================================================================ */
    /* EXTRACT NAME FROM BLOCK                                                                      */
    /*                                                                                              */
    /* A simple helper method to quickly fish out the "ID:" value from a raw text block.            */
    /* We need this to know which PlayerCharacter in our ActiveSessionData matches this             */
    /* specific block.                                                                              */
    /* ============================================================================================ */
    private String extractNameFromBlock(String block) {
        String[] lines = block.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("ID:")) {
                return line.replace("ID:", "").trim();
            }
        }
        return null;
    }

    /* ============================================================================================ */
    /* EXTRACT STRING ID FROM BLOCK                                                                 */
    /*                                                                                              */
    /* A simple helper method to quickly fish out the "StringID:" value from a raw text block.      */
    /* We need this to know which Item in our ActiveSessionData matches this specific block.        */
    /* ============================================================================================ */
    private String extractStringIdFromBlock(String block) {
        String[] lines = block.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("StringID:")) {
                return line.replace("StringID:", "").trim();
            }
        }
        return null;
    }

    /* ============================================================================================ */
    /* EXTRACT SCENARIO ITEM ID FROM BLOCK                                                          */
    /*                                                                                              */
    /* A simple helper method to quickly fish out the "ScenarioItemID:" value from a raw text block.*/
    /* We need this to know which Item in our ActiveSessionData matches this specific FHItem block. */
    /* ============================================================================================ */
    private String extractScenarioItemIdFromBlock(String block) {
        String[] lines = block.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("ScenarioItemID:")) {
                return line.replace("ScenarioItemID:", "").trim();
            }
        }
        return null;
    }

    /* ============================================================================== */
    /* This method handles updating FHItem blocks in the ruleset file.                */
    /* It replaces GoldCost and Quantity with values from the provided Item object.   */
    /* ============================================================================== */
    private String updateFHItemBlockString(String block, Item item) {
        if (item.getCost() != null) {
            block = GOLD_COST_PATTERN.matcher(block).replaceAll("GoldCost: " + item.getCost());
        }
        if (item.getTotalInGame() != null) {
            block = QUANTITY_PATTERN.matcher(block).replaceAll("Quantity: " + item.getTotalInGame());
        }
        return block;
    }
}
