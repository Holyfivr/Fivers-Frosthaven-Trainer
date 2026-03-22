package se.holyfivr.trainer.core.parser;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.enums.CardAttribute;
import se.holyfivr.trainer.service.AbilityCardService;

/**
 * Service for parsing ability card data from the ruleset file.
 */

@Service
public class AbilityCardParser {

    private final ActiveSessionData activeSessionData;
    private final AbilityCardService abilityCardService;

    // List of valid "stat"-keys. These are use when looping through the ruleset when finding abilities to change
    private static final List<String> VALID_STAT_KEYS = List.of(
            "Attack", "Damage", "Heal", "Move", "Range", "Shield", "Target",
            "Loot", "Pull", "Push", "Retaliate", "Pierce", "XP", "Consumes", "Consume", "Infuse"
    );
    
    // Pattern to make name-key only accept root-level card-names
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\$[^\\r\\n]+\\$$");

    public AbilityCardParser(ActiveSessionData activeSessionData, AbilityCardService abilityCardService) {
        this.activeSessionData = activeSessionData;
        this.abilityCardService = abilityCardService;
    }

    /** 
     * Parses a block of text representing an ability card and updates the active session data with the parsed card.
     * The method processes each line in the block, extracting key-value pairs and applying them to the card based on recognized attributes.
     * It handles special cases where certain keys (e.g., "Consumes") may have associated "Amount" or "Strength" lines that should be linked together.
     * Only cards with a recognized class name are added to the active session data.
     */
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
                // We normalize Consume to Consumes for easier handling
                if (key.equalsIgnoreCase("Consume")) {
                    storedKey = "Consumes";
                } else {
                    storedKey = key;
                }
            }

            // empty value = skip this iteration
            // We also skip values containing "Resonance", as this is a special element for the Astral class
            if (value.isEmpty() || value.equalsIgnoreCase("Resonance")) {
                continue;
            }

            // if storedKey has a value, and the current key is "Amount" or "Strength", it belongs to the stored key
            // so we set key to storedKey to apply the value to that key instead
            String effectiveKey = key;

            if (storedKey != null && (storedKey.equalsIgnoreCase("Consumes") || storedKey.equalsIgnoreCase("Consume"))) {
                if (key.equalsIgnoreCase("Elements")) {
                    effectiveKey = "Consumes";
                } else if (key.equalsIgnoreCase("Amount") || key.equalsIgnoreCase("Strength")) {
                    storedKey = null;
                }
            } else if ((key.equalsIgnoreCase("Amount") || key.equalsIgnoreCase("Strength")) && storedKey != null) {
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

    /**
     * Applies the given value to the specified attribute of the ability card, based on the provided CardAttribute enum.
     * 
     */
    @SuppressWarnings("incomplete-switch")
    private void applyAttribute(AbilityCard abilityCard, CardAttribute attrEnum, String value) {

        // set values to the current abilityCard, based on the attrEnum connected to the keys retrieved
        switch (attrEnum) {
            case NAME       -> setName      (abilityCard, value); 
            case INITIATIVE -> setValue     (abilityCard, value, AbilityCard::getInitiative     , AbilityCard::setInitiative);
            case HEALTH     -> setValue     (abilityCard, value, AbilityCard::getHealth         , AbilityCard::setHealth    );
            case JUMP       -> setValue     (abilityCard, value, AbilityCard::getJump           , AbilityCard::setJump      );
            case XP         -> setValues    (abilityCard, value, AbilityCard::getXpValues       , AbilityCard::setXP        );
            case ATTACK     -> setValues    (abilityCard, value, AbilityCard::getAttackValues   , AbilityCard::setAttack    );
            case HEAL       -> setValues    (abilityCard, value, AbilityCard::getHealValues     , AbilityCard::setHeal      );
            case DAMAGE     -> setValues    (abilityCard, value, AbilityCard::getDamageValues   , AbilityCard::setDamage    );
            case MOVE       -> setValues    (abilityCard, value, AbilityCard::getMoveValues     , AbilityCard::setMove      );
            case RANGE      -> setValues    (abilityCard, value, AbilityCard::getRangeValues    , AbilityCard::setRange     );
            case SHIELD     -> setValues    (abilityCard, value, AbilityCard::getShieldValues   , AbilityCard::setShield    );
            case TARGET     -> setValues    (abilityCard, value, AbilityCard::getTargetValues   , AbilityCard::setTarget    );
            case PULL       -> setValues    (abilityCard, value, AbilityCard::getPullValues     , AbilityCard::setPull      );
            case PUSH       -> setValues    (abilityCard, value, AbilityCard::getPushValues     , AbilityCard::setPush      );
            case PIERCE     -> setValues    (abilityCard, value, AbilityCard::getPierceValues   , AbilityCard::setPierce    );
            case RETALIATE  -> setValues    (abilityCard, value, AbilityCard::getRetaliateValues, AbilityCard::setRetaliate );
            case LOOT       -> setValues    (abilityCard, value, AbilityCard::getLootValues     , AbilityCard::setLoot      );
            case CONSUMES   -> setValue     (abilityCard, parseElementValue(value), AbilityCard::getConsumes , AbilityCard::setConsumes);
            case INFUSE     -> setValue     (abilityCard, parseElementValue(value), AbilityCard::getInfuse   , AbilityCard::setInfuse  );
            case DISCARD    -> abilityCard.setDiscard(null);
        }
    }

    /**
     * Sets the name, class name, and card name on the given card.
     * Only applies if the card doesn't already have a name and the value matches the expected root-level name pattern.
     * This is because there are instances in the ruleset where "Name:" is used for other purposes, 
     * and we only want to set the card's name based on the root-level name key.
     */
    private void setName(AbilityCard card, String value) {
        if (card.getName() == null && NAME_PATTERN.matcher(value).matches()) {
            card.setName(value);
            String[] details = abilityCardService.formatCardDetails(value);
            card.setClassName(details[0]);
            card.setCardName(details[1]);
        }
    }

    /**
     * Sets a singular (non-list) value on the given card, only if the current value is null.
     * Uses functional references to generalize across different card attributes.
     * Reduces repetitive code in the switch statement.
     */
    private void setValue(AbilityCard card, String value, Function<AbilityCard, String> getter, BiConsumer<AbilityCard, String> setter) {
        if (getter.apply(card) == null) {
            setter.accept(card, value);
        }
    }

    /**
     * Adds a trimmed value to the card's value list and updates the card's singular field.
     * Used for attributes that track multiple values (e.g. attack, heal, move) across different abilities on the same card.
     * Reduces repetitive code in the switch statement.
     */
    private void setValues(AbilityCard card, String value, Function<AbilityCard, List<String>> listGetter, BiConsumer<AbilityCard, String> setter) {
        String trimmedValue = trimmed(value);
        listGetter.apply(card).add(trimmedValue);
        setter.accept(card, trimmedValue);
    }

    /**
     * Strips brackets from element values for Infuse/Consumes.
     * Single element: "[Air]" → "Air". Multiple elements: "[Air, Fire]" → unchanged.
     */
    private static String parseElementValue(String value) {
        if (value.startsWith("[") && value.endsWith("]")) {
            String inner = value.substring(1, value.length() - 1);
            if (!inner.contains(",")) {
                return inner.trim();
            }
        }
        return value;
    }

    /**
     * Trims the value and removes a leading '+' if present.
     * Used to normalize parsed numeric values from the ruleset.
     */
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
