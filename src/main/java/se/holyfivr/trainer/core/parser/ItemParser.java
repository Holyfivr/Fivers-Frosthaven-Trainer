package se.holyfivr.trainer.core.parser;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.enums.ItemAttribute;

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


            ItemAttribute attrEnum = ItemAttribute.fromString(attribute);
            if (attrEnum != null) {
                switch (attrEnum) {
                    case STRING_ID      -> item.setStringId                 (value);
                    case ID             -> item.setId                       (value);
                    case COST           -> item.setCost                     (value);
                    case TOTAL_IN_GAME  -> item.setTotalInGame              (value);
                    case SLOT           -> item.setSlot                     (value);
                    case RARITY         -> item.setRarity                   (value);
                    case USAGE          -> item.setUsage                    (value);
                    case PROSPERITY_REQ -> item.setProsperityRequirement    (value);
                    case CONSUMES       -> item.setConsumes                 (value);
                    case HEAL           -> item.setHeal                     (value);
                    case ATTACK         -> item.setAttack                   (value);
                    case RANGE          -> item.setRange                    (value);
                    case TARGET         -> item.setTarget                   (value);
                    case SHIELD         -> item.setShield                   (value);
                    case SHIELD_VALUE   -> item.setShieldValue              (value);
                    case RETALIATE      -> item.setRetaliate                (value);
                    case MOVE           -> item.setMove                     (value);
                    case OMOVE          -> item.setOMove                    (value);
                    case AMOVE          -> item.setAMove                    (value);
                    case PULL           -> item.setPull                     (value);
                    case PUSH           -> item.setPush                     (value);
                    case JUMP           -> item.setJump                     (value);
                    case CONDITIONS     -> item.setConditions               (value);
                    case INFUSE         -> item.setInfuse                  (value);
                }
            } // else ignore unknown attributes
        }
        // Store the parsed item in the active session data
        activeSessionData.addItem(item);
        System.out.printf(" Slot: %s --- StringId: %s %n", item.getSlot(), item.getStringId()); // debug print
    }



}
