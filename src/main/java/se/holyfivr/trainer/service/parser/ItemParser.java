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
        String lastAttribute = "";
        
        for(String line : lines){
        
            String[] itemInfo = line.split(":", 2); 

            if (itemInfo.length < 1) { 
                continue; 
            }
             
            String attribute = itemInfo[0].trim();
            String value = (itemInfo.length > 1) ? itemInfo[1].trim() : "";

            if (attribute.equals("Amount") || attribute.equals("Strength")) {
                if (!lastAttribute.isEmpty()) {
                    attribute = lastAttribute;
                }
            } else if (value.isEmpty()) {
                lastAttribute = attribute;
                continue;
            } else {
                lastAttribute = attribute;
            }


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
                case "ShieldValue"          -> item.setShieldValue              (value);
                case "Retaliate"            -> item.setRetaliate                (value);
                case "Move"                 -> item.setMove                     (value);
                case "OMove"                -> item.setOMove                    (value);
                case "AMove"                -> item.setAMove                    (value);
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
