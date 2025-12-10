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
    /*  STORE ALL GAME CHARACTERS  */
    /* =========================== */

    private final Map<String, PlayerCharacter> characters = new LinkedHashMap<>();

    public Map<String, PlayerCharacter> getCharacters() {
        return characters;
    }
    
    /* ==================================================================== */
    /*      This filters out the tutorial versions of bannerspear.          */
    /*      It basically says "if there is no match of the received name    */
    /*      add it to the map". There is already a check like this one      */
    /*      in the RulesetParser, but having it here as well adds an        */
    /*      extra layer of safety.                                          */
    /* ==================================================================== */
    public void addCharacter(PlayerCharacter character) {
        if (character.getName() != null){
            characters.putIfAbsent(character.getName(), character);
        }
    }

    public void clearCharacters() {
        characters.clear();
    }

    
    /* =============================== */
    /*  STORE ALL UNLOCKED CHARACTERS  */
    /* =============================== */
    private final List<String> unlockedCharacters = new ArrayList<>();

    public List<String> getUnlockedCharacterList(){
        return unlockedCharacters;
    }

    public void addUnlockedCharacter(String character){
        unlockedCharacters.add(character);
    }

    public void clearUnlockedCharacters() {
       for (int i = unlockedCharacters.size() - 1; i >= 0; i--) {
            unlockedCharacters.remove(i);
        }
    }

    /* ================= */
    /*  STORE ALL ITEMS  */
    /* ================= */
    private final Map<String, Item> items = new LinkedHashMap<>();
    public Map<String, Item> getItems() {
        return items;
    }
    public void addItem(Item item) {
        if (item.getStringId() != null){
            //removes the "ID" part from the stringId to get the actual item name
            String itemName = item.getStringId().substring(0, (item.getStringId().length() -2));
            items.putIfAbsent(itemName, item);
        }
    }
    public void clearItems() {
        items.clear();
    }

}
