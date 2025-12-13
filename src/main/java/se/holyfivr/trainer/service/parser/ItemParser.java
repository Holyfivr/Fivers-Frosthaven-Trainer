package se.holyfivr.trainer.service.parser;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.service.ActiveSessionData;

@Component
public class ItemParser {

    private final ActiveSessionData activeSessionData;

    public ItemParser(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;
    }
    public void parseItemBlock(String currentBlock) {

        Item item = new Item();
        String[] lines = currentBlock.split("\n");
        
        for(String line : lines){
        
            String[] itemInfo = line.split(":"); // separates the attribute from the value

            if (itemInfo.length < 2) {
                continue; // skip lines that don't have a value (prevents ArrayIndexOutOfBoundsException)
            }
             
            String attribute = itemInfo[0].trim();
            String value = itemInfo[1].trim();

            switch (attribute){
                case "StringID"             -> item.setStringId                 (value);
                case "ID"                   -> item.setId                       (value);
                case "Cost"                 -> item.setCost                     (value);
                case "TotalInGame"          -> item.setTotalInGame              (value);
                case "Slot"                 -> item.setSlot                     (value);
                case "Rarity"               -> item.setRarity                   (value);
                case "Usage"                -> item.setUsage                    (value);
                case "ProsperityRequirement"-> item.setProsperityRequirement    (value);
                case "Consumes"             -> item.setConsumes                 (value);
                case "Heal"                 -> item.setHeal                     (value);
                case "Attack"               -> item.setAttack                   (value);
                case "Range"                -> item.setRange                    (value);
                case "Target"               -> item.setTarget                   (value);
                case "Shield"               -> item.setShield                   (value);
                case "Retaliate"            -> item.setRetaliate                (value);
                case "Move"                 -> item.setMove                     (value);
                case "Pull"                 -> item.setPull                     (value);
                case "Push"                 -> item.setPush                     (value);
                case "Jump"                 -> item.setJump                     (value);
                case "Conditions"           -> item.setConditions               (value);
                default                     -> {/* ignore unknown attributes */}
            }
        }

        // Store the parsed item in the active session data
        activeSessionData.addItem(item);

        System.out.printf(" Slot: %s --- StringId: %s %n", item.getSlot(), item.getStringId()); // debug print


    }

}
