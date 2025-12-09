package se.holyfivr.trainer.service;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

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

}
