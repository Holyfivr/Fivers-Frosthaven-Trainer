package se.holyfivr.trainer.core.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;



/**
 * Utility helpers used by RulesetSaver to keep the core class focused on flow.
 *
 * This class performs deterministic transformations that are not validations
 * themselves (validation stays in Validator).
 */
@Component
public class SaveUtils {

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


        /* checks for integers including leading/trailing whitespace */
    private static final Pattern IS_INTEGER = Pattern.compile("^\\s*\\d+\\s*$");

    /* Checks if a list is present and contains at least one element. */
    public boolean isValidList(List<String> values) {
        return values != null && !values.isEmpty();
    }

    /* Checks if a single string value is present and not blank. */
    public boolean isValidValue(String value) {
        return value != null && !value.isBlank();
    }

    /* Parses a value to integer and verifies that it matches the regex expression. */
    public boolean isValidInteger(String value) {
        try {
            return value != null && Integer.parseInt(value.trim()) > 0 ? IS_INTEGER.matcher(value).matches() : false;    
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /* Verifies that every value in the list is a non-negative integer. */
    public boolean areAllNonNegativeInts(List<String> values) {
        if (!isValidList(values)) {
            return false;
        }
        for (String value : values) {
            if (!isValidInteger(value)) {
                return false;
            }
        }
        return true;
    }

    /* Sanitizes lines that accidentally stack up multiple inline comments. */
    public String stripDoubleHashComments(String block) {
        if (block == null || block.isEmpty()) {
            return block;
        }
        String[] lines = block.split("\n", -1);
        StringBuilder result = new StringBuilder(block.length());
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int firstHash = line.indexOf('#');
            if (firstHash != -1) {
                int secondHash = line.indexOf('#', firstHash + 1);
                if (secondHash != -1) {
                    line = line.substring(0, secondHash);
                }
            }
            result.append(line);
            if (i < lines.length - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }
 

}
