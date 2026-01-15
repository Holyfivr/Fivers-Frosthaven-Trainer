package se.holyfivr.trainer.core.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.model.AbilityCard;

/**
 * Utility helpers used by RulesetSaver to keep the core class focused on flow.
 *
 * This class performs deterministic transformations that are not validations
 * themselves (validation stays in Validator).
 */
@Component
public class SaveUtils {

    private final Validator validator;

    /**
     * Constructs the helper with its validation dependency.
     *
     * @param validator shared validation logic used by save helpers
     */
    public SaveUtils(Validator validator) {
        this.validator = validator;
    }

    /**
     * Extracts a single identifier line from a block based on the given prefix.
     *
     * @param block full block text
     * @param phrase prefix to match (e.g., "ID:" or "Name:")
     * @return the trimmed identifier value, or null if not found
     */
    public String extractIdentifier(String block, String phrase) {
        String[] lines = block.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith(phrase)) {
                return line.replace(phrase, "").trim();
            }
        }
        return null;
    }

    /**
     * Updates a single top-level line (no indentation) within a block.
     *
     * This preserves the original line ending (CRLF vs LF) to avoid unintended
     * formatting changes.
     *
     * @param block full block text
     * @param key line key to replace (e.g., "Initiative")
     * @param value new value to write
     * @return updated block text
     */
    public String updateTopLevelLine(String block, String key, String value) {
        Pattern pattern = Pattern.compile("(?m)^" + Pattern.quote(key) + ":\\s*.*?(\\r?)$");
        Matcher matcher = pattern.matcher(block);
        if (!matcher.find()) {
            return block;
        }
        String lineEnding = matcher.group(1);
        String replacement = key + ": " + value + lineEnding;
        return matcher.replaceFirst(Matcher.quoteReplacement(replacement));
    }

    /**
     * Updates a boolean attribute line that uses True/False formatting.
     *
     * The ruleset expects capitalized booleans, so we normalize the input to
     * "True" or "False" before replacement.
     *
     * @param block full block text
     * @param key boolean attribute key
     * @param value incoming boolean string (case-insensitive)
     * @return updated block text
     */
    public String updateBooleanAttribute(String block, String key, String value) {
        String normalized = "True";
        if (value != null && value.equalsIgnoreCase("false")) {
            normalized = "False";
        }
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(key) + ":\\s*(True|False)\\b");
        Matcher matcher = pattern.matcher(block);
        return matcher.replaceAll(Matcher.quoteReplacement(key + ": " + normalized));
    }

    /**
     * Updates an ability-card stat using either list or single-value semantics.
     *
     * This method chooses between list and single value updates and enforces
     * non-negative integer validation before writing back into the block.
     *
     * @param block full block text
     * @param abilityCard source of stat values
     * @param stat stat metadata (key + getters)
     * @param listUpdater callback to update list-based attributes
     * @param valueUpdater callback to update single-value attributes
     * @return updated block text
     */
    public String updateAbilityCardStat(
            String block,
            AbilityCard abilityCard,
            AbilityStat stat,
            BlockListUpdater listUpdater,
            BlockValueUpdater valueUpdater) {
        if (validator.isValidList(stat.getList(abilityCard))) {
            return listUpdater.apply(block, stat.getKey(), stat.getList(abilityCard));
        } else if (validator.isValidValue(stat.getSingle(abilityCard))) {
            return valueUpdater.apply(block, stat.getKey(), stat.getSingle(abilityCard));
        }
        return block;
    }

    /**
     * Strategy for updating list-based attributes in a block.
     */
    @FunctionalInterface
    public interface BlockListUpdater {
        String apply(String block, String key, List<String> values);
    }

    /**
     * Strategy for updating single-value attributes in a block.
     */
    @FunctionalInterface
    public interface BlockValueUpdater {
        String apply(String block, String key, String value);
    }
}
