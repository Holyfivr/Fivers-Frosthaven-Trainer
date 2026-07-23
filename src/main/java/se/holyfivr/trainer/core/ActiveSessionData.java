package se.holyfivr.trainer.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.EnhancementGroup;
import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.model.SaveCharacter;

/* ======================================================================== */
/* This class is used to store all data that is relevant for the current    */
/* session. The changes made to the data when editing an object will be     */
/* used to display the changes on the item/character/abilitycard/etc pages. */
/* Once a session ends, the data from this class will be used to patch      */
/* together the content-section, and replace any changed parts, before      */
/* finally saving the updated content to the ruleset-file.                  */
/* ======================================================================== */

@Service
public class ActiveSessionData {

    private Path rulesetPath;

    public Path getRulesetPath() {
        return rulesetPath;
    }

    public void setRulesetPath(Path rulesetPath) {
        this.rulesetPath = rulesetPath;
    }

    /* ========================================================================= */
    /* Flag that signals the opened ruleset file differs in size from the        */
    /* original backup. This means the file was changed outside this tool, which */
    /* is either a game patch (new, valid file) or file corruption. We can't     */
    /* tell which, so we raise this flag and let the user decide on the /start    */
    /* page via a warning modal.                                                 */
    /* ========================================================================= */
    private boolean sizeMismatchWarning;

    public boolean isSizeMismatchWarning() {
        return sizeMismatchWarning;
    }

    public void setSizeMismatchWarning(boolean sizeMismatchWarning) {
        this.sizeMismatchWarning = sizeMismatchWarning;
    }

    /* ========================================================================= */
    /* SAVE FILE SESSION (.dat editing)                                          */
    /* Holds the currently opened save file: its path, the raw bytes, and the    */
    /* parsed character records. While a save file is open, ruleset saving is    */
    /* disabled in the menu to avoid confusion about which file is being edited. */
    /* ========================================================================= */
    private Path saveFilePath;
    private byte[] saveFileBytes;
    private final List<SaveCharacter> saveCharacters = new ArrayList<>();
    // Per-class enhancement summary of the open save (drives the reset buttons)
    private final List<EnhancementGroup> enhancementGroups = new ArrayList<>();

    public Path getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(Path saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public byte[] getSaveFileBytes() {
        return saveFileBytes;
    }

    public void setSaveFileBytes(byte[] saveFileBytes) {
        this.saveFileBytes = saveFileBytes;
    }

    public List<SaveCharacter> getSaveCharacters() {
        return saveCharacters;
    }

    public void setSaveCharacters(List<SaveCharacter> characters) {
        saveCharacters.clear();
        saveCharacters.addAll(characters);
    }

    public List<EnhancementGroup> getEnhancementGroups() {
        return enhancementGroups;
    }

    public void setEnhancementGroups(List<EnhancementGroup> groups) {
        enhancementGroups.clear();
        enhancementGroups.addAll(groups);
    }

    public boolean isSaveFileLoaded() {
        return saveFilePath != null;
    }

    public void clearSaveFile() {
        saveFilePath = null;
        saveFileBytes = null;
        saveCharacters.clear();
        enhancementGroups.clear();
    }

    /* ========================================== */
    /* RESET ALL DATA WHEN STARTING A NEW SESSION */
    /* ========================================== */
    public void reset() {
        clearCharacters();
        clearUnlockedCharacters();
        clearAbilityCards();
        clearItems();
    }

    /* =========================== */
    /* STORE ALL GAME CHARACTERS   */
    /* =========================== */

    private final Map<String, PlayerCharacter> characters = new LinkedHashMap<>();

    public Map<String, PlayerCharacter> getCharacters() {
        return characters;
    }

    /* ==================================================================== */
    /* This filters out the tutorial versions of bannerspear.               */
    /* It basically says "if there is no match of the received name         */
    /* add it to the map". There is already a check like this one           */
    /* in the RulesetParser, but having it here as well adds an             */
    /* extra layer of safety.                                               */
    /* ==================================================================== */
    public void addCharacter(PlayerCharacter character) {
        if (character.getName() != null) {
            characters.putIfAbsent(character.getName(), character);
        }
    }

    public void clearCharacters() {
        characters.clear();

    }

    /* ============================= */
    /* STORE ALL UNLOCKED CHARACTERS */
    /* ============================= */
    private final List<String> unlockedCharacters = new ArrayList<>();

    public List<String> getUnlockedCharacterList() {
        return unlockedCharacters;
    }

    public void addUnlockedCharacter(String character) {
        unlockedCharacters.add(character);
    }

    public void clearUnlockedCharacters() {
        unlockedCharacters.clear();
    }

    /* =============== */
    /* STORE ALL ITEMS */
    /* =============== */
    private final Map<String, Item> items = new LinkedHashMap<>();

    public Map<String, Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        if (item.getStringId() != null) {
            items.putIfAbsent(item.getStringId(), item);
        }
    }

    public void clearItems() {
        items.clear();

    }

    public void setID(String value) {
        for (Item item : items.values()) {
            if (item.getId() != null) {
                item.setId(value);
            }
        }
    }

    public void setProsperity(String value) {
        for (Item item : items.values()) {
            if (item.getProsperReq() != null && !item.getProsperReq().trim().equals("0")) {
                item.setProsperReq(value);
            }
        }
    }

    /* ====================== */
    /* STORE ALL ABILITYCARDS */
    /* ====================== */

    private final Map<String, AbilityCard> abilityCards = new LinkedHashMap<>();

    public Map<String, AbilityCard> getAbilityCards() {
        return abilityCards;
    }

    public List<String> getCardClasses() {
        Set<String> classSet = new LinkedHashSet<>();
        for (AbilityCard card : abilityCards.values()) {
            classSet.add(card.getClassName());
        }
        return new ArrayList<>(classSet);
    }

    public void addAbilityCard(AbilityCard abilityCard) {
        if (abilityCard.getName() != null) {
            abilityCards.putIfAbsent(abilityCard.getName(), abilityCard);
        }
    }

    public void clearAbilityCards() {
        abilityCards.clear();

    }

}
