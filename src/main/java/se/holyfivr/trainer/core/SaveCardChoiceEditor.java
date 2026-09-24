package se.holyfivr.trainer.core;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class SaveCardChoiceEditor {

    private final CardLevelCatalog cardLevels;

    public SaveCardChoiceEditor(CardLevelCatalog cardLevels) {
        this.cardLevels = cardLevels;
    }

    public record ResetOption(int characterIndex, String characterName, int currentLevel,
            List<Integer> targetLevels) {}

    private record Entry(int start, int end) {}
    private record IndexedList(int lengthOffset, int payloadStart, int payloadEnd, List<Entry> entries) {}
    private record CardList(IndexedList span, List<Integer> ids) {}
    private record CharacterState(int index, String name, String classId, Entry entry,
            int entryLengthOffset, int level, int unlocks, int unlockOffset,
            CardList unused, CardList selected, Set<Integer> disabledCardIds) {}
    private record Campaign(IndexedList characters, int outerA, int outerB) {}
    private record ChoiceState(Map<Integer, Integer> catalog, List<Integer> earned,
            int lastCompleted) {}

    public List<ResetOption> availableResets(byte[] data) {
        Campaign campaign = parseCampaign(data);
        List<ResetOption> result = new ArrayList<>();
        for (int i = 0; i < campaign.characters.entries.size(); i++) {
            try {
                CharacterState character = parseCharacter(data, campaign.characters.entries.get(i), i);
                ChoiceState state = validateCharacter(character);
                List<Integer> targets = new ArrayList<>();
                for (int target = 1; target < state.lastCompleted; target++) {
                    int mask = removalMask(state, target);
                    if (isSafeRemoval(character, state, mask)) {
                        targets.add(target);
                    }
                }
                if (!targets.isEmpty()) {
                    result.add(new ResetOption(i, character.name, character.level, List.copyOf(targets)));
                }
            } catch (IllegalArgumentException ignored) {
                continue;
            }
        }
        return result;
    }

    public byte[] reset(byte[] data, int characterIndex, int targetLevel) {
        Campaign campaign = parseCampaign(data);
        if (characterIndex < 0 || characterIndex >= campaign.characters.entries.size()) {
            throw new IllegalArgumentException("Character index is outside the save");
        }
        CharacterState character = parseCharacter(data,
                campaign.characters.entries.get(characterIndex), characterIndex);
        ChoiceState state = validateCharacter(character);
        if (targetLevel < 1 || targetLevel >= state.lastCompleted) {
            throw new IllegalArgumentException("No completed choice exists above that level");
        }
        int mask = removalMask(state, targetLevel);
        if (!isSafeRemoval(character, state, mask)) {
            throw new IllegalArgumentException("The choices above that level cannot be inferred safely");
        }

        List<Integer> unused = new ArrayList<>(character.unused.ids);
        List<Integer> selected = new ArrayList<>(character.selected.ids);
        for (int i = state.earned.size() - 1; i >= 0; i--) {
            if ((mask & (1 << i)) == 0) continue;
            int owned = state.earned.get(i);
            int handPosition = selected.indexOf(owned);
            if (handPosition >= 0) {
                int starterPosition = -1;
                for (int j = unused.size() - 1; j >= 0; j--) {
                    if (state.catalog.get(unused.get(j)) == 1) {
                        starterPosition = j;
                        break;
                    }
                }
                if (starterPosition < 0) {
                    throw new IllegalArgumentException("No unused starter card can fill the hand");
                }
                selected.set(handPosition, unused.remove(starterPosition));
            } else if (!unused.remove(Integer.valueOf(owned))) {
                throw new IllegalArgumentException("Chosen card is absent from both card lists");
            }
        }

        byte[] newUnusedPayload = encodeCardList(unused);
        int oldPayloadSize = character.unused.span.payloadEnd - character.unused.span.payloadStart;
        int delta = newUnusedPayload.length - oldPayloadSize;
        if (delta >= 0) {
            throw new IllegalArgumentException("A reset must remove at least one card entry");
        }
        byte[] patched = data.clone();
        writeInt(patched, character.unlockOffset, character.level - targetLevel);
        for (int i = 0; i < selected.size(); i++) {
            Entry entry = character.selected.span.entries.get(i);
            int idOffset = cardIdOffset(patched, entry);
            writeInt(patched, idOffset, selected.get(i));
        }
        for (int offset : new int[] {character.unused.span.lengthOffset,
                character.entryLengthOffset, campaign.characters.lengthOffset,
                campaign.outerA, campaign.outerB}) {
            int changedLength = readInt(patched, offset) + delta;
            if (changedLength < 5) {
                throw new IllegalArgumentException("Invalid enclosing save length");
            }
            writeInt(patched, offset, changedLength);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length + delta);
        out.write(patched, 0, character.unused.span.payloadStart);
        out.writeBytes(newUnusedPayload);
        out.write(patched, character.unused.span.payloadEnd,
                patched.length - character.unused.span.payloadEnd);
        byte[] result = out.toByteArray();

        Campaign checkCampaign = parseCampaign(result);
        CharacterState check = parseCharacter(result,
                checkCampaign.characters.entries.get(characterIndex), characterIndex);
        validateCharacter(check);
        if (check.unlocks != character.level - targetLevel
                || !check.unused.ids.equals(unused) || !check.selected.ids.equals(selected)
                || result.length != data.length + delta) {
            throw new IllegalArgumentException("Reserialized card choices failed verification");
        }
        return result;
    }

    private ChoiceState validateCharacter(CharacterState character) {
        Map<Integer, Integer> catalog = cardLevels.forClass(character.classId);
        if (catalog == null || character.level < 2 || character.level > 9
                || character.unlocks < 0 || character.unlocks >= character.level
                || character.selected.ids.isEmpty()) {
            throw new IllegalArgumentException("Unsupported character card state");
        }
        Set<Integer> owned = new HashSet<>();
        for (int id : character.unused.ids) {
            if (!owned.add(id)) throw new IllegalArgumentException("Duplicate owned card");
        }
        for (int id : character.selected.ids) {
            if (!owned.add(id)) throw new IllegalArgumentException("Duplicate owned card");
        }
        int lastCompleted = character.level - character.unlocks;
        List<Integer> earned = new ArrayList<>();
        for (int id : character.selected.ids) {
            Integer level = catalog.get(id);
            if (level == null || level > lastCompleted) throw new IllegalArgumentException("Unknown owned card");
            if (level > 1) earned.add(id);
        }
        for (int id : character.unused.ids) {
            Integer level = catalog.get(id);
            if (level == null || level > lastCompleted) throw new IllegalArgumentException("Unknown owned card");
            if (level > 1) earned.add(id);
        }
        for (Map.Entry<Integer, Integer> card : catalog.entrySet()) {
            if (card.getValue() == 1 && !owned.contains(card.getKey())) {
                throw new IllegalArgumentException("Missing starter card");
            }
        }
        if (earned.size() != lastCompleted - 1 || earned.size() > 8) {
            throw new IllegalArgumentException("Owned cards do not match spent choices");
        }
        return new ChoiceState(catalog, earned, lastCompleted);
    }

    private static int removalMask(ChoiceState state, int targetLevel) {
        int[] result = {-1};
        assign(state, targetLevel, 2, 0, 0, result);
        return result[0];
    }

    private static void assign(ChoiceState state, int target, int level, int used,
            int removed, int[] result) {
        if (result[0] == -2) return;
        if (level > state.lastCompleted) {
            if (result[0] == -1) result[0] = removed;
            else if (result[0] != removed) result[0] = -2;
            return;
        }
        for (int i = 0; i < state.earned.size(); i++) {
            int bit = 1 << i;
            if ((used & bit) == 0 && state.catalog.get(state.earned.get(i)) <= level) {
                assign(state, target, level + 1, used | bit,
                        removed | (level > target ? bit : 0), result);
            }
        }
    }

    private static boolean isSafeRemoval(CharacterState character, ChoiceState state, int mask) {
        if (mask < 0) return false;
        for (int i = 0; i < state.earned.size(); i++) {
            if ((mask & (1 << i)) != 0
                    && character.disabledCardIds.contains(state.earned.get(i))) {
                return false;
            }
        }
        return canFillHand(character, state.catalog, state.earned, mask);
    }

    private static boolean canFillHand(CharacterState character, Map<Integer, Integer> catalog,
            List<Integer> earned, int mask) {
        int handCardsToReplace = 0;
        for (int i = 0; i < earned.size(); i++) {
            if ((mask & (1 << i)) != 0 && character.selected.ids.contains(earned.get(i))) {
                handCardsToReplace++;
            }
        }
        int unusedStarters = 0;
        for (int id : character.unused.ids) {
            if (catalog.get(id) == 1) unusedStarters++;
        }
        return unusedStarters >= handCardsToReplace;
    }

    private static Campaign parseCampaign(byte[] data) {
        IndexedList characters = indexedList(data, new byte[] {4}, "AllCharacters", 0, data.length);
        int ns = unique(data, "NextScenarioModifiers".getBytes(StandardCharsets.ISO_8859_1), 0, data.length);
        if (ns < 10) throw new IllegalArgumentException("Missing outer save lengths");
        int outerA = ns - 10;
        int outerB = ns - 5;
        int length = readInt(data, outerA);
        if (length != readInt(data, outerB) || length < 5
                || outerA + length > data.length || characters.payloadEnd > outerA + length) {
            throw new IllegalArgumentException("Outer save lengths disagree");
        }
        return new Campaign(characters, outerA, outerB);
    }

    private static CharacterState parseCharacter(byte[] data, Entry entry, int index) {
        int keyEnd = zero(data, entry.start + 1, entry.end);
        int lengthOffset = keyEnd + 1;
        String name = stringField(data, "Name", entry.start, entry.end);
        byte[] classMarker = marker((byte) 3, "ClassID");
        int classOffset = unique(data, classMarker, entry.start, entry.end);
        int classLengthOffset = classOffset + classMarker.length;
        int classEnd = classLengthOffset + readInt(data, classLengthOffset);
        if (classEnd > entry.end) throw new IllegalArgumentException("Class ID exceeds character");
        String classId = stringField(data, "ID", classLengthOffset + 4, classEnd);
        int levelOffset = unique(data, marker((byte) 16, "Level"), entry.start, entry.end)
                + marker((byte) 16, "Level").length;
        int unlockOffset = unique(data, marker((byte) 16, "CardUnlocks"), entry.start, entry.end)
                + marker((byte) 16, "CardUnlocks").length;
        CardList unused = cardList(data, "UnusedCardIDs", entry.start, entry.end);
        CardList selected = cardList(data, "SelectedCardIDs", entry.start, entry.end);
        Set<Integer> disabledCardIds = new HashSet<>();
        byte[] disabledMarker = marker((byte) 16, "DisabledCardID");
        for (int offset = entry.start; offset <= entry.end - disabledMarker.length; offset++) {
            boolean match = true;
            for (int i = 0; i < disabledMarker.length; i++) {
                if (data[offset + i] != disabledMarker[i]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                int valueOffset = offset + disabledMarker.length;
                if (valueOffset + 4 > entry.end) {
                    throw new IllegalArgumentException("Short disabled card field");
                }
                int disabledCardId = readInt(data, valueOffset);
                if (disabledCardId != 0) disabledCardIds.add(disabledCardId);
            }
        }
        return new CharacterState(index, name, classId, entry, lengthOffset,
                readInt(data, levelOffset), readInt(data, unlockOffset), unlockOffset,
                unused, selected, disabledCardIds);
    }

    private static CardList cardList(byte[] data, String name, int start, int end) {
        IndexedList span = indexedList(data, new byte[] {4}, name, start, end);
        List<Integer> ids = new ArrayList<>();
        for (Entry entry : span.entries) ids.add(readInt(data, cardIdOffset(data, entry)));
        return new CardList(span, ids);
    }

    private static int cardIdOffset(byte[] data, Entry entry) {
        byte[] idMarker = marker((byte) 16, "ID");
        int keyEnd = zero(data, entry.start + 1, entry.end);
        int markerStart = unique(data, idMarker, entry.start, entry.end);
        int offset = markerStart + idMarker.length;
        if (markerStart != keyEnd + 5 || offset + 5 != entry.end
                || data[entry.end - 1] != 0) {
            throw new IllegalArgumentException("Unexpected card entry structure");
        }
        return offset;
    }

    private static IndexedList indexedList(byte[] data, byte[] tag, String name, int start, int end) {
        byte[] fieldMarker = marker(tag[0], name);
        int field = unique(data, fieldMarker, start, end);
        int lengthOffset = field + fieldMarker.length;
        int length = readInt(data, lengthOffset);
        int payloadStart = lengthOffset + 4;
        int payloadEnd = lengthOffset + length;
        if (length < 5 || payloadEnd > end) throw new IllegalArgumentException("List exceeds parent");
        List<Entry> entries = new ArrayList<>();
        int cursor = payloadStart;
        while (cursor < payloadEnd - 1) {
            if (data[cursor] != 3) throw new IllegalArgumentException("Unexpected list entry tag");
            int keyEnd = zero(data, cursor + 1, payloadEnd);
            String key = new String(data, cursor + 1, keyEnd - cursor - 1, StandardCharsets.US_ASCII);
            if (!key.equals(Integer.toString(entries.size()))) {
                throw new IllegalArgumentException("List indexes are not consecutive");
            }
            int size = readInt(data, keyEnd + 1);
            int next = keyEnd + 1 + size;
            if (size < 5 || next > payloadEnd - 1) {
                throw new IllegalArgumentException("Invalid list entry length");
            }
            entries.add(new Entry(cursor, next));
            cursor = next;
        }
        if (cursor != payloadEnd - 1 || data[cursor] != 0) {
            throw new IllegalArgumentException("Missing list terminator");
        }
        return new IndexedList(lengthOffset, payloadStart, payloadEnd, entries);
    }

    private static byte[] encodeCardList(List<Integer> ids) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < ids.size(); i++) {
            out.write(3);
            out.writeBytes(Integer.toString(i).getBytes(StandardCharsets.US_ASCII));
            out.write(0);
            out.writeBytes(new byte[] {13, 0, 0, 0, 16, 'I', 'D', 0});
            byte[] value = new byte[4];
            writeInt(value, 0, ids.get(i));
            out.writeBytes(value);
            out.write(0);
        }
        out.write(0);
        return out.toByteArray();
    }

    private static String stringField(byte[] data, String name, int start, int end) {
        byte[] fieldMarker = marker((byte) 2, name);
        int lengthOffset = unique(data, fieldMarker, start, end) + fieldMarker.length;
        int length = readInt(data, lengthOffset);
        if (length < 1 || length > 128 || lengthOffset + 4 + length > end
                || data[lengthOffset + 3 + length] != 0) {
            throw new IllegalArgumentException("Invalid string field");
        }
        return new String(data, lengthOffset + 4, length - 1, StandardCharsets.UTF_8);
    }

    private static byte[] marker(byte tag, String name) {
        byte[] text = name.getBytes(StandardCharsets.US_ASCII);
        byte[] result = new byte[text.length + 2];
        result[0] = tag;
        System.arraycopy(text, 0, result, 1, text.length);
        return result;
    }

    private static int unique(byte[] data, byte[] needle, int start, int end) {
        int found = -1;
        for (int i = start; i <= end - needle.length; i++) {
            boolean match = true;
            for (int j = 0; j < needle.length; j++) {
                if (data[i + j] != needle[j]) { match = false; break; }
            }
            if (match) {
                if (found >= 0) throw new IllegalArgumentException("Ambiguous save field");
                found = i;
            }
        }
        if (found < 0) throw new IllegalArgumentException("Missing save field");
        return found;
    }

    private static int zero(byte[] data, int start, int end) {
        for (int i = start; i < end; i++) if (data[i] == 0) return i;
        throw new IllegalArgumentException("Missing field terminator");
    }

    private static int readInt(byte[] data, int offset) {
        if (offset < 0 || offset + 4 > data.length) throw new IllegalArgumentException("Short save field");
        return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8)
                | ((data[offset + 2] & 0xff) << 16) | ((data[offset + 3] & 0xff) << 24);
    }

    private static void writeInt(byte[] data, int offset, int value) {
        if (offset < 0 || offset + 4 > data.length) throw new IllegalArgumentException("Short save field");
        data[offset] = (byte) value;
        data[offset + 1] = (byte) (value >>> 8);
        data[offset + 2] = (byte) (value >>> 16);
        data[offset + 3] = (byte) (value >>> 24);
    }
}
