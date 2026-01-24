package se.holyfivr.trainer.core.utils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.model.enums.CardAttribute;
import se.holyfivr.trainer.model.enums.DiscardEnum;
import se.holyfivr.trainer.model.enums.ItemAttribute;

@Service
public class BlockUpdater {


    private final ActiveSessionData activeSessionData;
    private final SaveUtils saveUtils;
    


    public BlockUpdater(ActiveSessionData activeSessionData, SaveUtils saveUtils) {
        this.activeSessionData = activeSessionData;
        this.saveUtils = saveUtils;        
      
    }


    
    public String updateAttribute(String block, String key, List<String> values, String singleValue) {
        
        Pattern attributePattern = Pattern.compile("\\b" + Pattern.quote(key) + PatternRepository.VALID_ATTRIBUTE_PATTERN);
        Matcher attributeMatcher = attributePattern.matcher(block);
        // If we have a list of values, we process them one by one
        if (values != null && !values.isEmpty()) {

            StringBuffer updatedBlock = new StringBuffer();
            int index = 0;

            while (attributeMatcher.find()) {
                String fullMatch = attributeMatcher.group(0);
                String oldValue = attributeMatcher.group(1);

                if (index >= values.size()) {
                    attributeMatcher.appendReplacement(updatedBlock, Matcher.quoteReplacement(fullMatch));
                    continue;
                }

                String replacementValue = values.get(index++);
                if (!saveUtils.isValidInteger(replacementValue)) {
                    attributeMatcher.appendReplacement(updatedBlock, Matcher.quoteReplacement(fullMatch));
                    continue;
                }

                if (oldValue.startsWith("+") && !replacementValue.startsWith("+") && !replacementValue.startsWith("-")) {
                    replacementValue = "+" + replacementValue;
                }

                String updated = fullMatch.replaceFirst(Pattern.quote(oldValue), replacementValue);
                attributeMatcher.appendReplacement(updatedBlock, Matcher.quoteReplacement(updated));
            }

            attributeMatcher.appendTail(updatedBlock);
            return updatedBlock.toString();
        }

        // otherwise we use the single value for all occurrences
        if (!saveUtils.isValidInteger(singleValue)) {
            return block;
        }

        StringBuffer updatedBlock = new StringBuffer();
        while (attributeMatcher.find()) {
            String fullMatch = attributeMatcher.group(0);
            String oldValue = attributeMatcher.group(1);

            String replacementValue = singleValue;
            if (oldValue.startsWith("+") && !replacementValue.startsWith("+") && !replacementValue.startsWith("-")) {
                replacementValue = "+" + replacementValue;
            }

            String updated = fullMatch.replaceFirst(Pattern.quote(oldValue), replacementValue);
            attributeMatcher.appendReplacement(updatedBlock, Matcher.quoteReplacement(updated));
        }

        attributeMatcher.appendTail(updatedBlock);
        return updatedBlock.toString();
    }


    /* Updates non-numerical updates (discard, true/false, etc) */
    private String updateAttribute(String block, String key, String value) {
        if (value == null) {
            return block;
        }

        // takes the key and finds the value after the colon
        Pattern attributePattern = Pattern.compile("\\b" + Pattern.quote(key) + "\\s*:\\s*([^\\r\\n]*)");
        // Create a matcher for the block
        Matcher attributeMatcher = attributePattern.matcher(block);

        // Use a StringBuffer to build the updated block
        StringBuffer updatedBlock = new StringBuffer();
        // Iterate through all matches and replace the old value with the new value
        while (attributeMatcher.find()) {
            String fullMatch = attributeMatcher.group(0);
            String oldValue = attributeMatcher.group(1);

            // Find the last occurrence of the old value in the full match
            int lastIndex = fullMatch.lastIndexOf(oldValue);
            // Replace the old value with the new value at the last occurrence
            if (lastIndex != -1) {
                String updated = fullMatch.substring(0, lastIndex)
                        + value
                        + fullMatch.substring(lastIndex + oldValue.length());
                attributeMatcher.appendReplacement(updatedBlock, Matcher.quoteReplacement(updated));
            }
        }

        // Append the remaining part of the block
        attributeMatcher.appendTail(updatedBlock);
        return updatedBlock.toString();
    } 



    public String updateBlock(String currentBlock, AbilityCard card, Item item, Map<String, AbilityCard> cardMap) {
        
        
        
        if (card != null) {
            currentBlock = updateAttribute(currentBlock, CardAttribute.HEAL           .get(),  card.getHealValues(),      null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.ATTACK         .get(),  card.getAttackValues(),    null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.RANGE          .get(),  card.getRangeValues(),     null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.TARGET         .get(),  card.getTargetValues(),    null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.SHIELD         .get(),  card.getShieldValues(),    null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.RETALIATE      .get(),  card.getRetaliateValues(), null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.MOVE           .get(),  card.getMoveValues(),      null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.PULL           .get(),  card.getPullValues(),      null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.PUSH           .get(),  card.getPushValues(),      null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.DAMAGE         .get(),  card.getDamageValues(),    null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.PIERCE         .get(),  card.getPierceValues(),    null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.XP             .get(),  card.getXpValues(),        null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.LOOT           .get(),  card.getLootValues(),      null);
            currentBlock = updateAttribute(currentBlock, CardAttribute.INITIATIVE     .get(),  null,           card.getInitiative());
            currentBlock = updateAttribute(currentBlock, CardAttribute.DISCARD        .get(),  card.getDiscard());
        }

        if (item != null) {
            currentBlock = updateAttribute(currentBlock, ItemAttribute.HEAL           .get(), null, item.getHeal());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.ATTACK         .get(), null, item.getAttack());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.RANGE          .get(), null, item.getRange());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.TARGET         .get(), null, item.getTarget());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.SHIELD         .get(), null, item.getShield());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.RETALIATE      .get(), null, item.getRetaliate());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.MOVE           .get(), null, item.getMove());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.PULL           .get(), null, item.getPull());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.PUSH           .get(), null, item.getPush());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.JUMP           .get(), null, item.getJump());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.OMOVE          .get(), null, item.getOMove());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.AMOVE          .get(), null, item.getAMove());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.SHIELD_VALUE   .get(), null, item.getShieldValue());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.PROSPERITY_REQ .get(), null, item.getProsperityRequirement());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.ITEM_NAME      .get(), null, item.getItemName());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.COST           .get(), null, item.getCost()); 
            currentBlock = updateAttribute(currentBlock, ItemAttribute.USAGE          .get(), null, item.getUsage());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.RARITY         .get(), null, item.getRarity());
            currentBlock = updateAttribute(currentBlock, ItemAttribute.TOTAL_IN_GAME  .get(), null, item.getTotalInGame());
        }

        return currentBlock;
    }




    /* ============================================================================================ */
    /*                                  UPDATE CHARACTER BLOCK                                      */
    /*                                                                                              */
    /* Here we use Regex to replace specific values inside a text block without touching the        */
    /* rest of the structure. This one is fairly straight forward.                                  */
    /*                                                                                              */
    /* 1. We replace the card count (NumberAbilityCardsInBattle).                                   */
    /* 2. We reconstruct the entire HealthTable array string and replace the old one.               */
    /* 3. We return the updated block.                                                              */
    /*                                                                                              */
    /* ============================================================================================ */
    
    public String updateBlock(String currentBlock, PlayerCharacter character) {
        if (character == null) {
            return currentBlock;
        }

        // First we update Card Amount
        // Regex looks for "NumberAbilityCardsInBattle:" followed by any whitespace and digits
        if (character.getCardAmount() != null) {
            currentBlock = PatternRepository.CARD_AMOUNT_PATTERN.matcher(currentBlock).replaceAll("NumberAbilityCardsInBattle: " + character.getCardAmount());
        }

        // Then we Update Health Table with the new values
        String newHealthTable = 
            "[" 
            + character.getHpLvlOne()   + ", " 
            + character.getHpLvlTwo()   + ", " 
            + character.getHpLvlThree() + ", " 
            + character.getHpLvlFour()  + ", " 
            + character.getHpLvlFive()  + ", " 
            + character.getHpLvlSix()   + ", " 
            + character.getHpLvlSeven() + ", " 
            + character.getHpLvlEight() + ", " 
            + character.getHpLvlNine()  + 
            "]";

        // The regex looks for the entire HealthTable line and replaces it
        currentBlock = PatternRepository.HEALTH_TABLE_PATTERN.matcher(currentBlock).replaceAll("HealthTable: " + newHealthTable);

        return currentBlock;
                
    }

    /* =================================== UPDATE UNIQUE BLOCKS ========================================= */
    /*                                                                                                    */
    /* Here we handle unique block updates that don't fit the other characters, and instead save based    */
    /* on identifier strings.                                                                             */
    /*                                                                                                    */
    /* 1. Unlock Character Block: We reconstruct the UnlockedClasses array based on active session data.  */
    /* 2. FH Item Block: We update GoldCost and Quantity based on the parsed Item data.                   */
    /* 3. We return the updated block.                                                                    */
    /*                                                                                                    */
    /* ================================================================================================== */

    public String updateBlock(String currentBlock, String FHItem) {
        if (FHItem == null) {
            return currentBlock;
        }


        if (PatternRepository.FH_ITEM_PARSER_PATTERN.matcher(currentBlock).matches()) {

            for (Item item : activeSessionData.getItems().values()) {
                if (FHItem.equals(item.getId())) {
                    if (item.getCost() != null) {
                        currentBlock = PatternRepository.GOLD_COST_PATTERN.matcher(currentBlock).replaceAll("GoldCost: " + item.getCost());
                    }
                    if (item.getTotalInGame() != null) {
                        currentBlock = PatternRepository.QUANTITY_PATTERN.matcher(currentBlock).replaceAll("Quantity: " + item.getTotalInGame());
                    }
                }
            }
        }
        return currentBlock;
    }


    public String updateBlock(String currentBlock, List<String> unlockedClasses) {
        if (unlockedClasses == null || unlockedClasses.isEmpty()) {
            return currentBlock;
        }

        StringBuilder classArray = new StringBuilder();
        classArray.append("UnlockedClasses: [");
        for (int i = 0; i < unlockedClasses.size(); i++) {
            classArray.append(unlockedClasses.get(i).trim());
            if (i < unlockedClasses.size() - 1) {
                classArray.append(", ");
            }
        }
        classArray.append("]");

        currentBlock = PatternRepository.UNLOCKED_CHARACTER_PATTERN.matcher(currentBlock).replaceAll(classArray.toString());
        
        return currentBlock;
    }

    // Helper overload for DiscardEnum to keep the call-site cleaner
    private String updateAttribute(String block, String key, DiscardEnum discard) {
        String value = discard != null ? discard.getValue() : null;
        return updateAttribute(block, key, value);
    }

}
