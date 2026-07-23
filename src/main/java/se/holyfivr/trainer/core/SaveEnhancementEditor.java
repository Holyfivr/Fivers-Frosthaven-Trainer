package se.holyfivr.trainer.core;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.model.EnhancementGroup;

/* ================================== SAVE ENHANCEMENT EDITOR ================================== */
/*                                                                                               */
/* Reads and removes card enhancements from a Frosthaven save file.                              */
/*                                                                                               */
/* Enhancements live in one "ClassCardModifications" container: a nested, length-prefixed tree.  */
/* Its payload is a list of entries, one per enhanced card, keyed by a sequential string index:  */
/*                                                                                               */
/*   03 <key> 00 <len4> <payload>   (len counts itself + payload; entries have tag 0x03)         */
/*                                                                                               */
/* Each entry carries a numeric CardID, which maps to a class via CardClassRepository. Since     */
/* enhancements are class-persistent, "reset a character" means "remove that class's entries".   */
/*                                                                                               */
/* The game accepts a smaller file (no checksum, no fixed size), so a wipe simply slices out the */
/* target entries and shrinks three length fields by the removed byte count:                     */
/*   - the ClassCardModifications length itself, and                                             */
/*   - the outer container length, stored TWICE right before the unique "NextScenarioModifiers"  */
/*     field (at nsOffset-10 and nsOffset-5). Verified as the only size fields that must change.  */
/* ============================================================================================= */

@Component
public class SaveEnhancementEditor {

    private static final String CONTAINER = "ClassCardModifications";
    private static final byte ENTRY_TAG = 0x03;
    private static final String OUTER_ANCHOR = "NextScenarioModifiers";

    private final CardClassRepository cardClasses;

    public SaveEnhancementEditor(CardClassRepository cardClasses) {
        this.cardClasses = cardClasses;
    }

    /* ======================================================================== */
    /* Summarizes the enhancements in a save: how many enhanced cards each class */
    /* has. Classes are returned in a stable, alphabetical order for the UI.     */
    /* ======================================================================== */
    public List<EnhancementGroup> parseGroups(byte[] data) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Entry e : walkEntries(data)) {
            String cls = classOf(data, e);
            if (cls != null) {
                counts.merge(cls, 1, Integer::sum);
            }
        }
        List<EnhancementGroup> groups = new ArrayList<>();
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(en -> groups.add(new EnhancementGroup(en.getKey(), en.getValue())));
        return groups;
    }

    /* ======================================================================== */
    /* Returns a new save byte array with all of the given class's enhanced-card */
    /* entries removed and the three length fields fixed. If the class isn't     */
    /* present (or there's no enhancement container), returns the input as-is.   */
    /* ======================================================================== */
    public byte[] wipeClass(byte[] data, String className) {
        int lenOff = containerLengthOffset(data);
        if (lenOff < 0 || className == null) {
            return data;
        }

        // Collect the byte ranges [start, end) of the target class's entries
        List<int[]> remove = new ArrayList<>();
        for (Entry e : walkEntries(data)) {
            if (className.equals(classOf(data, e))) {
                remove.add(new int[]{e.start, e.end});
            }
        }
        if (remove.isEmpty()) {
            return data;
        }

        int removedBytes = 0;
        for (int[] r : remove) {
            removedBytes += r[1] - r[0];
        }

        // Locate the outer container length FIRST. It's stored twice, right before the unique
        // "NextScenarioModifiers" field (at ns-10 and ns-5). If we can't find it, or the two
        // copies disagree, the structure isn't what we expect — abort and return the file
        // UNCHANGED rather than write a half-fixed save with a wrong length (which would corrupt).
        int ns = indexOf(data, OUTER_ANCHOR.getBytes(StandardCharsets.ISO_8859_1), 0);
        if (ns < 10) {
            return data;
        }
        int outerA = ns - 10;
        int outerB = ns - 5;
        if (readIntLE(data, outerA) != readIntLE(data, outerB)) {
            return data;
        }

        // Work on a copy so we can patch the length fields before slicing
        byte[] patched = data.clone();

        // Shrink all three length fields by the removed byte count
        writeIntLE(patched, lenOff, readIntLE(patched, lenOff) - removedBytes);
        writeIntLE(patched, outerA, readIntLE(patched, outerA) - removedBytes);
        writeIntLE(patched, outerB, readIntLE(patched, outerB) - removedBytes);

        // Emit everything except the removed ranges
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int cursor = 0;
        for (int[] r : remove) {
            out.write(patched, cursor, r[0] - cursor);
            cursor = r[1];
        }
        out.write(patched, cursor, patched.length - cursor);
        return out.toByteArray();
    }

    /* ---------------------------------------------------------------------- */
    /* Walk the top-level entries of the ClassCardModifications container.     */
    /* Each entry: 03 <key> 00 <len4> <payload>, len counts itself + payload.  */
    /* A strict tag==0x03 guard stops cleanly at the container's trailing byte */
    /* and the next sibling field.                                             */
    /* ---------------------------------------------------------------------- */
    private List<Entry> walkEntries(byte[] data) {
        List<Entry> entries = new ArrayList<>();
        int lenOff = containerLengthOffset(data);
        if (lenOff < 0) {
            return entries;
        }
        int payStart = lenOff + 4;
        int payEnd = lenOff + readIntLE(data, lenOff);
        if (payEnd > data.length) {
            return entries;
        }
        int i = payStart;
        while (i < payEnd && data[i] == ENTRY_TAG) {
            int keyEnd = i + 1;
            while (keyEnd < payEnd && data[keyEnd] != 0) {
                keyEnd++;
            }
            int lenField = keyEnd + 1;            // after the key's 0x00 terminator
            int elen = readIntLE(data, lenField); // counts itself + payload
            int end = lenField + elen;
            if (elen <= 0 || end > payEnd) {
                break;
            }
            entries.add(new Entry(i, end, lenField));
            i = end;
        }
        return entries;
    }

    // Class of an entry via its first CardID -> class map lookup (null if unknown/ambiguous)
    private String classOf(byte[] data, Entry e) {
        int cardIdOff = indexOf(data, "CardID".getBytes(StandardCharsets.ISO_8859_1), e.contentStart, e.end);
        if (cardIdOff < 0) {
            return null;
        }
        // The nested int is: 10 "ID" 00 <int32>. Search past "CardID" so we don't match its own "ID".
        int idOff = indexOf(data, "ID".getBytes(StandardCharsets.ISO_8859_1), cardIdOff + 6, e.end);
        if (idOff < 0) {
            return null;
        }
        int cardId = readIntLE(data, idOff + 3); // skip "ID" (2) + 0x00 (1)
        return cardClasses.classOf(cardId);
    }

    // Offset of the ClassCardModifications length field (right after the name + 0x00), or -1
    private int containerLengthOffset(byte[] data) {
        int nameOff = indexOf(data, CONTAINER.getBytes(StandardCharsets.ISO_8859_1), 0);
        if (nameOff < 0) {
            return -1;
        }
        return nameOff + CONTAINER.length() + 1;
    }

    private int readIntLE(byte[] data, int offset) {
        if (offset < 0 || offset + 4 > data.length) {
            return -1;
        }
        return (data[offset] & 0xff)
                | ((data[offset + 1] & 0xff) << 8)
                | ((data[offset + 2] & 0xff) << 16)
                | ((data[offset + 3] & 0xff) << 24);
    }

    private void writeIntLE(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xff);
        data[offset + 1] = (byte) ((value >> 8) & 0xff);
        data[offset + 2] = (byte) ((value >> 16) & 0xff);
        data[offset + 3] = (byte) ((value >> 24) & 0xff);
    }

    private int indexOf(byte[] data, byte[] pattern, int from) {
        return indexOf(data, pattern, from, data.length);
    }

    private int indexOf(byte[] data, byte[] pattern, int from, int to) {
        int end = Math.min(data.length, to);
        for (int i = Math.max(0, from); i <= end - pattern.length; i++) {
            boolean match = true;
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }

    // One top-level entry: [start, end) byte range; contentStart = the len field (start of value)
    private static final class Entry {
        final int start;
        final int end;
        final int contentStart;

        Entry(int start, int end, int contentStart) {
            this.start = start;
            this.end = end;
            this.contentStart = contentStart;
        }
    }
}
