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
             
            String attribute = itemInfo[0].toLowerCase().trim();
            String value = itemInfo[1].trim();

            switch (attribute){
                case "stringid"             -> item.setStringId                 (value);
                case "totalingame"          -> item.setTotalInGame              (value);
                case "slot"                 -> item.setSlot                     (value);
                case "rarity"               -> item.setRarity                   (value);
                case "usage"                -> item.setUsage                    (value);
                case "prosperityrequirement"-> item.setProsperityRequirement    (value);
                case "consumes"             -> item.setConsumes                 (value);
                case "heal"                 -> item.setHeal                     (value);
                case "attack"               -> item.setAttack                   (value);
                case "range"                -> item.setRange                    (value);
                case "target"               -> item.setTarget                   (value);
                case "shield"               -> item.setShield                   (value);
                case "retaliate"            -> item.setRetaliate                (value);
                case "move"                 -> item.setMove                     (value);
                case "pull"                 -> item.setPull                     (value);
                case "push"                 -> item.setPush                     (value);
                case "jump"                 -> item.setJump                     (value);
                default                     -> {/* ignore unknown attributes */}
            }
        }

        // Store the parsed item in the active session data
        activeSessionData.addItem(item);

        System.out.printf(" Slot: %s --- StringId: %s %n", item.getSlot(), item.getStringId()); // debug print


    }

}
