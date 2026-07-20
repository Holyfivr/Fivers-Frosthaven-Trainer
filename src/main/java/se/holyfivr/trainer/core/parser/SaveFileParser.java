package se.holyfivr.trainer.core.parser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.model.SaveCharacter;
import se.holyfivr.trainer.model.SaveField;

/* ====================================== SAVE FILE PARSER ===================================== */
/*                                                                                               */
/* Parses Frosthaven save files (.dat). Unlike the ruleset (which is mostly text), the save      */
/* file stores fields in a tagged binary format:                                                 */
/*                                                                                               */
/*   int field:    [0x10][fieldname][0x00][int32 little-endian value]                            */
/*   string field: [0x02][fieldname][0x00][int32 length][string bytes]                           */
/*                                                                                               */
/* Every character record starts with a standalone "Name" string field (the tag byte prevents    */
/* false matches on e.g. "AbilityName"), followed by the character's fields in a fixed order.    */
/* So for each character we anchor on its Name field, and each wanted value is simply the FIRST  */
/* occurrence of that field after the anchor, searched only up to the next character's Name.     */
/*                                                                                               */
/* Each parsed value keeps its exact byte offset, so saving is a same-size in-place overwrite    */
/* of 4 bytes per field. No byte compensation is needed (and the save has no checksum).          */
/* ============================================================================================= */

@Component
public class SaveFileParser {

    // [0x02]Name[0x00] = a standalone string field called exactly "Name"
    private static final byte[] NAME_PATTERN = {0x02, 'N', 'a', 'm', 'e', 0x00};

    // A real character record always has an Experience field right after its Name.
    // We use this to filter out the occasional non-character "Name" field.
    private static final byte[] EXPERIENCE_PATTERN = intFieldPattern("Experience");

    // How far after the name the Experience field must appear to count as a character
    private static final int EXPERIENCE_SEARCH_WINDOW = 80;

    // Gold and materials are stored per character...
    private static final String[] CHARACTER_FIELDS = {
            "Gold", "Lumber", "Metal", "Hide"
    };

    // ...but the herbs belong to the town, in a single record near the start of
    // the file. That record is anchored by the BonusDefense field, which only
    // occurs once in the whole file.
    private static final byte[] TOWN_HERBS_ANCHOR = intFieldPattern("BonusDefense");

    // The town's own resource list ends at its TotalNegativeValues field
    // (the field name itself appears in many records, but the first one
    // after the anchor is the town's).
    private static final byte[] TOWN_HERBS_END = intFieldPattern("TotalNegativeValues");

    private static final String[] TOWN_HERBS = {
            "Arrowvine", "Axenut", "Corpsecap", "Flamefruit", "Rockroot", "Snowthistle"
    };

    // The town's stats live between the ThreatBonus field (unique in the file)
    // and the BonusDefense field, so both ends of the search window are exact.
    private static final byte[] TOWN_STATS_ANCHOR = intFieldPattern("ThreatBonus");

    // Editable town stats: field name in the save, display label, the canonical
    // in-game maximum, and the step between selectable values in the editor.
    private record TownStat(String field, String label, int max, int step) {}

    private static final TownStat[] TOWN_STATS = {
            new TownStat("Morale", "Morale", 20, 1),
            new TownStat("Prosperity", "Prosperity", 26, 1),
            new TownStat("ProsperityLevel", "Prosperity Level", 9, 1),
            new TownStat("Soldiers", "Soldiers", 10, 1),
            new TownStat("SoldiersCapacity", "Soldiers Capacity", 10, 1),
            new TownStat("BaseDefense", "Base Defense", 40, 5),
            new TownStat("TownGuardPerksPoints", "Guard Perk Points", 15, 1)
    };

    /* ============================================================================================ */
    /*                                     PARSE SAVE FILE                                          */
    /*                                                                                              */
    /* Extracts the town record (herbs) and every character record (gold + materials), with the     */
    /* current value and byte offset of each editable field.                                        */
    /* ============================================================================================ */
    public List<SaveCharacter> parse(byte[] data) {
        List<SaveCharacter> characters = new ArrayList<>();

        // The town gets the first tab in the editor
        SaveCharacter town = parseTown(data);
        if (town != null) {
            characters.add(town);
        }

        // 1. Find every standalone "Name" field (candidate character anchors)
        List<Integer> nameOffsets = new ArrayList<>();
        for (int i = 0; i <= data.length - NAME_PATTERN.length; i++) {
            if (matchesAt(data, i, NAME_PATTERN)) {
                nameOffsets.add(i);
            }
        }

        // 2. For each anchor, validate it is a character and extract its fields
        for (int n = 0; n < nameOffsets.size(); n++) {
            int nameEnd = nameOffsets.get(n) + NAME_PATTERN.length;

            // Read the name string: [int32 length][string bytes]
            int nameLength = readIntLE(data, nameEnd);
            if (nameLength < 0 || nameLength > 64) {
                continue; // not a sane name, skip
            }
            String name = new String(data, nameEnd + 4, nameLength, StandardCharsets.ISO_8859_1)
                    .replace("\0", "").trim();
            int afterName = nameEnd + 4 + nameLength;

            // This character's record ends where the next character's Name begins
            int recordEnd = (n + 1 < nameOffsets.size()) ? nameOffsets.get(n + 1) : data.length;

            // Validate: a character record has an Experience field right after its name
            int windowEnd = Math.min(afterName + EXPERIENCE_SEARCH_WINDOW, recordEnd);
            if (findPattern(data, EXPERIENCE_PATTERN, afterName, windowEnd) < 0) {
                continue; // "Name" field that doesn't belong to a character
            }

            // Extract each editable field: first occurrence after the name, within this record
            SaveCharacter character = new SaveCharacter(name);
            for (String fieldName : CHARACTER_FIELDS) {
                byte[] fieldPattern = intFieldPattern(fieldName);
                int fieldIndex = findPattern(data, fieldPattern, afterName, recordEnd);
                if (fieldIndex >= 0) {
                    int valueOffset = fieldIndex + fieldPattern.length;
                    character.addField(new SaveField(fieldName, readIntLE(data, valueOffset), valueOffset));
                }
            }
            characters.add(character);
        }
        return characters;
    }

    /* ============================================================================================ */
    /*                                     PARSE TOWN RECORD                                        */
    /*                                                                                              */
    /* Extracts the town's stats and herb resources into a single "Town" tab.                       */
    /*                                                                                              */
    /* Stats: anchored on the unique ThreatBonus field, bounded by the unique BonusDefense field.   */
    /* Herbs: anchored on BonusDefense, bounded by the town's own TotalNegativeValues field, so     */
    /* we never read a character's copy of the same field names in later records.                   */
    /*                                                                                              */
    /* Returns null if nothing is found (reuses SaveCharacter so the town simply becomes a tab      */
    /* of its own in the editor).                                                                   */
    /* ============================================================================================ */
    private SaveCharacter parseTown(byte[] data) {
        SaveCharacter town = new SaveCharacter("Town");

        int herbsAnchor = findPattern(data, TOWN_HERBS_ANCHOR, 0, data.length);
        int statsAnchor = findPattern(data, TOWN_STATS_ANCHOR, 0, data.length);

        // Town stats: search between ThreatBonus and BonusDefense
        if (statsAnchor >= 0 && herbsAnchor > statsAnchor) {
            int searchFrom = statsAnchor + TOWN_STATS_ANCHOR.length + 4;
            for (TownStat stat : TOWN_STATS) {
                byte[] fieldPattern = intFieldPattern(stat.field());
                int fieldIndex = findPattern(data, fieldPattern, searchFrom, herbsAnchor);
                if (fieldIndex >= 0) {
                    int valueOffset = fieldIndex + fieldPattern.length;
                    int value = readIntLE(data, valueOffset);
                    town.addField(new SaveField(stat.label(), value, valueOffset, stat.max(), stat.step()));
                }
            }
        }

        // Town herbs: search between BonusDefense and TotalNegativeValues
        if (herbsAnchor >= 0) {
            int searchFrom = herbsAnchor + TOWN_HERBS_ANCHOR.length + 4;
            int searchTo = findPattern(data, TOWN_HERBS_END, searchFrom, data.length);
            if (searchTo < 0) {
                searchTo = searchFrom + 512; // fallback window, should never happen
            }
            for (String fieldName : TOWN_HERBS) {
                byte[] fieldPattern = intFieldPattern(fieldName);
                int fieldIndex = findPattern(data, fieldPattern, searchFrom, searchTo);
                if (fieldIndex >= 0) {
                    int valueOffset = fieldIndex + fieldPattern.length;
                    town.addField(new SaveField(fieldName, readIntLE(data, valueOffset), valueOffset));
                }
            }
        }
        return town.getFields().isEmpty() ? null : town;
    }

    // Helper: builds the byte pattern for an int field: [0x10][fieldname][0x00]
    private static byte[] intFieldPattern(String fieldName) {
        byte[] name = fieldName.getBytes(StandardCharsets.ISO_8859_1);
        byte[] pattern = new byte[name.length + 2];
        pattern[0] = 0x10;
        System.arraycopy(name, 0, pattern, 1, name.length);
        pattern[name.length + 1] = 0x00;
        return pattern;
    }

    // Helper: reads a 4-byte little-endian int at the given offset
    private int readIntLE(byte[] data, int offset) {
        if (offset < 0 || offset + 4 > data.length) {
            return -1;
        }
        return (data[offset] & 0xff)
                | ((data[offset + 1] & 0xff) << 8)
                | ((data[offset + 2] & 0xff) << 16)
                | ((data[offset + 3] & 0xff) << 24);
    }

    // Helper: checks if the pattern occurs at the given index
    private static boolean matchesAt(byte[] data, int index, byte[] pattern) {
        if (index < 0 || index + pattern.length > data.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; i++) {
            if (data[index + i] != pattern[i]) {
                return false;
            }
        }
        return true;
    }

    // Helper: finds the first occurrence of the pattern in [from, to), or -1
    private int findPattern(byte[] data, byte[] pattern, int from, int to) {
        int end = Math.min(data.length, to);
        for (int i = Math.max(0, from); i <= end - pattern.length; i++) {
            if (matchesAt(data, i, pattern)) {
                return i;
            }
        }
        return -1;
    }
}
