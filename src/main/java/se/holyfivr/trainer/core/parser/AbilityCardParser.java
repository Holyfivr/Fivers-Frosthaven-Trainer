package se.holyfivr.trainer.core.parser;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.enums.CardAttribute;
import se.holyfivr.trainer.service.AbilityCardService;

@Service
public class AbilityCardParser {

    private static final int MAX_ACCEPTED_CARD_LEVEL_INDENT = 8;

    private final ActiveSessionData activeSessionData;
    private final AbilityCardService abilityCardService;

    public AbilityCardParser(ActiveSessionData activeSessionData, AbilityCardService abilityCardService) {
        this.activeSessionData = activeSessionData;
        this.abilityCardService = abilityCardService;

    }

    public void parseAbilityCardBlock(String currentBlock) {

        AbilityCard abilityCard = new AbilityCard();
        String[] lines = currentBlock.split("\n");
        ParserContext context = new ParserContext();

        for (String line : lines) {

            if (line == null) {
                continue;
            }

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            int indent = countLeadingSpaces(line);
            context.pruneToIndent(indent);

            int separatorIndex = trimmed.indexOf(':');
            if (separatorIndex < 0) {
                continue;
            }

            // Split into key and value
            String key = trimmed.substring(0, separatorIndex).trim();
            String value = trimmed.substring(separatorIndex + 1).trim();

            // Track nested sections like ConditionalOverrides / AbilityOverrides so we can ignore
            // deeply nested values (e.g. XP/Pierce inside overrides).
            if (value.isEmpty()) {
                context.setSection(indent, key);
                context.maybeSetLastStatKey(key);
                continue;
            }

            // Remember last stat key for resolving "Amount" and "Strength"
            String effectiveKey = context.resolveStatKey(key);
            CardAttribute attrEnum = CardAttribute.fromString(effectiveKey);
            if (attrEnum == null) {
                continue;
            }

            // Apply the attribute to the ability card
            applyAttribute(abilityCard, attrEnum, value, indent, context);
        }
        if (!abilityCard.getClassName().equals("Unknown")) {
            activeSessionData.addAbilityCard(abilityCard);
        }
    }

    
    private void applyAttribute(AbilityCard abilityCard, CardAttribute attrEnum, String value, int indent,
            ParserContext context) {

        boolean isTopLevel = indent == 0;
        boolean inOverrides = context.isInsideOverrides();

        switch (attrEnum) {
            case NAME -> {
                // Only accept the block-level Name, not nested Name/ParentName keys.
                if (isTopLevel && abilityCard.getName() == null) {
                    abilityCard.setName(value);
                    String[] details = abilityCardService.formatCardDetails(value);
                    abilityCard.setClassName(details[0]);
                    abilityCard.setCardName(details[1]);
                }
            }
            case INITIATIVE -> {
                if (isTopLevel) {
                    abilityCard.setInitiative(value);
                }
            }
            case DISCARD -> {
                if (isTopLevel) {
                    abilityCard.setDiscard(value);
                }
            }
            case CONSUMES -> {
                // Often a block section, but can also appear as a key.
                if (!inOverrides && indent <= MAX_ACCEPTED_CARD_LEVEL_INDENT && abilityCard.getConsumes() == null) {
                    abilityCard.setConsumes(value);
                }
            }
            case INFUSE -> {
                if (!inOverrides && indent <= MAX_ACCEPTED_CARD_LEVEL_INDENT && abilityCard.getInfuses() == null) {
                    abilityCard.setInfuses(value);
                }
            }
            case XP -> {
                // XP appears both as card-level and nested under ConditionalOverrides; ignore nested.
                if (!inOverrides
                        && indent <= MAX_ACCEPTED_CARD_LEVEL_INDENT
                        && isPureInteger(value)) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getXpValues().add(normalized);
                    abilityCard.setXP(normalized);
                }
            }
            case PIERCE -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getPierceValues().add(normalized);
                    abilityCard.setPierce(normalized);
                }
            }
            case RETALIATE -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getRetaliateValues().add(normalized);
                    abilityCard.setRetaliate(normalized);
                }
            }
            case ATTACK -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getAttackValues().add(normalized);
                    abilityCard.setAttack(normalized);
                }
            }
            case HEAL -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getHealValues().add(normalized);
                    abilityCard.setHeal(normalized);
                }
            }
            case HEALTH -> {
                if (!inOverrides && abilityCard.getHealth() == null) {
                    // Keep as single value for now (not used in the card details UI).
                    abilityCard.setHealth(value);
                }
            }
            case DAMAGE -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getDamageValues().add(normalized);
                    abilityCard.setDamage(normalized);
                }
            }
            case MOVE -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getMoveValues().add(normalized);
                    abilityCard.setMove(normalized);
                }
            }
            case RANGE -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getRangeValues().add(normalized);
                    abilityCard.setRange(normalized);
                }
            }
            case SHIELD -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getShieldValues().add(normalized);
                    abilityCard.setShield(normalized);
                }
            }
            case TARGET -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getTargetValues().add(normalized);
                    abilityCard.setTarget(normalized);
                }
            }
            case PULL -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getPullValues().add(normalized);
                    abilityCard.setPull(normalized);
                }
            }
            case PUSH -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getPushValues().add(normalized);
                    abilityCard.setPush(normalized);
                }
            }
            case JUMP -> {
                if (!inOverrides && abilityCard.getJump() == null) {
                    abilityCard.setJump(value);
                }
            }
            case LOOT -> {
                if (!inOverrides) {
                    String normalized = normalizeSignedNumberForUi(value);
                    abilityCard.getLootValues().add(normalized);
                    abilityCard.setLoot(normalized);
                }
            }
        }
    }

    private static String normalizeSignedNumberForUi(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("+")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }

    private static int countLeadingSpaces(String value) {
        int count = 0;
        while (count < value.length() && value.charAt(count) == ' ') {
            count++;
        }
        return count;
    }

    private static boolean isPureInteger(String value) {
        if (value == null) {
            return false;
        }
        // Only accept lines like: XP: 1  (no extra symbols, quotes, comments, etc.)
        return value.matches("-?\\d+");
    }

    private static final class ParserContext {

        private final java.util.Map<Integer, String> sectionByIndent = new java.util.HashMap<>();
        private String lastStatKey;

        void pruneToIndent(int indent) {
            sectionByIndent.keySet().removeIf(existingIndent -> existingIndent > indent);
        }

        void setSection(int indent, String key) {
            sectionByIndent.put(indent, key);
        }

        boolean isInsideOverrides() {
            for (String section : sectionByIndent.values()) {
                if (section == null) {
                    continue;
                }
                if (section.equalsIgnoreCase("ConditionalOverrides") || section.equalsIgnoreCase("AbilityOverrides")) {
                    return true;
                }
            }
            return false;
        }

        void maybeSetLastStatKey(String key) {
            if (isStatKey(key)) {
                lastStatKey = key;
            }
        }

        String resolveStatKey(String key) {
            if ((key.equalsIgnoreCase("Amount") || key.equalsIgnoreCase("Strength")) && lastStatKey != null) {
                return lastStatKey;
            }
            return key;
        }

        private static boolean isStatKey(String key) {
            return key.equalsIgnoreCase("Attack")
                    || key.equalsIgnoreCase("Damage")
                    || key.equalsIgnoreCase("Heal")
                    || key.equalsIgnoreCase("Move")
                    || key.equalsIgnoreCase("Range")
                    || key.equalsIgnoreCase("Shield")
                    || key.equalsIgnoreCase("Target")
                    || key.equalsIgnoreCase("Loot")
                    || key.equalsIgnoreCase("Pull")
                    || key.equalsIgnoreCase("Push")
                    || key.equalsIgnoreCase("Retaliate")
                    || key.equalsIgnoreCase("Pierce")
                    || key.equalsIgnoreCase("XP");
        }
    }
}
