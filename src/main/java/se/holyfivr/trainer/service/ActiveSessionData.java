package se.holyfivr.trainer.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.PlayerCharacter;

@Component
public class ActiveSessionData {

    private Path rulesetPath;

    public Path getRulesetPath() {
        return rulesetPath;
    }

    public void setRulesetPath(Path rulesetPath) {
        this.rulesetPath = rulesetPath;
    }

    /* =========================== */
    /* STORE ALL GAME CHARACTERS */
    /* =========================== */

    private final Map<String, PlayerCharacter> characters = new LinkedHashMap<>();

    public Map<String, PlayerCharacter> getCharacters() {
        return characters;
    }

    /* ==================================================================== */
    /* This filters out the tutorial versions of bannerspear. */
    /* It basically says "if there is no match of the received name */
    /* add it to the map". There is already a check like this one */
    /* in the RulesetParser, but having it here as well adds an */
    /* extra layer of safety. */
    /* ==================================================================== */
    public void addCharacter(PlayerCharacter character) {
        if (character.getName() != null) {
            characters.putIfAbsent(character.getName(), character);
        }
    }

    public void clearCharacters() {
        characters.clear();
    }

    /* =============================== */
    /* STORE ALL UNLOCKED CHARACTERS */
    /* =============================== */
    private final List<String> unlockedCharacters = new ArrayList<>();

    public List<String> getUnlockedCharacterList() {
        return unlockedCharacters;
    }

    public void addUnlockedCharacter(String character) {
        unlockedCharacters.add(character);
    }

    public void clearUnlockedCharacters() {
        for (int i = unlockedCharacters.size() - 1; i >= 0; i--) {
            unlockedCharacters.remove(i);
        }
    }

    /* ================= */
    /* STORE ALL ITEMS */
    /* ================= */
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

    public void setGold(String itemCost) {
        System.err.println("Setting gold to: " + itemCost);
        for (Item item : items.values()) {
            item.setCost(itemCost);
        }
    }

    public void setProsperity(String prosperityRequirement) {
        for (Item item : items.values()) {
            item.setProsperityRequirement(prosperityRequirement);
        }
    }

    public void setHeal(String heal) {
        for (Item item : items.values()) {
            item.setHeal(heal);
        }
    }

    public void setRetaliate(String retaliate) {
        for (Item item : items.values()) {
            item.setRetaliate(retaliate);
        }
    }

    public void setMovement(String movement) {
        for (Item item : items.values()) {
            item.setMove(movement);
        }
    }

    public void setOMove(String oMove) {
        for (Item item : items.values()) {
            item.setOMove(oMove);
        }
    }
     
    public void setAMove(String aMove) {
        for (Item item : items.values()) {
            item.setAMove(aMove);
        }
    }

    public void setAttack(String attack) {
        for (Item item : items.values()) {
            item.setAttack(attack);
        }
    }

    public void setShield(String shield) {
        for (Item item : items.values()) {
            item.setShield(shield);
        }
    }

    public void setShieldValue(String shieldValue) {
        for (Item item : items.values()) {
            item.setShieldValue(shieldValue);
        }
    }

    public void setUsage(String usage) {
        for (Item item : items.values()) {
            item.setUsage(usage);
        }
    }

    public void setTotalInGame(String totalInGame) {
        for (Item item : items.values()) {
            item.setTotalInGame(totalInGame);
        }
    }
}
