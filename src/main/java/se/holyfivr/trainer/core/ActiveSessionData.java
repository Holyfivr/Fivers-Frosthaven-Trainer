// Flyttad till core-paketet
package se.holyfivr.trainer.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.PlayerCharacter;

@Service
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

    public void setGold(String value) {
        System.err.println("Setting gold to: " + value);
        for (Item item : items.values()) {
            if (item.getCost() != null && !item.getCost().trim().equals("0")) {
                item.setCost(value);
            }
        }
    }

    public void setProsperity(String value) {
        for (Item item : items.values()) {
            if (item.getProsperityRequirement() != null) {
                item.setProsperityRequirement(value);
            }
        }
    }

    public void setHeal(String value) {
        for (Item item : items.values()) {
            if (item.getHeal() != null) {
                item.setHeal(value);
            }
        }
    }

    public void setRetaliate(String value) {
        for (Item item : items.values()) {
            if (item.getRetaliate() != null) {
                item.setRetaliate(value);
            }
        }
    }

    public void setMovement(String value) {
        for (Item item : items.values()) {
            if (item.getMove() != null) {
                item.setMove(value);
            }
        }
    }

    public void setOMove(String value) {
        for (Item item : items.values()) {
            if (item.getOMove() != null) {
                item.setOMove(value);
            }
        }
    }

    public void setAMove(String value) {
        for (Item item : items.values()) {
            if (item.getAMove() != null) {
                item.setAMove(value);
            }
        }
    }

    public void setAttack(String value) {
        for (Item item : items.values()) {
            if (item.getAttack() != null) {
                item.setAttack(value);
            }
        }
    }

    public void setShield(String value) {
        for (Item item : items.values()) {
            if (item.getShield() != null) {
                item.setShield(value);
            }
        }
    }

    public void setShieldValue(String value) {
        for (Item item : items.values()) {
            if (item.getShieldValue() != null) {
                item.setShieldValue(value);
            }
        }
    }

    public void setUsage(String value) {
        for (Item item : items.values()) {
            if (item.getUsage() != null) {
                item.setUsage(value);
            }
        }
    }

    public void setRange(String value) {
        for (Item item : items.values()) {
            if (item.getRange() != null) {
                item.setRange(value);
            }
        }
    }

    public void setTotalInGame(String value) {
        for (Item item : items.values()) {
            if (item.getTotalInGame() != null) {
                item.setTotalInGame(value);
            }
        }
    }

}
