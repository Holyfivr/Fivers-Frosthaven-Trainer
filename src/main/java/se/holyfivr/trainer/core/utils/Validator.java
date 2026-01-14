package se.holyfivr.trainer.core.utils;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Centralized validation helpers used by the save pipeline.
 *
 * This class only answers the question "is this value acceptable?" and does not
 * perform transformations or side effects.
 */
@Component
public class Validator {

    /**
     * Checks if a list is present and contains at least one element.
     *
     * @param values list to verify
     * @return true if the list is non-null and non-empty
     */
    public boolean isValidList(List<String> values) {
        return values != null && !values.isEmpty();
    }

    /**
     * Checks if a single string value is present and not blank.
     *
     * @param value value to verify
     * @return true if the value is non-null and contains non-whitespace
     */
    public boolean isValidValue(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Parses a value to integer and verifies it is not negative.
     *
     * Used as a failsafe for numeric attributes where negative values should be
     * skipped when writing back to the file.
     *
     * @param value string containing an integer
     * @return true if value parses to an integer and is >= 0
     */
    public boolean isNonNegativeInt(String value) {
        if (!isValidValue(value)) {
            return false;
        }
        try {
            return Integer.parseInt(value.trim()) >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Verifies that every value in the list is a non-negative integer.
     *
     * If any value is missing, invalid, or negative, the whole list fails
     * validation so the caller can skip the update entirely.
     *
     * @param values list of numeric strings
     * @return true if all values are valid non-negative integers
     */
    public boolean areAllNonNegativeInts(List<String> values) {
        if (!isValidList(values)) {
            return false;
        }
        for (String value : values) {
            if (!isNonNegativeInt(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sanitizes lines that accidentally accumulate multiple inline comments.
     *
     * If a line contains two '#' characters, everything from the second '#'
     * onward is removed. This prevents repeated save cycles from appending
     * duplicate comment segments on the same line.
     *
     * @param block full text block to sanitize
     * @return sanitized block with repeated inline comments removed
     */
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