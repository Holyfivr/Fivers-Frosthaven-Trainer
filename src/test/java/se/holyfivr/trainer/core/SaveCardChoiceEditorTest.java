package se.holyfivr.trainer.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class SaveCardChoiceEditorTest {

    private final SaveCardChoiceEditor editor = new SaveCardChoiceEditor(new CardLevelCatalog());

    @Test
    void resetsBannerspearToLevelOneWithoutChangingTheOriginal() {
        byte[] source = save("Xena", "BannerSpearID", 4, 0,
                List.of(71, 72, 69, 63, 65, 78),
                List.of(61, 62, 64, 66, 67, 68, 70, 73, 75, 76));
        byte[] copy = source.clone();
        List<SaveCardChoiceEditor.ResetOption> options = editor.availableResets(source);
        assertEquals(1, options.size());
        assertEquals(List.of(1, 2, 3), options.getFirst().targetLevels());

        byte[] reset = editor.reset(source, 0, 1);
        assertArrayEquals(copy, source);
        assertEquals(3, readIntAfter(reset, "CardUnlocks"));
        assertEquals(4, readIntAfter(reset, "Level"));
        assertTrue(editor.availableResets(reset).isEmpty());
        assertEquals(source.length - 48, reset.length);
    }

    @Test
    void refusesAnAmbiguousPartialResetButAllowsAFullReset() {
        byte[] source = save("Hulk", "DrifterID", 3, 0,
                List.of(11, 12, 13, 14, 15),
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 17));
        List<SaveCardChoiceEditor.ResetOption> options = editor.availableResets(source);
        assertEquals(1, options.size());
        assertEquals(List.of(1), options.getFirst().targetLevels());
        assertThrows(IllegalArgumentException.class, () -> editor.reset(source, 0, 2));
        assertEquals(2, readIntAfter(editor.reset(source, 0, 1), "CardUnlocks"));
    }

    @Test
    void preservesAnAlreadyPendingChoice() {
        byte[] source = save("Xena", "BannerSpearID", 4, 1,
                List.of(71, 72, 69, 63, 65),
                List.of(61, 62, 64, 66, 67, 68, 70, 73, 75, 76));
        assertEquals(List.of(1, 2), editor.availableResets(source).getFirst().targetLevels());
        byte[] reset = editor.reset(source, 0, 2);
        assertEquals(2, readIntAfter(reset, "CardUnlocks"));
        assertEquals(4, readIntAfter(reset, "Level"));
        assertEquals(List.of(1), editor.availableResets(reset).getFirst().targetLevels());
    }

    @Test
    void reindexesCardListsAcrossTwoDigitIndexes() {
        byte[] source = save("Xena", "BannerSpearID", 9, 0,
                List.of(71, 72, 73, 74, 76, 78, 80, 82, 84, 86, 88),
                List.of(61, 62, 63, 64, 65, 66, 67, 68, 69, 70));
        byte[] reset = editor.reset(source, 0, 8);
        assertEquals(source.length - 17, reset.length);
        assertEquals(1, readIntAfter(reset, "CardUnlocks"));
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7),
                editor.availableResets(reset).getFirst().targetLevels());
    }

    @Test
    void refusesToRemoveADisabledCard() {
        byte[] source = save("Xena", "BannerSpearID", 4, 0,
                List.of(71, 72, 69, 63, 65, 78),
                List.of(61, 62, 64, 66, 67, 68, 70, 73, 75, 76), 76);
        assertEquals(List.of(3), editor.availableResets(source).getFirst().targetLevels());
        assertThrows(IllegalArgumentException.class, () -> editor.reset(source, 0, 2));
        assertEquals(1, readIntAfter(editor.reset(source, 0, 3), "CardUnlocks"));
    }

    @Test
    void ignoresADisabledStarterCard() {
        byte[] source = save("Xena", "BannerSpearID", 4, 0,
                List.of(71, 72, 69, 63, 65, 78),
                List.of(61, 62, 64, 66, 67, 68, 70, 73, 75, 76), 61);
        assertEquals(List.of(1, 2, 3), editor.availableResets(source).getFirst().targetLevels());
    }

    @Test
    void findsADisabledCardAmongSeveralModifiers() {
        // Real saves keep one modifier per entry, most with DisabledCardID 0.
        // 78 is the level 4 card, so every reset target would remove it.
        byte[] source = save("Xena", "BannerSpearID", 4, 0,
                List.of(71, 72, 69, 63, 65, 78),
                List.of(61, 62, 64, 66, 67, 68, 70, 73, 75, 76), 0, 78);
        assertTrue(editor.availableResets(source).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> editor.reset(source, 0, 3));
    }

    @Test
    void refusesAnUnexpectedDisabledCardStructure() {
        byte[] source = save("Xena", "BannerSpearID", 4, 0,
                List.of(71, 72, 69, 63, 65, 78),
                List.of(61, 62, 64, 66, 67, 68, 70, 73, 75, 76), 76);
        byte[] marker = concat(new byte[] {3}, "DisabledCardID\0".getBytes(StandardCharsets.US_ASCII));
        int lengthOffset = indexOf(source, marker) + marker.length;
        source[lengthOffset] = 14; // not the {ID} object the editor knows
        assertTrue(editor.availableResets(source).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> editor.reset(source, 0, 3));
    }

    private static byte[] save(String name, String classId, int level, int unlocks,
            List<Integer> unused, List<Integer> selected, int... disabledCardIds) {
        ByteArrayOutputStream character = new ByteArrayOutputStream();
        character.writeBytes(stringField("Name", name));
        byte[] nestedId = stringField("ID", classId);
        field(character, 3, "ClassID", concat(nestedId, new byte[] {0}));
        intField(character, "Level", level);
        intField(character, "CardUnlocks", unlocks);
        field(character, 4, "UnusedCardIDs", indexedCards(unused));
        field(character, 4, "SelectedCardIDs", indexedCards(selected));
        if (disabledCardIds.length > 0) {
            // Same layout as the game: DisabledItemID and DisabledCardID are {ID} objects
            ByteArrayOutputStream modifiers = new ByteArrayOutputStream();
            for (int i = 0; i < disabledCardIds.length; i++) {
                ByteArrayOutputStream modifier = new ByteArrayOutputStream();
                intField(modifier, "Type", 20);
                field(modifier, 3, "DisabledItemID", idObject(0));
                field(modifier, 3, "DisabledCardID", idObject(disabledCardIds[i]));
                modifier.write(0);
                modifiers.writeBytes(entry(i, modifier.toByteArray()));
            }
            modifiers.write(0);
            field(character, 4, "ScenarioModifiers", modifiers.toByteArray());
        }
        character.write(0);

        ByteArrayOutputStream characters = new ByteArrayOutputStream();
        characters.writeBytes(entry(0, character.toByteArray()));
        characters.write(0);
        ByteArrayOutputStream save = new ByteArrayOutputStream();
        save.writeBytes(new byte[10]); // identical outer lengths at offsets 0 and 5
        save.writeBytes("NextScenarioModifiers\0".getBytes(StandardCharsets.US_ASCII));
        field(save, 4, "AllCharacters", characters.toByteArray());
        save.write(0);
        byte[] result = save.toByteArray();
        byte[] size = intBytes(result.length);
        System.arraycopy(size, 0, result, 0, 4);
        System.arraycopy(size, 0, result, 5, 4);
        return result;
    }

    private static byte[] indexedCards(List<Integer> cards) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < cards.size(); i++) {
            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            intField(payload, "ID", cards.get(i));
            payload.write(0);
            out.writeBytes(entry(i, payload.toByteArray()));
        }
        out.write(0);
        return out.toByteArray();
    }

    private static byte[] idObject(int id) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        intField(out, "ID", id);
        out.write(0);
        return out.toByteArray();
    }

    private static int indexOf(byte[] bytes, byte[] needle) {
        for (int i = 0; i <= bytes.length - needle.length; i++) {
            if (Arrays.equals(bytes, i, i + needle.length, needle, 0, needle.length)) {
                return i;
            }
        }
        throw new AssertionError("Missing marker");
    }

    private static byte[] entry(int index, byte[] payload) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(3);
        out.writeBytes(Integer.toString(index).getBytes(StandardCharsets.US_ASCII));
        out.write(0);
        out.writeBytes(intBytes(4 + payload.length));
        out.writeBytes(payload);
        return out.toByteArray();
    }

    private static byte[] stringField(String key, String value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] payload = concat(value.getBytes(StandardCharsets.UTF_8), new byte[] {0});
        out.write(2);
        out.writeBytes(key.getBytes(StandardCharsets.US_ASCII));
        out.write(0);
        out.writeBytes(intBytes(payload.length));
        out.writeBytes(payload);
        return out.toByteArray();
    }

    private static void intField(ByteArrayOutputStream out, String key, int value) {
        out.write(16);
        out.writeBytes(key.getBytes(StandardCharsets.US_ASCII));
        out.write(0);
        out.writeBytes(intBytes(value));
    }

    private static void field(ByteArrayOutputStream out, int tag, String name, byte[] payload) {
        out.write(tag);
        out.writeBytes(name.getBytes(StandardCharsets.US_ASCII));
        out.write(0);
        out.writeBytes(intBytes(4 + payload.length));
        out.writeBytes(payload);
    }

    private static byte[] intBytes(int value) {
        return new byte[] {(byte) value, (byte) (value >>> 8),
                (byte) (value >>> 16), (byte) (value >>> 24)};
    }

    private static byte[] concat(byte[] first, byte[] second) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(first);
        out.writeBytes(second);
        return out.toByteArray();
    }

    private static int readIntAfter(byte[] bytes, String field) {
        byte[] marker = concat(new byte[] {16}, concat(field.getBytes(StandardCharsets.US_ASCII), new byte[] {0}));
        for (int i = 0; i <= bytes.length - marker.length - 4; i++) {
            boolean match = true;
            for (int j = 0; j < marker.length; j++) match &= bytes[i + j] == marker[j];
            if (match) {
                int offset = i + marker.length;
                return (bytes[offset] & 255) | ((bytes[offset + 1] & 255) << 8)
                        | ((bytes[offset + 2] & 255) << 16) | ((bytes[offset + 3] & 255) << 24);
            }
        }
        throw new AssertionError("Missing " + field);
    }
}
