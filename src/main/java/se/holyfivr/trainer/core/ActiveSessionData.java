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
import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.PlayerCharacter;

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
