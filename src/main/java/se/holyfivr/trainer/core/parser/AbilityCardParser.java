package se.holyfivr.trainer.core.parser;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.core.utils.SaveUtils;
import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.enums.CardAttribute;
import se.holyfivr.trainer.service.AbilityCardService;

@Service
public class AbilityCardParser {

    private final ActiveSessionData activeSessionData;
    private final AbilityCardService abilityCardService;
    private final SaveUtils saveUtils;

    // List of valid "stat"-keys. These are use when looping through the ruleset when finding abilities to change
    private static final List<String> VALID_STAT_KEYS = List.of(
            "Attack", "Damage", "Heal", "Move", "Range", "Shield", "Target",
            "Loot", "Pull", "Push", "Retaliate", "Pierce", "XP", "Consumes"
    );
    
    // Pattern to make name-key only accept root-level card-names
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\$[^\\r\\n]+\\$$");

    public AbilityCardParser(ActiveSessionData activeSessionData, AbilityCardService abilityCardService, SaveUtils saveUtils) {
        this.activeSessionData = activeSessionData;
        this.abilityCardService = abilityCardService;
        this.saveUtils = saveUtils;
    }

    public void parseAbilityCardBlock(String currentBlock) {

        AbilityCard abilityCard = new AbilityCard();
        String[] lines = currentBlock.split("\n");
        
        /* ========================================================================= */
        /* This is used to store temporary values during the loops. If an item with  */
        /* a valid key-phrase ends with ':' (which means it has no value), the       */
        /* the value is then stored in the temporary key variable, and when the next */
        /* "Amount" or "Strength" phase appears, we apply the value to update on     */
        /* that key instead. This is to handle occasions when the value of a key is  */
        /* not used in the original name of the key.                                 */
        /* ========================================================================= */
        String storedKey = null;

        // read all the rows in current block
        for (String line : lines) {


            // skip null or empty lines
            if (line == null) {
                continue;
            }

            // trim line and skip comments or empty lines
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                continue;
            }

            // separates the key and value by the first occurrence of ':'
            int separatorIndex = trimmedLine.indexOf(':');
            if (separatorIndex < 0) {
                continue;
            }

            // Assign key and value to 0, and 1 respectively
            String key = trimmedLine.substring(0, separatorIndex).trim();
            String value = trimmedLine.substring(separatorIndex + 1).trim();
            
            // Updates storedKey if key contains a valid string
            if (VALID_STAT_KEYS.contains(key)) {
                storedKey = key;
            }

            // empty value = skip this iteration
            // We also skip values containing "Resonance", as this is a special element for the Astral class
            if (value.isEmpty() || value.equalsIgnoreCase("Resonance")) {
                continue;
            }

            // if storedKey has a value, and the current key is "Amount" or "Strength", it belongs to the stored key
            // so we set key to storedKey to apply the value to that key instead
            String effectiveKey = key;
            if ((key.equalsIgnoreCase("Amount") || key.equalsIgnoreCase("Strength") || key.equalsIgnoreCase("Elements")) && storedKey != null) {
                effectiveKey = storedKey;
            }
            
            // we check the key towards the list of enums and assign it to attrEnum. if null, skip this iteration
            CardAttribute attrEnum = CardAttribute.fromString(effectiveKey);
            if (attrEnum == null) {
                continue;
            }

            // call applyAttribute to set the value on the abilityCard
            applyAttribute(abilityCard, attrEnum, value);
        }
        
        // Only add ability cards to activeSessionData with a known class name
        if (!abilityCard.getClassName().equals("Unknown")) {
            activeSessionData.addAbilityCard(abilityCard);
        }
    }


    private void applyAttribute(AbilityCard abilityCard, CardAttribute attrEnum, String value) {

        // set values to the current abilityCard, based on the attrEnum connected to the keys retrieved
        switch (attrEnum) {
            case NAME -> { 
                if (abilityCard.getName() == null && NAME_PATTERN.matcher(value).matches()) {
                    abilityCard.setName(value);
                    String[] details = abilityCardService.formatCardDetails(value);
                    abilityCard.setClassName(details[0]);
                    abilityCard.setCardName(details[1]);
                }
            }
            case INITIATIVE -> {
                if (abilityCard.getInitiative() == null) {
                    abilityCard.setInitiative(value);                
                }
            }
            case DISCARD -> {
                abilityCard.setDiscard(null);                
            }
            case CONSUMES -> {
                if (abilityCard.getConsumes() == null) {
                    abilityCard.setConsumes(value);
                }
            }
            case INFUSE -> {
                if (abilityCard.getInfuse() == null) {
                    abilityCard.setInfuse(value);
                }
            }
            case XP -> {
                if (saveUtils.isValidInteger(value)) {
                    String trimmed = trimmed(value);
                    abilityCard.getXpValues().add(trimmed);
                    abilityCard.setXP(trimmed);
                }
            }
            case PIERCE -> {
                String trimmed = trimmed(value);
                abilityCard.getPierceValues().add(trimmed);
                abilityCard.setPierce(trimmed);
            }
            case RETALIATE -> {
                String trimmed = trimmed(value);
                abilityCard.getRetaliateValues().add(trimmed);
                abilityCard.setRetaliate(trimmed);
            }
            case ATTACK -> {
                String trimmed = trimmed(value);
                abilityCard.getAttackValues().add(trimmed);
                abilityCard.setAttack(trimmed);
            }
            case HEAL -> {
                String trimmed = trimmed(value);
                abilityCard.getHealValues().add(trimmed);
                abilityCard.setHeal(trimmed);
            }
            case HEALTH -> {
                if (abilityCard.getHealth() == null) {
                    abilityCard.setHealth(value); 
                }
            }
            case DAMAGE -> {
                String trimmed = trimmed(value);
                abilityCard.getDamageValues().add(trimmed);
                abilityCard.setDamage(trimmed);
            }
            case MOVE -> {
                String trimmed = trimmed(value);
                abilityCard.getMoveValues().add(trimmed);
                abilityCard.setMove(trimmed);
            }
            case RANGE -> {
                String trimmed = trimmed(value);
                abilityCard.getRangeValues().add(trimmed);
                abilityCard.setRange(trimmed);
            }
            case SHIELD -> {
                String trimmed = trimmed(value);
                abilityCard.getShieldValues().add(trimmed);
                abilityCard.setShield(trimmed);
            }
            case TARGET -> {
                String trimmed = trimmed(value);
                abilityCard.getTargetValues().add(trimmed);
                abilityCard.setTarget(trimmed);
            }
            case PULL -> {
                String trimmed = trimmed(value);
                abilityCard.getPullValues().add(trimmed);
                abilityCard.setPull(trimmed);
            }
            case PUSH -> {
                String trimmed = trimmed(value);
                abilityCard.getPushValues().add(trimmed);
                abilityCard.setPush(trimmed);
            }
            case JUMP -> {
                if (abilityCard.getJump() == null) {
                    abilityCard.setJump(value);
                }
            }
            case LOOT -> {
                String trimmed = trimmed(value);
                abilityCard.getLootValues().add(trimmed);
                abilityCard.setLoot(trimmed);
            }
        }
    }

    private static String trimmed(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("+")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }
}
