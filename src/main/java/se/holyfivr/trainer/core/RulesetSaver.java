package se.holyfivr.trainer.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import se.holyfivr.trainer.model.enums.DiscardEnum;
import se.holyfivr.trainer.model.enums.RulesetFileName;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.utils.AbilityStat;
import se.holyfivr.trainer.core.utils.SaveUtils;
import se.holyfivr.trainer.core.utils.Validator;
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

    private final Validator validator;

    private final ActiveSessionData activeSessionData;
    private final SaveUtils saveUtils;
    

    // Regex patterns for finding character attributes
    private static final Pattern CARD_AMOUNT_PATTERN = Pattern.compile("\\bNumberAbilityCardsInBattle:\\s*\\d+");
    private static final Pattern HEALTH_TABLE_PATTERN = Pattern.compile("\\bHealthTable:\\s*\\[.*?\\]");
    private static final Pattern UNLOCKED_CHARACTER_PATTERN = Pattern.compile("\\bUnlockedClasses:\\s*\\[.*?\\]");

    // Item Patterns
    private static final Pattern TOTAL_IN_GAME_PATTERN = Pattern.compile("\\bTotalInGame:\\s*\\d+");
    private static final Pattern COST_PATTERN = Pattern.compile("\\bCost:\\s*\\d+");
    private static final Pattern USAGE_PATTERN = Pattern.compile("\\bUsage:\\s*\\w+");
    private static final Pattern PROSPERITY_PATTERN = Pattern.compile("\\bProsperityRequirement:\\s*\\d+");
    private static final Pattern CONSUMES_PATTERN = Pattern.compile("(?mi)^Consumes:\\s*(?!Any\\b)(\\[[A-Za-z0-9_]+\\]|[A-Za-z0-9_]+)\\s*$");
    private static final Pattern INFUSE_PATTERN = Pattern.compile("(?mi)^Infuse:\\s*(?!Any\\b)(\\[[A-Za-z0-9_]+\\]|[A-Za-z0-9_]+)\\s*$");
    
    // Item Data Patterns
    private static final Pattern JUMP_PATTERN = Pattern.compile("\\bJump:\\s*(True|False)");
    private static final Pattern CONDITIONS_PATTERN = Pattern.compile("\\bConditions:\\s*(\\[.*?\\]|\\w+)");

    // FHItem Patterns
    private static final Pattern GOLD_COST_PATTERN = Pattern.compile("\\bGoldCost:\\s*\\d+");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("\\bQuantity:\\s*\\d+");

    // Abilitycard Patterns




        /**
         * Constructs the saver with its collaborators.
         *
         * @param activeSessionData in-memory state used for updated values
         * @param saveUtils helper utilities for block-level operations
         * @param validator validation and sanitation logic
         */
        RulesetSaver(ActiveSessionData activeSessionData, SaveUtils saveUtils, Validator validator) {
            this.activeSessionData = activeSessionData;
            this.saveUtils = saveUtils;
            this.validator = validator;
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
                String name = saveUtils.extractIdentifier(currentBlock, "ID:");

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
                String stringId = saveUtils.extractIdentifier(currentBlock, "StringID:");
                if (stringId != null) {
                    Item item = activeSessionData.getItems().get(stringId);
                    if (item != null) {
                        currentBlock = updateItemBlockString(currentBlock, item);
                    }
                }
            }
            if (currentBlock.trim().startsWith("FHItem")) {
                String scenarioItemId = saveUtils.extractIdentifier(currentBlock, "ScenarioItemID:");
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
             if (currentBlock.trim().startsWith("AbilityCard")){
                String cardId = saveUtils.extractIdentifier(currentBlock, "Name:");
                if (cardId != null) {
                    AbilityCard abilityCard = activeSessionData.getAbilityCards().get(cardId);
                    if (abilityCard != null) {
                        currentBlock = updateAbilityCardBlockString(currentBlock, abilityCard);
                    }
                }
            }

            currentBlock = validator.stripDoubleHashComments(currentBlock);
 
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

        // Target can be negative in the ruleset for special semantics.
        // We normalize here to avoid writing invalid negatives back into the file.
        try {
            int parseTarget = Integer.parseInt(item.getTarget());
            if (parseTarget < 0){
                item.setTarget(null);
            }
        }
        catch (NumberFormatException e) {
            System.err.println("Error parsing target: " + e.getMessage()); 
        }


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
        if (item.getInfuse() != null)
            block = INFUSE_PATTERN.matcher(block).replaceAll("Infuse: " + item.getInfuse());
        
        // Complex attributes that might be nested (e.g. Move: 3 OR Move: \n Amount: 3)
        // These use a nested "Amount" or "Strength" key, so we need regex-based replacement.
        if (validator.isValidInteger(item.getHeal()))        block = updateAttribute(block, "Heal", item.getHeal());
        if (validator.isValidInteger(item.getAttack()))      block = updateAttribute(block, "Attack", item.getAttack());
        if (validator.isValidInteger(item.getRange()))       block = updateAttribute(block, "Range", item.getRange());
        if (validator.isValidInteger(item.getTarget()))      block = updateAttribute(block, "Target", item.getTarget());
        if (validator.isValidInteger(item.getShield()))      block = updateAttribute(block, "Shield", item.getShield());
        if (validator.isValidInteger(item.getShieldValue())) block = updateAttribute(block, "ShieldValue", item.getShieldValue());
        if (validator.isValidInteger(item.getRetaliate()))   block = updateAttribute(block, "Retaliate", item.getRetaliate());
        if (validator.isValidInteger(item.getMove()))        block = updateAttribute(block, "Move", item.getMove());
        if (validator.isValidInteger(item.getOMove()))       block = updateAttribute(block, "OMove", item.getOMove());
        if (validator.isValidInteger(item.getAMove()))       block = updateAttribute(block, "AMove", item.getAMove());
        if (validator.isValidInteger(item.getPull()))        block = updateAttribute(block, "Pull", item.getPull());
        if (validator.isValidInteger(item.getPush()))        block = updateAttribute(block, "Push", item.getPush());
        // Jump is stored as a boolean flag (True/False)
        if (item.getJump() != null)
            block = JUMP_PATTERN.matcher(block).replaceAll("Jump: " + item.getJump());

        // Conditions may be a list or a single token string
        if (item.getConditions() != null) {
             block = CONDITIONS_PATTERN.matcher(block).replaceAll("Conditions: " + item.getConditions());
        }

        return block;
    }

    /**
     * Updates an AbilityCard block using the model values from ActiveSessionData.
     *
     * This method is intentionally explicit so it is easy to see which stats
     * are written back and in what order.
     */
    private String updateAbilityCardBlockString(String block, AbilityCard abilityCard) {
        DiscardEnum discard = abilityCard.getDiscard();
        System.err.println("Discard before nullchecks in updateabilitycardblockstring: " + (discard == null ? "null" : discard));

        // Top-level fields stored as single lines (Initiative/Discard).
        if (abilityCard.getInitiative() != null) {
            block = saveUtils.updateTopLevelLine(block, "Initiative", abilityCard.getInitiative());
        }
        if (discard != null && discard != DiscardEnum.NONE) {
            System.err.println("Updating Discard to: " + discard.getValue());
            block = saveUtils.updateTopLevelLine(block, "Discard", discard.getValue());
        }

        // Numeric stats that may appear multiple times inside the same block.
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.ATTACK, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.DAMAGE, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.HEAL, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.MOVE, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.RANGE, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.SHIELD, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.XP, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.TARGET, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.PULL, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.PUSH, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.RETALIATE, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.LOOT, this::updateAttributeList, this::updateAttribute);
        block = saveUtils.updateAbilityCardStat(block, abilityCard, AbilityStat.PIERCE, this::updateAttributeList, this::updateAttribute);

        // Jump is a boolean flag stored as True/False.
        if (abilityCard.getJump() != null) {
            block = saveUtils.updateBooleanAttribute(block, "Jump", abilityCard.getJump());
        }
        return block;
    }


    /* ============================================================================================ */
    /*                                      UPDATE ATTRIBUTE HELPER                                 */
    /*                                                                                              */
    /* Handles updating attributes appear as a list of values (e.g. Attack: 2, Attack: 3).          */
    /* These are used by the ability cards to allow editing specific attributes, and not just       */
    /* blanket edit every single attribute of a specific kind.                                      */
    /* Previously, updating "attack" on a card, would change all attack values on the card. This    */
    /* allows us to change them individually.                                                       */
    /* ============================================================================================ */

    /**
     * Updates a repeated stat inside a block (e.g., multiple Attack entries).
     *
     * This preserves surrounding formatting and only replaces the numeric values
     * in the order they appear.
     */
    private String updateAttributeList(String block, String attributeKey, List<String> values) {
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(attributeKey)
            + ":[ \\t]*(?:(?:\\r?\\n[ \\t]*)+(?:Amount|Strength):[ \\t]*)?([+-]?\\d+)");
        Matcher matcher = pattern.matcher(block);

        int i = 0;
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String oldValue = matcher.group(1);

            // If we run out of values, leave remaining occurrences unchanged.
            // If we run out of values, keep remaining occurrences unchanged.
            if (values == null || i >= values.size()) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(fullMatch));
                continue;
            }
            String replacementValue = values.get(i++);

            // If either the existing value or the incoming value is negative, leave this occurrence unchanged.
            if (oldValue.startsWith("-") || !validator.isValidInteger(replacementValue)) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(fullMatch));
                continue;
            }

            // Preserve "+" prefix if the original value had it.
            if (oldValue.startsWith("+") && !replacementValue.startsWith("-") && !replacementValue.startsWith("+")) {
                replacementValue = "+" + replacementValue;
            }

            int lastIndex = fullMatch.lastIndexOf(oldValue);
            String newMatch = fullMatch.substring(0, lastIndex)
                    + replacementValue
                    + fullMatch.substring(lastIndex + oldValue.length());

            matcher.appendReplacement(sb, Matcher.quoteReplacement(newMatch));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /* ============================================================================================ */
    /*                                      UPDATE ATTRIBUTE HELPER                                 */
    /*                                                                                              */
    /* Handles updating attributes that need special logic due to possible nested structure.        */
    /* For example, there are many cases that looks like this:                                      */
    /* Attack:  3                                                                                   */ 
    /*     Amount: X                                                                                */
    /* Since we are checking for the "Attack:" identifier, these won't be properly updated when we  */
    /* try to rewrite them into the ruleset. There is similiar logic in the parsers to handle these */
    /* ============================================================================================ */
    /**
     * Updates a single stat occurrence that may be nested beneath Amount/Strength.
     *
     * The regex handles both flat "Key: Value" and nested
     * "Key:\n  Amount: Value" shapes found in the ruleset.
     */
    private String updateAttribute(String block, String attributeKey, String value) {
        // Matches "Key: Value" OR "Key:\n...Amount: Value" OR "Key:\n...Strength: Value"
        // Handles variable whitespace and newlines.
        Pattern pattern = Pattern.compile("\\b" + attributeKey + ":[ \\t]*(?:(?:\\r?\\n[ \\t]*)+(?:Amount|Strength):[ \\t]*)?([+-]?\\d+)");
        Matcher matcher = pattern.matcher(block);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String oldValue = matcher.group(1);
            

            // There are cases in the ruleset where attributes start with +.
            // Preserve that prefix to avoid changing the meaning of the value.
            String replacementValue = value;

            // If either the existing value or the incoming value is negative, leave this occurrence unchanged.
            if (oldValue.startsWith("-") || !validator.isValidInteger(replacementValue)) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(fullMatch));
                continue;
            }
            if (oldValue.startsWith("+") && !replacementValue.startsWith("-") && !replacementValue.startsWith("+")) {
                replacementValue = "+" + replacementValue;
            }
            
            // Replace the last occurrence of oldValue in fullMatch with newValue
            // This preserves the prefix (Key: \n Amount: )
            int lastIndex = fullMatch.lastIndexOf(oldValue);
            if (lastIndex != -1) {
                String newMatch = fullMatch.substring(0, lastIndex) 
                + replacementValue 
                + fullMatch.substring(lastIndex + oldValue.length());
                
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
        System.out.println(currentBlock + " | Original Length: " + originalLength); // debug

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
     * Trims whitespace in a prioritized order to reduce block length.
     *
     * This method intentionally removes the least risky whitespace first
     * to preserve formatting and semantic meaning.
     */
    private String trimBlock(String block, int amount) {

        // We need to remove 'amount' characters from the block
        int charsToRemove = amount;

        // Strategy 1: Remove spaces before newlines
        // Safest: Removes invisible "trailing whitespace" that serves no purpose.
        // This strategy, and strategy 3 look very similar, but there is one small difference.
        // This one only removes spaces on lines that do not have a colon.
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

        // Strategy 3: Remove any double spaces that exist after a : on a lina
        while (charsToRemove > 0 &&
                block.matches("(?m).*:[^\\n]* {2,}.*")) {

            block = block.replaceFirst(
                    "(?m)(:[^\\n]*?) {2,}",
                    "$1 ");
            charsToRemove--;
        }

        // Strategy 4: Remove double spaces anywhere
        // Least Safe: Affects indentation and visual formatting. Last resort.
        while (charsToRemove > 0 && block.contains("  ")) {
            block = block.replaceFirst("  ", " ");
            charsToRemove--;
        }



        // If we still haven't removed enough characters, we can't save safely.
        // This will throw an exception and abort the saving process.
        if (charsToRemove > 0) {
            throw new RuntimeException(
                    "Error: Could not trim enough bytes from block to match original size. Overflow by "
                            + charsToRemove + " bytes.");
        }

        return block;
    }

    // Identifier extraction is handled by SaveUtils to keep this class focused
    // on save flow and block sizing.
    
    /* ============================================================================== */
    /* This method handles updating FHItem blocks in the ruleset file.                */
    /* It replaces GoldCost and Quantity with values from the provided Item object.   */
    /* ============================================================================== */
    /**
     * Updates FHItem blocks that mirror item cost and quantity in scenario items.
     */
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
